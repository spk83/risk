package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class Player {

  private String playerId;
  private List<Integer> cards;
  private int unclaimedUnits;
  private Map<String, Integer> territoryUnitMap;
  private List<String> continent;
 
  @SuppressWarnings("unchecked")
  public Player(String playerId, Map<String, Object> playerMap) {
    this.playerId = playerId;
    this.cards = new ArrayList<Integer>((List<Integer>) playerMap.get(GameResources.CARDS));
    this.unclaimedUnits = (int) playerMap.get(GameResources.UNCLAIMED_UNITS);
    Map<String, Integer> territoryMap = (Map<String, Integer>) playerMap.get(
        GameResources.TERRITORY);
    this.territoryUnitMap = new HashMap<String, Integer>(territoryMap);
    this.continent = Lists.newArrayList((List<String>) playerMap.get(GameResources.CONTINENT));
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

  public List<Integer> getCards() {
    return cards;
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
