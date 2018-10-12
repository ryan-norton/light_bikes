package com.dhyer.light_bikes;

import java.awt.*;

public class LevelOneBotPlayer extends BotPlayer {
  LevelOneBotPlayer(Game game, String color, Point p) {
    super(game, color, p, "Bot Level 1");
  }

  protected Point findBestMove() {
    String[][] b = this.getGame().getBoard();
    int x = this.position.x;
    int y = this.position.y;

    /*
     * Find a valid move in any direction. Attempts to move right, then
     * down, then left. If no options are valid, move up. This handles the
     * case of killing the bot when it has no possible moves by forcing it
     * to take the up move.
     */
    if(x + 1 < b.length && b[x + 1][y] == null) {
      x++;
    } else if (y + 1 < b.length &&
        b[x][y + 1] == null) {
      y++;
    } else if (x - 1 >= 0 &&
        b[x - 1][y] == null) {
      x--;
    } else {
      y--;
    }

    return new Point(x, y);
  }
}
