package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class RiskState {
  
  private String turn;
  private String phase;
  private List<String> deck;
  private List<Integer> unclaimedTerritory;
  private Map<String, Player> playersMap;
  private List<Integer> turnOrder;
  private Map<String, List<Integer>> diceResult;
  private List<String> cardsVisibleToAll;
  private Map<String, Card> cardMap;
  private int tradeNumber;
  private List<Integer> cardsTraded;
  private Attack attack;
  private Integer lastAttackingTerritory;
  private String territoryWinner;
  private int continuousTrade;
  
  public int getContinuousTrade() {
    return continuousTrade;
  }
  public void setContinuousTrade(int continuousTrade) {
    this.continuousTrade = continuousTrade;
  }
  public String getTerritoryWinner() {
    return territoryWinner;
  }
  public void setTerritoryWinner(String territoryWinner) {
    this.territoryWinner = territoryWinner;
  }
  public Integer getLastAttackingTerritory() {
    return lastAttackingTerritory;
  }
  public void setLastAttackingTerritory(Integer lastAttackingTerritory) {
    this.lastAttackingTerritory = lastAttackingTerritory;
  }
  public Attack getAttack() {
    return attack;
  }
  public void setAttack(Attack attack) {
    this.attack = attack;
  }
  public String getTurn() {
    return turn;
  }
  public void setTurn(String turn) {
    this.turn = turn;
  }
  public String getPhase() {
    return phase;
  }
  public void setPhase(String phase) {
    this.phase = phase;
  }
  public List<String> getDeck() {
    return deck;
  }
  public void setDeck(List<String> deck) {
    this.deck = deck;
  }
  public List<Integer> getUnclaimedTerritory() {
    return unclaimedTerritory;
  }
  public void setUnclaimedTerritory(List<Integer> unclaimedTerritory) {
    this.unclaimedTerritory = unclaimedTerritory;
  }
  public Map<String, Player> getPlayersMap() {
    return playersMap;
  }
  public void setPlayersMap(Map<String, Player> playersMap) {
    this.playersMap = playersMap;
  }
  public List<Integer> getTurnOrder() {
    return turnOrder;
  }
  public void setTurnOrder(List<Integer> turnOrder) {
    this.turnOrder = turnOrder;
  }
  public Map<String, List<Integer>> getDiceResult() {
    return diceResult;
  }
  public void setDiceResult(Map<String, List<Integer>> diceResult) {
    this.diceResult = diceResult;
  }
  public List<String> getCardsVisibleToAll() {
    return cardsVisibleToAll;
  }
  public void setCardsVisibleToAll(List<String> cardsVisibleToAll) {
    this.cardsVisibleToAll = cardsVisibleToAll;
  }
  public Map<String, Card> getCardMap() {
    return cardMap;
  }
  public void setCardMap(Map<String, Card> cardMap) {
    this.cardMap = cardMap;
  }
  public int getTradeNumber() {
    return tradeNumber;
  }
  public void setTradeNumber(int tradeNumber) {
    this.tradeNumber = tradeNumber;
  }
  public List<Integer> getCardsTraded() {
    return cardsTraded;
  }
  public void setCardsTraded(List<Integer> cardsTraded) {
    this.cardsTraded = cardsTraded;
  }
  
  @SuppressWarnings("unchecked")
  public static RiskState gameApiStateToRiskState(Map<String, Object> lastApiState,
      int lastMovePlayerId, List<Integer> playerIds) {
   
    RiskState riskState = new RiskState();
    riskState.setTurn(GameResources.playerIdToString(lastMovePlayerId));
    riskState.setPhase(lastApiState.get(GameResources.PHASE).toString());
    
    Map<String, Player> playersMap = new HashMap<String, Player>();
    for (String playerId : GameResources.getPlayerKeys(playerIds)) {
       Map<String, Object> tempPlayersMap = (Map<String, Object>) lastApiState.get(playerId);
       if (tempPlayersMap != null) {
         playersMap.put(playerId, new Player(playerId, tempPlayersMap));         
       }
    }
    riskState.setPlayersMap(playersMap);
    Map<String, Card> cardsMap = new HashMap<String, Card>();
    for (int i = 0; i <= 43; i++) { 
      Object cardValue = lastApiState.get(GameResources.RISK_CARD + i);
      if ( cardValue != null) {
        cardsMap.put(
            GameResources.RISK_CARD + i, 
            new Card(cardValue.toString(), 
            GameResources.RISK_CARD + i));
      }
    }
    Integer tradeNumber = (Integer)lastApiState.get(GameResources.TRADE_NUMBER);
    if (tradeNumber == null) {
      tradeNumber = 0;
    }
    riskState.setTradeNumber(tradeNumber.intValue() + 1);
    riskState.setCardMap(cardsMap);
    riskState.setDeck((List<String>) lastApiState.get(GameResources.DECK));
    riskState.setUnclaimedTerritory((List<Integer>) lastApiState.get(
        GameResources.UNCLAIMED_TERRITORY));
    
    List<String> diceRolls = GameResources.getDiceRollKeys(GameResources.getPlayerKeys(playerIds));
    Map<String, List<Integer>> diceResultMap = new HashMap<String, List<Integer>>();
    int j = 0;
    for (String playerId : GameResources.getPlayerKeys(playerIds)) {
      List<Integer> results = Lists.newArrayList();
      for (int i = 0; i < GameResources.TOTAL_INITIAL_DICE_ROLL; i++) {
        Object tempDiceRoll = lastApiState.get(diceRolls.get(j));
        if (tempDiceRoll != null) {
          results.add(Integer.parseInt(tempDiceRoll.toString()));
          j++;
        }
      }
      diceResultMap.put(playerId, results);
    }
    riskState.setDiceResult(diceResultMap);
    
    if( lastApiState.get(GameResources.TURN_ORDER) != null ) {
      riskState.setTurnOrder(new ArrayList<Integer>
          ((List<Integer>) lastApiState.get(GameResources.TURN_ORDER)));
    }
    riskState.setCardsTraded((List<Integer>) lastApiState.get(GameResources.CARDS_BEING_TRADED));
    Map<String, Object> attacker = (Map<String, Object>) lastApiState.get(GameResources.ATTACKER);
    Map<String, Object> defender = (Map<String, Object>) lastApiState.get(GameResources.DEFENDER);
    if (attacker != null && defender != null) {
      List<Integer> attackerDiceRolls = 
          GameResources.getDiceRolls(lastApiState, GameResources.ATTACKER);
      List<Integer> defenderDiceRolls = 
          GameResources.getDiceRolls(lastApiState, GameResources.DEFENDER);
      riskState.setAttack(new Attack(attacker, defender, attackerDiceRolls, defenderDiceRolls));
    }
    Integer lastAttackingTerritory = (Integer)lastApiState.get(
        GameResources.LAST_ATTACKING_TERRITORY);
    riskState.setLastAttackingTerritory(lastAttackingTerritory);
    
    String territoryWinner = (String) lastApiState.get(GameResources.TERRITORY_WINNER);    
    riskState.setTerritoryWinner(territoryWinner);
    
    Integer continuousTrade = (Integer) lastApiState.get(GameResources.CONTINUOUS_TRADE);
    if (continuousTrade != null) {
      riskState.setContinuousTrade(continuousTrade);
    }
    return riskState;
  }
}
