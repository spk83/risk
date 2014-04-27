package org.risk.client;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class Continent {
  
  private Continent() {
    
  }

  public static final Map<String, Integer> UNITS_VALUE = ImmutableMap.<String, Integer>builder()
      .put("0", 5)
      .put("1", 2)
      .put("2", 5)
      .put("3", 3)
      .put("4", 7)
      .put("5", 2)
      .build();
  
  public static final Map<String, String> CONTINENT_NAMES = ImmutableMap.<String, String>builder()
      .put("0", "North America")
      .put("1", "South America")
      .put("2", "Europe")
      .put("3", "Africa")
      .put("4", "Asia")
      .put("5", "Australia")
      .build();
  
  public static final Map<String, String> CONTINENT_SVG_ID = ImmutableMap.<String, String>builder()
      .put("north_america", "0")
      .put("south_america", "1")
      .put("europe", "2")
      .put("africa", "3")
      .put("asia", "4")
      .put("australia", "5")
      .build();
  
  public static final Map<String, Set<String>> TERRITORY_SET = 
      ImmutableMap.<String, Set<String>>builder()
      .put("0", ImmutableSet.<String>of("0", "1", "2", "3", "4", "5", "6", "7", "8"))
      .put("1", ImmutableSet.<String>of("9", "10", "11", "12"))
      .put("2", ImmutableSet.<String>of("13", "14", "15", "16", "17", "18", "19"))
      .put("3", ImmutableSet.<String>of("20", "21", "22", "23", "24", "25"))
      .put("4", ImmutableSet.<String>of("26", "27", "28", "29", "30", "31", "32", "33", "34", "35", 
          "36", "37"))
      .put("5", ImmutableSet.<String>of("38", "39", "40", "41"))
      .build();
}
