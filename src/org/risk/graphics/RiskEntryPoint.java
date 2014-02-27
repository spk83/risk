package org.risk.graphics;

import org.risk.client.GameApi.UpdateUI;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.RiskLogic;
import org.risk.client.RiskPresenter;
import org.risk.client.GameApi.Game;
import org.risk.client.GameApi.IteratingPlayerContainer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Label;

public class RiskEntryPoint implements EntryPoint {

  IteratingPlayerContainer container;
  RiskPresenter riskPresenter;
  
  @UiField
  Button submitButton;
  
  @Override
  public void onModuleLoad() {
    
    final Label label = new Label("Select total players: ");
    final ListBox playerSelect = new ListBox();
    for (int i = 3; i <= 6; ++i) {
      playerSelect.addItem(i + "");
    }
    submitButton = new Button("Submit", new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        int selectedIndex = playerSelect.getSelectedIndex();
        playerSelect.clear();
        RootPanel.get("mainDiv").remove(playerSelect);
        RootPanel.get("mainDiv").remove(label);
        RootPanel.get("mainDiv").remove(submitButton);
        Game game = new Game() {
          @Override
          public void sendVerifyMove(VerifyMove verifyMove) {
            container.sendVerifyMoveDone(new RiskLogic().verify(verifyMove));
          }

          @Override
          public void sendUpdateUI(UpdateUI updateUI) {
            riskPresenter.updateUI(updateUI);
          }
        };
        container = new IteratingPlayerContainer(game, selectedIndex + 3);
        RiskGraphics riskGraphics = new RiskGraphics();
        riskGraphics.setPresenter(riskPresenter);
        riskPresenter = new RiskPresenter(riskGraphics, container, new RiskLogic());
        container.sendGameReady();
        container.updateUi(1);
        final ListBox playerSelect = new ListBox();
        for (int i = 1; i <= selectedIndex + 3; ++i) {
          playerSelect.addItem(i + "");
        }
        Label playerSelectLabel = new Label("Select player: ");
        RootPanel.get("mainDiv").add(playerSelectLabel);
        RootPanel.get("mainDiv").add(playerSelect);
        RootPanel.get("mainDiv").add(riskGraphics);
        playerSelect.addChangeHandler(new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent event) {
            container.updateUi(playerSelect.getSelectedIndex() + 1);
          }
        });
      }
    });
    RootPanel.get("mainDiv").add(label);
    RootPanel.get("mainDiv").add(playerSelect);
    RootPanel.get("mainDiv").add(submitButton);
  }
}
