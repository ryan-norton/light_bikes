package com.dhyer.light_bikes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GamesTaskService {

  private static final Logger log = LoggerFactory.getLogger(GamesTaskService.class);

  // Runs every 10 seconds
  // Kill active games that haven't had an update in 30s and un-started games that haven't
  // been updated in 120 seconds
  @Scheduled(fixedDelay = 10000)
  public void killOldGames() {
    log.info("Looking for games to kill...");
  }
}
