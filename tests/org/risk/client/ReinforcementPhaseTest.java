package org.risk.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetVisibility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class ReinforcementPhaseTest extends AbstractTest {

  private static final String REINFORCE = "reinforce";
  private static final String REINFORCE_UNITS = "reinforceUnits";
  private static final String CARD_TRADE = "cardTrade";
  private static final String ADD_UNITS = "addUnits";
  private static final String CARDS_BEING_TRADED = "cardsBeingTraded";
  private static final String CARD_VALUES = "cardValues";
  private static final String TRADE_NUMBER = "tradeNumber";
  private static final String UNCLAIMED_UNITS = "unclaimedUnits";

  //private static final Map<String, Integer> territoryMapA = ImmutableMap.<String, Integer>of();
  //private static final Map<String, Integer> territoryMapB = ImmutableMap.<String, Integer>of();
  //private static final Map<String, Integer> territoryMapC = ImmutableMap.<String, Integer>of();
  
  private static final List<String> continentsA = ImmutableList.<String>of();
  private static final List<String> continentsB = ImmutableList.<String>of();
  private static final List<String> continentsC = ImmutableList.<String>of();
  
  //player C turn to trade cards
  
  @Test
  public void testTradeCardsMoveByC(){
    List<Operation> tradeCardsMoveByC = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TRADE_NUMBER, 1),
        new Set(CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
        new Set(REINFORCE_UNITS, 4),
        new SetVisibility("RC0"),
        new SetVisibility("RC1"),
        new SetVisibility("RC2"));
   
    Map<String, Object> state = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsA),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsB),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(0, 1, 2),
                CARD_VALUES, ImmutableMap.<String,String>of("RC0", "I1","RC1", "I4","RC2","I7"),
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsC)),
        BOARD, ImmutableMap.<String, Object>of(
            CARDS, getCardsInRange(5, 43)));
     
  //  assertMoveOk(verifyMove);

  }
  
  //Game state as seen by C after the trade
  private final Map<String, Object> gameStateAfterTradeByC = ImmutableMap.<String, Object>of(
      TURN, cId+"",
      PLAYERS, ImmutableMap.<String, Object>of(
          PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
          PLAYER_B, ImmutableMap.<String, Object>of(
              CARDS, ImmutableList.<Integer>of(3),
              TERRITORY, getTerritories(PLAYER_B),
              UNCLAIMED_UNITS, 0,
              CONTINENT, continentsB),
          PLAYER_C, ImmutableMap.<String, Object>of(
              CARDS, ImmutableList.<Integer>of(),
              TERRITORY, getTerritories(PLAYER_C),
              UNCLAIMED_UNITS, 4,
              CONTINENT, continentsC)),
      BOARD, ImmutableMap.<String, Object>of(
          CARDS, getCardsInRange(5, 43),
          CARDS_TRADED, ImmutableMap.<String,String>of("RC0", "I1","RC1", "I4","RC2","I7")));
  
  private Map<String, Integer> getTerritories(String playerID) {
    Map<String, Integer> territoryMap = new HashMap<String, Integer>();
    int territory = 0;
    switch(playerID){
    case "1": 
      for(int i = 0; i < 14; i++) {
        territoryMap.put(territory++ + "", 1);
      }
    case "2": 
      for(int i = 0; i < 14; i++) {
        territoryMap.put(territory++ + "", 1);
      }
    case "3": 
      for(int i = 0; i < 14; i++) {
        territoryMap.put(territory++ + "", 1);
      }
    }
    return territoryMap;
  }
/*  
  @Test
  public void testTradeByC_AtB() {
    final Map<String, Object> lastGameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapA,
            UNITS, 10,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, territoryMapB,
            UNITS, 10,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1, 2, 3),
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsC));
    
    final Map<String, Object> gameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, territoryMapB,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 4,
            CONTINENT, continentsC));
    assertMoveOk(move(
        bId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
    assertHacker(move(
        aId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
  }
  
  @Test
  public void testTradeByC_AtA() {
    final Map<String, Object> lastGameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(5),
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapB,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1, 2, 3),
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsC));
    
    final Map<String, Object> gameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(5),
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapB,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 4,
            CONTINENT, continentsC));
    assertMoveOk(move(
        aId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
    assertHacker(move(
        bId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
  }
  
  @Test
  public void testTradeByC() {
    assertMoveOk(move(
        cId, playersInfo, gameStateAfterTradeByC, 
        cId, gameStateAtC, tradeCardsMoveByC));
    assertHacker(move(
        bId, playersInfo, gameStateAfterTradeByC, 
        cId, gameStateAtC, tradeCardsMoveByC));
    assertHacker(move(
        aId, playersInfo, gameStateAfterTradeByC, 
        cId, gameStateAtC, tradeCardsMoveByC));
  }
  */
  
  @Test
  public void testAddUnitsByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsA),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsB),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(),
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 4, // Got from Trading cards
                CONTINENT, continentsC)),
        BOARD, ImmutableMap.<String, Object>of(
            CARDS, getCardsInRange(5, 43)));
    
    List<Operation> addUnitsToC = ImmutableList.<Operation>of(
        new Set(PHASE, ADD_UNITS),
        new Set(REINFORCE_UNITS, 3));
  
    assertMoveOk(move(cId, state, addUnitsToC));
    assertHacker(move(bId, state, addUnitsToC));
    assertHacker(move(cId, emptyState, addUnitsToC));
    assertHacker(move(cId, nonEmptyState, addUnitsToC));
  }
  
  @Test
  public void testReinforceTerritoriesByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsA),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, continentsB),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(),
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 7,
                CONTINENT, continentsC)),
        BOARD, ImmutableMap.<String, Object>of(
            CARDS, getCardsInRange(0,2).addAll(getCardsInRange(5, 43))));
    
    List<Operation> reinforceTerritoryOfC = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Set(TERRITORY_DELTA, ImmutableMap.<Integer, Integer>of(
            27,3,
            28,2,
            41,2)));
    
    List<Operation> reinforceTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Set(TERRITORY_DELTA, ImmutableMap.<Integer, Integer>of(
            15,1,
            16,2)));
    
    List<Operation> reinforceTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Set(TERRITORY_DELTA, ImmutableMap.<Integer, Integer>of(
            27,5,
            28,2,
            41,2)));
    
    List<Operation> reinforceTerritoryOfCWithIncorrectTerritory = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Set(TERRITORY_DELTA, ImmutableMap.<Integer, Integer>of(
            4,1,
            28,2,
            41,2)));
    
    assertMoveOk(move(cId, state, reinforceTerritoryOfC));
    assertHacker(move(bId, state, reinforceTerritoryOfC));
    assertHacker(move(cId, state, reinforceTerritoryOfBInWrongTurn));
    assertHacker(move(cId, emptyState, reinforceTerritoryOfC));
    assertHacker(move(cId, nonEmptyState, reinforceTerritoryOfC));
    assertHacker(move(cId, state, reinforceTerritoryOfCWithIncorrectNumberOfUnits));
    assertHacker(move(cId, state, reinforceTerritoryOfCWithIncorrectTerritory));
  }
}
