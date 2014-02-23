package org.risk.client;

import java.util.List;
import java.util.Map;

import org.risk.client.GameApi.Container;

public class RiskPresenter {
  
  interface View {
    
    void setPresenter(RiskPresenter riskPresenter);
    void setViewerState(RiskState riskState);
    void setPlayerState(RiskState riskState);
    void choosePlayerColor(String color);
    void chooseNewTerritory(String territory);                       //First territory in Deployment
    void chooseTerritory(String territory);                          //Reinforce in Deployment
    void chooseCardsForTrading(List<Integer> cards);                 //pass null to skip
    void reinforceTerritories(Map<String, Integer> territoryDelta);  //pass null to skip
    void attack(String attackTerritory, String defendTerritory);
    void moveUnitsAfterAttack(int unitsFromAttackingTerritory);
    void tradeCardsInAttackPhase(List<Integer> cards);
    void endAttack();
    void reinforceInAttackPhase(Map<String, Integer> territoryDelta);
    void fortify(Map<String, Integer> territoryDelta);               //Size of map = 1
    
    //Functions with same input parameters can be clubbed !
  }
  
  private final RiskLogic riskLogic = new RiskLogic();
  private final View view;
  private final Container container;
  private RiskState riskState;
  
  public RiskPresenter(View view, Container container) {
    this.view = view;
    this.container = container;
    view.setPresenter(this);
  }
}
