package com.dhyer.light_bikes;

import java.awt.*;
import java.util.*;

public class GameSortByCreated implements Comparator<Game> {
  public int compare(Game a, Game b) {
    return a.getCreatedAt().compareTo(b.getCreatedAt());
  }
}

