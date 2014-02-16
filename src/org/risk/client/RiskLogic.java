package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetRandomInteger;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.Shuffle;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.VerifyMoveDone;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class RiskLogic {

  
  
  public VerifyMoveDone verify(VerifyMove verifyMove) {
    try {
      checkMoveIsLegal(verifyMove);
      return new VerifyMoveDone();
    } catch (Exception e) {
      return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
    }
    }
  
  void checkMoveIsLegal(VerifyMove verifyMove) {
    List<Operation> lastMove = verifyMove.getLastMove();
    Map<String, Object> lastState = verifyMove.getLastState();
    // Checking the operations are as expected.
    List<Operation> expectedOperations = getExpectedOperations(
        lastState, lastMove, verifyMove.getPlayerIds(), verifyMove.getLastMovePlayerId());
    check(expectedOperations.equals(lastMove), expectedOperations, lastMove);
    // We use SetTurn, so we don't need to check that the correct player did the move.
    // However, we do need to check the first move is done by the white player (and then in the
    // first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
    if (lastState.isEmpty() || lastState.get(GameResources.PHASE).toString().equals(GameResources.SET_TURN_ORDER)) {
      check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
    }
  }

  List<Operation> getExpectedOperations(
      Map<String, Object> lastApiState, List<Operation> lastMove, List<Integer> playerIds,
      int lastMovePlayerId) {
    if (lastApiState.isEmpty()) {
      return getInitialOperations(GameResources.getPlayerKeys(playerIds));
    }
    RiskState lastState = gameApiStateToRiskState(lastApiState, lastMovePlayerId, playerIds);
    if (lastState.getPhase().equals(GameResources.SET_TURN_ORDER)) {
      return setTurnOrderMove(lastState);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private RiskState gameApiStateToRiskState(Map<String, Object> lastApiState,
      int lastMovePlayerId, List<Integer> playerIds) {
   
    RiskState riskState = new RiskState();
    riskState.setTurn(GameResources.playerIdToString(lastMovePlayerId));
    riskState.setPhase(lastApiState.get(GameResources.PHASE).toString());
    
    Map<String, Player> playersMap = new HashMap<String, Player>();
    for (String playerId : GameResources.getPlayerKeys(playerIds)) {
       Map<String, Object> tempPlayersMap = (Map<String, Object>) lastApiState.get(playerId);
       playersMap.put(playerId, new Player(playerId, tempPlayersMap));
    }
    riskState.setPlayersMap(playersMap);

    Map<String, Card> cardsMap = new HashMap<String, Card>();
    for (int i = 0; i <= 43; i++) { 
      Object cardValue = lastApiState.get(GameResources.RISK_CARD + i);
      if ( cardValue != null) {
        cardsMap.put(GameResources.RISK_CARD + i, new Card(cardValue.toString(), GameResources.RISK_CARD + i));
      }
    }
    riskState.setCardMap(cardsMap);
    riskState.setDeck((List<String>) lastApiState.get(GameResources.DECK));
    riskState.setUnclaimedTerritory((List<Integer>) lastApiState.get(GameResources.UNCLAIMED_TERRITORY));
    
    List<String> diceRolls = getDiceRolls(GameResources.getPlayerKeys(playerIds));
    Map<String, List<Integer>> diceResultMap = new HashMap<String, List<Integer>>();
    int j = 0;
    for (String playerId : GameResources.getPlayerKeys(playerIds)) {
      List<Integer> results = Lists.newArrayList();
      for (int i = 0; i < GameResources.TOTAL_INITIAL_DICE_ROLL; i++) {
        results.add(Integer.parseInt(lastApiState.get(diceRolls.get(j)).toString()));
        j++;
      }
      diceResultMap.put(playerId, results);
    }
    riskState.setDiceResult(diceResultMap);
    
    return riskState;
  }

  private List<Operation> setTurnOrderMove(RiskState lastState) {
    List<Operation> turnOrderMove = Lists.newArrayList();
    int nextTurn;
    ImmutableSortedSet<Integer> turnOrder;
    Map<String, List<Integer>> diceResult = lastState.getDiceResult();
    Map<Integer, Integer> result = new HashMap<Integer, Integer>();
    for (Map.Entry<String, List<Integer>> entry : diceResult.entrySet()) {
      int total = 0;
      for (int i : entry.getValue()) {
        total += i;
      }
      result.put(GameResources.playerIdToInt(entry.getKey()), total);
    }
    
    Ordering<Integer> valueComparator = Ordering.natural().onResultOf(Functions.forMap(result)).compound(Ordering.natural());
    ImmutableSortedMap<Integer, Integer> resultMap = ImmutableSortedMap.copyOf(result, valueComparator);
    turnOrder = resultMap.descendingKeySet();
    nextTurn = turnOrder.first();
    turnOrderMove.add(new SetTurn(nextTurn));
    turnOrderMove.add(new Set(GameResources.PHASE, GameResources.CLAIM_TERRITORY));
    turnOrderMove.add(new Set(GameResources.TURN_ORDER, turnOrder.asList()));
    return turnOrderMove;
  }

  public List<Operation> getInitialOperations(List<String> playerIds) {
    List<Operation> operations = Lists.newArrayList();

    // set TURN to first player 
    operations.add(new SetTurn(GameResources.START_PLAYER_ID));
    
    // set PHASE to SET_TURN_ORDER
    operations.add(new Set(GameResources.PHASE, GameResources.SET_TURN_ORDER));
    
    for (String playerId : playerIds) {
      operations.add(new Set(playerId, setInitialPlayerState(playerIds.size())));
    }
    
    // sets all 44 cards: set(RC0,A0),set(RC1,I1),set(RC2,C2),..,set(RC43,W43)
    for (int i = 0; i < 44; i++) {
        operations.add(new Set(GameResources.RISK_CARD + i, GameResources.cardIdToString(i), GameResources.EMPTYLISTINT));
    }
    
    // Shuffle all the RISK cards in the deck
    operations.add(new Shuffle(GameResources.getCardsInRange(0, 43)));
 
    // Add cards to deck
    operations.add(new Set(GameResources.DECK, GameResources.getCardsInRange(0, 43)));
    
    // Create a list of unclaimed territories
    operations.add(new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.getTerritoriesInRange(0,41)));
    
    // Roll dice to decide turn order
    List<String> diceRolls = getDiceRolls(playerIds);
    for (String diceRoll : diceRolls) {
      operations.add(new SetRandomInteger(diceRoll, 1, 7));
    }
    
    return operations;
  }
    
  public static List<String> getDiceRolls(List<String> playerIds){
    List<String> diceRollList = new ArrayList<String>();
    for (String playerId : playerIds) {
      for (int i = 0; i< GameResources.TOTAL_INITIAL_DICE_ROLL; i++) {
        diceRollList.add(GameResources.DICE_ROLL + "_" + playerId + "_" + i);
      }
    }
   return diceRollList;
  }
  
  private static Map<String, Object> setInitialPlayerState(int totalPlayers){
    Map<String, Object> playerState = ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, GameResources.PLAYERS_UNIT_MAP.get(totalPlayers),
        GameResources.TERRITORY, GameResources.EMPTYMAP,
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING);
    return playerState;
  }  
  
  private void check(boolean val, Object... debugArguments) {
    if (!val) {
      throw new RuntimeException("Hacker found");
          //+ Arrays.toString(debugArguments));
    }
  }

}
