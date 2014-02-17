package org.risk.client;

import java.util.List;
import java.util.Map;

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
}
