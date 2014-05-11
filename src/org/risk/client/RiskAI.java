package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.risk.client.Card.Type;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class RiskAI {

  public String getNewTerritory(Player player, List<Integer> unclaimedTerritories) {
    Set<String> myTerritories = player.getTerritoryUnitMap().keySet();
    if (myTerritories.isEmpty()) {
      String continent = getEmptyContinent(unclaimedTerritories);
      if (continent != null) {
        return bestAvailableTerritory(continent);
      }
    }
    return nearestAvailableTerritory(myTerritories, unclaimedTerritories);
  }

  private String nearestAvailableTerritory(Set<String> myTerritories,
      List<Integer> unclaimedTerritories) {
    int maxConnectionsCount = 0;
    Integer bestTerritory =  null;
    for (String myTerritory : myTerritories) {
      int territory = Integer.parseInt(myTerritory);
      String continent = Territory.getContinentId(territory);
      int connectionCount = 0;
      Integer connectedTerritory = null;
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (unclaimedTerritories.contains(connection)) {
          if (Continent.TERRITORY_SET_INT.get(continent).contains(connection)) {
            connectionCount++;
            connectedTerritory = connection;
          }
          connectionCount++;
          if (connectedTerritory == null) {
            connectedTerritory = connection;
          }
        } else if (myTerritories.contains(connection + "")) {
          connectionCount++;
        }
      }
      if (connectionCount > maxConnectionsCount && connectedTerritory != null) {
        maxConnectionsCount = connectionCount;
        bestTerritory = connectedTerritory;
      }
    }
    if (bestTerritory == null) {
      String continent = getEmptyContinent(unclaimedTerritories);
      if (continent != null) {
        return bestAvailableTerritory(continent);
      }
      bestTerritory = territoryWithMaxUnclaimedConnections(unclaimedTerritories);
    }
    return Territory.SVG_NAME_MAP.get(bestTerritory);
  }

  private int territoryWithMaxUnclaimedConnections(
      List<Integer> unclaimedTerritories) {
    int maxConnections = 0;
    Integer bestTerritory = null;
    for (int territory : unclaimedTerritories) {
      int connectionCount = 0;
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (unclaimedTerritories.contains(connection)) {
          connectionCount++;
        }
      }
      if (connectionCount >= maxConnections) {
        maxConnections = connectionCount;
        bestTerritory = territory;
      }
    }
    return bestTerritory;
  }

  private String bestAvailableTerritory(String continent) {
    int maxConnectionsInSameContinent = 0;
    Integer bestTerritory =  null;
    for (int territory : Continent.TERRITORY_SET_INT.get(continent)) {
      int connectionCount = 0;
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (Continent.TERRITORY_SET_INT.get(continent).contains(connection)) {
          connectionCount++;
        }
      }
      if (connectionCount > maxConnectionsInSameContinent) {
        maxConnectionsInSameContinent = connectionCount;
        bestTerritory = territory;
      }
    }
    return Territory.SVG_NAME_MAP.get(bestTerritory);
  }

  private String getEmptyContinent(List<Integer> unclaimedTerritories) {
    List<String> continents = Lists.newArrayList("0", "1", "2", "3", "4", "5");
    Random r = new Random();
    while (!continents.isEmpty()) {
      String continent = continents.get(r.nextInt(continents.size()));
      if (unclaimedTerritories.containsAll(Continent.TERRITORY_SET_INT.get(continent))) {
        return continent;
      } else {
        continents.remove(continent);
      }
    }
    return null;
  }

  public String getTerritoryForDeployment(Map<String, Integer> playerMap, 
      Map<String, Territory> territoryMap) {
    return Territory.SVG_NAME_MAP.get(getTerritory(territoryMap, playerMap.keySet(), 
        playerMap.keySet()));
  }

  private Integer getTerritory(Map<String, Territory> territoryMap,
      Set<String> myTerritories, Set<String> playerTerritories) {
    Integer selectedTerritory = null;
    Float maxRequirement = null;
    for (String myTerritory : myTerritories) {
      int unitDifference = 0;
      int connections = 0;
      int territory = Integer.parseInt(myTerritory);
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (!playerTerritories.contains(connection + "")) {
          unitDifference += territoryMap.get(connection + "").getCurrentUnits()
                - territoryMap.get(territory + "").getCurrentUnits();
          connections++;
        }
      }
      if (maxRequirement == null || (unitDifference + connections) >= maxRequirement) {
        maxRequirement = (float) (unitDifference + connections);
        selectedTerritory = territory;
      }
    }
    return selectedTerritory;
  }

  public List<Card> chooseCardsForTrading(boolean mandatoryCardSelection, 
      List<Card> cardObjects, int territoryCount, int playerCount) {
    if (mandatoryCardSelection || territoryCount <= GameResources.TOTAL_TERRITORIES / playerCount 
        || territoryCount >= 1.4 * GameResources.TOTAL_TERRITORIES / playerCount) {
      List<Card> selectedCards = new ArrayList<Card>();
      Map<Type, Integer> cardType = Card.getCardTypeCountMap(cardObjects);
      for (Entry<Type, Integer> entry : cardType.entrySet()) {
        if (entry.getValue() >= 3) {
          selectedCards = new ArrayList<Card>();
        }
        for (Card card : cardObjects) {
          if (card.getCardType() == entry.getKey()) {
            selectedCards.add(card);
            if (selectedCards.size() == 3) {
              return selectedCards;
            }
          }
        }
      }
    } 
    return ImmutableList.<Card>of();
  }

  public List<String> performAttack(Map<String, Integer> playerMap,
      Map<String, Territory> territoryMap) {
    Set<String> myTerritories = playerMap.keySet();
    List<String> selectedTerritory = new ArrayList<String>();
    int maxRequirement = 2;
    for (String myTerritory : myTerritories) {
      int territory = Integer.parseInt(myTerritory);
      int territoryUnits = territoryMap.get(territory + "").getCurrentUnits();
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (!myTerritories.contains(connection + "") && territoryUnits > 2) {
          int unitDifference = territoryUnits - territoryMap.get(connection + "").getCurrentUnits();
          if (unitDifference >= maxRequirement || (territoryUnits >= 5 && unitDifference >= -2)) {
            maxRequirement = unitDifference >= 2 ? unitDifference : 2;
            selectedTerritory.clear();
            selectedTerritory.add(Territory.SVG_NAME_MAP.get(territory));
            selectedTerritory.add(Territory.SVG_NAME_MAP.get(connection));
          }
        }
      }
    }
    return selectedTerritory;
  }

  public int moveUnitsAfterAttack(int minUnits, int maxUnits, Map<String, Territory> territoryMap, 
      int sourceTerritory, int destinationTerritory, Set<String> playerTerritories) {
    if (minUnits ==  maxUnits) {
      return minUnits;
    } else {
      playerTerritories.add(destinationTerritory + "");
      int territory = destinationTerritory;
      int sourceUnits = maxUnits + 1; 
      int destinationUnits = 0;
      if (territoryMap.get(destinationTerritory + "") != null) {
        destinationUnits = territoryMap.get(destinationTerritory + "").getCurrentUnits();
      } else {
        Territory dt = new Territory(destinationTerritory + "", GameApi.AI_PLAYER_ID, 
            destinationUnits + minUnits); 
        territoryMap.put(destinationTerritory + "", dt);
      }
      while (territory ==  destinationTerritory && minUnits <= maxUnits) {
        territoryMap.get(sourceTerritory + "").setCurrentUnits(sourceUnits - minUnits);
        territoryMap.get(destinationTerritory + "").setCurrentUnits(destinationUnits + minUnits);
        territory = getTerritoryForMovingUnits(territoryMap, sourceTerritory, destinationTerritory, 
            playerTerritories);
        minUnits++;
      }
      return --minUnits;
    }
  }

  private int getTerritoryForMovingUnits(Map<String, Territory> territoryMap,
      int sourceTerritory, int destinationTerritory, Set<String> playerTerritories) {
    Integer unitDifference = null;
    for (int connection : Territory.CONNECTIONS.get(sourceTerritory)) {
      if (!playerTerritories.contains(connection + "")) {
        if (unitDifference == null) {
          unitDifference = 0;
        }
        unitDifference += territoryMap.get(connection + "").getCurrentUnits()
              - territoryMap.get(sourceTerritory + "").getCurrentUnits();
      }
    }
    if (unitDifference == null || unitDifference < -2) {
      return destinationTerritory;
    } else {
      return sourceTerritory;
    }
  }

  public Map<String, Integer> fortify(Map<String, Territory> territoryMap,
      Map<String, Integer> playerTerritoryMap) {
    List<String> list = getTerritoriesForFortify(territoryMap, playerTerritoryMap);
    Set<String> territoryList = Sets.newHashSet(playerTerritoryMap.keySet());
    Map<String, Integer> territoryDelta = new HashMap<String, Integer>();
    if (list != null && !list.isEmpty()) {
      int sourceTerritory = Integer.parseInt(list.get(0));
      int destinationTerritory = Integer.parseInt(list.get(1));
      int maxUnits = territoryMap.get(list.get(0)).getCurrentUnits() - 1;
      int delta = moveUnitsAfterAttack(1, maxUnits, territoryMap, sourceTerritory, 
          destinationTerritory, territoryList);
      territoryDelta.put(sourceTerritory + "", -delta);
      territoryDelta.put(destinationTerritory + "", delta);
      return territoryDelta;
    } else {
      return territoryDelta;
    }
  }
  
  public List<String> getTerritoriesForFortify(Map<String, Territory> territoryMap,
      Map<String, Integer> playerTerritoryMap) {
    List<String> territoryList = Lists.newArrayList(playerTerritoryMap.keySet());
    int maxDifference = 0;
    List<String> fortifyTerritories = new ArrayList<String>();
    List<String> weakestTerritories = topWeakestTerritories(playerTerritoryMap, territoryMap);
    for (Entry<String, Integer> source : playerTerritoryMap.entrySet()) {
      if (source.getValue() >= 2) {
        for (String destination : weakestTerritories) {
          if (!destination.equals(source.getKey()) 
              && source.getValue() - territoryMap.get(destination).getCurrentUnits() > maxDifference
              && Territory.isFortifyPossible(Integer.parseInt(source.getKey()), 
                  Integer.parseInt(destination), territoryList)) {
            fortifyTerritories.clear();
            fortifyTerritories.add(source.getKey());
            fortifyTerritories.add(destination);
            maxDifference = source.getValue() - territoryMap.get(destination).getCurrentUnits();
          }
        }
      }
    }
    return fortifyTerritories;
  }
  
  public List<String> topWeakestTerritories(Map<String, Integer> playerMap,
      Map<String, Territory> territoryMap) {
    Map<String, Integer> result = new HashMap<String, Integer>();
    Ordering<String> valueComparator = Ordering.natural().onResultOf(
        Functions.forMap(result)).compound(Ordering.natural());
    Set<String> myTerritories = playerMap.keySet();
    for (String myTerritory : myTerritories) {
      int territory = Integer.parseInt(myTerritory);
      int territoryUnits = territoryMap.get(territory + "").getCurrentUnits();
      int minDifference = Integer.MAX_VALUE;
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (!myTerritories.contains(connection + "")) {
          int unitDifference = territoryUnits - territoryMap.get(connection + "")
              .getCurrentUnits();
          if (unitDifference <= minDifference) {
            minDifference = unitDifference;
          }
        }
      }
      if (minDifference != Integer.MAX_VALUE) {
        result.put(myTerritory, minDifference);
      }
    }
    ImmutableSortedMap<String, Integer> resultMap = ImmutableSortedMap.copyOf(
        result, valueComparator);
    List<String> topWeakestTerritories = new ArrayList<String>(resultMap.keySet());
    for (int i = 5; i < topWeakestTerritories.size(); ++i) {
      topWeakestTerritories.remove(i);
    }
    return topWeakestTerritories;
  }
  
  public String getWeakestTerritory(Map<String, Integer> playerMap,
      Map<String, Territory> territoryMap) {
    Set<String> myTerritories = playerMap.keySet();
    int minUnitDifference = Integer.MAX_VALUE;
    Integer weakestTerritory = null;
    for (String myTerritory : myTerritories) {
      int territory = Integer.parseInt(myTerritory);
      int territoryUnits = territoryMap.get(territory + "").getCurrentUnits();
      int minDifference = Integer.MAX_VALUE;
      for (int connection : Territory.CONNECTIONS.get(territory)) {
        if (!myTerritories.contains(connection + "")) {
          int unitDifference = territoryUnits - territoryMap.get(connection + "")
              .getCurrentUnits();
          if (unitDifference <= minDifference) {
            minDifference = unitDifference;
          }
        }
      }
      if (minDifference != Integer.MAX_VALUE && minDifference < minUnitDifference) {
        weakestTerritory = territory;
        minUnitDifference = minDifference;
      }
    }
    return Territory.SVG_NAME_MAP.get(weakestTerritory);
  }
}
