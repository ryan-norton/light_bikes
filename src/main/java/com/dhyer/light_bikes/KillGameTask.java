package com.dhyer.light_bikes;

import java.util.TimerTask;
import java.util.UUID;

public class KillGameTask extends TimerTask {
  private final GameStore gameStore;
  private final UUID gameId;

  KillGameTask(GameStore gameStore, UUID gameId) {
    this.gameStore = gameStore;
    this.gameId = gameId;
  }

  @Override
  public void run() {
    this.gameStore.removeGame(this.gameId);
  }
}
