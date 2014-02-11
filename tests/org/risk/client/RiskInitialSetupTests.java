package org.risk.client;

import java.util.Map;
import java.util.List;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.Shuffle;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class RiskInitialSetupTests extends AbstractTest {
  
  // Create Game States like of following type
  // Can add more parameters to state, this is just for start
  private final Map<String, Object> turnOfA_ClaimTerritory = ImmutableMap.<String, Object>of(
      TURN, aId,
      aId+"", ImmutableMap.of(),
      bId+"", ImmutableMap.of(),
      cId+"", ImmutableMap.of() ); // Start from empty board

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
  
  private List<Operation> getInitialOperations() {
    List<Operation> operations = Lists.newArrayList();

    // Shuffle playerIds and assign that list as turns
    operations.add(new Set(TURN_ORDER, new Shuffle(getPlayerIds())));

    // Assign initial X armies to all the players
    //operations.add(new Set(UNITS, getPlayerIds()));

    // Shuffle all the RISK cards in the deck
    // sets all 44 cards: set(RC1,I1),set(RC2,C2),set(RC3,A3),..,set(RC44,W44)
    for (int i = 1; i <= 44; i++) {
        operations.add(new Set(RISK_CARD + i, cardIdToString(i)));
    }
    // shuffle(RC1,...,RC44)
    operations.add(new Shuffle(getCardsInRange(1, 44)));
    return operations;
  }
  
  // When game starts, assuming player A has to do all the initial settings
  @Test
  public void testInitialMove() {
    assertMoveOk(move(bId, playersInfo, turnOfA_ClaimTerritory, aId, emptyState,
            getInitialOperations()));
    assertMoveOk(move(cId, playersInfo, turnOfA_ClaimTerritory, aId, emptyState,
            getInitialOperations()));
  }

  @Test
  public void testInitialMoveByWrongPlayer() {
    assertHacker(move(aId, playersInfo, nonEmptyState, bId, emptyState, getInitialOperations()));
    assertHacker(move(cId, playersInfo, nonEmptyState, bId, emptyState, getInitialOperations()));
  }

  @Test
  public void testInitialMoveFromNonEmptyState() {
    assertHacker(move(bId, playersInfo, nonEmptyState, aId, nonEmptyState,
            getInitialOperations()));
    assertHacker(move(cId, playersInfo, nonEmptyState, aId, nonEmptyState,
            getInitialOperations()));
  }

  @Test
  public void testInitialMoveWithExtraOperation() {
    List<Operation> initialOperations = getInitialOperations();
    initialOperations.add(new Set(TERRITORY, new Set(PLAYER_ID, 2)));
    assertHacker(move(bId, playersInfo, nonEmptyState, aId, emptyState, initialOperations));
    assertHacker(move(cId, playersInfo, nonEmptyState, aId, emptyState, initialOperations));
  }

  // tests for claiming territories
  @Test
  public void testClaimTerritoryFromEmptyBoard(){
    assertMoveOk(move(bId, playersInfo, turnOfB_ClaimTerritory, aId, turnOfA_ClaimTerritory,
            claimTerritoryByA));
    assertMoveOk(move(cId, playersInfo, turnOfB_ClaimTerritory, aId, turnOfA_ClaimTerritory,
            claimTerritoryByA));

    assertMoveOk(move(aId, playersInfo, turnOfC_ClaimTerritory, bId, turnOfB_ClaimTerritory,
            claimTerritoryByB));
    assertMoveOk(move(cId, playersInfo, turnOfC_ClaimTerritory, bId, turnOfB_ClaimTerritory,
            claimTerritoryByB));

    assertMoveOk(move(aId, playersInfo, stateAfterClaimTerritory, cId, turnOfC_ClaimTerritory,
            claimTerritoryByC));
    assertMoveOk(move(bId, playersInfo, stateAfterClaimTerritory, cId, turnOfC_ClaimTerritory,
            claimTerritoryByC));
  }
  
}