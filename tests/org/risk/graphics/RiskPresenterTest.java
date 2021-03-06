package org.risk.graphics;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.risk.graphics.RiskPresenter.View;
import org.risk.logic.AbstractTest;
import org.risk.logic.GameApi;
import org.risk.logic.GameResources;
import org.risk.logic.Player;
import org.risk.logic.RiskLogic;
import org.risk.logic.RiskState;
import org.risk.logic.GameApi.Container;
import org.risk.logic.GameApi.Operation;
import org.risk.logic.GameApi.SetTurn;
import org.risk.logic.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;

/** Tests for {@link RiskPresenter}.
 * Test plan:
 * There are several interesting states:
 * 1) empty state
 * 2) set turn order
 * 3) claim territory 
 * 4) deployment
 * 5) card trade or no card trade
 * 6) add units
 * 6) reinforce or skip reinforce
 * 7) attack phase
 * 8) attack result
 * 9) attack occupy
 * 10) trade cards in attack phase
 * 11) reinforce in attack phase 
 * 11) fortify or skip fortify
 * 12) end game 
 * There are several interesting yourPlayerId:
 * 1) playerA
 * 2) playerB
 * 3) playerC
 * 4) viewer
 * For each one of these states and for each yourPlayerId,
 * I will test what methods the presenters calls on the view and container.
 * In addition I will also test the interactions between the presenter and view, i.e.,
 * the view can call one of these methods:
 * 1) newTerritorySelected
 * 2) territoryForDeployment
 * 3) addUnits
 * 4) territoriesReinforced
 * 5) cardsTraded
 * 6) attackResultMove
 * 7) attackTradeMove
 * 8) performAttack
 * 9) endAttack
 * 10) moveUnitsAfterAttack
 * 11) fortifyMove
 * 12) endGame
 */
@RunWith(JUnit4.class)
public class RiskPresenterTest extends AbstractTest {
  
  /** The object under test. */
  RiskLogic mockRiskLogic;
  
  /** The class under test. */
  private RiskPresenter riskPresenter;
  private View mockView;
  private Container mockContainer;
  private RiskState mockRiskState;
  
  private List<Operation> operations = ImmutableList.<Operation>of();
  private final String viewerId = GameApi.VIEWER_ID;
  private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();
  private final ImmutableList<Map<String, Object>> playerInfo = 
      ImmutableList.<Map<String, Object>>of(
          ImmutableMap.<String, Object>of(GameApi.PLAYER_ID, "1"),
          ImmutableMap.<String, Object>of(GameApi.PLAYER_ID, "2"),
          ImmutableMap.<String, Object>of(GameApi.PLAYER_ID, "3"));
  private final ImmutableList<String> playerIds = ImmutableList.<String>of("1", "2", "3");
  
  @Before
  public void runBefore() {
    mockView = Mockito.mock(View.class);
    mockContainer = Mockito.mock(Container.class);
    mockRiskLogic = Mockito.mock(RiskLogic.class);
    riskPresenter = new RiskPresenter(mockView, mockContainer, mockRiskLogic);
    mockRiskState = Mockito.mock(RiskState.class);
    verify(mockView).setPresenter(riskPresenter);
  }

  @After
  public void runAfter() {
    verifyNoMoreInteractions(mockContainer);
    verifyNoMoreInteractions(mockView);
  }

  @Test
  public void testEmptyStateForA() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, "0", emptyState));
    verify(mockContainer).sendMakeMove(mockRiskLogic.getInitialOperations(
        AbstractTest.getPlayerIds(), AbstractTest.AID));
  }

  @Test
  public void testEmptyStateForB() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, "0", emptyState));
  }

  @Test
  public void testEmptyStateForC() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, "0", emptyState));
  }

  @Test
  public void testEmptyStateForViewer() {
    riskPresenter.updateUI(createUpdateUI(viewerId, "0", emptyState));
  }

  @Test
  public void testSetTurnOrderStateForA() {
    UpdateUI updateUI = createUpdateUI(
        AbstractTest.AID, AbstractTest.AID, getSetTurnOrderState());
    riskPresenter.updateUI(updateUI);
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testSetTurnOrderStateForB() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.AID, getSetTurnOrderState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testSetTurnOrderStateForC() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.CID, AbstractTest.AID, getSetTurnOrderState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testSetTurnOrderStateForViewer() {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, getSetTurnOrderState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }

  @Test
  public void testClaimTerritoryByCForC() {
    Map<String, Object> state = getClaimTerritoryState(); 
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performClaimTerritory(mockRiskState, "30", AbstractTest.PLAYER_C, false))
        .thenReturn(operations);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    riskPresenter.newTerritorySelected("30", false);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).chooseNewTerritory();
    verify(mockRiskLogic).performClaimTerritory(mockRiskState, "30", AbstractTest.PLAYER_C, false);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testClaimTerritoryByCForA() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.AID, AbstractTest.CID, getClaimTerritoryState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testClaimTerritoryByCForB() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.CID, getClaimTerritoryState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testClaimTerritoryByCForViewer() {
    riskPresenter.updateUI(createUpdateUI(
        viewerId, AbstractTest.CID, getClaimTerritoryState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testDeploymentByBForB() {
    Map<String, Object> state = getDeploymentState(); 
    Map<String, Integer> differenceTerritoryUnitMap = Maps.newHashMap();
    differenceTerritoryUnitMap.put("19", 1);
  
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.BID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performDeployment(
        mockRiskState, differenceTerritoryUnitMap, AbstractTest.PLAYER_B, false))
        .thenReturn(operations);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.BID, state));
    riskPresenter.territoryForDeployment("19", false);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).chooseTerritoryForDeployment();
    verify(mockRiskLogic).performDeployment(
        mockRiskState, differenceTerritoryUnitMap, AbstractTest.PLAYER_B, false);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testDeploymentByBForA() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.AID, AbstractTest.BID, getDeploymentState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testDeploymentByBForC() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.CID, AbstractTest.BID, getDeploymentState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testDeploymentByBForViewer() {
    riskPresenter.updateUI(createUpdateUI(
        viewerId, AbstractTest.BID, getDeploymentState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testCardTradeByAForA() {
    Map<String, Object> state = getCardTradeState(); 
    List<Integer> cardsBeingTraded = ImmutableList.of(0, 1, 2);
    Player mockPlayer = Mockito.mock(Player.class);
    Map<String, Player> playerMap = Maps.newHashMap();
    playerMap.put(AbstractTest.PLAYER_A, mockPlayer);

    when(mockRiskState.getPlayersMap()).thenReturn(playerMap);
    when(mockPlayer.getCards()).thenReturn(cardsBeingTraded);
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performTrade(mockRiskState, cardsBeingTraded, AbstractTest.PLAYER_A, null))
        .thenReturn(operations);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.cardsTraded(cardsBeingTraded);
    
    verify(mockPlayer).getCards();
    verify(mockRiskState).getPlayersMap();
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).chooseCardsForTrading(false);
    verify(mockRiskLogic).performTrade(
        mockRiskState, cardsBeingTraded, AbstractTest.PLAYER_A, null);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testCardTradeByAForB() { 
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.AID, getCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testCardTradeByAForC() { 
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.CID, AbstractTest.AID, getCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testCardTradeByAForViewer() { 
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, getCardTradeState()));    
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testNoCardTradeByAForA() {
    Map<String, Object> state = getCardTradeState();
    List<Integer> cardsBeingTraded = ImmutableList.of();
    Player mockPlayer = Mockito.mock(Player.class);
    Map<String, Player> playerMap = Maps.newHashMap();
    playerMap.put(AbstractTest.PLAYER_A, mockPlayer);

    when(mockRiskState.getPlayersMap()).thenReturn(playerMap);
    when(mockPlayer.getCards()).thenReturn(cardsBeingTraded);
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.skipCardTrade(AbstractTest.PLAYER_A)).thenReturn(operations);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.cardsTraded(cardsBeingTraded);
    
    verify(mockPlayer).getCards();
    verify(mockRiskState).getPlayersMap();
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).chooseCardsForTrading(false);
    verify(mockRiskLogic).skipCardTrade(AbstractTest.PLAYER_A);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testNoCardTradeByAForB() { 
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.AID, getCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testNoCardTradeByAForC() { 
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.CID, AbstractTest.AID, getCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testNoCardTradeByAForViewer() { 
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, getCardTradeState()));    
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAddUnitsWithCardTradeByCForC() {
    Map<String, Object> state = getAddUnitsWithCardTradeState(); 
    Optional<List<Integer>> cardsBeingTraded = Optional.fromNullable(
        (List<Integer>) ImmutableList.of(1, 2, 3));
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskState.getCardsTraded()).thenReturn(cardsBeingTraded);
    when(mockRiskLogic.performAddUnitsWithTrade(mockRiskState, AbstractTest.PLAYER_C))
        .thenReturn(operations);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockRiskLogic).performAddUnitsWithTrade(mockRiskState, AbstractTest.PLAYER_C);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testAddUnitsWithCardTradeByCForA() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.AID, AbstractTest.CID, getAddUnitsWithCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAddUnitsWithCardTradeByCForB() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.CID, getAddUnitsWithCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAddUnitsWithCardTradeByCForViewer() {
    riskPresenter.updateUI(createUpdateUI(
        viewerId, AbstractTest.CID, getAddUnitsWithCardTradeState()));    
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAddUnitsWithoutCardTradeByCForC() {
    Map<String, Object> state = getAddUnitsWithoutCardTradeState(); 
    Optional<List<Integer>> cardsBeingTraded = Optional.fromNullable(null);
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskState.getCardsTraded()).thenReturn(cardsBeingTraded);
    when(mockRiskLogic.performAddUnitsWithOutTrade(mockRiskState, AbstractTest.PLAYER_C))
        .thenReturn(operations);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockRiskLogic).performAddUnitsWithOutTrade(mockRiskState, AbstractTest.PLAYER_C);
    verify(mockContainer).sendMakeMove(mockRiskLogic.performAddUnitsWithOutTrade(
        mockRiskState, AbstractTest.PLAYER_C));
  }
  
  @Test
  public void testAddUnitsWithoutCardTradeByCForA() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.AID, AbstractTest.CID, getAddUnitsWithoutCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAddUnitsWithoutCardTradeByCForB() {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.CID, getAddUnitsWithoutCardTradeState()));    
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAddUnitsWithoutCardTradeByCForViewer() {
    riskPresenter.updateUI(createUpdateUI(
        viewerId, AbstractTest.CID, getAddUnitsWithoutCardTradeState()));    
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testReinforceTerritoriesByCForC() {
    Map<String, Object> state = getReinforceState(); 
    Map<String, Integer> territoryDelta = ImmutableMap.<String, Integer>of(
        "29", 3,
        "38", 3,
        "41", 2);
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performReinforce(mockRiskState, 0, territoryDelta, AbstractTest.PLAYER_C))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    riskPresenter.territoriesReinforced(territoryDelta);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).reinforceTerritories();
    verify(mockRiskLogic).performReinforce(mockRiskState, 0, territoryDelta, AbstractTest.PLAYER_C);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testReinforceTerritoriesByCForA() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.CID, getReinforceState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
 
  @Test
  public void testReinforceTerritoriesByCForB() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.CID, getReinforceState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
 
  @Test
  public void testReinforceTerritoriesByCForViewer() {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.CID, getReinforceState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
 
  @Test
  public void testSkipReinforceTerritoriesByCForC() {
    Map<String, Object> state = getReinforceState(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performReinforce(mockRiskState, 0, null, AbstractTest.PLAYER_C))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    riskPresenter.territoriesReinforced(null);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).reinforceTerritories();
    verify(mockRiskLogic).performReinforce(
        mockRiskState, 0, new HashMap<String, Integer>(), AbstractTest.PLAYER_C);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testSkipReinforceTerritoriesByCForA() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.CID, getReinforceState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
 
  @Test
  public void testSkipReinforceTerritoriesByCForB() {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.CID, getReinforceState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
 
  @Test
  public void testSkipReinforceTerritoriesByCForViewer() {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.CID, getReinforceState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
 
  @Test
  public void testAttackPhaseByAForA() throws Exception {
    Map<String, Object> state = getAttackPhaseStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performAttack(mockRiskState, 5, 13, AbstractTest.PLAYER_A))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.performAttack("5", "13");
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).attack();
    verify(mockRiskLogic).performAttack(mockRiskState, 5, 13, AbstractTest.PLAYER_A);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testAttackPhaseByAForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.BID, AbstractTest.AID, getAttackPhaseStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackPhaseByAForC() throws Exception {
    riskPresenter.updateUI(createUpdateUI(
        AbstractTest.CID, AbstractTest.AID, getAttackPhaseStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackPhaseByAForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(
        viewerId, AbstractTest.AID, getAttackPhaseStateByA()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testEndAttackByAForA() throws Exception {
    Map<String, Object> state = getEndAttackStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performEndAttack(mockRiskState, AbstractTest.PLAYER_A))
        .thenReturn(operations);
    when(mockRiskState.getTerritoryWinner()).thenReturn(AbstractTest.PLAYER_A);
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.endAttack();
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).attack();
    verify(mockRiskLogic).performEndAttack(mockRiskState, AbstractTest.PLAYER_A);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testEndAttackByAForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.AID, 
        getEndAttackStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testEndAttackByAForC() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.AID, 
        getEndAttackStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testEndAttackByAForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, 
        getEndAttackStateByA()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }

  @Test
  public void testEndAttackWithNoCardByAForA() throws Exception {
    Map<String, Object> state = getEndAttackWithNoCardStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performEndAttackWithNoCard(AbstractTest.AID))
        .thenReturn(operations);
    when(mockRiskState.getTerritoryWinner()).thenReturn(null);
    
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.endAttack();
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).attack();
    verify(mockRiskLogic).performEndAttackWithNoCard(AbstractTest.AID);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testEndAttackWithNoCardByAForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.AID, 
        getEndAttackWithNoCardStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
   }
  
  @Test
  public void testEndAttackWithNoCardByAForC() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.AID, 
        getEndAttackWithNoCardStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
   }
  
  @Test
  public void testEndAttackWithNoCardByAForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, 
        getEndAttackWithNoCardStateByA()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
   }
  
  @Test
  public void testAttackResultByAForA() throws Exception {
    Map<String, Object> state = getAttackResultStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.attackResultOperations(mockRiskState, AbstractTest.AID))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).attackResult();
  }
  
  @Test
  public void testAttackResultByAForB() throws Exception {
    Map<String, Object> state = getAttackResultStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.attackResultOperations(mockRiskState, AbstractTest.AID))
        .thenReturn(operations);
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.AID, 
         getAttackResultStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
    verify(mockView).attackResult();
  }
  
  @Test
  public void testAttackResultByAForC() throws Exception {
    Map<String, Object> state = getAttackResultStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.attackResultOperations(mockRiskState, AbstractTest.AID))
        .thenReturn(operations);
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.AID, 
         getAttackResultStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
    verify(mockView).attackResult();
  }
  
  @Test
  public void testAttackResultByAForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, 
         getAttackResultStateByA()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackOccupyByAForA() throws Exception {
    Map<String, Object> state = getAttackOccupyStateByA(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performAttackOccupy(mockRiskState, 3, AbstractTest.PLAYER_A))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.moveUnitsAfterAttack(3);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).moveUnitsAfterAttack();
    verify(mockRiskLogic).performAttackOccupy(mockRiskState, 3, AbstractTest.PLAYER_A);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testAttackOccupyByAForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.AID, 
        getAttackOccupyStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackOccupyByAForC() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.AID, 
        getAttackOccupyStateByA()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackOccupyByAForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, 
        getAttackOccupyStateByA()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackTradeCardsByAForA() throws Exception {
    Map<String, Object> state = getAttackTradeCardsByAState();
    List<Integer> cards = ImmutableList.<Integer>of(0, 1, 2);
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performAttackTrade(mockRiskState, cards, AbstractTest.PLAYER_A, null))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.cardsTraded(cards);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).chooseCardsForTrading(true);
    verify(mockRiskLogic).performAttackTrade(mockRiskState, cards, AbstractTest.PLAYER_A, null);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testAttackTradeCardsByAForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.AID, 
        getAttackTradeCardsByAState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackTradeCardsByAForC() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.AID, 
        getAttackTradeCardsByAState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackTradeCardsByAForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, 
        getAttackTradeCardsByAState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackReinforceTerritoriesByAForA() throws Exception {
    Map<String, Object> state = getAttackReinforceState(); 
    Map<String, Integer> territoryDelta = ImmutableMap.<String, Integer>of(
        "0", 6,
        "1", 4);
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.AID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performReinforce(mockRiskState, 0, territoryDelta, AbstractTest.PLAYER_A))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.AID, state));
    riskPresenter.territoriesReinforced(territoryDelta);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).reinforceTerritories();
    verify(mockRiskLogic).performReinforce(mockRiskState, 0, territoryDelta, AbstractTest.PLAYER_A);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testAttackReinforceTerritoriesByAForB() throws Exception { 
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.AID, 
        getAttackReinforceState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackReinforceTerritoriesByAForC() throws Exception { 
    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.AID, 
        getAttackReinforceState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testAttackReinforceTerritoriesByAForViewer() throws Exception { 
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.AID, 
        getAttackReinforceState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testFortifyByCForC() throws Exception {
    Map<String, Object> state = getFortifyStateByC(); 
    Map<String, Integer> territoryDelta = ImmutableMap.<String, Integer>of(
        "30", -2,
        "38", 2);
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performFortify(mockRiskState, territoryDelta, AbstractTest.PLAYER_C))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    riskPresenter.fortifyMove(territoryDelta);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).fortify();
    verify(mockRiskLogic).performFortify(mockRiskState, territoryDelta, AbstractTest.PLAYER_C);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testFortifyByCForA() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.CID, 
        getFortifyStateByC()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testFortifyByCForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.CID, 
        getFortifyStateByC()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testFortifyByCForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.CID, 
        getFortifyStateByC()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testSkipFortifyByCForC() throws Exception {
    Map<String, Object> state = getFortifyStateByC(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performFortify(mockRiskState, null, AbstractTest.PLAYER_C))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    riskPresenter.fortifyMove(null);
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).fortify();
    verify(mockRiskLogic).performFortify(mockRiskState, null, AbstractTest.PLAYER_C);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testSkipFortifyByCForA() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.CID, 
        getFortifyStateByC()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testSkipFortifyByCForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.CID, 
        getFortifyStateByC()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testSkipFortifyByCForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.CID, 
        getFortifyStateByC()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testEndGameByCForC() throws Exception {
    Map<String, Object> state = getEndState(); 
    
    when(mockRiskLogic.gameApiStateToRiskState(state, AbstractTest.CID, playerInfo))
        .thenReturn(mockRiskState);
    when(mockRiskLogic.performEndGame(mockRiskState, AbstractTest.PLAYER_C, playerIds))
        .thenReturn(operations);

    riskPresenter.updateUI(createUpdateUI(AbstractTest.CID, AbstractTest.CID, state));
    riskPresenter.endGame();
    
    verify(mockView).setPlayerState(mockRiskState);
    verify(mockView).endGame();
    verify(mockRiskLogic).performEndGame(mockRiskState, AbstractTest.PLAYER_C, playerIds);
    verify(mockContainer).sendMakeMove(operations);
  }
  
  @Test
  public void testEndGameByCForA() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.AID, AbstractTest.CID, getEndState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }

  @Test
  public void testEndGameByCForB() throws Exception {
    riskPresenter.updateUI(createUpdateUI(AbstractTest.BID, AbstractTest.CID, getEndState()));
    verify(mockView).setPlayerState(riskPresenter.getRiskState());
  }
  
  @Test
  public void testEndGameByCForViewer() throws Exception {
    riskPresenter.updateUI(createUpdateUI(viewerId, AbstractTest.CID, getEndState()));
    verify(mockView).setViewerState(riskPresenter.getRiskState());
  }
  
  private Map<String, Object> getSetTurnOrderState() {
    Builder<String, Object> stateBuilder = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.SET_TURN_ORDER)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, AbstractTest.getTerritoriesInRange(0, 41));
    
    List<Integer> diceValues = Lists.newArrayList(2, 3, 1, 4, 2, 4, 5, 2, 4);
    int i = 0;
    for (String diceRoll : GameResources.getDiceRollKeys(AbstractTest.getPlayerIds())) {
      stateBuilder.put(diceRoll, diceValues.get(i++));
    }
    
    return stateBuilder.build();
  }
  
  private Map<String, Object> getClaimTerritoryState() {
    List<Integer> unclaimedTerritories = AbstractTest.getTerritoriesInRange(0, 41);
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CLAIM_TERRITORY)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 35,
            GameResources.TERRITORY, GameResources.EMPTYMAP,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.CARDS, AbstractTest.getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, unclaimedTerritories)
        .build();
    return state;
  }
  
  private Map<String, Object> getDeploymentState() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.DEPLOYMENT)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 1,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.CARDS, AbstractTest.getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    return state;
  }

  private Map<String, Object> getCardTradeState() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CARD_TRADE)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(
            3, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    return state;
  }
  
  private Map<String, Object> getAddUnitsWithCardTradeState() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ADD_UNITS)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(4),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_C),
            GameResources.CONTINENT, ImmutableList.<String>of("5")))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(
            5, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .put(GameResources.CARDS_BEING_TRADED, ImmutableList.<Integer>of(0, 1, 2))
        .build();
    return state;
  }

  private Map<String, Object> getAddUnitsWithoutCardTradeState() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ADD_UNITS)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(4),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_C),
            GameResources.CONTINENT, ImmutableList.<String>of("5")))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(
            5, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .put(GameResources.TRADE_NUMBER, 1)
        .build();
    return state;
  }
  
  private Map<String, Object> getReinforceState() {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.REINFORCE)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_A),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_B),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 8,
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_C),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(
            2, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .put(GameResources.TRADE_NUMBER, 1)
        .build();
    return state;
  }
  
  private Map<String, Object> getAttackPhaseStateByA() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_PHASE)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 10, 6),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(11, 29, 1),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 8,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(30, 41, 3),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(
            0, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .put(GameResources.TRADE_NUMBER, 1)
        .build();
    return state;
  }
  
  private Map<String, Object> getEndAttackStateByA() throws Exception {
    Map<String, Integer> territoryMapB = AbstractTest.getTerritoriesInRange(11, 29, 1);
    territoryMapB = AbstractTest.performDeltaOnTerritory(territoryMapB, "13", -1);
    territoryMapB.remove("13");
    
    Map<String, Integer> territoryMapA = AbstractTest.getTerritoriesInRange(0, 10, 6);
    territoryMapA.put("13", 0);
    territoryMapA = AbstractTest.performDeltaOnTerritory(territoryMapA, "5", -3);
    territoryMapA = AbstractTest.performDeltaOnTerritory(territoryMapA, "13", +3);
    
    //creating state after the attack of A
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_PHASE).
        put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapA,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID)).
        put(GameResources.DECK, AbstractTest.getCardsInRange(0, 43)).
        put(GameResources.TERRITORY_WINNER, AbstractTest.PLAYER_A).
        build();
    return state;
  }
  
  private Map<String, Object> getEndAttackWithNoCardStateByA() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_PHASE).
        put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID)).
        put(GameResources.DECK, AbstractTest.getCardsInRange(0, 43)).
        build();
    return state;
  }
  
  private Map<String, Object> getAttackResultStateByA() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0"))).
        put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("2"))).
        put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID)).
        put(GameResources.DECK, AbstractTest.getCardsInRange(0, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, AbstractTest.PLAYER_A,
            GameResources.TERRITORY, 5, 
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, AbstractTest.PLAYER_B,
            GameResources.TERRITORY, 13, 
            GameResources.UNITS, 1)).build();
    return state;
  }
  
  private Map<String, Object> getAttackOccupyStateByA() throws Exception {
    Map<String, Integer> territoryMapB = AbstractTest.getTerritoriesInRange(11, 29, 1);
    territoryMapB = AbstractTest.performDeltaOnTerritory(territoryMapB, "13", -1);
    territoryMapB.remove("13");
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_OCCUPY).
        put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0"))).
        put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryMapB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID)).
        put(GameResources.DECK, AbstractTest.getCardsInRange(0, 43)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)).
        put(GameResources.LAST_ATTACKING_TERRITORY, 5).
        put(GameResources.TERRITORY_WINNER, AbstractTest.PLAYER_A).
        build();
    return state;
  }
  
  private Map<String, Object> getAttackTradeCardsByAState() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_TRADE).
        put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2, 3, 4, 5),
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 12, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.AID)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)).
        put(GameResources.LAST_ATTACKING_TERRITORY, 5).
        put(GameResources.TERRITORY_WINNER, AbstractTest.PLAYER_A).
        put(GameResources.DECK, AbstractTest.getCardsInRange(6, 43)).
        build();
    return state;
  }
  
  private Map<String, Object> getAttackReinforceState() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.ATTACK_REINFORCE)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(3, 4, 5),
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 13, 6),
            GameResources.UNCLAIMED_UNITS, 10,
            GameResources.CONTINENT, ImmutableList.<String>of("0", "1")))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(
            6, GameResources.TOTAL_RISK_CARDS - 1))
        .put(GameResources.TRADE_NUMBER, 2)
        .put(GameResources.TERRITORY_WINNER, AbstractTest.PLAYER_A)
        .build();
    return state;
  }
  
  private Map<String, Object> getFortifyStateByC() {
    Map<String, Integer> territoryC = AbstractTest.performDeltaOnTerritory(
        AbstractTest.getTerritories(AbstractTest.PLAYER_C), "30", 30);
    Map<String, Integer> territoryB = AbstractTest.performDeltaOnTerritory(
        AbstractTest.getTerritories(AbstractTest.PLAYER_B), "15", 1);
    
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.FORTIFY)
        .put(AbstractTest.PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0),
            GameResources.TERRITORY, AbstractTest.getTerritories(AbstractTest.PLAYER_A),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(1),
            GameResources.TERRITORY, territoryB,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, territoryC,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(
            AbstractTest.CID, AbstractTest.BID, AbstractTest.AID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(2, 43))
        .build();
    return state;
  }
  
  private Map<String, Object> getEndState() throws Exception {
    Map<String, Object> state = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.END_GAME)
        .put(AbstractTest.PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, AbstractTest.getTerritoriesInRange(0, 40, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0", "1", "2", "3", "4")))
        .put(GameResources.TURN_ORDER, ImmutableList.<String>of(AbstractTest.CID))
        .put(GameResources.DECK, AbstractTest.getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(41))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 40)
        .put(GameResources.TERRITORY_WINNER, AbstractTest.PLAYER_C)
        .build();
    return state;
  }
  private UpdateUI createUpdateUI(
      String yourPlayerId, String turnOfPlayerId, Map<String, Object> state) {
    // Our UI only looks at the current state
    // (we ignore: lastState, lastMovePlayerId, playerIdToNumberOfTokensInPot)
    return new UpdateUI(yourPlayerId, AbstractTest.PLAYERSINFO, state,
        emptyState, // we ignore lastState
        ImmutableList.<Operation>of(new SetTurn(turnOfPlayerId)),
        "0",
        ImmutableMap.<String, Integer>of());
  }
}
