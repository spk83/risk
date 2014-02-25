package org.risk.client;

import java.util.List;
import java.util.Map;

import org.risk.client.GameApi.Container;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.UpdateUI;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class RiskPresenter {
  
  interface View {
    
    void setPresenter(RiskPresenter riskPresenter);
    void setViewerState(RiskState riskState);
    void setPlayerState(RiskState riskState);
    void choosePlayerColor(String color);
    void chooseNewTerritory();                                       //First territory in Deployment
    void chooseTerritoryForDeployment();                             //Reinforce in Deployment
    void chooseCardsForTrading();
    void reinforceTerritories();
    void attack();
    void moveUnitsAfterAttack();
    void tradeCardsInAttackPhase(List<Integer> cards);
    void reinforceInAttackPhase(Map<String, Integer> territoryDelta);
    void fortify();
    void endGame();
    
    //Functions with same input parameters can be clubbed !
  }
  
  private final RiskLogic riskLogic;// = new RiskLogic();
  private final View view;
  private final Container container;
  private RiskState riskState;
  private String myPlayerKey;
  
  public RiskPresenter(View view, Container container, RiskLogic riskLogic) {
    this.view = view;
    this.container = container;
    this.riskLogic = riskLogic;
    view.setPresenter(this);
    
  }
  
  public void updateUI(UpdateUI updateUI) {
    List<Integer> playerIds = updateUI.getPlayerIds();
    int myPlayerId = updateUI.getYourPlayerId();
    List<Operation> operations = updateUI.getLastMove();
    int turnPlayerId = getTurnPlayer(operations);
    Map<String, Object> state = updateUI.getState();
    myPlayerKey = GameResources.playerIdToString(myPlayerId);
    
    if (state.isEmpty()) {
      if (myPlayerId == GameResources.START_PLAYER_ID) {
        sendInitialMove(playerIds);
      }
      return;  
    }
    riskState = riskLogic.gameApiStateToRiskState(updateUI.getState(), turnPlayerId, playerIds);

    if (updateUI.isViewer()) {
      view.setViewerState(riskState);
      return;
    }
    if (updateUI.isAiPlayer()) {
      // TODO: implement AI in a later HW!
      //container.sendMakeMove(..);
      return;
    }
    view.setPlayerState(riskState);
    if (turnPlayerId == myPlayerId) {
      String phase = (String)state.get(GameResources.PHASE);
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        setTurnOrderMove();
      } else if (phase.equals(GameResources.CLAIM_TERRITORY)) {
        view.chooseNewTerritory();
      } else if (phase.equals(GameResources.DEPLOYMENT)) {
        view.chooseTerritoryForDeployment();
      } else if (phase.equals(GameResources.CARD_TRADE)) {
        view.chooseCardsForTrading();
      } else if (phase.equals(GameResources.ADD_UNITS)) {
        addUnits();
      } else if (phase.equals(GameResources.REINFORCE)) {
        view.reinforceTerritories();
      } else if (phase.equals(GameResources.ATTACK_PHASE)) {
        view.attack();
      } else if (phase.equals(GameResources.ATTACK_RESULT)) {
        attackResultMove();
      } else if (phase.equals(GameResources.ATTACK_REINFORCE)) {
        view.reinforceTerritories();
      } else if (phase.equals(GameResources.ATTACK_OCCUPY)) {
        view.moveUnitsAfterAttack();
      } else if (phase.equals(GameResources.FORTIFY)) {
        view.fortify();
      } else if (phase.equals(GameResources.END_GAME)) {
        view.endGame();
      }
    }
  }
  
  private int getTurnPlayer(List<Operation> operations) {
    for (Operation operation : operations) {
      if (operation instanceof SetTurn) {
        return ((SetTurn)operation).getPlayerId();
      }
    }
    throw new IllegalArgumentException("Invalid operations: Should contain SetTurn");
  }
  
  private void setTurnOrderMove() {
    container.sendMakeMove(riskLogic.setTurnOrderMove(riskState));
  }
  
  private void sendInitialMove(List<Integer> playerIds) {
    container.sendMakeMove(riskLogic.getInitialOperations(GameResources.getPlayerKeys(playerIds)));
  }
  
  void newTerritorySelected(String territory) {
    System.out.println("checked");
    container.sendMakeMove(riskLogic.performClaimTerritory(
        riskState, territory, myPlayerKey));
  }
  
  void territoryForDeployment(String territory) {
    Map<String, Integer> territoryUnitMap = Maps.newHashMap();
    territoryUnitMap.put(territory, 1);
    container.sendMakeMove(riskLogic.performDeployment(
        riskState, territoryUnitMap, myPlayerKey));
  }
  
  void addUnits() {
    if (riskState.getCardsTraded().isPresent()) {
      container.sendMakeMove(riskLogic.performAddUnitsWithTrade(riskState, myPlayerKey));
      return;
    }
    container.sendMakeMove(riskLogic.performAddUnitsWithOutTrade(riskState, myPlayerKey));
  }
  
  void territoriesReinforced(Map<String, Integer> territoryDelta) {
    if (territoryDelta == null) {
      //for skipping the reinforcement phase
      territoryDelta = Maps.<String, Integer>newHashMap();
    }
    Player myPlayer = riskState.getPlayersMap().get(myPlayerKey);
    Map<String, Integer> territoryUnitMap = myPlayer.getTerritoryUnitMap();
    for (Map.Entry<String, Integer> deltaEntry : territoryDelta.entrySet()) {
      String territory = deltaEntry.getKey();
      Integer delta = deltaEntry.getValue();
      Integer units = territoryUnitMap.get(territory);
      if (units == null) {
        throw new IllegalArgumentException(
            "Territory "+territory+" does not belong to player "+myPlayerKey);
      }
      territoryUnitMap.put(territory, delta + units);
    }
    container.sendMakeMove(riskLogic.performReinforce
        (riskState, 0, territoryUnitMap, myPlayerKey));
  }
  
  void cardsTraded(List<Integer> cards) {
    if (cards == null) {
      container.sendMakeMove(riskLogic.skipCardTrade(myPlayerKey));
    }
    container.sendMakeMove(riskLogic.performTrade
        (riskState, cards, myPlayerKey, null));
  }
  
  void attackResultMove() {
    container.sendMakeMove(riskLogic.attackResultOperations(
        riskState, GameResources.playerIdToInt(myPlayerKey)));
  }
  
  void performAttack(String attackingTerritory, String defendingTerritory) {
    Map<String, Object> attackerMap = ImmutableMap.<String, Object>of(
        GameResources.PLAYER, myPlayerKey,
        GameResources.TERRITORY, Integer.parseInt(attackingTerritory),
        GameResources.UNITS, riskState.getPlayersMap().get(myPlayerKey)
            .getTerritoryUnitMap().get(attackingTerritory));
    String defendingPlayer = riskState.getTerritoryMap().get(defendingTerritory).getPlayerKey();
    Map<String, Object> defenderMap = ImmutableMap.<String, Object>of(
        GameResources.PLAYER, defendingPlayer,
        GameResources.TERRITORY, Integer.parseInt(defendingTerritory),
        GameResources.UNITS, riskState.getPlayersMap().get(defendingPlayer)
            .getTerritoryUnitMap().get(attackingTerritory));
    container.sendMakeMove(riskLogic.performAttack
        (riskState, attackerMap, defenderMap, myPlayerKey));
  }
  
  void endAttack() {
    if (riskState.getTerritoryWinner() != null) {
      container.sendMakeMove(riskLogic.performEndAttack(riskState, myPlayerKey));
    } else {
      container.sendMakeMove(riskLogic.performEndAttackWithNoCard(
          GameResources.playerIdToInt(myPlayerKey)));
    }
  }
  
  void moveUnitsAfterAttack(int newUnitsAtUnclaimed) {
    container.sendMakeMove(riskLogic.performAttackOccupy(
        riskState, newUnitsAtUnclaimed, myPlayerKey));
  }
  
  void fortifyMove(Map<String, Integer> territoryDelta) {
    if (territoryDelta != null && !territoryDelta.isEmpty()) {
      container.sendMakeMove(riskLogic.performFortify(
          riskState, territoryDelta, myPlayerKey));
    }
    //pass null to end fortify
    container.sendMakeMove(riskLogic.performFortify(
        riskState, null, myPlayerKey));
  }
  
  void endGame() {
    container.sendMakeMove(riskLogic.performEndGame(riskState, myPlayerKey));
  }
  
  RiskState getRiskState() {
    return riskState;
  }

  void setRiskState(RiskState riskState) {
    this.riskState = riskState;
  }
  
  String getMyPlayerKey() {
    return myPlayerKey;
  }

  void setMyPlayerKey(String myPlayerKey) {
    this.myPlayerKey = myPlayerKey;
  }
  
}
