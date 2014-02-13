package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class FortifyPhaseTest extends AbstractTest{
  @Test
  public void testFortifyByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, FORTIFY)
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
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(2, 43))
        .build();
    
    Map<String, Integer> territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -2);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    
    List<Operation> fortifyTerritoryOfC = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));

    Map<String, Integer> territoryB = performDeltaOnTerritory(getTerritories(PLAYER_B), "15", -1);
    territoryB = performDeltaOnTerritory(territoryB, "16", 1);
    
    List<Operation> fortifyTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1),
            TERRITORY, territoryB,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));

    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 3);

    List<Operation> fortifyTerritoryOfCWithInvalidMove = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));

    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "4", -1);
    territoryC = performDeltaOnTerritory(territoryC, "38", 1);

    List<Operation> fortifyTerritoryOfCWithIncorrectTerritory = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));
    
    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    
    List<Operation> fortifyTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new Set(PHASE, CARD_TRADE),
        new Set(TURN, PLAYER_B),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, territoryC,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));
    
    // Check valid move
    assertMoveOk(move(cId, state, fortifyTerritoryOfC));
    
    // Check invalid move - wrong turn, invalid moves, from invalid states
    assertHacker(move(bId, state, fortifyTerritoryOfC));
    assertHacker(move(cId, state, fortifyTerritoryOfBInWrongTurn));
    assertHacker(move(cId, emptyState, fortifyTerritoryOfC));
    assertHacker(move(cId, nonEmptyState, fortifyTerritoryOfC));
    
    // Check if invalid operations - invalid number of units, invalid territorys, invalid move
    assertHacker(move(cId, state, fortifyTerritoryOfCWithInvalidMove));
    assertHacker(move(cId, state, fortifyTerritoryOfCWithIncorrectTerritory));
    assertHacker(move(cId, state, fortifyTerritoryOfCWithIncorrectNumberOfUnits));
  }
}
