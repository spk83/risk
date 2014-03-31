package org.risk.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Delete;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetRandomInteger;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.SetVisibility;
import org.risk.client.GameApi.Shuffle;
import org.risk.client.GameApi.VerifyMove;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(JUnit4.class)
public class AttackPhaseTest extends AbstractTest {
  
  
  @Test
  public void testAttackOfAOnB() throws Exception {
    final List<Operation> attackOperationsOfAOnB = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 6)),
        new Set(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13, 
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
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        build();
    
    assertMoveOk(move(AID, state, attackOperationsOfAOnB));
    assertHacker(move(AID, GameResources.EMPTYSTATE, attackOperationsOfAOnB));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, attackOperationsOfAOnB));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, attackOperationsOfAOnB));
    assertHacker(move(CID, state, attackOperationsOfAOnB));
    
    final List<Operation> attackFromInvalidTerritoryFromAOnB = ImmutableList.<Operation>of(
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
    
    final List<Operation> attackWithInvalidUnitsByAOnB = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 1)),
        new Set(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13, 
            GameResources.UNITS, 1)),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 1, 7),
        new SetRandomInteger(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 1, 7),
        new Set(GameResources.PHASE, GameResources.ATTACK_RESULT));
    
    final List<Operation> attackWithInvalidDiceRollsByAOnB = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 2)),
        new Set(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13, 
            GameResources.UNITS, 2)),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 1, 7),
        new SetRandomInteger(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 1, 7),
        new Set(GameResources.PHASE, GameResources.ATTACK_RESULT));
    
    final List<Operation> attackFromAOnA = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 6)),
        new Set(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 1, 
            GameResources.UNITS, 1)),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 1, 7),
        new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 1, 7),
        new SetRandomInteger(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 1, 7),
        new Set(GameResources.PHASE, GameResources.ATTACK_RESULT));
    
    assertHacker(move(AID, state, attackFromInvalidTerritoryFromAOnB));
    assertHacker(move(AID, state, attackWithInvalidUnitsByAOnB));
    assertHacker(move(AID, state, attackWithInvalidDiceRollsByAOnB));
    assertHacker(move(AID, state, attackFromAOnA));
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
            GameResources.CONTINENT, ImmutableList.<String>of("0"))).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("2"))).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5, 
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13, 
            GameResources.UNITS, 1)).build();
    
    //based on dice rolls
    territoryMapB = performDeltaOnTerritory(territoryMapB, "13", -1);
    territoryMapB.remove("13");
    
    @SuppressWarnings("unchecked")
    Map<String, Object> playerBMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_B));
    
    playerBMap.put(GameResources.TERRITORY, territoryMapB);
    playerBMap.put(GameResources.CONTINENT, GameResources.EMPTYLISTSTRING);
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, state.get(PLAYER_A)),
        new Set(PLAYER_B, playerBMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
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
    
    final List<Operation> declareWrongWinner = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, state.get(PLAYER_A)),
        new Set(PLAYER_B, playerBMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_B),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    final List<Operation> invalidDefenderTerritoryState = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, state.get(PLAYER_A)),
        new Set(PLAYER_B, state.get(PLAYER_B)),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    final List<Operation> invalidLastAttackingTerritoryState = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, state.get(PLAYER_A)),
        new Set(PLAYER_B, playerBMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 10),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    assertHacker(move(AID, state, declareWrongWinner));
    assertHacker(move(AID, state, invalidDefenderTerritoryState));
    assertHacker(move(AID, state, invalidLastAttackingTerritoryState));    
  }
  
  @Test
  public void testAttackOfAOnBADoesNotWin() throws Exception {
    
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 10, 6);
    
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
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 3).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 3).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5, 
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13, 
            GameResources.UNITS, 1)).
        build();
    
    //based on dice rolls if the attacker loses then we again go back to the start of attack phase
    //and the attacker decides to take a call
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -1);
    @SuppressWarnings("unchecked")
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(PLAYER_B, state.get(PLAYER_B)),
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
    territoryMapB = performDeltaOnTerritory(territoryMapB, "13", -1);
    territoryMapB.remove("13");
     
    //creating state after the attack of A
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_OCCUPY).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0"))).
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
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)).
        put(GameResources.LAST_ATTACKING_TERRITORY, 5).
        put(GameResources.TERRITORY_WINNER, PLAYER_A).
        build();
    
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 10, 6);
    
    territoryMapA.put("13", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "13", +3);
    
    //changing the territory map for A and going back to attack phase
    @SuppressWarnings("unchecked")
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT),
        new Delete(GameResources.LAST_ATTACKING_TERRITORY),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
    
    // Moving to wrong territory
    territoryMapA = getTerritoriesInRange(0, 10, 6);
    
    //Giving 13 to A and also shifting soldiers from 5 to 13
    territoryMapA.put("15", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "15", +3);
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> wrongTerritoryMovementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT),
        new Delete(GameResources.LAST_ATTACKING_TERRITORY),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    
    assertHacker(move(AID, state, wrongTerritoryMovementOperations));
    
    // Moving to with invalid number of units to new territory
    territoryMapA = getTerritoriesInRange(0, 10, 6);
    
    //Giving 13 to A and also shifting soldiers from 5 to 13
    territoryMapA.put("13", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -2);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "13", +2);
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> invalidUnitsForMovement = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT),
        new Delete(GameResources.LAST_ATTACKING_TERRITORY),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    
    assertHacker(move(AID, state, invalidUnitsForMovement));
    
    // Moving to with invalid number of units to new territory
    territoryMapA = getTerritoriesInRange(0, 10, 6);
    
    //Giving 13 to A and also shifting soldiers from 5 to 13
    territoryMapA.put("13", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "13", +5);
    playerAMap.put(GameResources.TERRITORY, territoryMapA);
    final List<Operation> invalidUnitsDelta = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT),
        new Delete(GameResources.LAST_ATTACKING_TERRITORY),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    
    assertHacker(move(AID, state, invalidUnitsDelta));
  }
  
  @Test
  public void testEndAttackByA() throws Exception {
    
    Map<String, Integer> territoryMapB = getTerritoriesInRange(11, 29, 1);
    territoryMapB = performDeltaOnTerritory(territoryMapB, "13", -1);
    territoryMapB.remove("13");
    
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 10, 6);
    territoryMapA.put("13", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "13", +3);
    
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
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
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
        new SetVisibility("RC0", ImmutableList.<String>of(AID)),
        new Delete(GameResources.TERRITORY_WINNER),
        new Set(GameResources.PHASE, GameResources.FORTIFY));
    
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));

    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));

    // giving two cards to player
    playerAMap.put(GameResources.CARDS, ImmutableList.<Integer>of(0, 1));
    final List<Operation> invalidMovementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Set(GameResources.DECK, getCardsInRange(2, 43)),
        new SetVisibility("RC0", ImmutableList.<String>of(AID)),
        new Delete(GameResources.TERRITORY_WINNER),
        new Set(GameResources.PHASE, GameResources.FORTIFY));
    
    assertHacker(move(AID, state, invalidMovementOperations));
  }
  
  @Test
  public void testEndAttackByAWithoutWinningAnyTerritory() throws Exception {
   
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
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        build();
    
    //ending the Attack phase where PLAYER_A didnt win any territory
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(GameResources.PHASE, GameResources.FORTIFY));
    
    assertMoveOk(move(AID, state, movementOperations));
    assertHacker(move(AID, GameResources.EMPTYSTATE, movementOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, movementOperations));
    assertHacker(move(BID, state, movementOperations));
    assertHacker(move(CID, state, movementOperations));
 
    final List<Operation> emptyOperations = ImmutableList.<Operation>of();
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, emptyOperations));
    
    final List<Operation> invalidMovementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, state.get(PLAYER_A)),
        new Set(GameResources.PHASE, GameResources.FORTIFY));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, invalidMovementOperations));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAttackOfAOnBAWinsAndBLosesTheGame() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 12, 6);
    final Map<String, Integer> territoryMapB = getTerritoriesInRange(13, 13, 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(),
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(2, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13,
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
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID)),
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
  @Test
  public void testAttackOfAOnBAWinsAndBLosesTheGameAndAHasToTrade() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 12, 6);
    final Map<String, Integer> territoryMapB = getTerritoriesInRange(13, 13, 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(5),
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2, 3, 4),
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(6, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13,
            GameResources.UNITS, 1)).
        build();
    
    //based on dice rolls
    //deleting the data of player B since it had just one territory. And giving the cards of B to A
    
    Map<String, Object> playerAMap = 
        new HashMap<String, Object>((Map<String, Object>) state.get(PLAYER_A));
    List<Integer> playerACards = new ArrayList<Integer>((List<Integer>) ((
        Map<String, Object>) state.get(PLAYER_A)).get(GameResources.CARDS));
    playerACards.addAll((List<Integer>) 
        ((Map<String, Object>) state.get(PLAYER_B)).get(GameResources.CARDS));
    playerAMap.put(GameResources.CARDS, playerACards);
    //Collections.sort((List<Integer>)playerAMap.get(GameResources.CARDS));
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, playerAMap),
        new Delete(PLAYER_B),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new SetVisibility("RC0", ImmutableList.<String>of(AID)),
        new SetVisibility("RC1", ImmutableList.<String>of(AID)),
        new SetVisibility("RC2", ImmutableList.<String>of(AID)),
        new SetVisibility("RC3", ImmutableList.<String>of(AID)),
        new SetVisibility("RC4", ImmutableList.<String>of(AID)),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID)),
        new Set(GameResources.PHASE, GameResources.ATTACK_TRADE));
    
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
  public void testCardTradingInAttackPhase() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 12, 6);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_TRADE).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2, 3, 4, 5),
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)).
        put(GameResources.LAST_ATTACKING_TERRITORY, 5).
        put(GameResources.TERRITORY_WINNER, PLAYER_A).
        put(GameResources.DECK, getCardsInRange(6, 43)).
        build();
    
    //trading cards
    Map<String, Object> newPlayerMap = Maps.newHashMap((Map<String, Object>) state.get(PLAYER_A));
    List<Integer> newCardList = Lists.newArrayList(3, 4, 5);
    newPlayerMap.put(GameResources.CARDS, newCardList);
    newPlayerMap.put(GameResources.UNCLAIMED_UNITS, 4);
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, newPlayerMap),
        new SetVisibility("RC0"),
        new SetVisibility("RC1"),
        new SetVisibility("RC2"),
        new Set(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
        new Set(GameResources.TRADE_NUMBER, 1),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Set(GameResources.CONTINUOUS_TRADE, 1),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    Map<String, Object> newStateAtC = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_OCCUPY)
        .put(PLAYER_A, newPlayerMap)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID))
        .put(GameResources.DECK, getCardsInRange(6, GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC0", "I1")
        .put("RC1", "I4")
        .put("RC2", "I7")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .put(GameResources.TRADE_NUMBER, 1)
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 5)
        .put(GameResources.TERRITORY_WINNER, PLAYER_A)
        .put(GameResources.CONTINUOUS_TRADE, 1)
        .build();
    
    assertMoveOk(new VerifyMove(
        PLAYERSINFO, newStateAtC, state, movementOperations, AID, null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testCardTradingWhenMoreThanOneTradeRequired() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 12, 6);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_TRADE).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2, 3, 4, 5, 6, 7, 8),
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)).
        put(GameResources.LAST_ATTACKING_TERRITORY, 5).
        put(GameResources.TERRITORY_WINNER, PLAYER_A).
        put(GameResources.DECK, getCardsInRange(9, 43)).
        build();
    
    //trading cards
    Map<String, Object> newPlayerMap = Maps.newHashMap((Map<String, Object>) state.get(PLAYER_A));
    List<Integer> newCardList = Lists.newArrayList(3, 4, 5, 6, 7, 8);
    newPlayerMap.put(GameResources.CARDS, newCardList);
    newPlayerMap.put(GameResources.UNCLAIMED_UNITS, 4);
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, newPlayerMap),
        new SetVisibility("RC0"),
        new SetVisibility("RC1"),
        new SetVisibility("RC2"),
        new Set(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
        new Set(GameResources.TRADE_NUMBER, 1),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Set(GameResources.CONTINUOUS_TRADE, 1),
        new Set(GameResources.PHASE, GameResources.ATTACK_TRADE));
    
    Map<String, Object> newStateAtC = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_TRADE)
        .put(PLAYER_A, newPlayerMap)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID))
        .put(GameResources.DECK, getCardsInRange(9, GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC0", "I1")
        .put("RC1", "I4")
        .put("RC2", "I7")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .put(GameResources.TRADE_NUMBER, 1)
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 5)
        .put(GameResources.TERRITORY_WINNER, PLAYER_A)
        .put(GameResources.CONTINUOUS_TRADE, 1)
        .build();
    
    assertMoveOk(new VerifyMove(
        PLAYERSINFO, newStateAtC, state, movementOperations, AID, null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testCardTradingOnSecondTrade() throws Exception {
    final Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 12, 6);
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_TRADE)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3, 4, 5, 6, 7, 8),
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.DECK, getCardsInRange(9, 43))
        .put("RC0", "I1")
        .put("RC1", "I4")
        .put("RC2", "I7")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .put(GameResources.TRADE_NUMBER, 1)
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 5)
        .put(GameResources.TERRITORY_WINNER, PLAYER_A)
        .put(GameResources.CONTINUOUS_TRADE, 1)
        .build();
    
    //trading cards
    Map<String, Object> newPlayerMap = Maps.newHashMap((Map<String, Object>) state.get(PLAYER_A));
    List<Integer> newCardList = Lists.newArrayList(6, 7, 8);
    newPlayerMap.put(GameResources.CARDS, newCardList);
    newPlayerMap.put(GameResources.UNCLAIMED_UNITS, 10);
    
    List<String> deck = Lists.newArrayList(getCardsInRange(9, 43));
    deck.add("RC0");
    deck.add("RC1");
    deck.add("RC2");
    Collections.sort(deck);
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, newPlayerMap),
        new SetVisibility("RC0", GameResources.EMPTYLISTSTRING),
        new SetVisibility("RC1", GameResources.EMPTYLISTSTRING),
        new SetVisibility("RC2", GameResources.EMPTYLISTSTRING),
        new SetVisibility("RC3"),
        new SetVisibility("RC4"),
        new SetVisibility("RC5"),
        new Set(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(3, 4, 5)),
        new Set(GameResources.TRADE_NUMBER, 2),
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 5),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new Shuffle(deck),
        new Set(GameResources.DECK, deck),
        new Set(GameResources.CONTINUOUS_TRADE, 2),
        new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY));
    
    Map<String, Object> newStateAtC = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_REINFORCE)
        .put(PLAYER_A, newPlayerMap)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID))
        .put(GameResources.DECK, getCardsInRange(9, GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC3", "W1")
        .put("RC4", "C2")
        .put("RC5", "A0")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(3, 4, 5))
        .put(GameResources.TRADE_NUMBER, 2)
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 5)
        .put(GameResources.TERRITORY_WINNER, PLAYER_A)
        .put(GameResources.CONTINUOUS_TRADE, 2)
        .build();
    
    assertMoveOk(new VerifyMove(
        PLAYERSINFO, newStateAtC, state, movementOperations, AID, null));
  }
  
  @Test
  public void testMovementAfterTrading() throws Exception {
    Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 12, 6);
    
    Map<String, Object> playerMap = ImmutableMap.<String, Object>of(
        GameResources.CARDS, ImmutableList.<Integer>of(3, 4, 5),
        GameResources.TERRITORY, territoryMapA,
        GameResources.UNCLAIMED_UNITS, 10,
        GameResources.CONTINENT, ImmutableList.<String>of("0"));
    
    List<String> deck = Lists.newArrayList(getCardsInRange(6, 43));
    deck.add("RC0");
    deck.add("RC1");
    deck.add("RC2");
    
    Collections.sort(deck);
    
    Map<String, Object> stateAtC = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_OCCUPY)
        .put(PLAYER_A, playerMap)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID))
        .put(GameResources.DECK, getCardsInRange(6, GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC0", "W1")
        .put("RC1", "C2")
        .put("RC2", "A0")
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .put(GameResources.TRADE_NUMBER, 2)
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 5)
        .put(GameResources.TERRITORY_WINNER, PLAYER_A)
        .put(GameResources.CONTINUOUS_TRADE, 1)
        .build();
    
    territoryMapA.put("13", 0);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "5", -3);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "13", +3);
    Map<String, Object> newPlayerMap = Maps.newHashMap(playerMap);
    newPlayerMap.put(GameResources.TERRITORY, territoryMapA);
    newPlayerMap.put(GameResources.CONTINENT, ImmutableList.<String>of("0", "1"));
    
    final List<Operation> movementOperations = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, newPlayerMap),
        new SetVisibility("RC0", GameResources.EMPTYLISTSTRING),
        new SetVisibility("RC1", GameResources.EMPTYLISTSTRING),
        new SetVisibility("RC2", GameResources.EMPTYLISTSTRING),
        new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT),
        new Delete(GameResources.LAST_ATTACKING_TERRITORY),
        new Delete(GameResources.CARDS_BEING_TRADED),
        new Shuffle(deck),
        new Set(GameResources.DECK, deck),
        new Delete(GameResources.CONTINUOUS_TRADE),
        new Set(GameResources.PHASE, GameResources.ATTACK_REINFORCE));
    
    assertMoveOk(move(AID, stateAtC, movementOperations));
  }
  
  @Test
  public void testEndReinformentInAttack() throws Exception {
Map<String, Integer> territoryMapA = getTerritoriesInRange(0, 13, 6);
    
    Map<String, Object> playerMap = ImmutableMap.<String, Object>of(
        GameResources.CARDS, ImmutableList.<Integer>of(3, 4, 5),
        GameResources.TERRITORY, territoryMapA,
        GameResources.UNCLAIMED_UNITS, 10,
        GameResources.CONTINENT, ImmutableList.<String>of("0", "1"));
    
    List<String> deck = Lists.newArrayList(getCardsInRange(6, 43));
    deck.add("RC0");
    deck.add("RC1");
    deck.add("RC2");
    
    Collections.sort(deck);
    
    Map<String, Object> stateAtC = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_REINFORCE)
        .put(PLAYER_A, playerMap)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, AID))
        .put(GameResources.DECK, getCardsInRange(6, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.TRADE_NUMBER, 2)
        .put(GameResources.TERRITORY_WINNER, PLAYER_A)
        .build();
    
    Map<String, Object> newPlayerMap = Maps.newHashMap(playerMap);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "0", 6);
    territoryMapA = performDeltaOnTerritory(territoryMapA, "1", 4);
    newPlayerMap.put(GameResources.TERRITORY, territoryMapA);
    newPlayerMap.put(GameResources.UNCLAIMED_UNITS, 0);
    
    List<Operation> reinforceTerritoryOfA = ImmutableList.<Operation>of(
        new SetTurn(AID),
        new Set(PLAYER_A, newPlayerMap),
        new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));

    // Check valid move
    assertMoveOk(move(AID, stateAtC, reinforceTerritoryOfA));
  }
  
  @Test
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
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(BID, AID)).
        put(GameResources.CARDS, getCardsInRange(2, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 40, 
            GameResources.UNITS,  6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 41, 
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
        new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(41)),
        new Set(GameResources.LAST_ATTACKING_TERRITORY, 40),
        new Set(GameResources.TERRITORY_WINNER, PLAYER_A),
        new SetVisibility("RC0", ImmutableList.<String>of(AID)),
        new SetVisibility("RC1", ImmutableList.<String>of(AID)),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "2"),
        new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + "3"),
        new Delete(GameResources.ATTACKER),
        new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + "1"),
        new Delete(GameResources.DEFENDER),
        new Set(GameResources.TURN_ORDER, ImmutableList.<String>of(AID)),
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