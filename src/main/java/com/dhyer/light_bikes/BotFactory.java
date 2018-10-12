package com.dhyer.light_bikes;

import java.awt.*;

public class BotFactory {

  public static Player generateBot(int level, Game game, String color, Point point) {
    level = Math.min(level, 4);
    level = Math.max(level, 1);

    switch(level) {
      case 4: return new LevelFourBotPlayer(game, color, point);
      case 3: return new LevelThreeBotPlayer(game, color, point);
      case 2: return new LevelTwoBotPlayer(game, color, point);
      default: return new LevelOneBotPlayer(game, color, point);
    }
  }
}
