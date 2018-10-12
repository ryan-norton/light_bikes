package com.dhyer.light_bikes;

import java.awt.*;

public class BotFactory {

  public static Player generateBot(int level, Game game, String color, Point point) {
    switch(level) {
      case 2: return new MediumBotPlayer(game, color, point);
      default: return new EasyBotPlayer(game, color, point);
    }
  }
}
