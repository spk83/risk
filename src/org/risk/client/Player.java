package org.risk.client;

import java.util.List;
import java.util.Map;

public class Player {

  private String playerId;
  private List<Integer> cards;
  private int unclaimedUnits;
  private Map<Integer, Territory> territoryMap;
  //private List<Continent> continent;
 
  @SuppressWarnings("unchecked")
  public Player(String playerId, Map<String, Object> playerMap) {
    this.playerId = playerId;
    this.cards = (List<Integer>) playerMap.get(GameResources.CARDS);
    this.unclaimedUnits = (int) playerMap.get(GameResources.UNCLAIMED_UNITS);
    Map<String, Integer> territoryMap = (Map<String, Integer>) playerMap.get(GameResources.TERRITORY);
    for (Map.Entry<String, Integer> entry : territoryMap.entrySet()) {
      int territoryId = Integer.parseInt(entry.getKey());
      int units = entry.getValue();
      this.territoryMap.put(territoryId, new Territory(territoryId, units, playerId));
    }
  }
  
  public int getUnclaimedUnits() {
    return unclaimedUnits;
  }
  public void setUnclaimedUnits(int unclaimedUnits) {
    this.unclaimedUnits = unclaimedUnits;
  }

  public String getPlayerId() {
    return playerId;
  }

  public void setPlayerId(String playerId) {
    this.playerId = playerId;
  }

  public List<Integer> getCards() {
    return cards;
  }

  public void setCards(List<Integer> cards) {
    this.cards = cards;
  }

  public Map<Integer, Territory> getTerritoryMap() {
    return territoryMap;
  }

  public void setTerritoryMap(Map<Integer, Territory> territoryMap) {
    this.territoryMap = territoryMap;
  }
 
  
}
