package org.risk.client;

import java.util.List;
import java.util.Map;

import org.risk.client.GameApi.Container;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.UpdateUI;

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
    
    void turnOrderMove();
    
    /**
     * Sets the presenter. The viewer will call certain methods on the presenter, e.g.,
     * when a territory is selected ({@link #newTerritorySelected}),
     * when attack selection is done ({@link #performAttack}), etc.
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
     * by calling {@link #newTerritorySelected}. 
     */
    void chooseNewTerritory();
    
    /**
     * Asks the player to select a territory where he want to deploy army units.
     * The player can finish selecting a territory for deployment,
     * by calling {@link #territoryForDeployment}}.
     */
    void chooseTerritoryForDeployment();
    
    /**
     * Asks the player to select 3 cards for trading.
     * Player can finish selecting cards by calling {@link #cardsTraded}.
     */
    void chooseCardsForTrading(boolean mandatoryCardSelection);
    
    /**
     * Asks the player to select territories to reinforce.
     * Player can finish selecting territories to reinforce,
     * by calling {@link #territoriesReinforced}.
     */
    void reinforceTerritories();
    
    /**
     * Asks the player to select attacking and defending territory. 
     * Player can either call {@link #performAttack} to attack or,
     * {@link #endAttack} to end the attack.
     */
    void attack();
    
    void attackResult();
    
    /**
     * If a player wins a territory after attack, he needs to select number of units to move 
     * in the new territory. Player can call {@link #moveUnitsAfterAttack} to do so.
     */
    void moveUnitsAfterAttack();
    
    /**
     * If player defeated an opponent and gains his RISK cards, he might have to trade cards if 
     * he has more than 5 cards now. To do so, player can call {@link #attackTradeMove} to do so.
     */
   /* void tradeCardsInAttackPhase();*/
    
    /**
     * Player can perform fortification by calling {@link #fortifyMove} with territories and 
     * number of units where he wants to perform fortification.
     */
    void fortify();
    
    /**
     * Player gets informed about end game by this method and he needs to confirm it by 
     * calling {@link #endgame}. 
     */
    void endGame();
  }
  
  private final RiskLogic riskLogic;
  private final View view;
  private final Container container;
  private RiskState riskState;
  private String myPlayerKey;
  private List<Integer> playerIds;
  private int myPlayerId;
  private String currentPhase;
  private int turnPlayerId;
  
  public RiskPresenter(View view, Container container, RiskLogic riskLogic) {
    this.view = view;
    this.container = container;
    this.riskLogic = riskLogic;
    view.setPresenter(this);
  }
  
  /** Updates the presenter and the view with the state in updateUI. */
  public void updateUI(UpdateUI updateUI) {
    playerIds = updateUI.getPlayerIds();
    myPlayerId = updateUI.getYourPlayerId();
    myPlayerKey = GameResources.playerIdToString(myPlayerId);
    Map<String, Object> state = updateUI.getState();
    if (state.isEmpty()) {
      if (myPlayerId == GameResources.START_PLAYER_ID) {
        sendInitialMove(playerIds);
      }
      
      //show a basic UI
      return;
    }
    List<Operation> operations = updateUI.getLastMove();
    turnPlayerId = getTurnPlayer(operations);
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
    
    // If it's your turn, call appropriate method for next move based on current phase in state
    if (turnPlayerId == myPlayerId) {
      String phase = (String) state.get(GameResources.PHASE);
      currentPhase = phase;
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        view.turnOrderMove();
      } else if (phase.equals(GameResources.CLAIM_TERRITORY)) {
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
      } else if (phase.equals(GameResources.ATTACK_RESULT)) {
        view.attackResult();
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
  
  private boolean isCardSelectionMandatory() {
    if (currentPhase.equals(GameResources.ATTACK_TRADE)) {
      return true;
    } 
    Player myPlayer = riskState.getPlayersMap().get(myPlayerKey);
    if (myPlayer.getCards().size() >= GameResources.MAX_CARDS_IN_ATTACK_TRADE - 1) {
      return true;
    }
    return false;
  }
  RiskState getRiskState() {
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
  private int getTurnPlayer(List<Operation> operations) {
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
  private void sendInitialMove(List<Integer> playerIds) {
    container.sendMakeMove(riskLogic.getInitialOperations(GameResources.getPlayerKeys(playerIds)));
  }
  
  /**
   * Set the turn order of the game based on current dice rolled in initial moves.
   */
  public void setTurnOrderMove() {
    container.sendMakeMove(riskLogic.setTurnOrderMove(
        riskState, GameResources.getPlayerKeys(playerIds)));
  }
  
  /**
   * Peform the claim territory operations for given territory.
   * This method is called by view only if the presenter called {@link View#chooseNewTerritory()}.
   * @param territory
   */
  public void newTerritorySelected(String territory) {
    container.sendMakeMove(riskLogic.performClaimTerritory(
        riskState, territory, myPlayerKey));
  }
  
  /**
   * Perform the deployment operations for given territory.
   * This method is called by view only if the presenter called 
   * {@link View#chooseTerritoryForDeployment()}.
   * @param territory
   */
  public void territoryForDeployment(String territory) {
    Map<String, Integer> territoryUnitMap = Maps.newHashMap();
    territoryUnitMap.put(territory, 1);
    container.sendMakeMove(riskLogic.performDeployment(
        riskState, territoryUnitMap, myPlayerKey));
  }
  
  /**
   * Calculate the units to be added based on current state of the player and
   * whether any cards were traded by that player or not.
   */
  void addUnits() {
    if (riskState.getCardsTraded().isPresent()) {
      container.sendMakeMove(riskLogic.performAddUnitsWithTrade(riskState, myPlayerKey));
      return;
    } else {
      container.sendMakeMove(riskLogic.performAddUnitsWithOutTrade(riskState, myPlayerKey));
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
    container.sendMakeMove(riskLogic.performReinforce(riskState, 0, territoryDelta, myPlayerKey));
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
        container.sendMakeMove(riskLogic.skipCardTrade(myPlayerKey));
      } else {
        container.sendMakeMove(riskLogic.performTrade(riskState, cards, myPlayerKey, null));
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
        Integer.parseInt(defendingTerritory), myPlayerKey));
  }
  
  /**
   * Peform the attack result operations based on the output of attack phase state.
   */
  public void attackResultMove() {
    container.sendMakeMove(riskLogic.attackResultOperations(
        riskState, GameResources.playerIdToInt(myPlayerKey)));
  }
  
  /**
   * Peform the territory occupy operations if a territory has been won by player.
   * This method is called by view only if the presenter called {@link View#moveUnitsAfterAttack()}.
   * @param newUnitsAtUnclaimed
   */
  void moveUnitsAfterAttack(int newUnitsAtUnclaimed) {
    container.sendMakeMove(riskLogic.performAttackOccupy(
        riskState, newUnitsAtUnclaimed, myPlayerKey));
  }

  /**
   * Perform cards trading if player has more than 5 cards after defeating a opponent.
   * This method is called by view only if the presenter called 
   * {@link View#tradeCardsInAttackPhase()}.
   * @param cardsToBeTraded
   */
  private void attackTradeMove(List<Integer> cardsToBeTraded) {
    container.sendMakeMove(riskLogic.performAttackTrade(
        riskState, cardsToBeTraded, myPlayerKey, null));
  }

  /**
   * Peform end attack operations based on result in attack phase.
   * This method is called by view only if the presenter called {@link View#attack()}.
   */
  public void endAttack() {
    if (riskState.getTerritoryWinner() != null) {
      container.sendMakeMove(riskLogic.performEndAttack(riskState, myPlayerKey));
    } else {
      container.sendMakeMove(riskLogic.performEndAttackWithNoCard(
          GameResources.playerIdToInt(myPlayerKey)));
    }
  }
  
  /**
   * Perform fortification operations with given territory and units.
   * This method is called by view only if the presenter called {@link View#fortify()}.
   * @param territoryDelta
   */
  void fortifyMove(Map<String, Integer> territoryDelta) {
    if (territoryDelta != null && !territoryDelta.isEmpty()) {
      container.sendMakeMove(riskLogic.performFortify(
          riskState, territoryDelta, myPlayerKey));
    } else {
      //pass null to end fortify
      container.sendMakeMove(riskLogic.performFortify(
          riskState, null, myPlayerKey));
    }
  }
  
  /**
   * Perform end game operations.
   * This method is called by view only if the presenter called {@link View#endGame()}.
   */
  void endGame() {
    container.sendMakeMove(riskLogic.performEndGame(riskState, myPlayerKey));
  }
  
  public int getMyPlayerId() {
    return myPlayerId;
  }
  
  public String getMyPlayerKey() {
    return myPlayerKey;
  }
}
