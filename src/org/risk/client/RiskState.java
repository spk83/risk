package org.risk.client;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

public class RiskState {
  
  private int turn;
  private String phase;
  private List<String> deck;
  private List<Integer> unclaimedTerritory;
  private Map<String, Player> playersMap;
  private Map<String, Territory> territoryMap;
  private List<Integer> turnOrder;
  private Map<String, List<Integer>> diceResult;
  private List<String> cardsVisibleToAll;
  private Map<String, Card> cardMap;
  private int tradeNumber;
  private Optional<List<Integer>> cardsTraded;
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
  public int getTurn() {
    return turn;
  }
  public void setTurn(int turn) {
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
  public Optional<List<Integer>> getCardsTraded() {
    return cardsTraded;
  }
  public void setCardsTraded(Optional<List<Integer>> cardsTraded) {
    this.cardsTraded = cardsTraded;
  }
  
  public Map<String, Territory> getTerritoryMap() {
    return territoryMap;
  }
  public void setTerritoryMap(Map<String, Territory> territoryMap) {
    this.territoryMap = territoryMap;
  }
}
