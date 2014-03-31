package org.risk.graphics;

import org.risk.client.GameApi.ContainerConnector;
import org.risk.client.GameApi.Container;
import org.risk.client.GameApi.Game;
import org.risk.client.GameApi.UpdateUI;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.RiskLogic;
import org.risk.client.RiskPresenter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class RiskEntryPoint implements EntryPoint {

  Container container;
  RiskPresenter riskPresenter;
  
  @UiField
  Button submitButton;
  
  @Override
  public void onModuleLoad() {
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
    container = new ContainerConnector(game);
    RiskGraphics riskGraphics = new RiskGraphics();
    RiskLogic riskLogic = new RiskLogic();
    riskPresenter = new RiskPresenter(riskGraphics, container, riskLogic);
    container.sendGameReady();
    RootPanel.get("mainDiv").add(riskGraphics);
  }
}
