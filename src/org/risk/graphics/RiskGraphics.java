package org.risk.graphics;

import java.util.List;
import java.util.Map;

import org.risk.client.RiskPresenter;
import org.risk.client.RiskState;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RiskGraphics extends Composite implements RiskPresenter.View {
    public interface RiskGraphicsUiBinder extends UiBinder<Widget, RiskGraphics> {
    }

  private final DiceImages diceImages;
  private RiskPresenter riskPresenter;
  
  @UiField
  HorizontalPanel playerArea;
  
  @UiField
  VerticalPanel diceArea;
  
  public RiskGraphics() {
    diceImages = GWT.create(DiceImages.class);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @Override
  public void setPresenter(RiskPresenter riskPresenter) {
    this.riskPresenter = riskPresenter;
  }

  @Override
  public void setViewerState(RiskState riskState) {
    // TODO Auto-generated method stub
  }

  @Override
  public void setPlayerState(RiskState riskState) {
    diceArea.clear();
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      for (Map.Entry<String, List<Integer>> entry : riskState.getDiceResult().entrySet()) {
        diceArea.add(getNewDicePanel(entry.getKey(), entry.getValue()));
      }
    }
    /*System.out.println("Current player turn: " + riskState.getTurn());
    System.out.println("Phase: " + riskState.getPhase());
    System.out.println("Turn order: " + riskState.getTurnOrder());*/
  }

  @Override
  public void chooseNewTerritory() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void chooseTerritoryForDeployment() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void chooseCardsForTrading() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void reinforceTerritories() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void attack() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void moveUnitsAfterAttack() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void tradeCardsInAttackPhase() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fortify() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void endGame() {
    // TODO Auto-generated method stub
    
  }
  
  private ImageResource getDiceImage(int dots) {
    switch(dots) {
      case 1 : return diceImages.dice1();
      case 2 : return diceImages.dice2();
      case 3 : return diceImages.dice3();
      case 4 : return diceImages.dice4();
      case 5 : return diceImages.dice5();
      case 6 : return diceImages.dice6();
      default : return null;
    }
  }
  
  private Panel getNewDicePanel(String userId, List<Integer> rolls) {
    FlowPanel imageContainer = new FlowPanel();
    imageContainer.add(new Label(userId));
    for (Integer dots : rolls) {
      Image diceImage = new Image(getDiceImage(dots));
      imageContainer.add(diceImage);
    }
    return imageContainer;
  }

  @Override
  public void turnOrderMove() {
    Button button;
    ClickHandler handler = new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        riskPresenter.setTurnOrderMove();
      }
    };
    button = new Button("Continue", handler);
    diceArea.add(button);
  }

}
