package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

  private String playerId;
  private List<Integer> cards;
  private int unclaimedUnits;
  private Map<String, Integer> territoryUnitMap;
  private List<String> continent;
 
  @SuppressWarnings("unchecked")
  public Player(String playerId, Map<String, Object> playerMap) {
    this.playerId = playerId;
    this.cards = new ArrayList<Integer>((List<Integer>)playerMap.get(GameResources.CARDS));
    this.unclaimedUnits = (int) playerMap.get(GameResources.UNCLAIMED_UNITS);
    Map<String, Integer> territoryMap = (Map<String, Integer>) playerMap.get(GameResources.TERRITORY);
    this.territoryUnitMap = new HashMap<String, Integer>();
    for (Map.Entry<String, Integer> entry : territoryMap.entrySet()) {
      this.territoryUnitMap.put(entry.getKey(), entry.getValue());
    }
    this.continent = (List<String>) playerMap.get(GameResources.CONTINENT);
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

  public Map<String, Integer> getTerritoryUnitMap() {
    return territoryUnitMap;
  }

  public void setTerritoryUnitMap(Map<String, Integer> territoryUnitMap) {
    this.territoryUnitMap = territoryUnitMap;
  }

  public List<String> getContinent() {
    return continent;
  }

  public void setContinent(List<String> continent) {
    this.continent = continent;
  }
  
}
