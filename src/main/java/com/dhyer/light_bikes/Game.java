package com.dhyer.light_bikes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.*;
import java.time.*;

public class Game {
  private static final int PLAYER_LIMIT = 2;
  public static final int BOARD_SIZE = 25;
  private UUID id;
  private String[][] board;
  private ArrayList<Player> players;
  private Player currentPlayer;
  private boolean started;
  private String winner;
  private LocalTime lastUpdated;

  @JsonIgnore
  private boolean hasBotPlayer;

  Game(boolean isTest, GameStore gameStore) {
    this.id = UUID.randomUUID();
    this.board = new String[BOARD_SIZE][BOARD_SIZE];
    this.players = new ArrayList<>();
    this.started = false;
    this.winner = null;
    this.lastUpdated = LocalTime.now();

    if(isTest) {
      addPlayer(new BotPlayer(this), gameStore);
      this.hasBotPlayer = true;
    }
  }

  public UUID getId() {
    return this.id;
  }

  public Player getCurrentPlayer() {
    return this.currentPlayer;
  }

  public String getWinner() {
    return this.winner;
  }

  public void addPlayer(Player player, GameStore gameStore) {
    this.players.add(player);

    if(this.players.size() == PLAYER_LIMIT) {
      this.started = true;

      // Choose a random player to be first
      this.currentPlayer = this.players.get(new Random().nextInt(PLAYER_LIMIT));

      // If the starting player is a bot, move them now and advance the current player
      // TODO: Make this work with > 2 players
      if (this.currentPlayer.isBot) {
        BotPlayer bp = (BotPlayer) this.currentPlayer;
        bp.move(gameStore);

        for(Player p : this.players) {
          if(!p.equals(player)) {
            this.currentPlayer = player;
          }
        }
      }
    }
  }

  public JSONObject toJson() {
    JSONObject obj = new JSONObject();
    obj.put("id", this.id);
    obj.put("board", this.board);

    // Add the games players
    JSONArray arr = new JSONArray();
    for(Player p : this.players) {
      arr.add(p.toJson());
    }
    obj.put("players", arr);

    // Add the current player
    if(this.currentPlayer != null) {
      obj.put("current_player", this.currentPlayer.toJson(true));
    }
    obj.put("winner", this.winner);

    return obj;
  }

  public String getAvailableColor(String[] colors) {
    for (Player p : this.players) {
      if(p != null) {
        colors = ArrayUtils.removeElement(colors, p.getColor());
      }
    }
    return colors[new Random().nextInt(colors.length)];
  }

  public Point getAvailableStartingPoint(String color) {
    // First player starts in top left. Second player starts in bottom right
    Point p;
    if(this.board[0][0] == null) {
      p = new Point(0, 0);
    } else {
      p = new Point(this.board.length - 1, this.board.length - 1);
    }
    this.board[p.x][p.y] = color;
    return p;
  }

  public boolean hasPlayer(UUID playerId) {
    return getPlayer(playerId) != null;
  }

  public Player getPlayer(UUID playerId) {
    for(Player p : this.players) {
      if(playerId.equals(p.getId())) {
        return p;
      }
    }
    return null;
  }

  public boolean hasStarted() {
    return this.started;
  }

  public String[][] getBoard() {
    return board;
  }

  public boolean isPlayersTurn(UUID playerId) {
    return this.currentPlayer != null && playerId.equals(this.currentPlayer.getId());
  }

  public void updatePlayerLocation(Player p, int x, int y, GameStore gameStore) {
    if(Math.abs(x - p.getCurrentX()) + Math.abs(y - p.getCurrentY()) > 1) {
      killPlayer(p, gameStore);
      throw new InvalidRequestException("You can only move a maximum of 1 space per turn");
    } else if(x >= BOARD_SIZE || x < 0 || y >= BOARD_SIZE || y < 0) {
      killPlayer(p, gameStore);
      throw new InvalidRequestException("You must stay on the board");
    }

    if(this.board[x][y] != null) {
      killPlayer(p, gameStore);
    } else {
      p.updateCurrentLocation(x, y);
      // TODO: this won't work if there are more than 2 players in a game
      for(Player player : this.players) {
        // move all bots, pick next currentPlayer
        if(player.isBot) {
          BotPlayer bp = (BotPlayer) player;
          bp.move(gameStore);
        } else if(!p.equals(player)) {
          this.currentPlayer = player;
        }
      }
    }
  }

  public void updateBoard(int x, int y, String color) {
    this.board[x][y] = color;
  }

  public void killPlayer(Player player, GameStore gameStore) {
    player.kill();

    Collection<Player> alivePlayers = Collections2.filter(this.players, Player::isAlive);
    if (alivePlayers.size() == 1) {
      this.winner = Iterables.get(alivePlayers, 0).getColor();
    }
  }

  public boolean hasExpired() {
    int expirationPeriod = this.started ? 30 : 120;
    return this.lastUpdated
      .plusSeconds(expirationPeriod)
      .isBefore(LocalTime.now());
  }
}
