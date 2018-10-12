package com.dhyer.light_bikes;

import java.awt.*;

public class BotFactory {

  public static Player generateBot(int level, Game game, String color, Point point) {
    switch(level) {
      case 3: return new LevelThreeBotPlayer(game, color, point);
      case 2: return new LevelTwoBotPlayer(game, color, point);
      default: return new LevelOneBotPlayer(game, color, point);
    }
  }
}
