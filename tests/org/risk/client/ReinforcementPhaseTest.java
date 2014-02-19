package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.SetVisibility;
import org.risk.client.GameApi.Shuffle;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.Delete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class ReinforcementPhaseTest extends AbstractTest {

  /*
   * Test the operations done while trading RISK cards
   */
  @Test
  public void testCardTrade() {
    Map<String, Object> oldStateAtA = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(3, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
    //choose to trade and set PHASE as ADD_UNITS
    List<Operation> tradeCardsMoveByC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new SetVisibility("RC0"),
        new SetVisibility("RC1"),
        new SetVisibility("RC2"),
        new Set(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
        new Set(GameResources.TRADE_NUMBER, 1),
        new Set(GameResources.PHASE, GameResources.ADD_UNITS));
    
    Map<String, Object> newStateAtA = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ADD_UNITS)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(3, GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC0","I1")
        .put("RC1","I4")
        .put("RC2","I7")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .put(GameResources.TRADE_NUMBER, 1)
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
    assertMoveOk(new VerifyMove(
        PLAYERSINFO, newStateAtA, oldStateAtA, tradeCardsMoveByC, CID, null));
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, GameResources.EMPTYSTATE, tradeCardsMoveByC, CID, null));
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, oldStateAtA, tradeCardsMoveByC, BID, null));

    Map<String, Object> newStateAtA1 = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(4),
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3),
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(3, GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC0","I1")
        .put("RC1","C4")
        .put("RC2","I7")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .put(GameResources.TRADE_NUMBER, 1)
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
    // Check if hacker is trading invalid pair of cards
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA1, oldStateAtA, tradeCardsMoveByC, CID, null));
  }

  @Test
  public void testCardTradeOptional() {
    Map<String, Object> oldStateAtA = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CARD_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(3, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
    //choose NOT to trade and set PHASE as ADD_UNITS
    List<Operation> tradeNoCardsMoveByC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(GameResources.PHASE, GameResources.ADD_UNITS));
    
    Map<String, Object> newStateAtA = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ADD_UNITS)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(3, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
    assertMoveOk(new VerifyMove(
        PLAYERSINFO, newStateAtA, oldStateAtA, tradeNoCardsMoveByC, CID, null));
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, GameResources.EMPTYSTATE, tradeNoCardsMoveByC, CID, null));
    assertHacker(new VerifyMove(
        PLAYERSINFO, newStateAtA, oldStateAtA, tradeNoCardsMoveByC, BID, null));
  }
  
  /*
   * Test operations for giving a player army units based on 
   * number of territories and continents.
   */
  @Test
  public void testAddUnitsByCWithCardTrade() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ADD_UNITS)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(4),
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3),
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.CONTINENT, ImmutableList.<String>of("5")))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(5, 43))
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .build();
    
    List<String> deck = Lists.newArrayList(getCardsInRange(5, 43));
    deck.add("RC0");
    deck.add("RC1");
    deck.add("RC2");
    
    List<Operation> addUnitsToC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 10,
            GameResources.CONTINENT, ImmutableList.<String>of("5"))),
        new Delete(GameResources.CARDS_BEING_TRADED),
        new SetVisibility("RC0", GameResources.EMPTYLISTINT),
        new SetVisibility("RC1", GameResources.EMPTYLISTINT),
        new SetVisibility("RC2", GameResources.EMPTYLISTINT),
        new Shuffle(deck),
        new Set(GameResources.DECK, deck),
        new Set(GameResources.PHASE, GameResources.REINFORCE));
    
    // Check if valid move
    assertMoveOk(move(CID, state, addUnitsToC));
    
    // Check for invalid moves
    assertHacker(move(BID, state, addUnitsToC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, addUnitsToC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, addUnitsToC));
    
    // add test case for hecker with wrong number of unclaimed units
    List<Operation> addWrongUnitsToC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 10,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Delete(GameResources.CARDS_BEING_TRADED),
        new SetVisibility("RC0", GameResources.EMPTYLISTINT),
        new SetVisibility("RC1", GameResources.EMPTYLISTINT),
        new SetVisibility("RC2", GameResources.EMPTYLISTINT),
        new Shuffle(deck),
        new Set(GameResources.DECK, deck),
        new Set(GameResources.PHASE, GameResources.REINFORCE));
    
    assertHacker(move(CID, state, addWrongUnitsToC));
  }
  
  @Test
  public void testAddUnitsByCWithOutCardTrade() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ADD_UNITS)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(4),
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3),
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("5")))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(3, 43))
        .put(GameResources.TRADE_NUMBER, 1)
        .build();
      
    List<Operation> addUnitsToC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 6,
            GameResources.CONTINENT, ImmutableList.<String>of("5"))),
        new Set(GameResources.PHASE, GameResources.REINFORCE));
    
    // Check if valid move
    assertMoveOk(move(CID, state, addUnitsToC));
    
    // Check for invalid moves
    assertHacker(move(BID, state, addUnitsToC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, addUnitsToC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, addUnitsToC));

    // add test case for hacker with wrong number of unclaimed units
    List<Operation> addWrongUnitsToC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 5,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.REINFORCE));
    
    assertHacker(move(CID, state, addWrongUnitsToC));
  }
 
  @Test
  public void testReinforceTerritoriesByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.REINFORCE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0),
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 8,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(2, 43))
        .build();
    
    Map<String, Integer> territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 3);
    territoryC = performDeltaOnTerritory(territoryC, "38", 3);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);
    
    List<Operation> reinforceTerritoryOfC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));

    // Check valid move
    assertMoveOk(move(CID, state, reinforceTerritoryOfC));
    
    // Check for hecker - wrong turn, from invalid states
    assertHacker(move(BID, state, reinforceTerritoryOfC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, reinforceTerritoryOfC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, reinforceTerritoryOfC));

    // Check invalid move - wrong turn, invalid moves, from invalid states
    Map<String, Integer> territoryB = performDeltaOnTerritory(getTerritories(PLAYER_B), "15", 1);
    territoryB = performDeltaOnTerritory(territoryB, "16", 2);
    
    List<Operation> reinforceTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.TERRITORY, territoryB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));

    assertHacker(move(CID, state, reinforceTerritoryOfBInWrongTurn));
    
    // Check if invalid operations - invalid number of units
    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 2);

    List<Operation> reinforceTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));

    assertHacker(move(CID, state, reinforceTerritoryOfCWithIncorrectNumberOfUnits));
  }
  
  @Test
  public void testNoReinforceTerritory() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.REINFORCE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0),
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 8,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(2, 43))
        .build();
    
    List<Operation> noReinforceTerritoryOfC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));

    // Check valid move
    assertMoveOk(move(CID, state, noReinforceTerritoryOfC));
    
    // Check for hacker - wrong turn, from invalid states
    assertHacker(move(BID, state, noReinforceTerritoryOfC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, noReinforceTerritoryOfC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, noReinforceTerritoryOfC));
  }
}
