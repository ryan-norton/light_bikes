package com.dhyer.light_bikes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import static java.util.stream.Collectors.*;

@Component
public class GamesTaskService {

  private static final Logger log = LoggerFactory.getLogger(GamesTaskService.class);

  @Autowired
  GameStore gameStore;

  // Runs every 10 seconds
  // Kill active games that haven't had an update in 30s and un-started games that haven't
  // been updated in 120 seconds
  @Scheduled(fixedDelay = 10000)
  public void killOldGames() {
    Collection<UUID> doomedIds = gameStore
      .getGames()
      .stream()
      .filter(g -> g.hasExpired())
      .map(g -> g.getId())
      .collect(toList());
    
    if (doomedIds.size() > 0) {
      log.info(String.format("Killing %d old games", doomedIds.size()));
    } else {
      log.info("No games to kill :(");
    }

    gameStore.removeGames(doomedIds);
  }
}
