package org.risk.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * This class test the operations performed in the deployment phase of the game.
 * @author vishal
 *
 */
public class DeploymentPhaseTest extends AbstractTest {
  
  /*
   * Test the operations performed in claimTerritory phase of the game
   */
  @Test
  public void testClaimTerritory() {
    Map<String, Object> stateTurn1 = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, CLAIM_TERRITORY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 35,
            TERRITORY, emptyMap,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 35,
            TERRITORY, emptyMap,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 35,
            TERRITORY, emptyMap,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(0, 43))
        .put(UNCLAIMED_TERRITORY, getTerritoriesInRange(0, 41))
        .build();

    List<Operation> claimTerritoryByC = Lists.newArrayList();
    claimTerritoryByC.add(new Set(TURN, PLAYER_B));
    claimTerritoryByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 35,
        TERRITORY, ImmutableMap.<String, Object>of("30", 1),
        CONTINENT, emptyListString)));
    
    // Check valid move
    assertMoveOk(move(cId, stateTurn1, claimTerritoryByC));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(bId, stateTurn1, claimTerritoryByC));
    assertHacker(move(cId, emptyState, claimTerritoryByC));
    assertHacker(move(cId, nonEmptyState, claimTerritoryByC));
    
    // Check if hacker has added any extra operations
    claimTerritoryByC.remove(claimTerritoryByC.size() - 1);
    claimTerritoryByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 35,
        TERRITORY, ImmutableMap.<String, Object>of("30", 1,"32",1),
        CONTINENT, emptyListString)));
    assertHacker(move(cId, stateTurn1, claimTerritoryByC));
    
    // Check if hacker is adding units more than allowed in single move
    claimTerritoryByC.remove(claimTerritoryByC.size() - 1);
    claimTerritoryByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 35,
        TERRITORY, ImmutableMap.<String, Object>of("32", 2),
        CONTINENT, emptyListString)));
    assertHacker(move(cId, stateTurn1, claimTerritoryByC));
    
    Map<String, Object> stateTurn2 = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_B)
        .put(PHASE, CLAIM_TERRITORY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 35,
            TERRITORY, emptyMap,
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 35,
            TERRITORY, emptyMap,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 34,
            TERRITORY, ImmutableMap.<String, Object>of("30", 1),
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(0, 43))
        .put(UNCLAIMED_TERRITORY, getTerritoriesInRange(0, 41).remove(30))
        .build();
    
    List<Operation> claimTerritoryByB = Lists.newArrayList();
    claimTerritoryByB.add(new Set(TURN, PLAYER_A));
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 34,
        TERRITORY, ImmutableMap.<String, Object>of("20", 1),
        CONTINENT, emptyListString)));
    
    // Check valid moves
    assertMoveOk(move(bId, stateTurn2, claimTerritoryByB));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(cId, stateTurn2, claimTerritoryByB));
    assertHacker(move(bId, emptyState, claimTerritoryByB));
    assertHacker(move(bId, nonEmptyState, claimTerritoryByB));
    
    // Check if hacker has added any extra operations
    claimTerritoryByB.remove(claimTerritoryByB.size() - 1);
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 34,
        TERRITORY, ImmutableMap.<String, Object>of("20", 1, "14", 1),
        CONTINENT, emptyListString)));
    assertHacker(move(bId, stateTurn2, claimTerritoryByB));
    
    // Check if hacker is claiming already claimed territory
    claimTerritoryByB.remove(claimTerritoryByB.size() - 1);
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 34,
        TERRITORY, ImmutableMap.<String, Object>of("30", 1),
        CONTINENT, emptyListString)));
    assertHacker(move(bId, stateTurn2, claimTerritoryByB));
   
    // Check if hacker is adding units more than allowed in single move
    claimTerritoryByB.remove(claimTerritoryByB.size() - 1);
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 34,
        TERRITORY, ImmutableMap.<String, Object>of("20", 2),
        CONTINENT, emptyListString)));
    assertHacker(move(bId, stateTurn2, claimTerritoryByB));
    
    Map<String, Object> stateTurnLast = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_A)
        .put(PHASE, CLAIM_TERRITORY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 21,
            TERRITORY, getTerritories(PLAYER_A),
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 21,
            TERRITORY, getTerritories(PLAYER_B),
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 21,
            TERRITORY, getTerritories(PLAYER_C),
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(0, 43))
        .put(UNCLAIMED_TERRITORY, emptyListInt)
        .build();
    
    List<Operation> changePhase = Lists.newArrayList();
    changePhase.add(new Set(PHASE, DEPLOYMENT));
    changePhase.add(new Set(TURN, PLAYER_C));
    
    // Check if valid move
    assertMoveOk(move(aId, stateTurnLast, changePhase));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(bId, stateTurnLast, changePhase));
    assertHacker(move(aId, emptyState, changePhase));
    assertHacker(move(aId, nonEmptyState, changePhase));
    
    // Check if hacker has added any extra operations
    changePhase.add(new Set(PLAYER_A, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 21,
        TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_A), "10", 1),
        CONTINENT, emptyListString)));
    assertHacker(move(aId, stateTurnLast, changePhase));
  }
  
  /*
   * Helper method to get list of territory from given range
   */
  private List<Integer> getTerritoriesInRange(int fromInclusive, int toInclusive) {
    List<Integer> listOfTerritories = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      listOfTerritories.add(i);
    }
    return listOfTerritories;
  }
  
  /*
   * Test the helper method getTerritoriesInRange
   */
  public void testgetTerritoriesInRange() {
    assertEquals(Lists.newArrayList(0, 1, 2, 3, 4, 5), getTerritoriesInRange(0, 5));
  }

  /*
   * Test the operations performed in deployment phase of the game
   */
  public void testDeployment() {
    Map<String, Object> stateTurn1 = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, DEPLOYMENT)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 21,
            TERRITORY, getTerritories(PLAYER_A),
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 21,
            TERRITORY, getTerritories(PLAYER_B),
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 21,
            TERRITORY, getTerritories(PLAYER_C),
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(0, 43))
        .put(UNCLAIMED_TERRITORY, emptyListInt)
        .build();

    List<Operation> deploymentByC = Lists.newArrayList();
    deploymentByC.add(new Set(TURN, PLAYER_B));
    deploymentByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 21,
        TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 1),
        CONTINENT, emptyListString)));
    
    // Check valid move
    assertMoveOk(move(cId, stateTurn1, deploymentByC));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(bId, stateTurn1, deploymentByC));
    assertHacker(move(cId, emptyState, deploymentByC));
    assertHacker(move(cId, nonEmptyState, deploymentByC));
     
    // Check if hacker is adding units more than allowed in single move
    deploymentByC.remove(deploymentByC.size() - 1);
    deploymentByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 21,
        TERRITORY, performDeltaOnTerritory(
            performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 1), "32", 2),
        CONTINENT, emptyListString)));
    
    assertHacker(move(cId, stateTurn1, deploymentByC));
    
    // Check if hacker is adding units on territory not held by him
    deploymentByC.remove(deploymentByC.size() - 1);
    deploymentByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 21,
        TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_C), "20", 1),
        CONTINENT, emptyListString)));

    assertHacker(move(cId, stateTurn1, deploymentByC));
    
    Map<String, Object> stateTurnLast = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_B)
        .put(PHASE, DEPLOYMENT)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 0,
            TERRITORY, getTerritories(PLAYER_A),
            CONTINENT, emptyListString))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 0,
            TERRITORY, getTerritories(PLAYER_B),
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            UNCLAIMED_UNITS, 0,
            TERRITORY, getTerritories(PLAYER_C),
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_B, PLAYER_A))
        .put(CARDS, getCardsInRange(0, 43))
        .put(UNCLAIMED_TERRITORY, emptyListInt)
        .build();
    
    List<Operation> changePhase = Lists.newArrayList();
    changePhase.add(new Set(PHASE, CARD_TRADE));
    changePhase.add(new Set(TURN, PLAYER_C));
    
    // Check valid move
    assertMoveOk(move(bId, stateTurnLast, changePhase));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(cId, stateTurnLast, changePhase));
    assertHacker(move(bId, emptyState, changePhase));
    assertHacker(move(bId, nonEmptyState, changePhase));
    
    // Check if hacker has added any extra operations
    changePhase.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        CARDS, emptyListInt,
        UNCLAIMED_UNITS, 21,
        TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_B), "20", 1),
        CONTINENT, emptyListString)));
    assertHacker(move(bId, stateTurnLast, changePhase));
  }
}
