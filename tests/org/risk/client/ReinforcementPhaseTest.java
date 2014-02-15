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
  public void testTradeCardsMoveByC() {
    Map<String, Object> lastStateAtC = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            CARD_VALUES, ImmutableMap.<String, String>of("RC4", ""),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            CARD_VALUES, ImmutableMap.<String, String>of("RC3", ""),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(0, 1, 2),
            CARD_VALUES, ImmutableMap.<String, String>of("RC0", "I1", "RC1", "I4", "RC2", "I7"),
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
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
            CARDS, EMPTYLISTINT,
            CARD_VALUES, EMPTYMAP,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 4,
            CONTINENT, EMPTYLISTSTRING)),
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
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            CARD_VALUES, ImmutableMap.<String, String>of("RC3", ""),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            CARD_VALUES, EMPTYMAP,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 4,
            CONTINENT, EMPTYLISTSTRING))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 1)
        .put(CARDS_BEING_TRADED, ImmutableMap.<String, String>of(
                "RC0", "I1", "RC1", "I4", "RC2", "I7"))
        .put(REINFORCE_UNITS, 4)
        .build();
    
    // Check valid move
    assertMoveOk(new VerifyMove(
        PLAYERSINFO, newStateAtA, lastStateAtC, tradeCardsMoveByC, CID, null));
    
    // Check invalid move
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, EMPTYSTATE, tradeCardsMoveByC, CID, null));
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, lastStateAtC, tradeCardsMoveByC, BID, null));

    Map<String, Object> lastStateAtC1 = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            CARD_VALUES, ImmutableMap.<String, String>of("RC4", ""),
            TERRITORY, getTerritories(PLAYER_A),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            CARD_VALUES, ImmutableMap.<String, String>of("RC3", ""),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(0, 1, 2),
            CARD_VALUES, ImmutableMap.<String, String>of("RC0", "C1", "RC1", "I4", "RC2", "I7"),
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 0)
        .build();
    
    // Check if hacker is trading invalid pair of cards
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, lastStateAtC1, tradeCardsMoveByC, CID, null));
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
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(3),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 4,
            CONTINENT, EMPTYLISTSTRING))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(5, 43))
        .put(TRADE_NUMBER, 1)
        .put(CARDS_BEING_TRADED, ImmutableMap.<String, String>of(
                "RC0", "I1", "RC1", "I4", "RC2", "I7"))
        .put(REINFORCE_UNITS, 4)
        .build();
    
    List<String> cards = Lists.newArrayList("RC0", "RC1", "RC2");
    cards.addAll(getCardsInRange(4, 43));
    
    List<Operation> addUnitsToC = ImmutableList.<Operation>of(
        new Set(PHASE, REINFORCE),
        new Delete(CARDS_BEING_TRADED),
        new Set(REINFORCE_UNITS, 3),
        new Set(CARDS, new Shuffle(cards).getKeys()));
  
    // Check if valid move
    assertMoveOk(move(CID, state, addUnitsToC));
    
    // Check for invalid moves
    assertHacker(move(BID, state, addUnitsToC));
    assertHacker(move(CID, EMPTYSTATE, addUnitsToC));
    assertHacker(move(CID, NONEMPTYSTATE, addUnitsToC));    
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
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1),
            TERRITORY, getTerritories(PLAYER_B),
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, getTerritories(PLAYER_C),
            UNCLAIMED_UNITS, 7,
            CONTINENT, EMPTYLISTSTRING))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(2, 43))
        .build();
    
    Map<String, Integer> territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 3);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);
    
    List<Operation> reinforceTerritoryOfC = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 7,
            CONTINENT, EMPTYLISTSTRING)));

    Map<String, Integer> territoryB = performDeltaOnTerritory(getTerritories(PLAYER_B), "15", 1);
    territoryB = performDeltaOnTerritory(territoryB, "16", 2);
    
    List<Operation> reinforceTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1),
            TERRITORY, territoryB,
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING)));

    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);

    List<Operation> reinforceTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new Set(PHASE, ATTACK_PHASE),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 7,
            CONTINENT, EMPTYLISTSTRING)));
    
    // Check valid move
    assertMoveOk(move(CID, state, reinforceTerritoryOfC));
    
    // Check invalid move - wrong turn, invalid moves, from invalid states
    assertHacker(move(BID, state, reinforceTerritoryOfC));
    assertHacker(move(CID, state, reinforceTerritoryOfBInWrongTurn));
    assertHacker(move(CID, EMPTYSTATE, reinforceTerritoryOfC));
    assertHacker(move(CID, NONEMPTYSTATE, reinforceTerritoryOfC));
    
    // Check if invalid operations - invalid number of units
    assertHacker(move(CID, state, reinforceTerritoryOfCWithIncorrectNumberOfUnits));
  }
}
