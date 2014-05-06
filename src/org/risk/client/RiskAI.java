package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.risk.client.Card.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.Window;

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

  public List<Integer> chooseCardsForTrading(boolean mandatoryCardSelection, 
      List<Card> cardObjects, int territoryCount, int playerCount) {
    if (mandatoryCardSelection || territoryCount < GameResources.TOTAL_TERRITORIES / playerCount) {
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
              return Card.getCardIdsFromCardObjects(selectedCards);
            }
          }
        }
      }
    } 
    return ImmutableList.<Integer>of();
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
      int territory = sourceTerritory;
      int sourceUnits = maxUnits + 1; 
      while (territory ==  sourceTerritory && maxUnits >= minUnits) {
        territoryMap.get(sourceTerritory + "").setCurrentUnits(sourceUnits - maxUnits);
        Territory dt = new Territory(destinationTerritory + "", GameApi.AI_PLAYER_ID, maxUnits); 
        territoryMap.put(destinationTerritory + "", dt);
        territory = getTerritory(territoryMap, Sets.newHashSet(sourceTerritory + "", 
            destinationTerritory + ""), playerTerritories);
        maxUnits--;
      }
      return ++maxUnits;
    }
  }

  public Map<String, Integer> fortify(Map<String, Territory> territoryMap,
      Map<String, Integer> playerTerritoryMap) {
    int maxRequirement = 0;
    Map<String, Integer> territoryDelta = new HashMap<String, Integer>();
    List<String> territoryList = Lists.newArrayList(playerTerritoryMap.keySet());
    for (Entry<String, Integer> source : playerTerritoryMap.entrySet()) {
      if (source.getValue() >= 2) {
        for (Entry<String, Integer> destination : playerTerritoryMap.entrySet()) {
          if (!destination.getKey().equals(source.getKey()) 
              && Territory.isFortifyPossible(Integer.parseInt(source.getKey()), 
                  Integer.parseInt(destination.getKey()), territoryList)) {
            int sourceTerritory = Integer.parseInt(source.getKey());
            int destinationTerritory = Integer.parseInt(destination.getKey());
            int sourceUnits = source.getValue();
            int destinationUnits = source.getValue();
            int minUnits = 0;
            int maxUnits = sourceUnits - 1;
            int territory = destinationTerritory;
            while (territory ==  destinationTerritory && minUnits <= maxUnits) {
              territoryMap.get(sourceTerritory + "").setCurrentUnits(sourceUnits - minUnits);
              territoryMap.get(destinationTerritory + "")
                .setCurrentUnits(destinationUnits + minUnits);
              territory = getTerritory(territoryMap, Sets.newHashSet(sourceTerritory + "", 
                  destinationTerritory + ""), playerTerritoryMap.keySet());
              minUnits++;
            }
            minUnits--;
            if (minUnits > maxRequirement) {
              maxRequirement = minUnits;
              territoryDelta.clear();
              territoryDelta.put(sourceTerritory + "", -minUnits);
              territoryDelta.put(destinationTerritory + "", minUnits);
            }
          }
        }
      }
    }
    return territoryDelta;
  }
}
