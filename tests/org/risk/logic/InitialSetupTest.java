package org.risk.logic;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.logic.GameResources;
import org.risk.logic.RiskLogic;
import org.risk.logic.GameApi.Delete;
import org.risk.logic.GameApi.Operation;
import org.risk.logic.GameApi.Set;
import org.risk.logic.GameApi.SetTurn;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class InitialSetupTest extends AbstractTest {
  
  private final RiskLogic riskLogic = new RiskLogic();
 
  /*
   * Test the helper method getInitialOperations
   */
  @Test
  public void testgetInitialOperations() {
    assertEquals(1 + 1 + 3 + 44 + 1 + 1 + 1 + 9, riskLogic.getInitialOperations(
        getPlayerIds(), AID).size());
  }
  
  /*
   * Test the initial operations performed by player
   * Detect if there is any hacker move
   * Assume PLAYER_A will always do the initial operations
   */
  @Test
  public void testInitialSetup() {
    List<Operation> initialOperations = riskLogic.getInitialOperations(getPlayerIds(), AID);

    // Check valid move
    assertMoveOk(move(AID, GameResources.EMPTYSTATE, initialOperations));
    
    // Check invalid moves - turn by wrong player, from invalid state, with additional operation
    assertHacker(move(BID, GameResources.EMPTYSTATE, initialOperations));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, initialOperations));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, initialOperations));
     
    Builder<String, Object> stateBuilder = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.SET_TURN_ORDER)
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
        .put(GameResources.DECK, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, getTerritoriesInRange(0, 41));
    
    List<Integer> diceValues = Lists.newArrayList(2, 3, 1, 4, 2, 4, 5, 2, 4);
    int i = 0;
    for (String diceRoll : GameResources.getDiceRollKeys(getPlayerIds())) {
      stateBuilder.put(diceRoll, diceValues.get(i++));
    }
    
    Map<String, Object> state = stateBuilder.build();
    
    List<Operation> setupTurnOrder = Lists.newArrayList();
    setupTurnOrder.add(new SetTurn(CID));
    List<String> deleteKeys = GameResources.getDiceRollKeys(
        ImmutableList.<String>of(AID, BID, CID));
    for (String deleteKey : deleteKeys) {
      setupTurnOrder.add(new Delete(deleteKey));
    }
    setupTurnOrder.add(new Set(GameResources.PHASE, GameResources.CLAIM_TERRITORY));
    setupTurnOrder.add(new Set(GameResources.TURN_ORDER, ImmutableList.<String>of(CID, BID, AID)));
    
    // Check valid move
    assertMoveOk(move(AID, state, setupTurnOrder));
    
    // Check invalid moves - turn by wrong player, from invalid state, with additional operation
    assertHacker(move(BID, state, setupTurnOrder));
    assertHacker(move(AID, GameResources.EMPTYSTATE, setupTurnOrder));
    assertHacker(move(AID, GameResources.NONEMPTYSTATE, setupTurnOrder));
  }
}