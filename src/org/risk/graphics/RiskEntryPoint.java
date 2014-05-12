package org.risk.graphics;

import org.risk.logic.RiskLogic;
import org.risk.logic.GameApi.Container;
import org.risk.logic.GameApi.ContainerConnector;
import org.risk.logic.GameApi.Game;
import org.risk.logic.GameApi.UpdateUI;
import org.risk.logic.GameApi.VerifyMove;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.mvp.client.AnimationEndCallback;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;

public class RiskEntryPoint implements EntryPoint {

  Container container;
  RiskPresenter riskPresenter;
  RiskLogic riskLogic = new RiskLogic();
  RiskGraphics riskGraphics = new RiskGraphics();
  
  @Override
  public void onModuleLoad() {
    // set viewport and other settings for mobile
    MGWT.applySettings(MGWTSettings.getAppSetting());

    // build animation helper and attach it
    AnimationHelper animationHelper = new AnimationHelper();
    RootPanel.get().add(animationHelper);
    
    Game game = new Game() {
      @Override
      public void sendVerifyMove(VerifyMove verifyMove) {
        container.sendVerifyMoveDone(riskLogic.verify(verifyMove));
      }
      
      @Override
      public void sendUpdateUI(UpdateUI updateUI) {
        riskPresenter.updateUI(updateUI);
      }
    };
    container = new ContainerConnector(game);
    riskPresenter = new RiskPresenter(riskGraphics, container, riskLogic);
    container.sendGameReady();
    
    // animate
    animationHelper.goTo(riskGraphics, Animation.SLIDE, new AnimationEndCallback() {
      @Override
      public void onAnimationEnd() {
        riskGraphics.resize();
      }
    });
  }
}
