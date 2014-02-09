package org.risk.client;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.VerifyMoveDone;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class AbstractTest {
    final int aId = 1; // Player A
    final int bId = 2; // Player B
    final int cId = 3; // Player C
    final int reinforcement = 0; // Reinforcement Phase
    final int attack = 1; // Attack Phase
    final int fortify = 2; // Fortify Phase
    final String playerId = "playerId";
    final String turn = "turn"; // turn of which player (either A or B)
    final String phase = "phase"; // Reinforcement or Attack or Fortify
    final String C = "RC"; // RISK Card key (1 ..44)
    final String attackArmy = "attackArmy"; // Size of attack army
    final String defenceArmy = "defenceArmy"; // Size of defence army
    final String territory = "territory"; // (1..42)
    final String territory_delta = "delta";
    final String claimTerritory = "claim";
    final String continent = "continent"; // (1..6)
    final String units = "units"; // Units of armies assigned
    final Map<String, Object> aInfo = ImmutableMap.<String, Object>of(playerId, aId);
    final Map<String, Object> bInfo = ImmutableMap.<String, Object>of(playerId, bId);
    final Map<String, Object> cInfo = ImmutableMap.<String, Object>of(playerId, cId);
    final List<Map<String, Object>> playersInfo = ImmutableList.of(aInfo, bInfo,cInfo);
    final Map<String, Object> emptyState = ImmutableMap.<String, Object>of();
    final Map<String, Object> nonEmptyState = ImmutableMap.<String, Object>of("k", "v");


    protected void assertMoveOk(List<VerifyMove> verifyMoveList) {
        for(VerifyMove verifyMove : verifyMoveList){
            VerifyMoveDone verifyDone = new RiskLogic().verify(verifyMove);
            assertEquals(new VerifyMoveDone(), verifyDone);
        }
    }

    protected void assertHacker(List<VerifyMove> verifyMoveList) {
        for(VerifyMove verifyMove : verifyMoveList){
            VerifyMoveDone verifyDone = new RiskLogic().verify(verifyMove);
            assertEquals(new VerifyMoveDone(
                    verifyMove.getLastMovePlayerId(), "Hacker found"), verifyDone);
        }
    }
    protected List<VerifyMove> move(
            int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
        List<VerifyMove> verifyMoveList = Lists.newArrayList();

        for(String playerId : getPlayerIds()){
            // Not required to send it to lastMovePlayerId
            if( lastMovePlayerId != Integer.parseInt(playerId))
                verifyMoveList.add(new VerifyMove(
                        Integer.parseInt(playerId), playersInfo, emptyState, lastState, 
                        lastMove, lastMovePlayerId));
        }
        return verifyMoveList;
    }

    protected List<String> getPlayerIds() {
        List<String> playerIds = Lists.newArrayList();
        playerIds.add(aId+"");
        playerIds.add(bId+"");
        playerIds.add(cId+"");
        return playerIds;
    }

    protected String cardIdToString(int cardId) {
        checkArgument(cardId >= 1 && cardId <= 44);
        int category = cardId % 3;
        String categoryString = cardId > 42 ? "W"
                : category == 1 ? "I"
                        : category == 2 ? "C" : "A";
        return categoryString + cardId;
    }

    protected List<String> getCardsInRange(int fromInclusive, int toInclusive) {
        List<String> keys = Lists.newArrayList();
        for (int i = fromInclusive; i <= toInclusive; i++) {
            keys.add(C + i);
        }
        return keys;
    }



}
