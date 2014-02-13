package org.risk.client;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.Shuffle;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class InitialSetupTest extends AbstractTest {
  
  private List<Operation> getInitialOperations() {
    List<Operation> operations = Lists.newArrayList();

    // Shuffle playerIds, assign that list as turnOrder
    List<String> turnOrder = new Shuffle(getPlayerIds()).getKeys();
    operations.add(new Set(TURN_ORDER, new Shuffle(getPlayerIds())));
    
    // set TURN to first from that shuffled list
    operations.add(new Set(TURN, turnOrder.get(0)));
    
    // Assign initial army units to all the players
    operations.add(new Set(UNITS, assignInitialUnits()));

    // sets all 44 cards: set(RC0,A0),set(RC1,I1),set(RC2,C2),..,set(RC43,W43)
    for (int i = 0; i < 44; i++) {
        operations.add(new Set(RISK_CARD + i, cardIdToString(i)));
    }
    
    // Shuffle all the RISK cards in the deck
    operations.add(new Shuffle(getCardsInRange(0, 43)));
    return operations;
  }
  
  private Map<Integer, Integer> assignInitialUnits() {
    int initialNumberOfUnits = GameConstants.getInitialNumberOfUnits(getPlayerIds().size());
    Map<Integer, Integer> assignUnits = new HashMap<Integer, Integer>();
    
    for(String playerId: getPlayerIds()) {
      assignUnits.put(Integer.parseInt(playerId), initialNumberOfUnits);
    }
    return assignUnits;
  }
  
  @Test
  public void testassignInitialUnits() {
    assertEquals(ImmutableMap.<Integer, Integer>of(
        aId, 35,
        bId, 35,
        cId, 35), assignInitialUnits());
  }
  
  @Test
  public void testgetInitialOperations(){
    assertEquals( 1 + 1 + 1 + 44 + 1, getInitialOperations().size());
  }
  
  @Test
  public void testInitialSetup(){
    List<Operation> initialOperations = getInitialOperations();
    
    assertMoveOk(move(aId, emptyState, initialOperations));
    assertHacker(move(bId, emptyState, initialOperations));
    assertHacker(move(aId, nonEmptyState, initialOperations));
    assertHacker(move(cId, nonEmptyState, initialOperations));
    
    initialOperations.add(new Set(TERRITORY, new Set(PLAYER_ID, 2)));
    assertHacker(move(aId, emptyState, initialOperations));
    assertHacker(move(bId, emptyState, initialOperations));
  }
 
}