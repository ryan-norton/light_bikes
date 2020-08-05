package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.FluxSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameWatcher {
  private GameWatcher() {}

  public static void waitForTurn(FluxSink<JSONObject> sink, GameStore gameStore, int gameId, UUID playerId) {
    GameWatcherRunnable runnable = new GameWatcherRunnable(sink, gameStore, gameId, playerId);
    Thread t = new Thread(runnable);
    t.start();
  }

  public static class GameWatcherRunnable implements Runnable {
    private static final int WAIT_INTERVAL = 20;
    private static final int WAIT_TIMEOUT = 120000; // 120 seconds :shrug:

    private static final Logger log = LoggerFactory.getLogger(GameWatcherRunnable.class);

    private FluxSink<JSONObject> sink;
    private GameStore gameStore;
    private int gameId;
    private UUID playerId;

    public GameWatcherRunnable(FluxSink<JSONObject> sink, GameStore gameStore, int gameId, UUID playerId) {
      this.sink = sink;
      this.gameId = gameId;
      this.playerId = playerId;
      this.gameStore = gameStore;
    }

    public void run() {
      int timeout = 0;

      while (timeout < WAIT_TIMEOUT) {
        try {
          Game game = gameStore.findById(gameId);

          // Uh-oh
          if (game == null) {
            sink.error(new InvalidRequestException("The game has gone missing :scream:"));
            break;
          }

          if (game.isPlayersTurn(playerId) || game.getWinner() != null) {
            sink.next(game.toJson());
            break;
          } else {
            timeout += WAIT_INTERVAL;
            Thread.sleep(WAIT_INTERVAL);
          }
        } catch (InterruptedException e) {}
      }

      sink.complete();
    }
  }
}
