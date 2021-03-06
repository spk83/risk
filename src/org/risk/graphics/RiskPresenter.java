package org.risk.graphics;

import java.util.List;
import java.util.Map;

import org.risk.logic.GameResources;
import org.risk.logic.Player;
import org.risk.logic.RiskLogic;
import org.risk.logic.RiskState;
import org.risk.logic.GameApi.Container;
import org.risk.logic.GameApi.Operation;
import org.risk.logic.GameApi.SetTurn;
import org.risk.logic.GameApi.UpdateUI;

import com.google.common.collect.Maps;

/**
 * The presenter that controls the cheat graphics.
 * We use the MVP pattern:
 * the model is {@link RiskState},
 * the view will have the risk graphics and it will implement {@link RiskPresenter.View},
 * and the presenter is {@link RiskPresenter}.
 */
public class RiskPresenter {
  
  public interface View {
    
    /**
     * Sets the presenter. The viewer will call certain methods on the presenter, e.g.,
     * when a territory is selected ({@link #newTerritorySelected()}),
     * when attack selection is done ({@link #performAttack()}), etc.
     */
    void setPresenter(RiskPresenter riskPresenter);
    
    /** Sets the state for a viewer, i.e., not one of the players. */
    void setViewerState(RiskState riskState);
    
    /**
     * Sets the state for a player with the given riskState.
     */
    void setPlayerState(RiskState riskState);

    /**
     * Asks the player to claim a territory.
     * The player can finish selecting a territory for claim,
     * by calling {@link #newTerritorySelected()}. 
     */
    void chooseNewTerritory();
    
    /**
     * Asks the player to select a territory where he want to deploy army units.
     * The player can finish selecting a territory for deployment,
     * by calling {@link #territoryForDeployment()}}.
     */
    void chooseTerritoryForDeployment();
    
    /**
     * Asks the player to select 3 cards for trading.
     * If player defeated an opponent and gains his RISK cards, he might have to trade cards if 
     * he has more than 5 cards now.
     * Player can finish selecting cards by calling {@link #cardsTraded()}.
     */
    void chooseCardsForTrading(boolean mandatoryCardSelection);
    
    /**
     * Asks the player to select territories to reinforce.
     * Player can finish selecting territories to reinforce,
     * by calling {@link #territoriesReinforced()}.
     */
    void reinforceTerritories();
    
    /**
     * Asks the player to select attacking and defending territory. 
     * Player can either call {@link #performAttack()} to attack or,
     * {@link #endAttack} to end the attack.
     */
    void attack();
    
    /**
     * Display attack result to the player. 
     * Player can either call {@link #attackResult()} to confirm.
     */
    void attackResult();
    
    /**
     * If a player wins a territory after attack, he needs to select number of units to move 
     * in the new territory. Player can call {@link #moveUnitsAfterAttack()} to do so.
     */
    void moveUnitsAfterAttack();
    
    /**
     * Player can perform fortification by calling {@link #fortifyMove()} with territories and 
     * number of units where he wants to perform fortification.
     */
    void fortify();
    
    /**
     * Player gets informed about end game by this method and he needs to confirm it by 
     * calling {@link #endgame()}. 
     */
    void endGame();

    /**
     * Display proper message when number of players selected is invalid by calling 
     * {@link #invalidNumberOfPlayers(int)}.
     * @param numberOfPlayers
     */
    void invalidNumberOfPlayers(int size);
  }
  
  private final RiskLogic riskLogic;
  private final View view;
  private final Container container;
  private RiskState riskState;
  private List<String> playerIds;
  private String myPlayerId;
  private String currentPhase;
  private String turnPlayerId;
  private List<Map<String, Object>> playersInfo;
  private boolean isAIPresent;
  
  public RiskPresenter(View view, Container container, RiskLogic riskLogic) {
    this.view = view;
    this.container = container;
    this.riskLogic = riskLogic;
    view.setPresenter(this);
  }
  
  /** Updates the presenter and the view with the state in updateUI. */
  public void updateUI(UpdateUI updateUI) {
    isAIPresent = false;
    playerIds = updateUI.getPlayerIds();
    playersInfo = updateUI.getPlayersInfo();
    myPlayerId = updateUI.getYourPlayerId();
    Map<String, Object> state = updateUI.getState();
    GameResources.removeViewer(playerIds, playersInfo);
    if (state.isEmpty()) {
      if (playerIds.size() < 2 || playerIds.size() > 6) {
       view.invalidNumberOfPlayers(playerIds.size());
       return;
      }
      String startPlayerId = GameResources.getStartPlayerId(playerIds);
      if (myPlayerId.equals(startPlayerId)) {
        sendInitialMove(playerIds, startPlayerId);
      }
      //show a basic UI
      return;
    }
    List<Operation> operations = updateUI.getLastMove();
    turnPlayerId = getTurnPlayer(operations);
    riskState = riskLogic.gameApiStateToRiskState(updateUI.getState(), turnPlayerId, playersInfo);

    if (updateUI.isViewer()) {
      view.setViewerState(riskState);
      return;
    }

    if (updateUI.isAiPlayer()) {
      isAIPresent = true;
    }
    
    view.setPlayerState(riskState);
    
    // If it's your turn, call appropriate method for next move based on current phase in state
    String phase = (String) state.get(GameResources.PHASE);
    currentPhase = phase;
    if (turnPlayerId.equals(myPlayerId)) {
      if (phase.equals(GameResources.CLAIM_TERRITORY)) {
        view.chooseNewTerritory();
      } else if (phase.equals(GameResources.DEPLOYMENT)) {
        view.chooseTerritoryForDeployment();
      } else if (phase.equals(GameResources.CARD_TRADE) 
          || phase.equals(GameResources.ATTACK_TRADE)) {
        view.chooseCardsForTrading(isCardSelectionMandatory());
      } else if (phase.equals(GameResources.ADD_UNITS)) {
        addUnits();
      } else if (phase.equals(GameResources.REINFORCE)) {
        view.reinforceTerritories();
      } else if (phase.equals(GameResources.ATTACK_PHASE)) {
        view.attack();
      } else if (phase.equals(GameResources.ATTACK_REINFORCE)) {
        view.reinforceTerritories();
      } else if (phase.equals(GameResources.ATTACK_OCCUPY)) {
        view.moveUnitsAfterAttack();
      } else if (phase.equals(GameResources.FORTIFY)) {
        view.fortify();
      } else if (phase.equals(GameResources.END_GAME)) {
        view.endGame();
      } else if (phase.equals(GameResources.GAME_ENDED)) {
        return;
      }
    }
    if (phase.equals(GameResources.ATTACK_RESULT)) {
      view.attackResult();
    }
  }
  
  private boolean isCardSelectionMandatory() {
    if (currentPhase.equals(GameResources.ATTACK_TRADE)) {
      return true;
    } 
    Player myPlayer = riskState.getPlayersMap().get(myPlayerId);
    if (myPlayer.getCards().size() >= GameResources.MAX_CARDS_IN_ATTACK_TRADE - 1) {
      return true;
    }
    return false;
  }
  
  public RiskState getRiskState() {
    return riskState;
  }
  
  public void setRiskState(RiskState riskState) {
    this.riskState = riskState;
  }

  /**
   * Get the playerId who has the turn.
   * @param operations
   * @return id of player who has the turn
   */
  private String getTurnPlayer(List<Operation> operations) {
    for (Operation operation : operations) {
      if (operation instanceof SetTurn) {
        return ((SetTurn) operation).getPlayerId();
      }
    }
    throw new IllegalArgumentException("Invalid operations: Should contain SetTurn");
  }
  
  /**
   * Perform the initial operations when current state of the game is empty.
   * @param playerIds
   */
  private void sendInitialMove(List<String> playerIds, String startPlayerId) {
   container.sendMakeMove(riskLogic.getInitialOperations(playerIds, startPlayerId));
  }
  
  /**
   * Set the turn order of the game based on current dice rolled in initial moves.
   */
  public void setTurnOrderMove() {
    container.sendMakeMove(riskLogic.setTurnOrderMove(
        riskState, playerIds));
  }
  
  /**
   * Peform the claim territory operations for given territory.
   * This method is called by view only if the presenter called {@link View#chooseNewTerritory()}.
   * @param territory
   */
  public void newTerritorySelected(String territory, boolean autoClaim) {
    container.sendMakeMove(riskLogic.performClaimTerritory(
        riskState, territory, myPlayerId, autoClaim));
  }
  
  /**
   * Perform the deployment operations for given territory.
   * This method is called by view only if the presenter called 
   * {@link View#chooseTerritoryForDeployment()}.
   * @param territory
   */
  public void territoryForDeployment(String territory, boolean autoDeploy) {
    Map<String, Integer> territoryUnitMap = Maps.newHashMap();
    territoryUnitMap.put(territory, 1);
    container.sendMakeMove(riskLogic.performDeployment(
        riskState, territoryUnitMap, myPlayerId, autoDeploy));
  }
  
  /**
   * Calculate the units to be added based on current state of the player and
   * whether any cards were traded by that player or not.
   */
  void addUnits() {
    if (riskState.getCardsTraded().isPresent()) {
      container.sendMakeMove(riskLogic.performAddUnitsWithTrade(riskState, myPlayerId));
      return;
    } else {
      container.sendMakeMove(riskLogic.performAddUnitsWithOutTrade(riskState, myPlayerId));
    }
  }
  
  /**
   * Perform the reinforce operation for given territories with units specified.
   * This method is called by view only if the presenter called {@link View#reinforceTerritories()}.
   * @param territoryDelta
   */
  public void territoriesReinforced(Map<String, Integer> territoryDelta) {
    if (territoryDelta == null) {
      //for skipping the reinforcement phase
      territoryDelta = Maps.<String, Integer>newHashMap();
    }
    container.sendMakeMove(riskLogic.performReinforce(riskState, 0, territoryDelta, myPlayerId));
  }
  
  /**
   * Peform the card trade operations or skip card trade operations based on 
   * list of cards being traded. This method is called by view only if the presenter 
   * called {@link View#chooseCardsForTrading()}.
   * @param cards being traded
   */
  public void cardsTraded(List<Integer> cards) {
    if (currentPhase.equals(GameResources.CARD_TRADE)) {
      if (cards == null || cards.isEmpty()) {
        container.sendMakeMove(riskLogic.skipCardTrade(myPlayerId));
      } else {
        container.sendMakeMove(riskLogic.performTrade(riskState, cards, myPlayerId, null));
      }
    } else if (currentPhase.equals(GameResources.ATTACK_TRADE)) {
      attackTradeMove(cards);
    }
  }
  
  /**
   * Peform the attack operations.
   * This method is called by view only if the presenter called {@link View#attack()}.
   * @param attackingTerritory
   * @param defendingTerritory
   */
  public void performAttack(String attackingTerritory, String defendingTerritory) {
    container.sendMakeMove(riskLogic.performAttack(riskState, Integer.parseInt(attackingTerritory),
        Integer.parseInt(defendingTerritory), myPlayerId));
  }
  
  /**
   * Peform the attack result operations based on the output of attack phase state.
   * This method is called by view only if the presenter called {@link View#attackResult()}.
   */
  public void attackResultMove() {
    container.sendMakeMove(riskLogic.attackResultOperations(riskState, myPlayerId));
  }
  
  /**
   * Peform the territory occupy operations if a territory has been won by player.
   * This method is called by view only if the presenter called {@link View#moveUnitsAfterAttack()}.
   * @param newUnitsAtUnclaimed
   */
  public void moveUnitsAfterAttack(int newUnitsAtUnclaimed) {
    container.sendMakeMove(riskLogic.performAttackOccupy(
        riskState, newUnitsAtUnclaimed, myPlayerId));
  }

  /**
   * Perform cards trading if player has more than 5 cards after defeating a opponent.
   * This method is called by view only if the presenter called 
   * {@link View#tradeCardsInAttackPhase()}.
   * @param cardsToBeTraded
   */
  private void attackTradeMove(List<Integer> cardsToBeTraded) {
    container.sendMakeMove(riskLogic.performAttackTrade(
        riskState, cardsToBeTraded, myPlayerId, null));
  }

  /**
   * Peform end attack operations based on result in attack phase.
   * This method is called by view only if the presenter called {@link View#attack()}.
   */
  public void endAttack() {
    if (riskState.getTerritoryWinner() != null) {
      container.sendMakeMove(riskLogic.performEndAttack(riskState, myPlayerId));
    } else {
      container.sendMakeMove(riskLogic.performEndAttackWithNoCard(myPlayerId));
    }
  }
  
  /**
   * Perform fortification operations with given territory and units.
   * This method is called by view only if the presenter called {@link View#fortify()}.
   * @param territoryDelta
   */
  public void fortifyMove(Map<String, Integer> territoryDelta) {
    if (territoryDelta != null && !territoryDelta.isEmpty()) {
      container.sendMakeMove(riskLogic.performFortify(
          riskState, territoryDelta, myPlayerId));
    } else {
      //pass null to end fortify
      container.sendMakeMove(riskLogic.performFortify(
          riskState, null, myPlayerId));
    }
  }
  
  /**
   * Perform end game operations.
   * This method is called by view only if the presenter called {@link View#endGame()}.
   */
  public void endGame() {
    container.sendMakeMove(riskLogic.performEndGame(riskState, myPlayerId, playerIds));
  }
  
  public String getMyPlayerId() {
    return myPlayerId;
  }

  public boolean isAIPresent() {
    return isAIPresent;
  }
}
