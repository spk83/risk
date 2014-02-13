package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class test the operations that can be performed in fortify phase.
 * @author vishal
 *
 */
public class FortifyPhaseTest extends AbstractTest {
  
  /*
   * This test checks for the valid/invalid operations in fortify phase.
   */
  @Test
  public void testFortifyByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, FORTIFY)
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
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(2, 43))
        .build();
    
    Map<String, Integer> territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -2);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    
    List<Operation> fortifyTerritoryOfC = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING)));

    Map<String, Integer> territoryB = performDeltaOnTerritory(getTerritories(PLAYER_B), "15", -1);
    territoryB = performDeltaOnTerritory(territoryB, "16", 1);
    
    List<Operation> fortifyTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1),
            TERRITORY, territoryB,
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING)));

    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 3);

    List<Operation> fortifyTerritoryOfCWithInvalidMove = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING)));
    
    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    
    List<Operation> fortifyTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, EMPTYLISTINT,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, EMPTYLISTSTRING)));
    
    // Check valid move
    assertMoveOk(move(CID, state, fortifyTerritoryOfC));
    
    // Check invalid move - wrong turn, invalid moves, from invalid states
    assertHacker(move(BID, state, fortifyTerritoryOfC));
    assertHacker(move(CID, state, fortifyTerritoryOfBInWrongTurn));
    assertHacker(move(CID, EMPTYSTATE, fortifyTerritoryOfC));
    assertHacker(move(CID, NONEMPTYSTATE, fortifyTerritoryOfC));
    
    // Check if invalid operations - invalid number of units, invalid territorys, invalid move
    assertHacker(move(CID, state, fortifyTerritoryOfCWithInvalidMove));
    assertHacker(move(CID, state, fortifyTerritoryOfCWithIncorrectNumberOfUnits));
  }
}
