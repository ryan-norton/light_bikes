package com.dhyer.light_bikes;

import java.awt.*;

public class EasyBotPlayer extends BotPlayer {
  EasyBotPlayer(Game game, String color, Point p) {
    super(game, color, p, "Easy Bot");
  }

  public void move(GameStore gameStore) {
    Game g = this.getGame();
    String[][] b = g.getBoard();
    int newX = this.getCurrentX();
    int newY = this.getCurrentY();

    /*
     * Find a valid move in any direction. Attempts to move right, then
     * down, then left. If no options are valid, move up. This handles the
     * case of killing the bot when it has no possible moves by forcing it
     * to take the up move.
     */
    if(this.getCurrentX() + 1 < g.getBoardSize() &&
        b[this.getCurrentX() + 1][this.getCurrentY()] == null) {
      newX++;
    } else if (this.getCurrentY() + 1 < g.getBoardSize() &&
        b[this.getCurrentX()][this.getCurrentY() + 1] == null) {
      newY++;
    } else if (this.getCurrentX() - 1 >= 0 &&
        b[this.getCurrentX() - 1][this.getCurrentY()] == null) {
      newX--;
    } else {
      newY--;
    }

    // check if the bot has died, otherwise make the move
    if(newY < 0 || b[newX][newY] != null) {
      g.killPlayer(this, gameStore);
    } else {
      this.updateCurrentLocation(newX, newY);
    }
  }
}
