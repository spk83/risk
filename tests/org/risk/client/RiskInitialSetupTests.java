package org.risk.client;

import java.util.Map;
import java.util.List;

import org.junit.Test;
import org.risk.client.GameApi.Delete;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetVisibility;
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
    Map<String, Object> turnOfA_ClaimTerritory = ImmutableMap.<String, Object>of(
            turn, aId,
            territory, ImmutableList.of() ); // Start from empty board

    // Create state after A has claimed #1 territory 
    Map<String, Object> turnOfB_ClaimTerritory = ImmutableMap.<String, Object>of(
            turn, bId,
            territory_delta, ImmutableList.of(new Set(1+"", new Set(playerId, 1))) );

    // Create state after B has claimed #2 territory 
    Map<String, Object> turnOfC_ClaimTerritory = ImmutableMap.<String, Object>of(
            turn, bId,
            territory_delta, ImmutableList.of(new Set(2+"", new Set(playerId, 1))) );

    // Write operations to be performed
    final List<Operation> claimTerritoryByA = ImmutableList.<Operation>of(
            new Set(turn, aId),
            new Set(claimTerritory, 1));
        
    final List<Operation> claimTerritoryByB = ImmutableList.<Operation>of(
            new Set(turn, bId),
            new Set(claimTerritory, 2));
    
    final List<Operation> claimTerritoryByC = ImmutableList.<Operation>of(
            new Set(turn, bId),
            new Set(claimTerritory, 3));
    
    private List<Operation> getInitialOperations() {
        List<Operation> operations = Lists.newArrayList();

        // Shuffle playerIds and assign that list as turns
        operations.add(new Set(turn, new Shuffle(getPlayerIds())));

        // Assign initial X armies to all the players
        operations.add(new Set(units, getPlayerIds()));

        // Shuffle all the RISK cards in the deck
        // sets all 44 cards: set(RC1,I1),set(RC2,C2),set(RC3,A3),..,set(RC44,W44)
        for (int i = 1; i <= 44; i++) {
            operations.add(new Set(C + i, cardIdToString(i)));
        }
        // shuffle(RC1,...,RC44)
        operations.add(new Shuffle(getCardsInRange(1, 44)));
        return operations;
    }
    
    // When game starts, assuming player A has to do all the initial settings
    @Test
    public void testInitialMove() {
        assertMoveOk(move(aId, emptyState, getInitialOperations()));
    }

    @Test
    public void testInitialMoveByWrongPlayer() {
        assertHacker(move(bId, emptyState, getInitialOperations()));
    }

    @Test
    public void testInitialMoveFromNonEmptyState() {
        assertHacker(move(aId, nonEmptyState, getInitialOperations()));
    }

    @Test
    public void testInitialMoveWithExtraOperation() {
        List<Operation> initialOperations = getInitialOperations();
        initialOperations.add(new Set(territory, new Set(playerId, 2)));
        assertHacker(move(aId, emptyState, initialOperations));
    }

    // tests for claiming territories
    @Test
    public void testClaimTerritoryByAFromEmptyBoard(){
        assertMoveOk(move(aId, turnOfA_ClaimTerritory, claimTerritoryByA));
        assertMoveOk(move(bId, turnOfB_ClaimTerritory, claimTerritoryByB));
        assertMoveOk(move(cId, turnOfC_ClaimTerritory, claimTerritoryByC));
    }
 }
