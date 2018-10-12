package com.dhyer.light_bikes;

import java.awt.*;
import java.util.HashMap;

public class LevelTwoBotPlayer extends BotPlayer {
  LevelTwoBotPlayer(Game game, String color, Point p) {
    super(game, color, p, "Bot Level 2");
  }

  protected Point findBestMove() {
    Game g = this.getGame();
    String[][] b = g.getBoard();
    int currentX = this.getCurrentX();
    int currentY = this.getCurrentY();

    int rightScore = 1;
    while(currentX + rightScore < b.length && b[currentX + rightScore][currentY] == null) {
      rightScore++;
    }

    int leftScore = -1;
    while(currentX + leftScore >= 0 && b[currentX + leftScore][currentY] == null) {
      leftScore--;
    }

    int downScore = 1;
    while(currentY + downScore < b.length && b[currentX][currentY + downScore] == null) {
      downScore++;
    }

    int upScore = -1;
    while(currentY + upScore >= 0 && b[currentX][currentY + upScore] == null) {
      upScore--;
    }

    HashMap<String, Integer> scores = new HashMap<>();
    scores.put("right", rightScore);
    scores.put("left", leftScore);
    scores.put("down", downScore);
    scores.put("up", upScore);

    String direction = scores
        .entrySet()
        .stream()
        .max((entry1, entry2) -> Math.abs(entry1.getValue()) > Math.abs(entry2.getValue()) ? 1 : -1)
        .get()
        .getKey();

    Point p = new Point(currentX, currentY);
    switch (direction) {
      case "right": p = new Point(currentX + 1, currentY);
                    break;
      case "left": p = new Point(currentX - 1, currentY);
                    break;
      case "down": p = new Point(currentX, currentY + 1);
                    break;
      case "up": p = new Point(currentX, currentY - 1);
                  break;
    }

    return p;
  }
}
