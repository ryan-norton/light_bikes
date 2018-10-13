package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;

public class LevelThreeBotPlayer extends BotPlayer {
  protected Random rand = new Random();
  protected Point centerPoint;
  protected Point targetEdge;
  protected String lastDir;
  protected String currentDir;
  protected String objective = "get_to_center";

  LevelThreeBotPlayer(Game game, String color, Point p) {
    this(game, color, p, "Bot Level 3");
  }
  LevelThreeBotPlayer(Game game, String color, Point p, String name) {
    super(game, color, p, name);

    int half = game.getBoard().length / 2;
    this.centerPoint = new Point(half, half);
  }

  protected Point findBestMove() {
    Point move = null;
    ArrayList<Point> moves;

    if (this.objective == "get_to_center") {
      move = moveToCenter();
      if (move == null) {
        this.objective = "hunt";
      }
    }

    if (this.objective == "hunt") {
      move = moveToOpponent();
      if (move == null) {
        this.objective = "get_to_edge";
      }
    }

    // if (this.objective == "get_to_edge") {
    //   move = moveToFurthestEdge();
    //   if (move == null) {
    //     this.objective = "survive";
    //   }
    // }

    // YOLO!
    if (move == null) {
      move = moveToSurvive();
    }

    // If we haven't found something yet, just take the first move
    moves = availableMoves();
    if (move == null && moves.size() > 0) {
      move = moves.get(rand.nextInt(moves.size()));
    }
    if (move == null) {
      moves = availableMoves(true);
      move = moves.get(rand.nextInt(moves.size()));
    }

    String newDir = dir(this.position, move);
    if (newDir != currentDir) {
      lastDir = currentDir;
    }

    this.currentDir = newDir;
    return move;
  }

  protected Point moveToCenter() {
    double distanceToCenter = this.position.distance(this.centerPoint);

    return safeMoves()
      .stream()
      .filter(m -> m.distance(centerPoint) < distanceToCenter)
      .sorted((m1, m2) -> compareForDirection(m1, m2))
      .findFirst().orElse(null);
  }

  protected Point moveToOpponent() {
    Player opponent = firstOpponent();
    double distanceToOpponent = this.position.distance(opponent.getPosition());
    int lead = Math.min((int)distanceToOpponent, 3);
    Point head = new Point(opponent.getPosition());
    head.translate(
      opponent.getPosDiff().x * lead,
      opponent.getPosDiff().y * lead
    );

    double distanceToHead = this.position.distance(head);
    return safeMoves()
      .stream()
      .filter(m -> m.distance(head) < distanceToHead)
      .sorted((m1, m2) -> compareForDirection(m1, m2))
      .findFirst().orElse(null);
  }

  protected Point moveToFurthestEdge() {
    if (targetEdge == null) {
      targetEdge = findFurthestEdge();
      if (targetEdge == null) {
        return null;
      }
    }

    double distanceToEdge = this.position.distance(targetEdge);

    return safeMoves()
      .stream()
      .filter(m -> m.distance(targetEdge) < distanceToEdge)
      .sorted((m1, m2) -> compareForDirection(m1, m2))
      .findFirst().orElse(null);
  }

  protected Point findFurthestEdge() {
    return safeMoves()
      .stream()
      .sorted((m1, m2) -> Integer.compare(distanceToEdge(m2), distanceToEdge(m1)))
      .findFirst().orElse(null);
  }

  protected Point moveToSurvive() {
    return safeMoves()
      .stream()
      .sorted((m1, m2) -> compareForEdgeRiding(m1, m2))
      .findFirst().orElse(null);
  }

  protected int edgeWeight(Point move) {
    int weight = 0;
    Point a = new Point(move.x - 1, move.y);
    Point b = new Point(move.x + 1, move.y);
    Point c = new Point(move.x, move.y - 1);
    Point d = new Point(move.x, move.y + 1);

    if (!a.equals(position) && allPoints.get(a) != null) { weight++; }
    if (!b.equals(position) && allPoints.get(b) != null) { weight++; }
    if (!c.equals(position) && allPoints.get(c) != null) { weight++; }
    if (!d.equals(position) && allPoints.get(d) != null) { weight++; }

    return weight;
  }

  protected int compareForEdgeRiding(Point m1, Point m2) {
    int weight1 = edgeWeight(m1);
    int weight2 = edgeWeight(m2);

    // If same weight, prefer same direction
    if (weight1 == weight2) { return compareForDirection(m2, m1); }

    // Prefer more edges
    return Integer.compare(weight2, weight1);
  }

  protected int compareForDirection(Point m1, Point m2) {
    boolean m1IsSameDir = dir(this.position, m1) == this.currentDir;
    boolean m2IsSameDir = dir(this.position, m2) == this.currentDir;

    // Prefer the different direction
    return Boolean.compare(m1IsSameDir, m2IsSameDir);
  }
}
