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

/**
 * This class provides helper methods and define variables that are used by all the test classes.
 * @author vishal
 *
 */
@RunWith(JUnit4.class)
public class AbstractTest {
  protected static final int TOTAL_TERRITORIES = 42; // Number of territories
  protected static final String PLAYER_ID = "playerId";
  protected static final String TURN_ORDER = "turnOrder";
  protected static final String TURN = "turn";
  protected static final String PHASE = "phase"; // reinforce, attack, fortify
  protected static final String RISK_CARD = "RC";
  protected static final String TERRITORY = "territory";
  protected static final String UNCLAIMED_TERRITORY = "unclaimedTerritory";
  protected static final String CONTINENT = "continent";
  protected static final String UNITS = "units";
  protected static final String ATTACK_TO_TERRITORY = "attackToTerritory";
  protected static final String ATTACK_FROM_TERRITORY = "attackFromTerritory";
  protected static final String DICE_ROLL = "diceRoll";
  protected static final String WINNING_TERRITORY = "winningTerritory";
  protected static final String MOVEMENT_FROM_TERRITORY = "movementFromTerritory";
  protected static final String MOVEMENT_TO_TERRITORY = "movementFromTerritory";
  protected static final String UNITS_FROM_TERRITORY = "unitsFromTerritory";
  protected static final String UNITS_TO_TERRITORY = "unitsFromTerritory";
  protected static final String UNCLAIMED_UNITS = "unclaimedUnits";
  protected static final int TOTAL_PLAYERS = 3; // Number of players playing
  protected static final String CARDS = "cards";
  protected static final String CARDS_TRADED = "cards_traded";
  protected static final String DEPLOYMENT = "deployment";
  protected static final String CLAIM_TERRITORY = "claimTerritory";
  protected static final String CARD_TRADE = "cardTrade";
  protected static final String ATTACK_PHASE = "attackPhase";
  protected static final String FORTIFY = "fortify";
  protected static final String END_GAME = "endGame";
  
  protected static final int AID = 1; // Player A
  protected static final int BID = 2; // Player B
  protected static final int CID = 3; // Player C
  protected static final String PLAYER_A = playerIdToString(AID);
  protected static final String PLAYER_B = playerIdToString(BID);
  protected static final String PLAYER_C = playerIdToString(CID);
  
  protected static final Map<String, Object> AINFO = ImmutableMap.<String, Object>of(
      PLAYER_ID, AID);
  protected static final Map<String, Object> BINFO = ImmutableMap.<String, Object>of(
      PLAYER_ID, BID);
  protected static final Map<String, Object> CINFO = ImmutableMap.<String, Object>of(
      PLAYER_ID, CID);
  protected static final List<Map<String, Object>> PLAYERSINFO = ImmutableList.of(
      AINFO, BINFO, CINFO);
  
  protected static final Map<String, Object> EMPTYSTATE = ImmutableMap.<String, Object>of();
  protected static final Map<String, Object> NONEMPTYSTATE = ImmutableMap.<String, Object>of(
      "k", "v");
  protected static final Map<String, Object> EMPTYMAP = ImmutableMap.<String, Object>of();
  protected static final List<String> EMPTYLISTSTRING = ImmutableList.<String>of();
  protected static final List<Integer> EMPTYLISTINT = ImmutableList.<Integer>of();
  
  /*
   * This method is used to check if verifyMove outcome is valid. 
   */
  protected void assertMoveOk(VerifyMove verifyMove) {
    VerifyMoveDone verifyDone = new RiskLogic().verify(verifyMove);
    assertEquals(new VerifyMoveDone(), verifyDone);
  }

  /*
   * This method is used to check if verifyMove outcome is invalid. 
   */
  protected void assertHacker(VerifyMove verifyMove) {
    VerifyMoveDone verifyDone = new RiskLogic().verify(verifyMove);
    assertEquals(new VerifyMoveDone(verifyMove.getLastMovePlayerId(), "Hacker found"), verifyDone);
  }

  /*
   * This method is used to create object of VerifyMove class.
   */
  protected VerifyMove move(
      int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
    return new VerifyMove(
        AID, PLAYERSINFO, EMPTYSTATE, lastState, lastMove, lastMovePlayerId);
  }

  /*
   * This is a helper method to get list of player IDs.
   */
  protected List<String> getPlayerIds() {
    List<String> playerIds = Lists.newArrayList();
    playerIds.add("P" + AID);
    playerIds.add("P" + BID);
    playerIds.add("P" + CID);
    return playerIds;
  }

  /*
   * Test for getPlayerIds method.
   */
  @Test
  public void testgetPlayersIds() {
    assertEquals(Lists.newArrayList("P1", "P2", "P3"), getPlayerIds());
  }
  
  /*
   * This is a helper method to get risk card value from its ID.
   */
  protected String cardIdToString(int cardId) {
    checkArgument(cardId >= 0 && cardId <= 43);
    int category = cardId % 3;
    String categoryString = cardId > 41 ? "W"
        : category == 1 ? "I"
            : category == 2 ? "C" : "A";
    return categoryString + cardId;
  }
  
  /*
   * Test for cardIdToString.
   */
  @Test
  public void testcardIdToString() {
    assertEquals("I1", cardIdToString(1));
    assertEquals("C8", cardIdToString(8));
    assertEquals("A15", cardIdToString(15));
    assertEquals("W42", cardIdToString(42));
  }
  
  /*
   * This is a helper method to convert player's ID from int to String.
   */
  protected static String playerIdToString(int playerId) {
    return "P" + playerId;
  }
  
  /* 
   * Test for playerIdToString.
   */
  @Test
  public void testplayerIdToString() {
    assertEquals("P1", playerIdToString(AID));
    assertEquals("P2", playerIdToString(BID));
    assertEquals("P3", playerIdToString(CID));
  }

  /*
   * This is a helper method which returns a list of RISK cards of given range.
   */
  protected List<String> getCardsInRange(int fromInclusive, int toInclusive) {
    List<String> keys = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      keys.add(RISK_CARD + i);
    }
    return keys;
  }
  
  /*
   * Test for getCardsInRange method.
   */
  @Test
  public void testgetCardsInRange() {
    assertEquals(Lists.newArrayList("RC0", "RC1", "RC2", "RC3"), getCardsInRange(0, 3));
    assertEquals(Lists.newArrayList("RC41", "RC42", "RC43"), getCardsInRange(41, 43));
  }
  
  /* 
   * This is a helper method to get list of territories for a playerID.
   */
  protected Map<String, Integer> getTerritories(String playerID) {
    Map<String, Integer> territoryMap = new HashMap<String, Integer>();
    switch(playerID) {
    case "P1":
      for (int i = 0; i < 14; i++) {
        territoryMap.put(i + "", 1);
      }
      break;
    case "P2": 
      for (int i = 14; i < 28; i++) {
        territoryMap.put(i + "", 1);
      }
      break;
    case "P3": 
      for (int i = 28; i < 42; i++) {
        territoryMap.put(i + "", 1);
      }
      break;
    default:
    }
    return territoryMap;
  }
  
  /*
   * Test for getTerritories method
   */
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
    
    assertEquals(equalMaps(playerATerritoryMap, getTerritories("P2")), true);
  }
  
  /*
   * Helper method to get Map of territories with specified change.
   */
  protected Map<String, Integer> performDeltaOnTerritory(
      Map<String, Integer> currentMap, String territory, int delta) {
    int oldValue = currentMap.get(territory);
    int newValue = oldValue + delta;
    Map<String, Integer> newMap = new HashMap<String, Integer>();
    newMap.putAll(currentMap);
    newMap.put(territory, newValue);
    return newMap;
  }
  
  /*
   * Test for performDeltaOnTerritory.
   */
  @Test
  public void testperformDeltaOnTerritory() {
    Map<String, Integer> oldTerritory = ImmutableMap.<String, Integer>builder()
        .put("1", 1)
        .put("20", 3)
        .build();
    
    Map<String, Integer> newTerritory = ImmutableMap.<String, Integer>builder()
        .put("1", 3)
        .put("20", 1)
        .build();
        
    assertEquals(newTerritory.get("1"), performDeltaOnTerritory(oldTerritory, "1", 2).get("1"));
    assertEquals(newTerritory.get("20"), performDeltaOnTerritory(oldTerritory, "20", -2)
        .get("20"));    
  }
  
  /*
   * Helper method to get list of territory from given range.
   */
  protected List<Integer> getTerritoriesInRange(int fromInclusive, int toInclusive) {
    List<Integer> listOfTerritories = Lists.newArrayList();
    for (int i = fromInclusive; i <= toInclusive; i++) {
      listOfTerritories.add(i);
    }
    return listOfTerritories;
  }
  
  /*
   * Test the helper method getTerritoriesInRange.
   */
  public void testgetTerritoriesInRange() {
    assertEquals(Lists.newArrayList(0, 1, 2, 3, 4, 5), getTerritoriesInRange(0, 5));
  }
  
  /*
   * Compare two maps
   */
  public boolean equalMaps(Map<String, Integer>m1, Map<String, Integer>m2) {
    if (m1.size() != m2.size()) {
       return false;
    }
    for (String key: m1.keySet()) {
       if (!m1.get(key).equals(m2.get(key))) {
          return false;
       }
    }
    return true;
 }
}
