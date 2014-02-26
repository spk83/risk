package org.risk.graphics;

import org.risk.client.GameApi.UpdateUI;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.RiskLogic;
import org.risk.client.RiskPresenter;
import org.risk.client.GameApi.Game;
import org.risk.client.GameApi.IteratingPlayerContainer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class RiskEntryPoint implements EntryPoint {

  IteratingPlayerContainer container;
  RiskPresenter riskPresenter;
  
  @UiField
  Button submitButton;
  
  @Override
  public void onModuleLoad() {
    
    final TextBox textBox = new TextBox();
    textBox.setText("Select total players: ");
    
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
        RootPanel.get("mainDiv").remove(textBox);
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
        riskPresenter = new RiskPresenter(new RiskGraphics(), container, new RiskLogic());
        container.sendGameReady();
        container.updateUi(1);
      }
    });
    RootPanel.get("mainDiv").add(textBox);
    RootPanel.get("mainDiv").add(playerSelect);
    RootPanel.get("mainDiv").add(submitButton);
  }

}
