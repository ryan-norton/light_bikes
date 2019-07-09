package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;

public class LevelFourBotPlayer extends LevelThreeBotPlayer {
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
      .sorted((m1, m2) -> compareForAreaSize(m1, m2))
      .findFirst().orElse(null);
  }

  protected int compareForAreaSize(Point m1, Point m2) {
    updateOpenAreas();

    int size1 = areaSizeFor(m1);
    int size2 = areaSizeFor(m2);

    if (size1 == size2) {
      return compareForEdgeRiding(m1, m2);
    }

    // Prefer largest area size
    return Integer.compare(size2, size1);
  }

  protected int compareForDirection(Point m1, Point m2) {
    boolean m1Current = dir(this.position, m1) == this.currentDir;
    boolean m2Current = dir(this.position, m2) == this.currentDir;

    // If neither are in the current direction
    if (m1Current == m2Current) {
      // Pick the penultimate direction
      return Boolean.compare(
        dir(this.position, m1) == this.lastDir,
        dir(this.position, m2) == this.lastDir
      );
    }

    // Prefer the different direction
    return Boolean.compare(m1Current, m2Current);
  }
}
