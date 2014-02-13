package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DeploymentPhaseTest extends AbstractTest{
  Map<String, Object> initialState = ImmutableMap.<String, Object>of(
      TURN, aId+"",
      PLAYERS, ImmutableMap.<String, Object>of(
          PLAYER_A, ImmutableMap.<String, Object>of(
              CARDS, emptyListInt,
              TERRITORY, emptyMap,
              CONTINENT, emptyListString),
          PLAYER_B, ImmutableMap.<String, Object>of(
              CARDS, emptyListInt,
              TERRITORY, emptyMap,
              CONTINENT, emptyListString),
          PLAYER_C, ImmutableMap.<String, Object>of(
              CARDS, emptyListInt,
              TERRITORY, emptyMap,
              CONTINENT, emptyListString),
      BOARD, ImmutableMap.<String, Object>of(
          CARDS, emptyListString)));
  
  // Create state after A has claimed #1 territory 
  private final Map<String, Object> turnOfB_ClaimTerritory = ImmutableMap.<String, Object>of(
      TURN, bId,
      aId+"", ImmutableMap.<String, Object>of(
          TERRITORY, ImmutableMap.<String, Object>of(
              1 + "", new Set(UNITS, 1))),
      bId+"", ImmutableMap.of(),
      cId+"", ImmutableMap.of() );

  // Create state after B has claimed #2 territory 
  Map<String, Object> turnOfC_ClaimTerritory = ImmutableMap.<String, Object>of(
      TURN, cId,
      aId+"", ImmutableMap.<String, Object>of(
          TERRITORY, ImmutableMap.<String, Object>of(
              1 + "", new Set(UNITS, 1))),
      bId+"", ImmutableMap.<String, Object>of(
          TERRITORY, ImmutableMap.<String, Object>of(
              2 + "", new Set(UNITS, 1))),
      cId+"", ImmutableMap.of() );

  Map<String, Object> stateAfterClaimTerritory = ImmutableMap.<String, Object>of(
      TURN, aId,
      aId+"", ImmutableMap.<String, Object>of(
          TERRITORY, ImmutableMap.<String, Object>of(
              1 + "", new Set(UNITS, 1))),
      bId+"", ImmutableMap.<String, Object>of(
          TERRITORY, ImmutableMap.<String, Object>of(
              2 + "", new Set(UNITS, 1))),
      cId+"", ImmutableMap.<String, Object>of(
          TERRITORY, ImmutableMap.<String, Object>of(
              3 + "", new Set(UNITS, 1))) );
  
  // Write operations to be performed for doing initial setup
  final List<Operation> claimTerritoryByA = ImmutableList.<Operation>of(
      new Set(TURN, bId),
      new Set(CLAIM_TERRITORY, 1));
      
  final List<Operation> claimTerritoryByB = ImmutableList.<Operation>of(
      new Set(TURN, cId),
      new Set(CLAIM_TERRITORY, 2));
  
  final List<Operation> claimTerritoryByC = ImmutableList.<Operation>of(
      new Set(TURN, aId),
      new Set(CLAIM_TERRITORY, 3));
  
  
  
 

  @Test
  public void testInitialMoveWithExtraOperation() {
    List<Operation> initialOperations = getInitialOperations();
    initialOperations.add(new Set(TERRITORY, new Set(PLAYER_ID, 2)));
    assertHacker(move(aId, emptyState, initialOperations));
  }

  // tests for claiming territories
  @Test
  public void testClaimTerritoryFromEmptyBoard(){
    assertMoveOk(move(aId, turnOfA_ClaimTerritory,
            claimTerritoryByA));
    assertMoveOk(move(aId, turnOfA_ClaimTerritory,
            claimTerritoryByA));

    assertMoveOk(move(bId, turnOfB_ClaimTerritory,
            claimTerritoryByB));
    assertMoveOk(move(bId, turnOfB_ClaimTerritory,
            claimTerritoryByB));

    assertMoveOk(move(cId, turnOfC_ClaimTerritory,
            claimTerritoryByC));
    assertMoveOk(move(cId, turnOfC_ClaimTerritory,
            claimTerritoryByC));
  }
 
}
