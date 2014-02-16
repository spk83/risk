package org.risk.client;

import java.util.List;

public class Continent {

  private int continentId;
  private String continentName;
  private int unitValue;
  private List<Territory> territoryList;
  public int getContinentId() {
    return continentId;
  }
  public void setContinentId(int continentId) {
    this.continentId = continentId;
  }
  public String getContinentName() {
    return continentName;
  }
  public void setContinentName(String continentName) {
    this.continentName = continentName;
  }
  public int getUnitValue() {
    return unitValue;
  }
  public void setUnitValue(int unitValue) {
    this.unitValue = unitValue;
  }
  public List<Territory> getTerritoryList() {
    return territoryList;
  }
  public void setTerritoryList(List<Territory> territoryList) {
    this.territoryList = territoryList;
  }
  
  
}
