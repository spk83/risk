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
        .put(GameResources.PHASE, GameResources.END_GAME)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 41),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID))
        .put(GameResources.DECK, getCardsInRange(0, 43))
        .build();
        
    List<Operation> claimEndGameByC = ImmutableList.<Operation>of(
        new EndGame(CID));

    assertMoveOk(move(CID, state, claimEndGameByC));
    
    // Check invalid operations - wrong turn, from invalid states
    assertHacker(move(AID, state, claimEndGameByC));
    assertHacker(move(CID, GameResources.EMPTYSTATE, claimEndGameByC));
    assertHacker(move(CID, GameResources.NONEMPTYSTATE, claimEndGameByC));
    
    List<Operation> invalidClaimEndGameByC = ImmutableList.<Operation>of(
        new EndGame(CID),
        new Set(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 41),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)));
    
    // Check invalid move 
    assertHacker(move(CID, state, invalidClaimEndGameByC));
  }
}
