package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetTurn;

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
    
    Map<String, Integer> territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", 30);
    Map<String, Integer> territoryB = performDeltaOnTerritory(getTerritories(PLAYER_B), "15", 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.FORTIFY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0),
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.TERRITORY, territoryB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(2, 43))
        .build();
    
    territoryC = performDeltaOnTerritory(territoryC, "30", -2);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    
    List<Operation> fortifyTerritoryOfC = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.CARD_TRADE));
    
    // Check valid move
    assertMoveOk(move(CID, state, fortifyTerritoryOfC));
  
    // Check invalid move - wrong turn, from invalid states
    assertHacker(move(BID, state, fortifyTerritoryOfC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, fortifyTerritoryOfC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, fortifyTerritoryOfC));
  
    territoryB = performDeltaOnTerritory(territoryB, "15", -1);
    territoryB = performDeltaOnTerritory(territoryB, "16", 1);
    
    List<Operation> fortifyTerritoryOfBInWrongTurn = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.TERRITORY, territoryB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.CARD_TRADE));
    
    assertHacker(move(CID, state, fortifyTerritoryOfBInWrongTurn));
    
    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    territoryC = performDeltaOnTerritory(territoryC, "41", 3);

    List<Operation> fortifyTerritoryOfCWithInvalidMove = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.CARD_TRADE));
            
    
    assertHacker(move(CID, state, fortifyTerritoryOfCWithInvalidMove));
    
    territoryC = performDeltaOnTerritory(getTerritories(PLAYER_C), "30", -5);
    territoryC = performDeltaOnTerritory(territoryC, "38", 2);
    
    List<Operation> fortifyTerritoryOfCWithIncorrectNumberOfUnits = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)),
        new Set(GameResources.PHASE, GameResources.CARD_TRADE));

    assertHacker(move(CID, state, fortifyTerritoryOfCWithIncorrectNumberOfUnits));
  }
  
  @Test
  public void testFortifySkippedByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.FORTIFY)
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
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(2, 43))
        .build();
        
    List<Operation> fortifyTerritoryOfC = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(GameResources.PHASE, GameResources.CARD_TRADE));
    
    // Check valid move
    assertMoveOk(move(CID, state, fortifyTerritoryOfC));
  
    // Check invalid move - wrong turn, from invalid states
    assertHacker(move(BID, state, fortifyTerritoryOfC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, fortifyTerritoryOfC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, fortifyTerritoryOfC));
  }
}
