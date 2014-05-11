package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class Player {

  private final Map<Integer, String> playerColorMap = ImmutableMap.<Integer, String>builder()
    .put(0, "lightskyblue")
    .put(1, "lightgreen")
    .put(2, "orange")
    .put(3, "lightcoral")
    .put(4, "lightsalmon")
    .put(5, "khaki")
    .build();
      
  private String playerId;
  private String playerName;
  private List<Integer> cards;
  private int unclaimedUnits;
  private Map<String, Integer> territoryUnitMap;
  private List<String> continent;
  private String playerColor;
  private boolean autoClaim;
  private boolean autoDeploy;  
 
  @SuppressWarnings("unchecked")
  public Player(String playerId, String playerName, Map<String, Object> playerMap, int colorIndex, 
      boolean autoClaim, boolean autoDeploy) {
    this.playerId = playerId;
    this.playerName = playerName;
    this.playerColor = playerColorMap.get(colorIndex);
    this.cards = new ArrayList<Integer>((List<Integer>) playerMap.get(GameResources.CARDS));
    this.unclaimedUnits = (Integer) playerMap.get(GameResources.UNCLAIMED_UNITS);
    Map<String, Integer> territoryMap = (Map<String, Integer>) playerMap.get(
        GameResources.TERRITORY);
    this.territoryUnitMap = new HashMap<String, Integer>(territoryMap);
    this.continent = Lists.newArrayList((List<String>) playerMap.get(GameResources.CONTINENT));
    this.autoClaim = autoClaim;
    this.autoDeploy = autoDeploy;
  }
  
  public String getPlayerName() {
    return playerName;
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

  public String getPlayerColor() {
    return playerColor;
  }

  public boolean isAutoClaim() {
    return autoClaim;
  }

  public boolean isAutoDeploy() {
    return autoDeploy;
  }

  public void setAutoClaim(boolean autoClaim) {
    this.autoClaim = autoClaim;
  }

  public void setAutoDeploy(boolean autoDeploy) {
    this.autoDeploy = autoDeploy;
  }
}
