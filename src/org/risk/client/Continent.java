package org.risk.client;

import java.util.List;
import java.util.Map;

import sun.management.counter.Units;

import com.google.common.collect.ImmutableMap;

public class Continent {

  private int continentId;
  private String continentName;
  private int unitValue;
  private List<Territory> territoryList;
  
  public static final Map<String, Integer> unitsValue = ImmutableMap.<String, Integer>builder()
      .put("1", 2)
      .put("2", 2)
      .put("3", 2)
      .put("4", 2)
      .put("5", 2)
      .put("6", 2)
      .build();
  
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
