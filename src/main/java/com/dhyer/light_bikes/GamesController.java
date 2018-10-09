package com.dhyer.light_bikes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.*;
import reactor.core.publisher.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GamesController {

  private static final Logger log = LoggerFactory.getLogger(GamesController.class);

  @Autowired
  GameStore gameStore;

  @GetMapping
  public JSONObject index() {
    log.info("Requesting all the games!");

    JSONObject obj = new JSONObject();
    JSONArray arr = new JSONArray();
    gameStore.getGames().forEach( g -> arr.add(g.toJson()) );

    obj.put("games", arr);
    return obj;
  }

  @GetMapping("/{gameId}")
  public JSONObject show(@PathVariable UUID gameId) {
    log.info("Requesting game ID " + gameId);

    Game game = gameStore.findById(gameId);
    if(game == null) {
      throw new ResourceNotFoundException("Game not found with id " + gameId);
    }
    JSONObject obj = new JSONObject();
    JSONArray arr = new JSONArray();
    arr.add(game.toJson());
    obj.put("games", arr);
    return obj;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public JSONObject create(
      @RequestParam(value = "test", required = false, defaultValue = "false") boolean test
  ) {
    Game game = new Game(test, gameStore);
    gameStore.addGame(game);
    JSONObject obj = new JSONObject();

    log.info("Created game with ID " + game.getId());

    obj.put("id", game.getId());
    return obj;
  }

  @PostMapping("/{gameId}/join")
  public Flux<JSONObject> joinGame(@PathVariable UUID gameId,
                             @RequestParam(required = true) String name) {
    log.info(name + " is joining game " + gameId);

    Game game = gameStore.findById(gameId);
    if(game == null) {
      log.warn("Game does not exist with id " + gameId);
      throw new ResourceNotFoundException("Game not found with id " + gameId);
    } else if (game.hasStarted()) {
      log.warn("Game " + gameId + " is not available");
      throw new InvalidRequestException("The game you attempted to join has already started.");
    }

    Player p = new Player(game, name, false);
    game.addPlayer(p, gameStore);

    log.info(name + " joined as " + p.getId());

    Flux<JSONObject> dynamicFlux = Flux.create(sink -> {
      GameWatcher.waitForTurn(sink, gameStore, gameId, p.getId());
    });
    return dynamicFlux;
  }

  @PostMapping("/{gameId}/move")
  public Flux<JSONObject> move(@PathVariable UUID gameId,
                         @RequestParam("id") UUID playerId,
                         @RequestParam("x") int x,
                         @RequestParam("y") int y) {
    log.info(
      String.format(
        "Player %s, in Game %s is moving to %d-%d",
        playerId,
        gameId,
        x, y
      )
    );

    Game game = gameStore.findById(gameId);
    if(game == null) {
      throw new ResourceNotFoundException("Game not found with id " + gameId);
    } else if(!game.hasPlayer(playerId)) {
      throw new InvalidRequestException("The specified player does not exist");
    } else if(!game.hasStarted()) {
      throw new InvalidRequestException("The game has not started yet");
    } else if(game.getWinner() != null) {
      throw new InvalidRequestException("The game is over");
    } else if(!game.isPlayersTurn(playerId)) {
      game.killPlayer(game.getPlayer(playerId), gameStore);
      throw new InvalidRequestException("It is not your turn");
    }

    game.updatePlayerLocation(game.getPlayer(playerId), x, y, gameStore);

    Flux<JSONObject> dynamicFlux = Flux.create(sink -> {
      GameWatcher.waitForTurn(sink, gameStore, gameId, playerId);
    });
    return dynamicFlux;
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearGames() {
    log.info("Clearing all games!!!");

    gameStore.clear();
  }
}
