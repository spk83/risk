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
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.Delete;
import org.risk.client.GameApi.Shuffle;

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
    Map<String, Object> lastStateAtC = ImmutableMap.<String, Object>of(
        TURN, PLAYER_C,
        PHASE, ADD_UNITS,
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                CARD_VALUES, ImmutableMap.<String, String>of("RC4", null),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                CARD_VALUES, ImmutableMap.<String, String>of("RC3", null),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(0, 1, 2),
                CARD_VALUES, ImmutableMap.<String,String>of("RC0", "I1","RC1", "I4","RC2","I7"),
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString)),
        BOARD, ImmutableMap.<String, Object>of(
            TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A),
            CARDS, getCardsInRange(5, 43),
            TRADE_NUMBER, 0));

    List<Operation> tradeCardsMoveByC = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Set(TRADE_NUMBER, 1),
        new Set(CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
        new Set(REINFORCE_UNITS, 4),
        new SetVisibility("RC0"),
        new SetVisibility("RC1"),
        new SetVisibility("RC2"));
   
    Map<String, Object> newStateAtA = ImmutableMap.<String, Object>of(
        TURN, PLAYER_C,
        PHASE, ADD_UNITS,
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                CARD_VALUES, ImmutableMap.<String, String>of("RC4", "C41"),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                CARD_VALUES, ImmutableMap.<String, String>of("RC3", null),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, emptyListInt,
                CARD_VALUES, emptyMap,
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 4,
                CONTINENT, emptyListString)),
        BOARD, ImmutableMap.<String, Object>of(
            TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A),
            CARDS, getCardsInRange(5, 43),
            TRADE_NUMBER, 1,
            CARDS_BEING_TRADED, ImmutableMap.<String, String>of(
                "RC0", "I1","RC1", "I4","RC2","I7"),
            REINFORCE_UNITS, 4));

    // Check valid move
    assertMoveOk(new VerifyMove(
        cId, playersInfo, newStateAtA, lastStateAtC, tradeCardsMoveByC, cId));
    
    // Check invalid move
    assertHacker(new VerifyMove(
        cId, playersInfo, newStateAtA, emptyState, tradeCardsMoveByC, cId));
    assertHacker(new VerifyMove(
        cId, playersInfo, newStateAtA, lastStateAtC, tradeCardsMoveByC, bId));

    Map<String, Object> lastStateAtC_1 = ImmutableMap.<String, Object>of(
        TURN, PLAYER_C,
        PHASE, CARD_TRADE,
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                CARD_VALUES, ImmutableMap.<String, String>of("RC4", null),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                CARD_VALUES, ImmutableMap.<String, String>of("RC3", null),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(0, 1, 2),
                CARD_VALUES, ImmutableMap.<String,String>of("RC0", "C1","RC1", "I4","RC2","I7"),
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString)),
        BOARD, ImmutableMap.<String, Object>of(
            TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A),
            CARDS, getCardsInRange(5, 43),
            TRADE_NUMBER, 0));
    
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
    Map<String, Object> state = ImmutableMap.<String, Object>of(
        TURN, PLAYER_C,
        PHASE, ADD_UNITS,
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, emptyListInt,
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 4, // Got from Trading cards
                CONTINENT, emptyListString)),
        BOARD, ImmutableMap.<String, Object>of(
            TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A),
            CARDS, getCardsInRange(5, 43),
            TRADE_NUMBER, 1,
            CARDS_BEING_TRADED, ImmutableMap.<String, String>of(
                "RC0", "I1","RC1", "I4","RC2","I7"),
            REINFORCE_UNITS, 4));
    
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
    Map<String, Object> state = ImmutableMap.<String, Object>of(
        TURN, PLAYER_C,
        PHASE, ADD_UNITS, // not sure
        PLAYERS, ImmutableMap.<String, Object>of(
            PLAYER_A, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(4),
                TERRITORY, getTerritories(PLAYER_A),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_B, ImmutableMap.<String, Object>of(
                CARDS, ImmutableList.<Integer>of(3),
                TERRITORY, getTerritories(PLAYER_B),
                UNCLAIMED_UNITS, 0,
                CONTINENT, emptyListString),
            PLAYER_C, ImmutableMap.<String, Object>of(
                CARDS, emptyListInt,
                TERRITORY, getTerritories(PLAYER_C),
                UNCLAIMED_UNITS, 7,
                CONTINENT, emptyListString)),
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
