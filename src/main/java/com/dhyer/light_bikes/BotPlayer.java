package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public abstract class BotPlayer extends Player {
  protected Map<Point, String> allPoints; 

  BotPlayer(Game game, String color, Point p, String name) {
    super(game, name, color, p);

    this.isBot = true;
    this.allPoints = new HashMap<>();
  }

  public void move(GameStore gameStore) {
    updateAllPoints();
    Point move = findBestMove();

    // check if the bot has died, otherwise make the move
    if(this.allPoints.get(move) != null) {
      this.getGame().killPlayer(this, gameStore);
    } else {
      this.updateCurrentLocation(move.x, move.y);
    }
  }

  private void updateAllPoints() {
    String[][] b = this.getGame().getBoard();

    for (int x = 0; x < b.length; x++) {
      // Set the wall points
      this.allPoints.put(new Point(-1, x), "wall");
      this.allPoints.put(new Point(x, -1), "wall");
      this.allPoints.put(new Point(x, b.length), "wall");
      this.allPoints.put(new Point(b.length, x), "wall");

      // Set all the board points
      for (int y = 0; y < b.length; y++) {
        this.allPoints.put(new Point(x,y), b[x][y]);
      }
    }
  }

  protected ArrayList<Point> safeMoves() { return safeMoves(false); }
  protected ArrayList<Point> safeMoves(boolean all) {
    return Lists.newArrayList(Collections2.filter(
      availableMoves(all),
      m -> all || !causesDeath(m) 
    ));
  }

  protected ArrayList<Point> availableMoves() {
    return availableMoves(false);
  }
  protected ArrayList<Point> availableMoves(boolean all) {
    return availableMoves(this.position, all);
  }
  protected ArrayList<Point> availableMoves(Point from, boolean all) {
    Point up = new Point(from.x - 1, from.y);
    Point left = new Point(from.x, from.y -1 );
    Point down = new Point(from.x + 1, from.y);
    Point right = new Point(from.x, from.y + 1);
    ArrayList<Point> moves = new ArrayList<>();

    if (all || isLegitMove(left)) {
      moves.add(left);
    }
    if (all || isLegitMove(up)) {
      moves.add(up);
    }
    if (all || isLegitMove(right)) {
      moves.add(right);
    }
    if (all || isLegitMove(down)) {
      moves.add(down);
    }

    return moves;
  }

  protected boolean isLegitMove(Point move) {
    return this.allPoints.get(move) == null;
  }

  protected boolean causesDeath(Point move) {
    return availableMoves(move, false).isEmpty();
  }

  protected Player firstOpponent() {
    return getGame()
      .players
      .stream()
      .filter(p -> p.getColor() != this.color)
      .findFirst().orElse(null);
  }

  protected String dir(Point a, Point b) {
    if (a.x > b.x) { return "a"; }
    if (a.x < b.x) { return "b"; }
    if (a.y > b.y) { return "c"; }
    if (a.y < b.y) { return "d"; }

    return "same";
  }

  protected abstract Point findBestMove();
}
