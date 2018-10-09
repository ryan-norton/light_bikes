package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;

// Sorts games by their createdAt time in descending order
public class GameSortByCreated implements Comparator<Game> {
  public int compare(Game a, Game b) {
    return b.getCreatedAt().compareTo(a.getCreatedAt());
  }
}

