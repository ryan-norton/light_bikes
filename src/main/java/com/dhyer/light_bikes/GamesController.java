package com.dhyer.light_bikes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
  public JSONObject create() {
    Game game = new Game();
    gameStore.addGame(game);
    JSONObject obj = new JSONObject();

    log.info("Created game with ID " + game.getId());

    obj.put("id", game.getId());
    return obj;
  }

  @PostMapping("/{gameId}/join")
  public JSONObject joinGame(@PathVariable UUID gameId,
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
    JSONObject obj = new JSONObject();
    Player p = new Player(game, name);
    game.addPlayer(p);
    obj.put("id", p.getId());
    obj.put("name", p.getName());
    obj.put("color", p.getColor());
    return obj;
  }

  @PostMapping("/{gameId}/move")
  public JSONObject move(@PathVariable UUID gameId,
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
    } else if(!game.isPlayersTurn(playerId)) {
      game.killPlayer(game.getPlayer(playerId), gameStore);
      throw new InvalidRequestException("It is not your turn");
    }
    game.updatePlayerLocation(game.getPlayer(playerId), x, y, gameStore);
    JSONObject obj = new JSONObject();
    obj.put("game", game.toJson());
    return obj;
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearGames() {
    log.info("Clearing all games!!!");

    gameStore.clear();
  }
}
