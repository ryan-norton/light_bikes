package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;

public class LevelThreeBotPlayer extends BotPlayer {
  protected Random rand = new Random();
  protected Point centerPoint;
  protected Point targetEdge;
  protected String lastDir;
  protected String objective = "get_to_center";

  LevelThreeBotPlayer(Game game, String color, Point p) {
    super(game, color, p, "Bot Level 3");

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

    if (this.objective == "get_to_edge") {
      move = moveToFurthestEdge();
      if (move == null) {
        this.objective = "survive";
      }
    }

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

    this.lastDir = dir(this.position, move);
    return move;
  }

  protected Point moveToCenter() {
    double distanceToCenter = this.position.distance(this.centerPoint);

    return safeMoves()
      .stream()
      .filter(m -> m.distance(centerPoint) < distanceToCenter)
      .sorted((m1, m2) -> Boolean.compare(dir(this.position, m1) == this.lastDir, dir(this.position, m2) == this.lastDir))
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
      .sorted((m1, m2) -> Boolean.compare(dir(this.position, m1) == this.lastDir, dir(this.position, m2) == this.lastDir))
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
      .sorted((m1, m2) -> Boolean.compare(dir(position, m1) == lastDir, dir(position, m2) == lastDir))
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
      .findFirst().orElse(null);
  }
}
