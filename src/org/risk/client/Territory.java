package org.risk.client;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Territory {
  
  public static final Map<Integer, String> territoryName = ImmutableMap.<Integer, String>builder()
      .put(0, "Alaska")
      .put(1, "North West Territory")
      .put(2, "Alberta")
      .put(3, "Western United States")
      .put(4, "Central America")
      .put(5, "Greenland")
      .put(6, "Ontario")
      .put(7, "Quebec")
      .put(8, "Eastern United States")
      .put(9, "Venezuela")
      .put(10, "Peru")
      .put(11, "Brazil")
      .put(12, "Argentina")
      .put(13, "Iceland")
      .put(14, "Scandinavia")
      .put(15, "Ukraine")
      .put(16, "Great Britain")
      .put(17, "Northern Europe")
      .put(18, "Western Europe")
      .put(19, "Southern Europe")
      .put(20, "North Africa")
      .put(21, "Egypt")
      .put(22, "Congo")
      .put(23, "East Africa")
      .put(24, "South Africa")
      .put(25, "Madagascar")
      .put(26, "Siberia")
      .put(27, "Ural")
      .put(28, "China")
      .put(29, "Afghanistan")
      .put(30, "Middle East")
      .put(31, "India")
      .put(32, "Siam")
      .put(33, "Yakutsk")
      .put(34, "Irkutsk")
      .put(35, "Mongolia")
      .put(36, "Japan")
      .put(37, "Kamchatka")
      .put(38, "Indonesia")
      .put(39, "New Guinea")
      .put(40, "Western Australia")
      .put(41, "Eastern Australia")
      .build();

  public static String getContinentId(int territoryId) {
    if (territoryId >= 0 && territoryId <= 8) {
      return "0";
    } else if (territoryId >= 9 && territoryId <= 12) {
      return "1";      
    } else if (territoryId >= 13 && territoryId <= 19) {
      return "2";      
    } else if (territoryId >= 20 && territoryId <= 25) {
      return "3";      
    } else if (territoryId >= 26 && territoryId <= 37) {
      return "4";      
    } else if (territoryId >= 38 && territoryId <= 41) {
      return "5";      
    } else {
      throw new IllegalArgumentException("Invalide territory Id");
    }
  }
  
  public static final Map<Integer, List<Integer>> connections = ImmutableMap.<Integer, List<Integer>>builder()
      .put(0, ImmutableList.<Integer>of(1, 2, 37))
      .put(1, ImmutableList.<Integer>of(0, 2, 6, 5))
      .put(2, ImmutableList.<Integer>of(0, 1, 6, 4))
      .put(3, ImmutableList.<Integer>of(2, 6, 4, 8))
      .put(4, ImmutableList.<Integer>of(3, 8, 9))
      .put(5, ImmutableList.<Integer>of(1, 6, 7, 13))
      .put(6, ImmutableList.<Integer>of(1, 2, 3, 5, 7, 8))
      .put(7, ImmutableList.<Integer>of(5, 6, 8))
      .put(8, ImmutableList.<Integer>of(3, 4, 6, 7))
      .put(9, ImmutableList.<Integer>of(4, 10, 11))
      .put(10, ImmutableList.<Integer>of(9, 11, 12))
      .put(11, ImmutableList.<Integer>of(9, 10, 12, 20))
      .put(12, ImmutableList.<Integer>of(10, 11))
      .put(13, ImmutableList.<Integer>of(5, 16, 15))
      .put(14, ImmutableList.<Integer>of(15, 17, 13, 16))
      .put(15, ImmutableList.<Integer>of(14, 27, 29, 30, 17, 19))
      .put(16, ImmutableList.<Integer>of(13, 17, 18, 14))
      .put(17, ImmutableList.<Integer>of(14, 15, 16, 18, 19))
      .put(18, ImmutableList.<Integer>of(16, 17, 19, 20))
      .put(19, ImmutableList.<Integer>of(15, 17, 18, 20, 21, 30))
      .put(20, ImmutableList.<Integer>of(11, 18, 19, 21, 22, 23))
      .put(21, ImmutableList.<Integer>of(19, 20, 23, 30))
      .put(22, ImmutableList.<Integer>of(20, 23, 24))
      .put(23, ImmutableList.<Integer>of(20, 21, 22, 24, 25))
      .put(24, ImmutableList.<Integer>of(22, 23, 25))
      .put(25, ImmutableList.<Integer>of(23, 24))
      .put(26, ImmutableList.<Integer>of(27, 28, 33, 34, 35))
      .put(27, ImmutableList.<Integer>of(15, 26, 28, 29))
      .put(28, ImmutableList.<Integer>of(26, 27, 29, 31, 32, 35))
      .put(29, ImmutableList.<Integer>of(15, 27, 28, 30, 31))
      .put(30, ImmutableList.<Integer>of(15, 21, 29, 31, 19))
      .put(31, ImmutableList.<Integer>of(28, 29, 30, 32))
      .put(32, ImmutableList.<Integer>of(28, 31, 38))
      .put(33, ImmutableList.<Integer>of(26, 34, 37))
      .put(34, ImmutableList.<Integer>of(26, 33, 35, 37))
      .put(35, ImmutableList.<Integer>of(28, 34, 36, 37, 26))
      .put(36, ImmutableList.<Integer>of(35, 37))
      .put(37, ImmutableList.<Integer>of(0, 33, 34, 35, 36))
      .put(38, ImmutableList.<Integer>of(32, 40, 39))
      .put(39, ImmutableList.<Integer>of(38, 40, 41))
      .put(40, ImmutableList.<Integer>of(38, 39, 41))
      .put(41, ImmutableList.<Integer>of(39, 40))
      .build();

  public static boolean isAttackPossible(int territory1, int territory2) {
    return true;
  }
  
  public static boolean isFortifyPossible(int territory1, int territory2) {
    return true;
  }
}
