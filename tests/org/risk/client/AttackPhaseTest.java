package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class AttackPhaseTest extends AbstractTest {
  
  //creating objects for initial territories of players
  private static final Map<String, Object> initialTerritoriesOfA =ImmutableMap.<String, Object>of();
  private static final Map<String, Object> initialTerritoriesOfB =ImmutableMap.<String, Object>of();
  private static final Map<String, Object> initialTerritoriesOfC =ImmutableMap.<String, Object>of();
  private static final Map<String, Object> finalTerritoriesOfA =ImmutableMap.<String, Object>of();
  private static final Map<String, Object> finalTerritoriesOfB =ImmutableMap.<String, Object>of();
  private static final Map<String, Object> finalTerritoriesOfC =ImmutableMap.<String, Object>of();
  private static final int attackFromTerritory = 1;
  private static final int attackToTerritory = 17;
  private static final int unitsOnAttackingTeritory = 6;
  
  //Game states for attack phase
  private final Map<String, Object> stateBeforeAttackByA = ImmutableMap.<String, Object>of(
      TURN, aId,
      aId+"", initialTerritoriesOfA,
      bId+"", initialTerritoriesOfB,
      cId+"", initialTerritoriesOfC);
  
  private final Map<String, Object> stateAfterAttackByA = ImmutableMap.<String, Object>of(
      TURN, aId,
      aId+"", initialTerritoriesOfA,
      bId+"", finalTerritoriesOfB,
      cId+"", initialTerritoriesOfC,
      WINNING_TERRITORY, attackToTerritory );
  
  private final Map<String, Object> stateAfterOccupancyByA = ImmutableMap.<String, Object>of(
      TURN, aId,
      aId+"", finalTerritoriesOfA,
      bId+"", finalTerritoriesOfB,
      cId+"", initialTerritoriesOfC,
      WINNING_TERRITORY, attackToTerritory );
  
  private final List<Operation> attackOperationsOfAOnB = ImmutableList.<Operation>of(
      new Set(TURN, aId),
      new Set(ATTACK_TO_TERRITORY, attackToTerritory),
      new Set(ATTACK_FROM_TERRITORY, attackFromTerritory),
      new Set(DICE_ROLL, ImmutableMap.<String, Object>of(
          aId+"", ImmutableList.<Integer>of(5, 6, 6),
          bId+"", ImmutableList.<Integer>of(4))));
  
  //Game operations in attack phase
  private final List<Operation> occupancyOperationsOfA = ImmutableList.<Operation>of(
      new Set(TURN, aId),
      new Set(MOVEMENT_FROM_TERRITORY, attackFromTerritory),
      new Set(MOVEMENT_TO_TERRITORY, attackToTerritory),
      new Set(UNITS_FROM_TERRITORY, 4),
      new Set(UNITS_TO_TERRITORY, 2));
  
  @BeforeClass
  private static void setup() {
    int i = 1;
    int territoriesPerPerson = TOTAL_TERRITORIES/3; 
    for( ; i <= territoriesPerPerson; ++i ) {
      initialTerritoriesOfA.put(i + "", new Set(UNITS, 1));
    }
    for( ; i <= territoriesPerPerson * 2; ++i ) {
      initialTerritoriesOfB.put(i + "", new Set(UNITS, 1));
    }
    for( ; i <= territoriesPerPerson * 3; ++i ) {
      initialTerritoriesOfC.put(i + "", new Set(UNITS, 1));
    }
    initialTerritoriesOfA.put(attackFromTerritory + "", new Set(UNITS, unitsOnAttackingTeritory));
    
    finalTerritoriesOfB.putAll(initialTerritoriesOfB);
    finalTerritoriesOfB.remove(attackToTerritory);
    
    finalTerritoriesOfA.putAll(initialTerritoriesOfA);
    finalTerritoriesOfA.put(attackToTerritory+"", new Set(UNITS, 4));
    finalTerritoriesOfA.put(attackFromTerritory+"", new Set(UNITS, 2));
  }
  
  @Test
  private void testAttackOfAOnB_AWins() {
    assertMoveOk(move
        (cId, playersInfo, stateAfterAttackByA, aId, stateBeforeAttackByA, attackOperationsOfAOnB));
    assertMoveOk(move
        (bId, playersInfo, stateAfterAttackByA, aId, stateBeforeAttackByA, attackOperationsOfAOnB));
  }
  
  @Test
  private void testOccupancyOfA_OnWinningTerritory() {
    assertMoveOk(move(
        cId, playersInfo, stateAfterOccupancyByA,
        aId, stateAfterAttackByA, occupancyOperationsOfA));
    assertMoveOk(move(
        bId, playersInfo, stateAfterOccupancyByA,
        aId, stateAfterAttackByA, occupancyOperationsOfA));
  }
}