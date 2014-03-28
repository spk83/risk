package org.risk.client;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.risk.client.GameApi.Operation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public final class GameResources {
  
  private GameResources() {
  }
  
  public static final Map<Integer, Integer> PLAYERS_UNIT_MAP = ImmutableMap.<Integer, Integer>of(
      2, 40,
      3, 35,
      4, 30,
      5, 25,
      6, 20);
  
  public static final int MIN_ALLOCATED_UNITS = 3;
  public static final int TOTAL_TERRITORIES = 42; // Number of territories
  public static final int TOTAL_CONTINENTS = 6;
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
  public static final int START_PLAYER_ID_INDEX = 0;
  public static final int MAX_PLAYERS = 6;
  public static final String REINFORCE = "reinforce";
  public static final String REINFORCE_UNITS = "reinforceUnits";
  public static final String ADD_UNITS = "addUnits";
  public static final String CARDS_BEING_TRADED = "cardsBeingTraded";
  public static final String TRADE_NUMBER = "tradeNumber";
  public static final String ATTACK_TRADE = "attackTrade";
  public static final String ATTACK_REINFORCE = "attackReinforce";
  public static final String CONTINUOUS_TRADE = "continuousTrade";
  public static final Integer MIN_CARDS_IN_ATTACK_TRADE = 4;
  public static final Integer MAX_CARDS_IN_ATTACK_TRADE = 6;
  public static final String GAME_ENDED = "gameEnded";
  
  public static final Map<String, Object> EMPTYSTATE = ImmutableMap.<String, Object>of();
  public static final Map<String, Object> NONEMPTYSTATE = ImmutableMap.<String, Object>of(
      "k", "v");
  public static final Map<String, Object> EMPTYMAP = ImmutableMap.<String, Object>of();
  public static final List<String> EMPTYLISTSTRING = ImmutableList.<String>of();
  public static final List<Integer> EMPTYLISTINT = ImmutableList.<Integer>of();
  public static final Map<Integer, Integer> EMPTYINTMAP = ImmutableMap.<Integer, Integer>of();
  public static final int TOTAL_INITIAL_DICE_ROLL = 3;
  
  public static final Map<String, String> UI_PHASE_MAPPING = ImmutableMap.<String, String>builder()
      .put(SET_TURN_ORDER, "Decide Turn Order")
      .put(CLAIM_TERRITORY, "Claim Territory")
      .put(DEPLOYMENT, "Deploy Units")
      .put(CARD_TRADE, "Trade Cards")
      .put(ADD_UNITS, "Add units for Reinforcement")
      .put(REINFORCE, "Reinforce Territories")
      .put(ATTACK_PHASE, "Attack")
      .put(ATTACK_TRADE, "Trade Cards in Attack Phase")
      .put(ATTACK_REINFORCE, "Reinforce Territory in Attack Phase")
      .put(ATTACK_RESULT, "Result of Attack")
      .put(ATTACK_OCCUPY, "Occupy new territory")
      .put(END_ATTACK, "End of attack")
      .put(FORTIFY, "Fortify Territory")
      .put(END_GAME, "End of Game")
      .put(GAME_ENDED, "End of Game")
      .build();
  
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
  
  public static List<String> getPlayerKeys(List<String> playerIds) {
   Builder<String> playerKeysBuilder = ImmutableList.<String>builder();
   for (String playerId : playerIds) {
     playerKeysBuilder.add(playerIdToKey(playerId));
   }
    return playerKeysBuilder.build();
  }
  /*
   * This is a helper method to convert player's ID from player key.
   */
  public static String playerIdToKey(String playerId) {
    return "P" + playerId;
  }
  
  /*
   * This is a helper method to convert player's key to player ID.
   */
  public static String playerKeyToId(String playerKey) {
    return playerKey.substring(1);
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
   * Helper method to get list of territory from given range.
   */
  public static List<Integer> getTerritoriesInRange(int fromInclusive, int toInclusive) {
    List<Integer> listOfTerritories = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      listOfTerritories.add(i);
    }
    return listOfTerritories;
  }
  
  public static int getMaxDiceRollsForAttacker(int units) {
    if (units  < 2) {
      return 0;
    }
    return (units - 1) >= 3 ? 3 : (units - 1);
  }
  
  public static int getMaxDiceRollsForDefender(int units) {
    if (units  < 1) {
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

  public static int getNewUnits(int territories, List<String> continent) {
    int newUnits = territories / 3;
    if (newUnits < GameResources.MIN_ALLOCATED_UNITS) {
      newUnits = GameResources.MIN_ALLOCATED_UNITS;
    }
    for (String continentId : continent) {
      newUnits += Continent.UNITS_VALUE.get(continentId);
    }
    return newUnits;
  }
  
  //Assumes both map have equal size
  public static Map<String, Integer> differenceTerritoryMap(
      Map<String, Integer> oldTerritories, Map<String, Integer> newTerritories) {
    Map<String, Integer> differenceMap = new HashMap<String, Integer>();
    for (Map.Entry<String, Integer> oldEntry : oldTerritories.entrySet()) {
      int difference = newTerritories.get(oldEntry.getKey()) - oldEntry.getValue();
      if (difference != 0) {
        differenceMap.put(oldEntry.getKey(), difference);
      }
    }
    return differenceMap;
  }
  
  @SuppressWarnings("unchecked")
  public static Map<String, Integer> differenceTerritoryMap(
      Map<String, Object> currentPlayerState, RiskState lastPlayerState, String lastMovePlayerId) {
    Map<String, Integer> territoryUnitMap = 
        (Map<String, Integer>) currentPlayerState.get(GameResources.TERRITORY);
    Map<String, Integer> oldTerritoryMap = lastPlayerState.getPlayersMap().get(
        GameResources.playerIdToKey(lastMovePlayerId)).getTerritoryUnitMap();
    Map<String, Integer> differenceTerritoryMap = 
        GameResources.differenceTerritoryMap(oldTerritoryMap, territoryUnitMap);
    return differenceTerritoryMap;
  }
  
  //Finds the new territory in newTerritories otherwise returns null
  public static String findNewTerritory(
      Set<String> oldTerritories, Set<String> newTerritories) {
    String newTerritory = null;
    for (String territory : newTerritories) {
      if (!oldTerritories.contains(territory)) {
        if (newTerritory == null) {
          newTerritory = territory;
        } else {
          return null;
        }
      }
    }
    return newTerritory;
  }
  
  public static List<Integer> getDiceRolls(Map<String, Object> lastApiState, String type) {
    List<Integer> diceRolls = new ArrayList<Integer>();
    boolean rolls = true;
    int count = 0;
    while (rolls) {
      Integer diceRoll = (Integer) lastApiState.get(
          type + GameResources.DICE_ROLL + (++count));
      if (diceRoll != null) {
        diceRolls.add(diceRoll);
      } else {
        rolls = false;
      }
    }
    return diceRolls;
  }
  
  public static List<String> getDiceRollKeys(List<String> playerIds) {
    List<String> diceRollList = new ArrayList<String>();
    for (String playerId : playerIds) {
      for (int i = 0; i < GameResources.TOTAL_INITIAL_DICE_ROLL; i++) {
        diceRollList.add(GameResources.DICE_ROLL + "_" + playerId + "_" + i);
      }
    }
   return diceRollList;
  }
  
  @SuppressWarnings("unchecked")
  public static List<Integer> getTradedCards(List<Operation> operations) {
    for (Operation operation : operations) {
      if (operation instanceof org.risk.client.GameApi.Set) {
        if (((org.risk.client.GameApi.Set) operation).getKey()
            .equals(GameResources.CARDS_BEING_TRADED)) {
              return (List<Integer>) ((org.risk.client.GameApi.Set) operation).getValue();
        }
      }
    }
    return null;
  }
  
  public static String getStartPlayerId(List<String> playerIds) {
    String startPlayerId = playerIds.get(GameResources.START_PLAYER_ID_INDEX);
    if (startPlayerId.equals(GameApi.AI_PLAYER_ID)) {
      startPlayerId = playerIds.get((GameResources.START_PLAYER_ID_INDEX + 1) % playerIds.size());
    }
    return startPlayerId;
  }
}
