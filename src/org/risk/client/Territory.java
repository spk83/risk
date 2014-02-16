package org.risk.client;

public class Territory {

  private int territoryId;
  private String territoryName;
  private String playerId;
  private int units;
  private String continentId;
  
  public Territory(int territoryId, int units, String playerId) {
    this.territoryId = territoryId;
    this.units = units;
    this.playerId = playerId;
    // TO-DO : get values of territoryname and continentid from static map
  }
  public int getTerritoryId() {
    return territoryId;
  }
  public void setTerritoryId(int territoryId) {
    this.territoryId = territoryId;
  }
  public String getTerritoryName() {
    return territoryName;
  }
  public void setTerritoryName(String territoryName) {
    this.territoryName = territoryName;
  }
  public int getUnits() {
    return units;
  }
  public void setUnits(int units) {
    this.units = units;
  }
  public String getContinent() {
    return continentId;
  }
  public void setContinent(String continent) {
    this.continentId = continent;
  }
  public String getPlayer() {
    return playerId;
  }
  public void setPlayer(String player) {
    this.playerId = player;
  }
}
