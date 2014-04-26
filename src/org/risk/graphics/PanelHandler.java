package org.risk.graphics;

import java.util.List;
import java.util.Map;

import org.risk.client.Card;
import org.risk.client.GameResources;
import org.risk.client.Player;
import org.risk.client.RiskState;
import org.risk.graphics.i18n.messages.ConstantMessages;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public final class PanelHandler {
  
  private PanelHandler() {
  }
  
  public static RoundPanel getPlayerPanel(CardImages cardImages, RiskState state, 
      Player player, String currentPlayerId, Map<Image, Card> currentPlayerCardImages) {
    String myPlayerKey = player.getPlayerId();
    String myPlayerId = GameResources.playerKeyToId(myPlayerKey);
    RoundPanel panel = new RoundPanel();
    //panel.setSpacing(5);
    HorizontalPanel colorPanel = new HorizontalPanel();
    colorPanel.setSpacing(5);
    colorPanel.add(new Label("Color: "));
    colorPanel.add(new HTML("<div style='background-color: " + Player.getPlayerColor(myPlayerId) 
        + "; height: 25px; width: 25px;'>" + "</div>"));
    panel.add(new HTML("Player: <b>" + myPlayerKey + "</b>"));
    panel.add(colorPanel);
    List<Integer> cards = player.getCards();
    int totalCards = 0;
    if (cards != null && !cards.isEmpty()) {
      totalCards = cards.size();
    }
    panel.add(new HTML("Total cards: <b>" + totalCards + "</b>"));
    if (myPlayerId.equals(currentPlayerId)) {
      if (totalCards > 0) { 
        FlowPanel cardsPanel = new FlowPanel();
        for (int cardId : cards) {
          Card card = state.getCardMap().get(GameResources.RISK_CARD + cardId);
          Image image = new Image(ImagesHandler.getCardImageResource(cardImages, card));
          image.setSize("60px", "90px");
          image.setAltText(card.getCardType().name());
          image.setStyleName("risk-cards");
          currentPlayerCardImages.put(image, card);
          cardsPanel.add(image);
        }
        panel.add(cardsPanel);
      }
    }
    panel.add(new HTML("Unclaimed Units:<b>" + player.getUnclaimedUnits() + "</b>"));
    return panel;
  }
  
  public static Panel getGameStatusPanel(RiskState state, ConstantMessages constantMessages) {
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(5);
    panel.add(new HTML("Turn: " + "<b>" + state.getTurn() + "</b>"));
    if (state.getTurnOrder() != null) {
      panel.add(new HTML(constantMessages.turnOrder() + ": " + "<b>" + state.getTurnOrder()
          + "</b>"));
    }
    panel.add(new HTML("Phase: " + "<b>" + constantMessages.uiPhaseMap().get(state.getPhase()) 
        + "</b>"));
    return panel;
  }
}
