package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetTurn;

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
  public void testClaimTerritoryFirstTurn() {
    
    List<Integer> unclaimedTerritories = getTerritoriesInRange(0, 41);
    Map<String, Object> stateTurn1 = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CLAIM_TERRITORY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.CARDS, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, unclaimedTerritories)
        .build();

    List<Integer> newUnclaimedTerritories = Lists.newArrayList(unclaimedTerritories);
    newUnclaimedTerritories.remove(30);
    List<Operation> claimTerritoryByC = Lists.newArrayList();
    claimTerritoryByC.add(new SetTurn(BID));
    claimTerritoryByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 34,
        GameResources.TERRITORY, ImmutableMap.<String, Integer>of("30", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    claimTerritoryByC.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritories));
    
    // Check valid move
    assertMoveOk(move(CID, stateTurn1, claimTerritoryByC));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(BID, stateTurn1, claimTerritoryByC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, claimTerritoryByC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, claimTerritoryByC));
    
    // Check if hacker has added any extra operations
    claimTerritoryByC.remove(claimTerritoryByC.size() - 1);
    claimTerritoryByC.remove(claimTerritoryByC.size() - 2);
    newUnclaimedTerritories.remove(32);
    claimTerritoryByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 35,
        GameResources.TERRITORY, ImmutableMap.<String, Integer>of("30", 1, "32", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    assertHacker(move(CID, stateTurn1, claimTerritoryByC));
    claimTerritoryByC.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritories));
    
    
    // Check if hacker is adding units more than allowed in single move
    claimTerritoryByC.remove(claimTerritoryByC.size() - 1);
    claimTerritoryByC.remove(claimTerritoryByC.size() - 2);
    newUnclaimedTerritories.add(30);
    claimTerritoryByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 35,
        GameResources.TERRITORY, ImmutableMap.<String, Integer>of("32", 2),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    assertHacker(move(CID, stateTurn1, claimTerritoryByC));
    claimTerritoryByC.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritories));
  }
  

  @Test
  public void testClaimTerritoryNextTurn() {
    
    List<Integer> unclaimedTerritory = getTerritoriesInRange(0, 41);
    unclaimedTerritory.remove(30);
    
    Map<String, Object> stateTurn2 = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CLAIM_TERRITORY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 34,
            GameResources.TERRITORY, ImmutableMap.<String, Integer>of("30", 1),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.CARDS, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, unclaimedTerritory)
        .build();
    
    List<Integer> newUnclaimedTerritory = Lists.newArrayList(unclaimedTerritory); 
    newUnclaimedTerritory.remove(20);
    List<Operation> claimTerritoryByB = Lists.newArrayList();
    claimTerritoryByB.add(new SetTurn(AID));
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 34,
        GameResources.TERRITORY, ImmutableMap.<String, Object>of("20", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    claimTerritoryByB.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritory));

    // Check valid moves
    assertMoveOk(move(BID, stateTurn2, claimTerritoryByB));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(CID, stateTurn2, claimTerritoryByB));
    assertHacker(move(BID, GameResources.EMPTYSTATE, claimTerritoryByB));
    assertHacker(move(BID, GameResources.NONEMPTYSTATE, claimTerritoryByB));
    
    // Check if hacker has added any extra operations
    claimTerritoryByB.remove(claimTerritoryByB.size() - 1);
    claimTerritoryByB.remove(claimTerritoryByB.size() - 2);
    newUnclaimedTerritory.remove(14);
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 34,
        GameResources.TERRITORY, ImmutableMap.<String, Object>of("20", 1, "14", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    claimTerritoryByB.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritory));
    assertHacker(move(BID, stateTurn2, claimTerritoryByB));
    
    // Check if hacker is claiming already claimed territory
    claimTerritoryByB.remove(claimTerritoryByB.size() - 1);
    claimTerritoryByB.remove(claimTerritoryByB.size() - 2);
    newUnclaimedTerritory.add(14);
    newUnclaimedTerritory.add(20);
    newUnclaimedTerritory.remove(30);
    
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 34,
        GameResources.TERRITORY, ImmutableMap.<String, Object>of("30", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    claimTerritoryByB.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritory));
    assertHacker(move(BID, stateTurn2, claimTerritoryByB));
   
    // Check if hacker is adding units more than allowed in single move
    claimTerritoryByB.remove(claimTerritoryByB.size() - 1);
    claimTerritoryByB.remove(claimTerritoryByB.size() - 2);
    newUnclaimedTerritory.add(30);
    newUnclaimedTerritory.remove(20);
    claimTerritoryByB.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 34,
        GameResources.TERRITORY, ImmutableMap.<String, Object>of("20", 2),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    claimTerritoryByB.add(new Set(GameResources.UNCLAIMED_TERRITORY, newUnclaimedTerritory));
    assertHacker(move(BID, stateTurn2, claimTerritoryByB));
  }
  
  @Test
  public void testClaimTerritoryLastTurn() {
    Map<String, Integer> territoryMap = getTerritories(PLAYER_A);
    territoryMap.remove("13");
    Map<String, Object> stateTurnLast = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CLAIM_TERRITORY)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 22,
            GameResources.TERRITORY, territoryMap,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 21,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 21,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.CARDS, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13))
        .build();
    
    List<Operation> changePhase = Lists.newArrayList();
    changePhase.add(new SetTurn(CID));
    changePhase.add(new Set(PLAYER_A, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 21,
        GameResources.TERRITORY, getTerritories(PLAYER_A),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    changePhase.add(new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT));
    changePhase.add(new Set(GameResources.PHASE, GameResources.DEPLOYMENT));
    
    // Check if valid move
    assertMoveOk(move(AID, stateTurnLast, changePhase));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(BID, stateTurnLast, changePhase));
    assertHacker(move(AID, GameResources.EMPTYSTATE, changePhase));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, changePhase));
    
    // Check if hacker has modified its state
    changePhase.remove(changePhase.size() - 3);
    changePhase.add(1, new Set(PLAYER_A, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 35,
        GameResources.TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_A), "10", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    assertHacker(move(AID, stateTurnLast, changePhase));
  }
  

  /*
   * Test the operations performed in deployment phase of the game
   */
  @Test
  public void testDeploymentFirstTurn() {
    Map<String, Object> stateTurn1 = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.DEPLOYMENT)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 21,
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 21,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 21,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.DECK, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();

    List<Operation> deploymentByC = Lists.newArrayList();
    deploymentByC.add(new SetTurn(BID));
    deploymentByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 20,
        GameResources.TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    
    // Check valid move
    assertMoveOk(move(CID, stateTurn1, deploymentByC));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(BID, stateTurn1, deploymentByC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, deploymentByC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, deploymentByC));
     
    // Check if hacker is adding units more than allowed in single move
    deploymentByC.remove(deploymentByC.size() - 1);
    deploymentByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 18,
        GameResources.TERRITORY, performDeltaOnTerritory(
            performDeltaOnTerritory(getTerritories(PLAYER_C), "29", 1), "32", 2),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    
    assertHacker(move(CID, stateTurn1, deploymentByC));
    
    // Check if hacker is adding units on territory not held by him
    deploymentByC.remove(deploymentByC.size() - 1);
    deploymentByC.add(new Set(PLAYER_C, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 20,
        GameResources.TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_C), "20", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));

    assertHacker(move(CID, stateTurn1, deploymentByC));
  }
  
  @Test
  public void testDeploymentLastTurn(){
    Map<String, Object> stateTurnLast = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.DEPLOYMENT)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 1,
            GameResources.TERRITORY, getTerritories(PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories(PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID))
        .put(GameResources.CARDS, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
    List<Operation> changePhase = Lists.newArrayList();
    changePhase.add(new SetTurn(CID));
    changePhase.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_B), "19", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));  
    changePhase.add(new Set(GameResources.PHASE, GameResources.CARD_TRADE));
    
    // Check valid move
    assertMoveOk(move(BID, stateTurnLast, changePhase));
    
    // Check invalid moves, by wrong player, from invalid states
    assertHacker(move(CID, stateTurnLast, changePhase));
    assertHacker(move(BID, GameResources.EMPTYSTATE, changePhase));
    assertHacker(move(BID, GameResources.NONEMPTYSTATE, changePhase));
    
    // Check if hacker has modified any operation
    changePhase.add(new Set(PLAYER_B, ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, 2,
        GameResources.TERRITORY, performDeltaOnTerritory(getTerritories(PLAYER_B), "20", 1),
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    assertHacker(move(BID, stateTurnLast, changePhase));
  }
}
