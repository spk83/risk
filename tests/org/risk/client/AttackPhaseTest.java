package org.risk.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetRandomInteger;
import org.risk.client.GameApi.Delete;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.SetVisibility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class AttackPhaseTest extends AbstractTest {
  
  private Map<String, Integer> getTerritoriesInRange(
      int lowestTerritoryIdInclusive, int highestTerritoryIdInclusive, int baseUnits) 
          throws Exception {
    if (isTerritoryInRange(highestTerritoryIdInclusive) 
        && isTerritoryInRange(lowestTerritoryIdInclusive)
            && lowestTerritoryIdInclusive <= highestTerritoryIdInclusive) {
      Map<String, Integer> territoryMap = new HashMap<String, Integer>();
      for (int i = lowestTerritoryIdInclusive; i <= highestTerritoryIdInclusive; i++) {
        territoryMap.put(i + "", baseUnits);
      }
      return territoryMap;
    } else {
      throw new Exception("Invalid Territory ID");
    }
  }
  
  private boolean isTerritoryInRange(int territoryId) {
    if (territoryId >= 0 && territoryId < 42) {
      return true;
    }
    return false;
  }
  
  @Test
  public void testGetTerritoriesInRange() throws Exception {
    Map<String, Integer> territoryMap = getTerritoriesInRange(0, 4, 2);
    Assert.assertEquals(territoryMap.size(), 5);
    for (int i = 0; i <= 4; ++i) {
      Assert.assertEquals(2, territoryMap.get(i + "").intValue());
    }
  }
  
  @Test(expected = Exception.class)
  public void testGetTerritoriesInRangeWithInvalidHighRange() throws Exception {
    getTerritoriesInRange(0, 50, 2);
  }
  
  @Test(expected = Exception.class)
  public void testGetTerritoriesInRangeWithInvalidLowRange() throws Exception {
    getTerritoriesInRange(-1, 41, 2);
  }
  
  @Test(expected = Exception.class)
  public void testGetTerritoriesInRangeWithLowGreaterThanHigh() throws Exception {
    getTerritoriesInRange(23, 1, 2);
  }
  
  @Test
  public void testAttackOfAOnB() throws Exception {
    final List<Operation> attackOperationsOfAOnB = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 10,
            GameResources.UNITS, 6)),
        new Set(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
                GameResources.PLAYER, PLAYER_B,
                GameResources.TERRITORY, 15, 
                GameResources.UNITS, 1)),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 1, 7),
        new SetRandomInteger(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 1, 7),
        new Set(GameResources.PHASE, GameResources.ATTACK_RESULT));
        
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_PHASE).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        build();
    
    assertMoveOk(move(AID, state, attackOperationsOfAOnB));
    assertHacker(move(AID, GameResources.EMPTYSTATE, attackOperationsOfAOnB));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, attackOperationsOfAOnB));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, attackOperationsOfAOnB));
    assertHacker(move(CID, state, attackOperationsOfAOnB));
  }
  
  @Test
  public void testAttackOfAOnBAWins() throws Exception {
    
    Map<String, Integer> territoryMapB = getTerritoriesInRange(11, 29, 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 10, 
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 15, 
            GameResources.UNITS, 1)).build();
    
    //based on dice rolls
    territoryMapB = performDeltaOnTerritory(territoryMapB, "15", -1);
    territoryMapB.remove("15");
    
    @SuppressWarnings("unchecked")
    Map<String, Object> playerBMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_B));
    
    playerBMap.put(GameResources.TERRITORY, territoryMapB);
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(BID),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(15)),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
  }
  
  @Test
  public void testAttackOfAOnBADoesNotWin() throws Exception {
    
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 10, 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 3).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 3).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 10, 
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 15, 
            GameResources.UNITS, 1)).
        build();
    
    //based on dice rolls if the attacker loses then we again go back to the start of attack phase
    //and the attacker decides to take a call
    territoryMapA = performDeltaOnTerritory(territoryMapA, "10", -1);
    @SuppressWarnings("unchecked")
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));

    //consequently the ATTACK_DETAILS will be removed while computing new state
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
  }
  
  @Test
  public void testAttackOfAOnBAOccupying() throws Exception {
    Map<String, Integer> territoryMapB = getTerritoriesInRange(11, 29, 1);
    territoryMapB = performDeltaOnTerritory(territoryMapB, "15", -1);
    territoryMapB.remove("15");
     
    //creating state after the attack of A
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_OCCUPY).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(15)).
        put(GameResources.TERRITORY_WINNER, PLAYER_A).build();
    
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 10, 6);
    
    //Giving 15 to A and also shifting soldiers from 10 to 15
    territoryMapA.put("15", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "10", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "15", +3);
    
    //changing the territory map for A and going back to attack phase
    @SuppressWarnings("unchecked")
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Delete(GameResources.UNCLAIMED_TERRITORY),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
  }
  @Test
  public void testEndAttackByA() throws Exception {
    
    Map<String, Integer> territoryMapB = getTerritoriesInRange(11, 29, 1);
    territoryMapB = performDeltaOnTerritory(territoryMapB, "15", -1);
    territoryMapB.remove("15");
    
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 10, 6);
    territoryMapA.put("15", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "10", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "15", +3);
    
    //creating state after the attack of A
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_PHASE).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.TERRITORY_WINNER, PLAYER_A).build();
    
    //ending the Attack phase and since PLAYER_A won a territory, giving it a card.
    @SuppressWarnings("unchecked")
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.CARDS, ImmutableList.<Integer>of(0));
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(GameResources.DECK, getCardsInRange(1, 43)),
        new SetVisibility("RC0",ImmutableList.<Integer>of(AID)),
        new Delete(GameResources.TERRITORY_WINNER),
        new Set(GameResources.PHASE, GameResources.FORTIFY));
    
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAttackOfAOnBAWinsAndBLosesTheGame() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 14, 6);
    final Map<String, Integer> territoryMapB = getTerritoriesInRange(15, 15, 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1),
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(2, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 10,
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 15,
            GameResources.UNITS, 1)).
        build();
    
    //based on dice rolls
    //deleting the data of player B since it had just one territory. And giving the cards of B to A
    
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.CARDS, ImmutableList.<Integer>copyOf(
        (List<Integer>) ((Map<String, Object>) state.get(PLAYER_B)).get(GameResources.CARDS)));
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Delete(PLAYER_B),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(15)),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.MESSAGE, PLAYER_B + " out of the game !"),
        new Set(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, AID)),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
  }
  
  @SuppressWarnings("unchecked")
  public void testAttackOfAOnBAWinsTheGame() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 40, 6);
    final Map<String, Integer> territoryMapB = getTerritoriesInRange(41, 41, 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1),
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(BID, AID)).
        put(GameResources.CARDS, getCardsInRange(2, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 10, 
            GameResources.UNITS,  6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "3", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 15, 
            GameResources.UNITS, 1)).build();
    
    //based on dice rolls
    //deleting the data of player B, giving B's cards to A and sending the victory message for A
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.CARDS, ImmutableList.<Integer>copyOf(
        (List<Integer>) ((Map<String, Object>) state.get(PLAYER_B)).get(GameResources.CARDS)));
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Delete(PLAYER_B),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(15)),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.MESSAGE, 
            PLAYER_B + " out of the game ! and " + PLAYER_A + " wins !!!"),
        new Set(GameResources.TURN_ORDER, ImmutableList.<Integer>of(AID)),
        new Set(GameResources.PHASE, GameResources.END_GAME));
    
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
  }
}