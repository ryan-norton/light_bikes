package com.dhyer.light_bikes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.UUID;

public class Player {
  private String name;
  private boolean alive;
  private String color;
  private int currentX;
  private int currentY;

  @JsonIgnore
  private UUID id;
  private Game game;
  boolean isBot;

  Player(Game game, String name, String color, Point p) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.game = game;
    this.alive = true;
    this.name = name;
    this.color = color;
    this.currentX = p.x;
    this.currentY = p.y;
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
    obj.put("x", this.currentX);
    obj.put("y", this.currentY);

    if (includeId) {
      obj.put("id", this.id);
    }

    return obj;
  }

  public int getCurrentX() {
    return this.currentX;
  }

  public int getCurrentY() {
    return this.currentY;
  }

  public void updateCurrentLocation(int x, int y) {
    this.currentX = x;
    this.currentY = y;

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
