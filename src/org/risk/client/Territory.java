package org.risk.client;

public class Territory {

  private int territoryId;
  private String territoryName;
  private String playerId;
  private int units;
  private String continentId;
  
//  public enum Names { ALASKA, ALBERTA, CENTRAL_AMERICA, EASTERN_UNITED_STATES, GREENLAND, 
//    NORTHWEST_TERRITORY, ONTARIO, QUEBEC, WESTERN_UNITED_STATES,
//    Alberta[note 1]
//    Central America
//    Eastern United States
//    Greenland
//    Northwest Territory
//    Ontario[note 1]
//    Quebec[note 1]
//    Western United States
//    South America (2)
//    Argentina
//    Brazil
//    Peru
//    Venezuela
//    Europe (5)
//    Great Britain[note 1]
//    Iceland
//    Northern Europe
//    Scandinavia
//    Southern Europe
//    Ukraine[note 1]
//    Western Europe
//    Africa (3)
//    Congo[note 1]
//    East Africa
//    Egypt
//    Madagascar
//    North Africa
//    South Africa
//    Asia (7)
//    Afghanistan[note 2]
//    China
//    India[note 1]
//    Irkutsk
//    Japan
//    Kamchatka
//    Middle East
//    Mongolia
//    Siam[note 1]
//    Siberia
//    Ural
//    Yakutsk
//    Australia (2)
//    Eastern Australia
//    Indonesia
//    New Guinea
//    Western Australia }
  
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
  
  public static boolean isAttackPossible(int territory1, int territory2) {
    return true;
  }
  
  public static boolean isFortifyPossible(int territory1, int territory2) {
    return true;
  }
}
