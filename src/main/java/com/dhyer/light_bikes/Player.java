package com.dhyer.light_bikes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.UUID;

public class Player {
  protected String name;
  protected boolean alive;
  protected String color;
  protected Point position;
  protected Point posDiff = new Point(0,0);

  @JsonIgnore
  protected UUID id;
  protected Game game;
  public boolean isBot;

  Player(Game game, String name, String color, Point p) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.game = game;
    this.alive = true;
    this.name = name;
    this.color = color;
    this.position = p;
  }

  public UUID getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getColor() {
    return this.color;
  }

  public JSONObject toJson() { return toJson(false); }
  public JSONObject toJson(boolean includeId) {
    JSONObject obj = new JSONObject();
    obj.put("name", this.name);
    obj.put("color", this.color);
    obj.put("alive", this.alive);
    obj.put("x", this.position.x);
    obj.put("y", this.position.y);

    if (includeId) {
      obj.put("id", this.id);
    }

    return obj;
  }

  public Point getPosition() {
    return this.position;
  }

  public Point getPosDiff() {
    return this.posDiff;
  }

  public int getCurrentX() {
    return this.position.x;
  }

  public int getCurrentY() {
    return this.position.y;
  }

  public void updateCurrentLocation(int x, int y) {
    this.posDiff = new Point(
      this.position.x - x,
      this.position.y - y
    );
    this.position = new Point(x, y);

    this.game.updateBoard(x, y, this.getColor());
  }

  public void kill() {
    this.alive = false;
  }

  public boolean isAlive() {
    return alive;
  }

  public Game getGame() {
    return game;
  }
}
