package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Delete;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetVisibility;
import org.risk.client.GameApi.Shuffle;
import org.risk.client.GameApi.VerifyMove;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * This class test the operations performed in the Reinforcement phase of the game.
 * @author vishal
 *
 */
@RunWith(JUnit4.class)
public class ReinforcementPhaseTest extends AbstractTest {

  private static final String REINFORCE = "reinforce";
  private static final String REINFORCE_UNITS = "reinforceUnits";
  private static final String ADD_UNITS = "addUnits";
  private static final String CARDS_BEING_TRADED = "cardsBeingTraded";
  private static final String CARD_VALUES = "cardValues";
  private static final String TRADE_NUMBER = "tradeNumber";

  /*
   * Test the operations done while trading RISK cards
   */
  @Test
  public void testTradeCardsMoveByC(){
    Map<String, Object> lastStateAtC = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            CARD_VALUES, ImmutableMap.<String, String>of("RC4", null),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            CARD_VALUES, ImmutableMap.<String, String>of("RC3", null),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(0, 1, 2),
            CARD_VALUES, ImmutableMap.<String,String>of("RC0", "I1","RC1", "I4","RC2","I7"),
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 0)
        .build();
    
    List<Operation> tradeCardsMoveByC = ImmutableList.<Operation>of(
        new Set(PHASE, ADD_UNITS),
        new Set(TRADE_NUMBER, 1),
        new Set(CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
        new Set(REINFORCE_UNITS, 4),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            CARD_VALUES, emptyMap,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 4,
            CONTINENT, emptyListString)),
        new SetVisibility("RC0"),
        new SetVisibility("RC1"),
        new SetVisibility("RC2"));
   
    Map<String, Object> newStateAtA = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, ADD_UNITS)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            CARD_VALUES, ImmutableMap.<String, String>of("RC4", "C41"),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            CARD_VALUES, ImmutableMap.<String, String>of("RC3", null),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            CARD_VALUES, emptyMap,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 4,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 1)
        .put(CARDS_BEING_TRADED, ImmutableMap.<String, String>of(
                "RC0", "I1","RC1", "I4","RC2","I7"))
        .put(REINFORCE_UNITS, 4)
        .build();
    
    // Check valid move
    assertMoveOk(new VerifyMove(
        cId, playersInfo, newStateAtA, lastStateAtC, tradeCardsMoveByC, cId));
    
    // Check invalid move
    assertHacker(new VerifyMove(
        cId, playersInfo, newStateAtA, emptyState, tradeCardsMoveByC, cId));
    assertHacker(new VerifyMove(
        cId, playersInfo, newStateAtA, lastStateAtC, tradeCardsMoveByC, bId));

    Map<String, Object> lastStateAtC_1 = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            CARD_VALUES, ImmutableMap.<String, String>of("RC4", null),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            CARD_VALUES, ImmutableMap.<String, String>of("RC3", null),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(0, 1, 2),
            CARD_VALUES, ImmutableMap.<String,String>of("RC0", "C1","RC1", "I4","RC2","I7"),
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 0)
        .build();
    
    // Check if hacker is trading invalid pair of cards
    assertHacker(new VerifyMove(
        cId, playersInfo, newStateAtA, lastStateAtC_1, tradeCardsMoveByC, cId));
  }
  
  /*
   * Test operations for giving a player army units based on 
   * number of territories and continents.
   */
  @Test
  public void testAddUnitsByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, ADD_UNITS)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 4,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 1)
        .put(CARDS_BEING_TRADED, ImmutableMap.<String, String>of(
                "RC0", "I1","RC1", "I4","RC2","I7"))
        .put(REINFORCE_UNITS, 4)
        .build();
    
    List<String> cards = Lists.newArrayList("RC0","RC1","RC2");
    cards.addAll(getCardsInRange(4, 43));
    
    List<Operation> addUnitsToC = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Delete(CARDS_BEING_TRADED),
        new Set(REINFORCE_UNITS, 3),
        new Set(CARDS, new Shuffle(cards).getKeys()));
  
    // Check if valid move
    assertMoveOk(move(cId, state, addUnitsToC));
    
    // Check for invalid moves
    assertHacker(move(bId, state, addUnitsToC));
    assertHacker(move(cId, emptyState, addUnitsToC));
    assertHacker(move(cId, nonEmptyState, addUnitsToC));    
  }
  
  @Test
  public void testReinforceTerritoriesByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, REINFORCE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(0),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 7,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(2, 43))
        .build();
    
    Map<String, Integer> territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "27", 3);
    territoryC = performDeltaOnTerritory(territoryC, "28", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);
    
    List<Operation> reinforceTerritoryOfC = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 7,
            CONTINENT, emptyListString)));

    Map<String, Integer> territoryB = performDeltaOnTerritory(getTerritories(PLAYER_B), "15", 1);
    territoryB = performDeltaOnTerritory(territoryB, "16", 2);
    
    List<Operation> reinforceTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1),
            TERRITORY, territoryB,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));

    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "27", 5);
    territoryC = performDeltaOnTerritory(territoryC, "28", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);

    List<Operation> reinforceTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 7,
            CONTINENT, emptyListString)));

    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "4", 1);
    territoryC = performDeltaOnTerritory(territoryC, "28", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);

    List<Operation> reinforceTerritoryOfCWithIncorrectTerritory = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 7,
            CONTINENT, emptyListString)));
    
    // Check valid move
    assertMoveOk(move(cId, state, reinforceTerritoryOfC));
    
    // Check invalid move - wrong turn, invalid moves, from invalid states
    assertHacker(move(bId, state, reinforceTerritoryOfC));
    assertHacker(move(cId, state, reinforceTerritoryOfBInWrongTurn));
    assertHacker(move(cId, emptyState, reinforceTerritoryOfC));
    assertHacker(move(cId, nonEmptyState, reinforceTerritoryOfC));
    
    // Check if invalid operations - invalid number of units, invalid territory
    assertHacker(move(cId, state, reinforceTerritoryOfCWithIncorrectNumberOfUnits));
    assertHacker(move(cId, state, reinforceTerritoryOfCWithIncorrectTerritory));
  }
}
