package org.risk.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetVisibility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ReinforcementPhaseTests extends AbstractTest {

  private static final String REINFORCE = "reinforce";
  private static final String REINFORCE_UNITS = "reinforceUnits";
  private static final String CARD_TRADE = "cardTrade";
  private static final String CARDS_BEING_TRADED = "cardsBeingTraded";
  private static final String TRADE_NUMBER = "tradeNumber";
  private static final String PLAYER_A = playerIdToString(aId);
  private static final String PLAYER_B = playerIdToString(bId);
  private static final String PLAYER_C = playerIdToString(cId);
  private static final String UNCLAIMED_UNITS = "unclaimedUnits";

  private static final Map<String, Integer> territoryMapA = ImmutableMap.<String, Integer>of();
  private static final Map<String, Integer> territoryMapB = ImmutableMap.<String, Integer>of();
  private static final Map<String, Integer> territoryMapC = ImmutableMap.<String, Integer>of();
  
  private static final List<Operation> continentsA = ImmutableList.<Operation>of();
  private static final List<Operation> continentsB = ImmutableList.<Operation>of();
  private static final List<Operation> continentsC = ImmutableList.<Operation>of();
  
  //player C turn to trade cards
  private final List<Operation> tradeCardsMoveByC = ImmutableList.<Operation>of(
      new Set(PHASE, CARD_TRADE),
      new Set(CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2)),
      new Set(REINFORCE_UNITS, 4),
      new Set(TRADE_NUMBER, 1),
      new SetVisibility("RC0"),
      new SetVisibility("RC1"),
      new SetVisibility("RC2"));
 
  //Game state as seen by C before the trade
  private final Map<String, Object> gameStateAtC = ImmutableMap.<String, Object>of(
      TURN, cId+"",
      PLAYERS, ImmutableMap.<String, Object>of(
          PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
          PLAYER_B, ImmutableMap.<String, Object>of(
              CARDS, ImmutableList.<Integer>of(3),
              TERRITORY, territoryMapB,
              UNITS, 10,
              UNCLAIMED_UNITS, 0,
              CONTINENT, continentsB),
          PLAYER_C, ImmutableMap.<String, Object>of(
              CARDS, ImmutableList.<Integer>of(0, 1, 2),
              TERRITORY, territoryMapC,
              UNITS, 20,
              UNCLAIMED_UNITS, 0,
              CONTINENT, continentsC)),
      BOARD, ImmutableMap.<String, Object>of(
          CARDS, getCardsInRange(5, 43)));
      
  //Game state as seen by C after the trade
  private final Map<String, Object> gameStateAfterTradeByC = ImmutableMap.<String, Object>of(
      TURN, cId+"",
      PLAYERS, ImmutableMap.<String, Object>of(
          PLAYER_A, ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
          PLAYER_B, ImmutableMap.<String, Object>of(
              CARDS, ImmutableList.<Integer>of(3),
              TERRITORY, territoryMapB,
              UNITS, 10,
              UNCLAIMED_UNITS, 0,
              CONTINENT, continentsB),
          PLAYER_C, ImmutableMap.<String, Object>of(
              CARDS, ImmutableList.<Integer>of(0, 1, 2),
              TERRITORY, territoryMapC,
              UNITS, 20,
              UNCLAIMED_UNITS, 0,
              CONTINENT, continentsC)),
      BOARD, ImmutableMap.<String, Object>of(
          CARDS, getCardsInRange(5, 43)));
  
  @Test
  public void testTradeByC_AtB() {
    final Map<String, Object> lastGameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapA,
            UNITS, 10,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, territoryMapB,
            UNITS, 10,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1, 2, 3),
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsC));
    
    final Map<String, Object> gameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(4),
            TERRITORY, territoryMapB,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 4,
            CONTINENT, continentsC));
    assertMoveOk(move(
        bId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
    assertHacker(move(
        aId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
  }
  
  @Test
  public void testTradeByC_AtA() {
    final Map<String, Object> lastGameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(5),
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapB,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(1, 2, 3),
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsC));
    
    final Map<String, Object> gameStateAtB = ImmutableMap.<String, Object>of(
        TURN, cId+"",
        PLAYER_A+"", ImmutableMap.<String, Object>of(
            CARDS, ImmutableList.<Integer>of(5),
            TERRITORY, territoryMapA,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsA),
        PLAYER_B+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapB,
            UNITS, 10,
            UNCLAIMED_UNITS, 0,
            CONTINENT, continentsB),
        PLAYER_C+"", ImmutableMap.<String, Object>of(
            CARDS, null,
            TERRITORY, territoryMapC,
            UNITS, 20,
            UNCLAIMED_UNITS, 4,
            CONTINENT, continentsC));
    assertMoveOk(move(
        aId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
    assertHacker(move(
        bId, playersInfo, gameStateAtB, 
        cId, lastGameStateAtB, tradeCardsMoveByC));
  }
  
  @Test
  public void testTradeByC() {
    assertMoveOk(move(
        cId, playersInfo, gameStateAfterTradeByC, 
        cId, gameStateAtC, tradeCardsMoveByC));
    assertHacker(move(
        bId, playersInfo, gameStateAfterTradeByC, 
        cId, gameStateAtC, tradeCardsMoveByC));
    assertHacker(move(
        aId, playersInfo, gameStateAfterTradeByC, 
        cId, gameStateAtC, tradeCardsMoveByC));
  }
}
