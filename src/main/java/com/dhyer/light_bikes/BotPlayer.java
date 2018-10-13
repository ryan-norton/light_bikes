package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public abstract class BotPlayer extends Player {
  protected Map<Point, String> allPoints; 
  protected ArrayList<ArrayList<Point>> openAreas;

  BotPlayer(Game game, String color, Point p, String name) {
    super(game, name, color, p);

    this.isBot = true;
    this.allPoints = new HashMap<>();
    this.openAreas = new ArrayList<>();
  }

  public void move(GameStore gameStore) {
    updateAllPoints();
    updateOpenAreas();
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

  protected void updateOpenAreas() {
    int length = getGame().getBoard().length;
    Map<Point, String> visited = new HashMap<>();

    openAreas.clear();

    for (int x = 0; x < length; x++) {
      for (int y = 0; y < length; y++) {
        Point p = new Point(x, y);

        if (allPoints.get(p) == null && visited.get(p) == null) {
          openAreas.add(deepSearch(p, visited));
        }
      }
    }
  }

  protected ArrayList<Point> deepSearch(Point point, Map<Point, String> visited) {
    ArrayList<Point> area = new ArrayList<>();
    area.add(point);
    visited.put(point, "Y");

    int[] rowNbr = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] colNbr = {-1, 0, 1, -1, 1, -1, 0, 1};

    for (int k = 0; k < 8; k++) {
      Point p = new Point(point.x + rowNbr[k], point.y + colNbr[k]);
      if (allPoints.containsKey(p) && allPoints.get(p) == null && visited.get(p) == null) {
        area.addAll(deepSearch(p, visited));
      }
    }

    return area;
  }

  protected int areaSizeFor(Point move) {
    ArrayList<Point> area = openAreas
      .stream()
      .filter(a -> a.contains(move))
      .findFirst().orElse(null);

    return area == null ? 0 : area.size();
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

  protected int distanceToEdge(Point move) {
    int d = 0;
    Point diff = new Point(position.x - move.x, position.y - move.y);

    do {
      d++;
      move.translate(diff.x, diff.y);
    } while (allPoints.get(move) == null);

    return d;
  }

  protected abstract Point findBestMove();
}
