package org.risk.client;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class GameConstants {
  public static final Map<Integer, Integer> PLAYERS_UNIT_MAP = ImmutableMap.<Integer, Integer>of(
      2, 40,
      3, 35,
      4, 30,
      5, 25,
      6, 20);

  public static int getInitialNumberOfUnits(int numberOfPlayers) {
    return PLAYERS_UNIT_MAP.get(numberOfPlayers);
  }
}
