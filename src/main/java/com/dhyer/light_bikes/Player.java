package com.dhyer.light_bikes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.UUID;

public class Player {
  private boolean alive;
  private String color;
  private int currentX;
  private int currentY;

  @JsonIgnore
  private UUID id;
  private Game game;
  private static final String[] COLORS = {"red", "blue", "green", "yellow"};

  Player(Game game) {
    this.id = UUID.randomUUID();
    this.game = game;
    this.alive = true;
    this.color = game.getAvailableColor(COLORS);

    // gets starting point, and updates board with position
    Point p = game.getAvailableStartingPoint(color);
    this.currentX = p.x;
    this.currentY = p.y;
  }

  public UUID getId() {
    return this.id;
  }

  public String getColor() {
    return this.color;
  }

  public JSONObject toJson() {
    JSONObject obj = new JSONObject();
    obj.put("color", this.color);
    obj.put("alive", this.alive);
    obj.put("x", this.currentX);
    obj.put("y", this.currentY);
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
  }

  public void kill() {
    this.alive = false;
  }

  public boolean isAlive() {
    return alive;
  }
}
