package org.risk.client;

import java.util.List;

import org.risk.client.GameApi.Container;
import org.risk.client.GameApi.Game;
import org.risk.client.GameApi.GameReady;
import org.risk.client.GameApi.MakeMove;
import org.risk.client.GameApi.Message;
import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.UpdateUI;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.VerifyMoveDone;

public class ContainerConnector implements Container {
  
  private final Game game;
  
  public ContainerConnector(Game game) {
    this.game = game;
    injectEventListener(this);
  }

  @Override
  public void sendGameReady() {
    GameReady gameReady = new GameReady();
    postMessageToParent(GameApiJsonHelper.getJsonString(gameReady));
  }

  @Override
  public void sendVerifyMoveDone(VerifyMoveDone verifyMoveDone) {
    postMessageToParent(GameApiJsonHelper.getJsonString(verifyMoveDone));
  }

  @Override
  public void sendMakeMove(List<Operation> operations) {
    MakeMove makeMove = new MakeMove(operations);
    postMessageToParent(GameApiJsonHelper.getJsonString(makeMove));
  }
  
  public static native void postMessageToParent(String message) /*-{
    $wnd.parent.postMessage(JSON.parse(message), "*");
  }-*/;
  
  public void eventListner(String message) {
    Message messageObj = GameApiJsonHelper.getMessageObject(message);
    if (messageObj instanceof UpdateUI) {
      game.sendUpdateUI((UpdateUI) messageObj);
    } else if (messageObj instanceof VerifyMove) {
      game.sendVerifyMove((VerifyMove) messageObj);
    }
  }
  
  private native void injectEventListener(ContainerConnector containerConnector) /*-{
    function postMessageListener(e) {
       containerConnector.@org.risk.client.ContainerConnector::eventListner(Ljava/lang/String;)(JSON.stringify(e.data));
    }
    $wnd.addEventListener("message", postMessageListener, false);
  }-*/;

}
