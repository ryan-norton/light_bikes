package com.dhyer.light_bikes;

import java.util.*;

public class GameStore {
  private Map<UUID, Game> games;

  GameStore() {
    this.games = new HashMap<>();
  }

  public void addGame(Game game) {
    this.games.put(game.getId(), game);
  }

  public Game findById(UUID id) {
    return this.games.get(id);
  }

  public Map<UUID, Game> getGames() {
    return this.games;
  }
}



