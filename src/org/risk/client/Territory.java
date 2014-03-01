package org.risk.client;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Territory {
  
  private String territoryId;
  private String playerKey;
  
  public String getTerritoryId() {
    return territoryId;
  }

  public void setTerritoryId(String territoryId) {
    this.territoryId = territoryId;
  }

  public String getPlayerKey() {
    return playerKey;
  }

  public void setPlayerKey(String playerKey) {
    this.playerKey = playerKey;
  }

  public Territory(String territoryId, String playerKey) {
    this.territoryId = territoryId;
    this.playerKey = playerKey;
  }
  
  public static final Map<Integer, String> TERRITORY_NAME = ImmutableMap.<Integer, String>builder()
      .put(0, "Alaska")
      .put(1, "North West Territory")
      .put(2, "Alberta")
      .put(3, "Western United States")
      .put(4, "Central America")
      .put(5, "Greenland")
      .put(6, "Ontario")
      .put(7, "Quebec")
      .put(8, "Eastern United States")
      .put(9, "Venezuela")
      .put(10, "Peru")
      .put(11, "Brazil")
      .put(12, "Argentina")
      .put(13, "Iceland")
      .put(14, "Scandinavia")
      .put(15, "Ukraine")
      .put(16, "Great Britain")
      .put(17, "Northern Europe")
      .put(18, "Western Europe")
      .put(19, "Southern Europe")
      .put(20, "North Africa")
      .put(21, "Egypt")
      .put(22, "Congo")
      .put(23, "East Africa")
      .put(24, "South Africa")
      .put(25, "Madagascar")
      .put(26, "Siberia")
      .put(27, "Ural")
      .put(28, "China")
      .put(29, "Afghanistan")
      .put(30, "Middle East")
      .put(31, "India")
      .put(32, "Siam")
      .put(33, "Yakutsk")
      .put(34, "Irkutsk")
      .put(35, "Mongolia")
      .put(36, "Japan")
      .put(37, "Kamchatka")
      .put(38, "Indonesia")
      .put(39, "New Guinea")
      .put(40, "Western Australia")
      .put(41, "Eastern Australia")
      .build();
  
  public static final Map<Integer, String> COORDINATES = ImmutableMap.<Integer, String>builder()
      .put(0, "22,78,29,83,13,85,17,96,28,97,26,103,14,102,10,109,11,117,16,123,19,132,25,132,29,"
          + "141,20,150,26,154,47,133,50,137,64,125,77,133,89,144,95,160,105,151,95,141,99,131,93,"
          + "120,82,116,87,88,89,73,59,61,46,63,35,74")
      .put(1, "238,46,209,60,210,77,203,69,196,78,178,72,172,83,160,80,148,72,134,62,116,72,101,"
          + "72,88,70,85,95,85,110,93,118,130,117,198,120,208,122,229,107,237,93,251,84,243,71,"
          + "232,80,225,60")
      .put(2, "101,123,128,123,178,126,176,147,175,189,108,187,109,177,96,162,109,151,102,141,104,"
          + "130,100,123")
      .put(3, "113,194,133,197,155,196,175,197,189,197,192,227,187,243,175,248,172,253,164,261,"
          + "161,272,156,277,156,286,150,281,142,277,137,276,130,274,124,270,117,265,112,264,106,"
          + "254,103,244,106,216")
      .put(4, "112,271,125,278,131,283,145,283,152,288,160,292,164,303,163,313,165,326,178,326,184,"
          + "315,188,318,191,319,192,326,190,329,183,334,193,344,187,352,180,364,194,366,182,380,"
          + "171,371,167,354,158,349,160,340,141,332,142,320,115,278,129,315,128,320,111,285")
      .put(5, "314,29,340,11,353,13,373,15,377,18,366,21,374,28,396,21,387,36,389,66,382,73,391,82,"
          + "383,80,373,75,381,87,361,96,344,108,331,131,333,133,331,141,324,141,309,128,310,114,"
          + "306,105,313,97,308,90,311,83,288,59,270,64,273,58,272,50,280,49,282,41,292,38,295,31,"
          + "301,27,310,32,312,30")
      .put(6, "183,125,199,126,205,128,206,142,217,154,224,156,237,161,239,176,238,199,238,206,228,"
          + "197,224,190,216,188,211,191,181,188,179,171")
      .put(7, "256,137,257,128,259,118,278,120,282,126,282,139,289,141,294,132,298,132,305,152,312,"
          + "159,320,172,316,179,302,187,290,190,288,194,292,201,291,208,300,209,296,217,288,230,"
          + "285,230,286,209,279,200,271,202,265,212,254,210,246,201,242,203,245,179,248,163,257,"
          + "149,255,139")
      .put(8, "203,195,194,191,198,238,194,245,178,251,175,258,170,260,168,269,163,282,169,298,185,"
          + "287,201,287,218,291,225,288,229,310,239,303,234,288,242,275,258,262,252,249,272,228,"
          + "279,219,279,206,272,208,266,217,249,213,239,213,226,204,219,195")
      .put(9, "187,399,190,386,188,381,198,370,208,360,218,356,239,367,258,365,278,386,300,387,288,"
          + "397,272,399,260,390,260,387,250,386,251,392,240,392,243,403,231,399,223,396,218,402,"
          + "221,415,216,419,197,410")
      .put(10, "181,401,211,419,195,436,204,443,210,450,218,445,219,448,212,453,220,459,237,452,"
          + "257,467,261,464,274,484,274,495,281,502,287,514,277,523,277,510,264,508,258,498,247,"
          + "499,235,496,235,501,186,455,184,444,173,432,173,415,175,408")
      .put(11, "227,415,223,401,235,406,245,407,247,402,247,397,253,397,256,393,260,401,274,408,"
          + "288,403,298,394,300,393,314,410,326,411,332,415,346,415,367,432,369,442,359,450,347,"
          + "458,349,477,336,501,314,507,309,521,309,530,299,549,293,538,286,528,296,516,285,497,"
          + "276,493,279,478,267,466,252,454,245,452,231,444,222,453,222,443,216,440,212,442,204,"
          + "436,213,424,225,423,227,416")
      .put(12, "Argentina")
      .put(13, "Iceland")
      .put(14, "Scandinavia")
      .put(15, "Ukraine")
      .put(16, "Great Britain")
      .put(17, "Northern Europe")
      .put(18, "Western Europe")
      .put(19, "Southern Europe")
      .put(20, "North Africa")
      .put(21, "Egypt")
      .put(22, "Congo")
      .put(23, "East Africa")
      .put(24, "South Africa")
      .put(25, "Madagascar")
      .put(26, "Siberia")
      .put(27, "Ural")
      .put(28, "China")
      .put(29, "Afghanistan")
      .put(30, "Middle East")
      .put(31, "India")
      .put(32, "Siam")
      .put(33, "Yakutsk")
      .put(34, "Irkutsk")
      .put(35, "Mongolia")
      .put(36, "Japan")
      .put(37, "Kamchatka")
      .put(38, "Indonesia")
      .put(39, "New Guinea")
      .put(40, "Western Australia")
      .put(41, "Eastern Australia")
      .build();

  public static String getContinentId(int territoryId) {
    if (territoryId >= 0 && territoryId <= 8) {
      return "0";
    } else if (territoryId >= 9 && territoryId <= 12) {
      return "1";      
    } else if (territoryId >= 13 && territoryId <= 19) {
      return "2";      
    } else if (territoryId >= 20 && territoryId <= 25) {
      return "3";      
    } else if (territoryId >= 26 && territoryId <= 37) {
      return "4";      
    } else if (territoryId >= 38 && territoryId <= 41) {
      return "5";      
    } else {
      throw new IllegalArgumentException("Invalide territory Id");
    }
  }
  
  public static final Map<Integer, List<Integer>> CONNECTIONS
    = ImmutableMap.<Integer, List<Integer>>builder()
      .put(0, ImmutableList.<Integer>of(1, 2, 37))
      .put(1, ImmutableList.<Integer>of(0, 2, 6, 5))
      .put(2, ImmutableList.<Integer>of(0, 1, 6, 4))
      .put(3, ImmutableList.<Integer>of(2, 6, 4, 8))
      .put(4, ImmutableList.<Integer>of(3, 8, 9))
      .put(5, ImmutableList.<Integer>of(1, 6, 7, 13))
      .put(6, ImmutableList.<Integer>of(1, 2, 3, 5, 7, 8))
      .put(7, ImmutableList.<Integer>of(5, 6, 8))
      .put(8, ImmutableList.<Integer>of(3, 4, 6, 7))
      .put(9, ImmutableList.<Integer>of(4, 10, 11))
      .put(10, ImmutableList.<Integer>of(9, 11, 12))
      .put(11, ImmutableList.<Integer>of(9, 10, 12, 20))
      .put(12, ImmutableList.<Integer>of(10, 11))
      .put(13, ImmutableList.<Integer>of(5, 16, 15))
      .put(14, ImmutableList.<Integer>of(15, 17, 13, 16))
      .put(15, ImmutableList.<Integer>of(14, 27, 29, 30, 17, 19))
      .put(16, ImmutableList.<Integer>of(13, 17, 18, 14))
      .put(17, ImmutableList.<Integer>of(14, 15, 16, 18, 19))
      .put(18, ImmutableList.<Integer>of(16, 17, 19, 20))
      .put(19, ImmutableList.<Integer>of(15, 17, 18, 20, 21, 30))
      .put(20, ImmutableList.<Integer>of(11, 18, 19, 21, 22, 23))
      .put(21, ImmutableList.<Integer>of(19, 20, 23, 30))
      .put(22, ImmutableList.<Integer>of(20, 23, 24))
      .put(23, ImmutableList.<Integer>of(20, 21, 22, 24, 25))
      .put(24, ImmutableList.<Integer>of(22, 23, 25))
      .put(25, ImmutableList.<Integer>of(23, 24))
      .put(26, ImmutableList.<Integer>of(27, 28, 33, 34, 35))
      .put(27, ImmutableList.<Integer>of(15, 26, 28, 29))
      .put(28, ImmutableList.<Integer>of(26, 27, 29, 31, 32, 35))
      .put(29, ImmutableList.<Integer>of(15, 27, 28, 30, 31))
      .put(30, ImmutableList.<Integer>of(15, 21, 29, 31, 19))
      .put(31, ImmutableList.<Integer>of(28, 29, 30, 32))
      .put(32, ImmutableList.<Integer>of(28, 31, 38))
      .put(33, ImmutableList.<Integer>of(26, 34, 37))
      .put(34, ImmutableList.<Integer>of(26, 33, 35, 37))
      .put(35, ImmutableList.<Integer>of(28, 34, 36, 37, 26))
      .put(36, ImmutableList.<Integer>of(35, 37))
      .put(37, ImmutableList.<Integer>of(0, 33, 34, 35, 36))
      .put(38, ImmutableList.<Integer>of(32, 40, 39))
      .put(39, ImmutableList.<Integer>of(38, 40, 41))
      .put(40, ImmutableList.<Integer>of(38, 39, 41))
      .put(41, ImmutableList.<Integer>of(39, 40))
      .build();

  public static boolean isAttackPossible(int fromTerritory, int toTerritory) {
    return CONNECTIONS.get(fromTerritory).contains(toTerritory);
  }
  
  public static boolean isFortifyPossible(int fromTerritory, int toTerritory, 
      List<String> territoryList) {
    if (territoryList.contains(fromTerritory + "") && territoryList.contains(toTerritory + "")) {
      if (CONNECTIONS.get(fromTerritory).contains(toTerritory)) {
        return true;
      } else {
        territoryList.remove(territoryList.indexOf(fromTerritory + ""));
        for (int territory : CONNECTIONS.get(fromTerritory)) {
          if (territoryList.contains(territory + "") 
              && isFortifyPossible(territory, toTerritory, territoryList)) {
            return true;
          }
        }
      } 
    } else {
      return false;
    }
    return false;
  }
}
