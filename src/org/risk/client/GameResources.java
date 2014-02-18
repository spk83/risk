package org.risk.client;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class GameResources {
  
  public static final Map<Integer, Integer> PLAYERS_UNIT_MAP = ImmutableMap.<Integer, Integer>of(
      2, 40,
      3, 35,
      4, 30,
      5, 25,
      6, 20);

  public static int getInitialNumberOfUnits(int numberOfPlayers) {
    return PLAYERS_UNIT_MAP.get(numberOfPlayers);
  }
  
  public static final int MIN_ALLOCATED_UNITS = 3;
  public static final int TOTAL_TERRITORIES = 42; // Number of territories
  public static final int TOTAL_WILD_CARDS = 2;
  public static final int TOTAL_RISK_CARDS = TOTAL_TERRITORIES + TOTAL_WILD_CARDS;
  public static final int MAX_DICE_ROLL = 6;
  public static final int MIN_DICE_ROLL = 1;
  public static final String TURN_ORDER = "turnOrder";
  public static final String TURN = "turn";
  public static final String PHASE = "phase"; // reinforce, attack, fortify
  public static final String RISK_CARD = "RC";
  public static final String TERRITORY = "territory";
  public static final String UNCLAIMED_TERRITORY = "unclaimedTerritory";
  public static final String CONTINENT = "continent";
  public static final String UNITS = "units";
  public static final String ATTACK_OCCUPY = "attackOccupy";
  public static final String ATTACK_RESULT = "attackResult";
  public static final String END_ATTACK = "endAttack";
  public static final String ATTACKER = "attacker";
  public static final String DEFENDER = "defender";
  public static final String TERRITORY_WINNER = "territoryWinner";
  public static final String PLAYER = "player";
  public static final String MESSAGE = "message";
  public static final String ATTACK_TO_TERRITORY = "attackToTerritory";
  public static final String LAST_ATTACKING_TERRITORY = "lastAttackingTerritory";
  public static final String DICE_ROLL = "diceRoll";
  public static final String WINNING_TERRITORY = "winningTerritory";
  public static final String MOVEMENT_FROM_TERRITORY = "movementFromTerritory";
  public static final String MOVEMENT_TO_TERRITORY = "movementFromTerritory";
  public static final String UNITS_FROM_TERRITORY = "unitsFromTerritory";
  public static final String UNITS_TO_TERRITORY = "unitsFromTerritory";
  public static final String UNCLAIMED_UNITS = "unclaimedUnits";
  public static final String CARDS = "cards";
  public static final String CARDS_TRADED = "cards_traded";
  public static final String DECK = "deck";
  public static final String DEPLOYMENT = "deployment";
  public static final String CLAIM_TERRITORY = "claimTerritory";
  public static final String CARD_TRADE = "cardTrade";
  public static final String ATTACK_PHASE = "attackPhase";
  public static final String FORTIFY = "fortify";
  public static final String END_GAME = "endGame";
  public static final String SET_TURN_ORDER = "setTurnOrder";
  public static final int START_PLAYER_ID = 1;
  public static final int MAX_PLAYERS = 6;
  public static final String REINFORCE = "reinforce";
  public static final String REINFORCE_UNITS = "reinforceUnits";
  public static final String ADD_UNITS = "addUnits";
  public static final String CARDS_BEING_TRADED = "cardsBeingTraded";
  public static final String TRADE_NUMBER = "tradeNumber";
  
  public static final Map<String, Object> EMPTYSTATE = ImmutableMap.<String, Object>of();
  public static final Map<String, Object> NONEMPTYSTATE = ImmutableMap.<String, Object>of(
      "k", "v");
  public static final Map<String, Object> EMPTYMAP = ImmutableMap.<String, Object>of();
  public static final List<String> EMPTYLISTSTRING = ImmutableList.<String>of();
  public static final List<Integer> EMPTYLISTINT = ImmutableList.<Integer>of();
  public static final Map<Integer, Integer> EMPTYINTMAP = ImmutableMap.<Integer, Integer>of();
  public static final int TOTAL_INITIAL_DICE_ROLL = 3;
  
  
  /*
   * This is a helper method to get risk card value from its ID.
   */
  public static String cardIdToString(int cardId) {
    checkArgument(cardId >= 0 && cardId <= 43);
    int category = cardId % 3;
    String categoryString = cardId > 41 ? "W"
        : category == 1 ? "I"
            : category == 2 ? "C" : "A";
    return categoryString + cardId;
  }
  
  public static List<String> getPlayerKeys(List<Integer> playerIds) {
   Builder<String> playerKeysBuilder = ImmutableList.<String>builder();
   for (int playerId : playerIds) {
     playerKeysBuilder.add(playerIdToString(playerId));
   }
    return playerKeysBuilder.build();
  }
  /*
   * This is a helper method to convert player's ID from int to String.
   */
  public static String playerIdToString(int playerId) {
    return "P" + playerId;
  }
  
  /*
   * This is a helper method to convert player's ID from String to int.
   */
  public static int playerIdToInt(String playerId) {
    return Integer.parseInt(playerId.substring(1));
  }
  
  /*
   * This is a helper method which returns a list of RISK cards of given range.
   */
  public static List<String> getCardsInRange(int fromInclusive, int toInclusive) {
    List<String> keys = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      keys.add(RISK_CARD + i);
    }
    return keys;
  }
  
  /* 
   * This is a helper method to get list of territories for a playerID.
   */
  public Map<String, Integer> getTerritories(String playerID) {
    Map<String, Integer> territoryMap = new HashMap<String, Integer>();
    switch(playerID) {
    case "P1":
      for (int i = 0; i < 14; i++) {
        territoryMap.put(i + "", 1);
      }
      break;
    case "P2": 
      for (int i = 14; i < 28; i++) {
        territoryMap.put(i + "", 1);
      }
      break;
    case "P3": 
      for (int i = 28; i < 42; i++) {
        territoryMap.put(i + "", 1);
      }
      break;
    default:
    }
    return territoryMap;
  }
  
  /*
   * Helper method to get Map of territories with specified change.
   */
  public Map<String, Integer> performDeltaOnTerritory(
      Map<String, Integer> currentMap, String territory, int delta) {
    int oldValue = currentMap.get(territory);
    int newValue = oldValue + delta;
    Map<String, Integer> newMap = new HashMap<String, Integer>();
    newMap.putAll(currentMap);
    newMap.put(territory, newValue);
    return newMap;
  }
  
  /*
   * Helper method to get list of territory from given range.
   */
  public static List<Integer> getTerritoriesInRange(int fromInclusive, int toInclusive) {
    List<Integer> listOfTerritories = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      listOfTerritories.add(i);
    }
    return listOfTerritories;
  }
    
  /*
   * Compare two maps
   */
  public boolean equalMaps(Map<String, Integer>m1, Map<String, Integer>m2) {
    if (m1.size() != m2.size()) {
       return false;
    }
    for (String key: m1.keySet()) {
       if (!m1.get(key).equals(m2.get(key))) {
          return false;
       }
    }
    return true;
  }
  
  public int getNewReinforcementUnits(int totalTerritories) {
    int units = totalTerritories/3;
    return units < 3 ? 3 : units;
  }
  
  public static int getMaxDiceRollsForAttacker(int units) {
    if(units  < 2) {
      return 0;
    }
    return (units - 1) >= 3 ? 3 : (units - 1);
  }
  
  public static int getMaxDiceRollsForDefender(int units) {
    if(units  < 1) {
      return 0;
    }
    return units >= 2 ? 2 : 1;
  }
  
  public static int getMinUnitsToNewTerritory(int remainingUnits) {
    if (remainingUnits < 2) {
      return 0; //error
    }
    if (remainingUnits > 3) {
      return 3;
    }
    return remainingUnits - 1;
  }
}
