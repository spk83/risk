package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.EndGame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class checks for endgame operations.
 * @author vishal
 *
 */
public class EndGameTest extends AbstractTest {
  /*
   * This test checks for valid end game scenario
   */
  @Test
  public void testEndGameByC() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(TURN, PLAYER_C)
        .put(PHASE, END_GAME)
        .put(PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, emptyMap,
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, getTerritoriesInRange(0, 41),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString))
        .put(TURN_ORDER, ImmutableList.<String>of(PLAYER_C, PLAYER_A))
        .put(CARDS, getCardsInRange(0, 43))
        .build();
        
    List<Operation> claimEndGameByC = ImmutableList.<Operation>of(
        new EndGame(cId));

    assertMoveOk(move(cId, state, claimEndGameByC));
    
    // Check invalid operations - wrong turn, from invalid states
    assertHacker(move(aId, state, claimEndGameByC));
    assertHacker(move(cId, emptyState, claimEndGameByC));
    assertHacker(move(cId, nonEmptyState, claimEndGameByC));
    
    List<Operation> invalidClaimEndGameByC = ImmutableList.<Operation>of(
        new EndGame(cId),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            CARDS, emptyListInt,
            TERRITORY, getTerritoriesInRange(0, 41),
            UNCLAIMED_UNITS, 0,
            CONTINENT, emptyListString)));
    
    // Check invalid move 
    assertHacker(move(cId, state, invalidClaimEndGameByC));
  }
}
