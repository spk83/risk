package org.risk.client;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.VerifyMoveDone;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class AbstractTest {
  protected static final int TOTAL_TERRITORIES = 42;
  protected static final String PLAYER_ID = "playerId";
  protected static final String TURN_ORDER = "turnOrder"; // turn of which player (either A or B)
  protected static final String TURN = "turn"; // turn of which player (either A or B)
  protected static final String PHASE = "phase"; // Reinforcement or Attack or Fortify
  protected static final String RISK_CARD = "RC"; // RISK Card key (1 ..44)
  protected static final String ATTACK_ARMY = "attackArmy"; // Size of attack army
  protected static final String DEFENCE_ARMY = "defenceArmy"; // Size of defence army
  protected static final String TERRITORY = "territory"; // (1..42)
  protected static final String UNCLAIMED_TERRITORY = "unclaimedTerritory"; // (1..42)
  protected static final String TERRITORY_DELTA = "territoryDelta";
  protected static final String CONTINENT = "continent"; // (1..6)
  protected static final String UNITS = "units"; // Units of armies assigned
  protected static final String ATTACK_TO_TERRITORY = "attackToTerritory";
  protected static final String ATTACK_FROM_TERRITORY = "attackFromTerritory";
  protected static final String DICE_ROLL = "diceRoll";
  protected static final String WINNING_TERRITORY = "winningTerritory";
  protected static final String MOVEMENT_FROM_TERRITORY = "movementFromTerritory";
  protected static final String MOVEMENT_TO_TERRITORY = "movementFromTerritory";
  protected static final String UNITS_FROM_TERRITORY = "unitsFromTerritory";
  protected static final String UNITS_TO_TERRITORY = "unitsFromTerritory";
  protected static final String UNCLAIMED_UNITS = "unclaimedUnits";
  protected static final int TOTAL_PLAYERS = 3;
  protected static final String PLAYERS = "players";
  protected static final String CARDS = "cards";
  protected static final String BOARD = "board";
  protected static final String CARDS_TRADED = "cards_traded";
  protected static final String DEPLOYMENT = "deployment";
  protected static final String CLAIM_TERRITORY = "claimTerritory";
  protected static final String CARD_TRADE = "cardTrade";
  
  protected static final int TOTAL_UNITS = GameConstants.PLAYERS_UNIT_MAP.get(TOTAL_PLAYERS);
  protected static final int aId = 1; // Player A
  protected static final int bId = 2; // Player B
  protected static final int cId = 3; // Player C
  protected static final String PLAYER_A = playerIdToString(aId);
  protected static final String PLAYER_B = playerIdToString(bId);
  protected static final String PLAYER_C = playerIdToString(cId);
  
  protected static final Map<String, Object> aInfo = ImmutableMap.<String, Object>of(PLAYER_ID, aId);
  protected static final Map<String, Object> bInfo = ImmutableMap.<String, Object>of(PLAYER_ID, bId);
  protected static final Map<String, Object> cInfo = ImmutableMap.<String, Object>of(PLAYER_ID, cId);
  protected static final List<Map<String, Object>> playersInfo = ImmutableList.of(aInfo, bInfo,cInfo);
  
  protected static final Map<String, Object> emptyState = ImmutableMap.<String, Object>of();
  protected static final Map<String, Object> nonEmptyState = ImmutableMap.<String, Object>of("k", "v");
  
  protected static final Map<String, Object> emptyMap = ImmutableMap.<String, Object>of();
  protected static final List<String> emptyListString = ImmutableList.<String>of();
  protected static final List<Integer> emptyListInt = ImmutableList.<Integer>of();
  
  protected void assertMoveOk(VerifyMove verifyMove) {
    VerifyMoveDone verifyDone = new RiskLogic().verify(verifyMove);
    assertEquals(new VerifyMoveDone(), verifyDone);
  }

  protected void assertHacker(VerifyMove verifyMove) {
    VerifyMoveDone verifyDone = new RiskLogic().verify(verifyMove);
    assertEquals(new VerifyMoveDone(verifyMove.getLastMovePlayerId(), "Hacker found"), verifyDone);
  }

  protected VerifyMove move(
      int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
    return new VerifyMove(
        aId, playersInfo, emptyState, lastState, lastMove, 
        lastMovePlayerId);
  }

  protected List<String> getPlayerIds() {
    List<String> playerIds = Lists.newArrayList();
    playerIds.add("P"+aId);
    playerIds.add("P"+bId);
    playerIds.add("P"+cId);
    return playerIds;
  }

  @Test
  public void testgetPlayersIds(){
    assertEquals(Lists.newArrayList("P1", "P2", "P3"), getPlayerIds());
  }
  
  protected String cardIdToString(int cardId) {
    checkArgument(cardId >= 0 && cardId <= 43);
    int category = cardId % 3;
    String categoryString = cardId > 41 ? "W"
        : category == 1 ? "I"
            : category == 2 ? "C" : "A";
    return categoryString + cardId;
  }
  
  @Test
  public void testcardIdToString(){
    assertEquals("I1", cardIdToString(1));
    assertEquals("C8", cardIdToString(8));
    assertEquals("A15", cardIdToString(15));
    assertEquals("W42", cardIdToString(42));
  }
  
  protected static String playerIdToString(int playerId) {
    return "P"+playerId;
  }
  
  @Test
  public void testplayerIdToString(){
    assertEquals("P1", playerIdToString(aId));
    assertEquals("P2", playerIdToString(bId));
    assertEquals("P3", playerIdToString(cId));
  }

  protected List<String> getCardsInRange(int fromInclusive, int toInclusive) {
    List<String> keys = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      keys.add(RISK_CARD + i);
    }
    return keys;
  }
  
  @Test
  public void testgetCardsInRange(){
    assertEquals(Lists.newArrayList("RC0","RC1","RC2","RC3"), getCardsInRange(0, 3));
    assertEquals(Lists.newArrayList("RC41","RC42","RC43"), getCardsInRange(41, 43));
  }
  
  protected Map<String, Integer> getTerritories(String playerID) {
    Map<String, Integer> territoryMap = new HashMap<String, Integer>();
    int territory = 0;
    switch(playerID){
    case "P1": 
      for(int i = 0; i < 14; i++) {
        territoryMap.put(territory++ + "", 1);
      }
    case "P2": 
      for(int i = 0; i < 14; i++) {
        territoryMap.put(territory++ + "", 1);
      }
    case "P3": 
      for(int i = 0; i < 14; i++) {
        territoryMap.put(territory++ + "", 1);
      }
    }
    return territoryMap;
  }
  
  @Test
  public void testgetTerritories() {
    Map<String, Integer> playerATerritoryMap = ImmutableMap.<String, Integer>builder()
        .put("14", 1)
        .put("15", 1)
        .put("16", 1)
        .put("17", 1)
        .put("18", 1)
        .put("19", 1)
        .put("20", 1)
        .put("21", 1)
        .put("22", 1)
        .put("23", 1)
        .put("24", 1)
        .put("25", 1)
        .put("26", 1)
        .put("27", 1)
        .build();
    
    assertEquals(playerATerritoryMap, getTerritories("P2"));
  }
}
