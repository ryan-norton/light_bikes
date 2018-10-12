package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;

public class LevelFourBotPlayer extends LevelThreeBotPlayer {
  protected Random rand = new Random();
  protected Point centerPoint;
  protected Point targetEdge;
  protected String lastDir;
  protected String objective = "get_to_center";

  LevelFourBotPlayer(Game game, String color, Point p) {
    super(game, color, p, "Bot Level 4");
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
