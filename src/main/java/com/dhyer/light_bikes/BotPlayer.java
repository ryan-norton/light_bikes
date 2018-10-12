package com.dhyer.light_bikes;

import java.awt.*;

public abstract class BotPlayer extends Player {
  BotPlayer(Game game, String color, Point p, String name) {
    super(game, name, color, p);

    this.isBot = true;
  }

  public abstract void move(GameStore gameStore);
}
