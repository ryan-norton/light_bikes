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
import java.time.temporal.*;

public class Game {
  public static final int BOARD_SIZE_MIN = 15;
  public static final int BOARD_SIZE_MAX = 30;
  public static final int MAX_PLAYERS = 4;
  public static final int TURN_TIME_LIMIT_MS = 5000;

  private static final String[] COLORS = {"red", "blue", "green", "yellow"};

  private UUID id;
  private String[][] board;
  private int boardSize;
  private int playerLimit;
  private ArrayList<Player> players;
  private Player currentPlayer;
  private boolean started;
  private String winner;
  private LocalTime createdAt;
  private LocalTime lastUpdated;

  @JsonIgnore
  private boolean hasBotPlayer;

  private ArrayList<Point> startingPoints;

  private final Object playerLock = new Object();

  Game(int boardSize, int playersCount, boolean isTest, GameStore gameStore) {
    this.id = UUID.randomUUID();
    this.boardSize = boardSize;
    this.board = new String[this.boardSize][this.boardSize];
    this.playerLimit = playersCount;
    this.players = new ArrayList<>();
    this.startingPoints = new ArrayList<>();
    this.started = false;
    this.winner = null;
    this.createdAt = LocalTime.now();
    this.lastUpdated = this.createdAt;

    generateStartingPoints();

    if (isTest) {
      this.hasBotPlayer = true;

      // Fill the game with bots, save one place
      for (int i = 0; i < this.playerLimit - 1; i++) {
        String color = getAvailableColor();
        Player bot = new BotPlayer(
          this,
          color,
          getAvailableStartingPoint(color)
        );

        this.players.add(bot);
      }
    }
  }

  public UUID getId() {
    return this.id;
  }

  public LocalTime getCreatedAt() {
    return this.createdAt;
  }

  public Player getCurrentPlayer() {
    return this.currentPlayer;
  }

  public String getWinner() {
    return this.winner;
  }

  public int getBoardSize() {
    return this.boardSize;
  }

  public String[][] getBoard() {
    return board;
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

  public Player addPlayer(String name, GameStore gameStore) {
    refreshLastUpdated();

    synchronized(playerLock) {
      String color = getAvailableColor();
      Player player = new Player(
        this,
        name,
        color,
        getAvailableStartingPoint(color)
      );

      this.players.add(player);

      if(this.players.size() == this.playerLimit) {
        this.started = true;

        // Choose a random player to be first
        this.currentPlayer = this.players.get(new Random().nextInt(this.playerLimit));

        // If the starting player is a bot, move them now and advance to the external player
        if (this.currentPlayer.isBot) {
          ((BotPlayer)this.currentPlayer).move(gameStore);
          advanceTurn(gameStore);
        }
      }

      return player;
    }
  }

  public String getAvailableColor() {
    ArrayList<String> colors = new ArrayList(Arrays.asList(COLORS));

    for (Player p : this.players) {
      if(p != null) {
        colors.remove(p.getColor());
      }
    }

    return colors.get(new Random().nextInt(colors.size()));
  }

  public Point getAvailableStartingPoint(String color) {
    Point p = this.startingPoints.get(new Random().nextInt(this.startingPoints.size()));
    this.startingPoints.remove(p);
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

  public boolean isActive() {
    return this.started && this.winner == null;
  }

  public void refreshLastUpdated() {
    this.lastUpdated = LocalTime.now();
  }

  public boolean isPlayersTurn(UUID playerId) {
    return this.currentPlayer != null && playerId.equals(this.currentPlayer.getId());
  }

  public void updatePlayerLocation(Player p, int x, int y, GameStore gameStore) {
    refreshLastUpdated();

    if (Math.abs(x - p.getCurrentX()) + Math.abs(y - p.getCurrentY()) > 1) {
      killPlayer(p, gameStore);
      throw new InvalidRequestException("You can only move a maximum of 1 space per turn");
    }
    if (x >= this.boardSize || x < 0 || y >= this.boardSize || y < 0) {
      killPlayer(p, gameStore);
      throw new InvalidRequestException("You must stay on the board");
    }
    if (this.board[x][y] != null) {
      killPlayer(p, gameStore);
      String causeOfDeath;
      if (this.board[x][y] == p.getColor()) {
        causeOfDeath = "You ran into your own tail";
      } else {
        causeOfDeath = String.format("You ran into the %s player's tail", this.board[x][y]);
      }
      throw new InvalidRequestException(causeOfDeath);
    }

    p.updateCurrentLocation(x, y);
  }

  public void advanceTurn(GameStore gameStore) {
    Player lastActive = this.currentPlayer;

    do {
      this.currentPlayer = getNextPlayer();

      if (this.currentPlayer.isBot) {
        ((BotPlayer)this.currentPlayer).move(gameStore);
      }
    } while (
      !this.currentPlayer.equals(lastActive) &&
        (this.currentPlayer.isBot || !this.currentPlayer.isAlive())
    );
  }

  public Player getNextPlayer() {
    int currentIndex = this.players.indexOf(this.currentPlayer);
    int nextIndex = currentIndex == this.players.size() - 1 ? 0 : currentIndex + 1;

    return this.players.get(nextIndex);
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

  public boolean hasTurnExpired() {
    return this.lastUpdated
      .plus(TURN_TIME_LIMIT_MS, ChronoUnit.MILLIS)
      .isBefore(LocalTime.now());
  }

  private void generateStartingPoints() {
    int length = this.board.length;
    int halfLength = length / 2;

    startingPoints.clear();
    switch (new Random().nextInt(3)) {
      // Corners
      case 0:
        startingPoints.add(new Point(0, 0));
        startingPoints.add(new Point(0, length - 1));
        startingPoints.add(new Point(length - 1, 0));
        startingPoints.add(new Point(length - 1, length - 1));
        break;

        // Center-Edge
      case 1:
        startingPoints.add(new Point(0, halfLength));
        startingPoints.add(new Point(length - 1, halfLength));
        startingPoints.add(new Point(halfLength, 0));
        startingPoints.add(new Point(halfLength, length - 1));
        break;

        // Center(ish) of Arena
      case 2:
        startingPoints.add(new Point(halfLength, halfLength));
        startingPoints.add(new Point(halfLength, halfLength + 1));
        startingPoints.add(new Point(halfLength + 1, halfLength));
        startingPoints.add(new Point(halfLength + 1, halfLength + 1));
        break;
    }
  } 
}
