package org.risk.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.logic.GameResources;
import org.risk.logic.GameApi.EndGame;
import org.risk.logic.GameApi.Operation;
import org.risk.logic.GameApi.Set;
import org.risk.logic.GameApi.SetTurn;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EndGameTest extends AbstractTest {
  /*
   * This test checks for valid end game scenario
   */
  @Test
  public void testEndGameByC() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.END_GAME)
        .put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 40, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0", "1", "2", "3", "4")))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(CID))
        .put(GameResources.DECK, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(41))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 40)
        .put(GameResources.TERRITORY_WINNER, PLAYER_C)
        .build();
    Map<String, Integer> playerIdToScore = new HashMap<String, Integer>();
    for (String playerId : getPlayerIds()) {
        playerIdToScore.put(playerId, CID.equals(playerId) ? 1 : 0);
    }
    List<Operation> claimEndGameByC = ImmutableList.<Operation>of(
        new SetTurn(CID),
        new EndGame(playerIdToScore),
        new Set(GameResources.PHASE, GameResources.GAME_ENDED));

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
    
    // declaring wrong winner
    List<Operation> invalidWinnerByC = ImmutableList.<Operation>of(
        new EndGame(AID));
    
    // Check invalid move 
    assertHacker(move(CID, state, invalidWinnerByC));
  }
}
