package org.risk.graphics;

import java.util.List;
import java.util.Map;

import org.risk.client.Card;
import org.risk.client.GameResources;
import org.risk.client.Player;
import org.risk.client.RiskState;
import org.risk.graphics.i18n.messages.ConstantMessages;
import org.risk.graphics.i18n.messages.DialogInstructions;
import org.risk.graphics.i18n.messages.VariableMessages;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public final class PanelHandler {
  
  private PanelHandler() {
  }
  
  public static RoundPanel getPlayerPanel(CardImages cardImages, RiskState state, 
      Player player, String currentPlayerId, Map<Image, Card> currentPlayerCardImages,
      VariableMessages variableMessages, DialogInstructions dialogInstructions) {
    String myPlayerKey = player.getPlayerId();
    String myPlayerId = GameResources.playerKeyToId(myPlayerKey);
    RoundPanel panel = new RoundPanel();
    HorizontalPanel colorPanel = new HorizontalPanel();
    colorPanel.setSpacing(5);
    String color = dialogInstructions.color();
    String colorHtml = "<div style='background-color: "
        + Player.getPlayerColor(myPlayerId) + "; height: 25px; width: 25px;'>" + "</div>";
    String [] order = variableMessages.colorOrder(color, colorHtml).split(">>>>");
    colorPanel.add(new HTML(order[0]));
    colorPanel.add(new HTML(order[1]));
    //panel.setSpacing(5);
    panel.add(new HTML(variableMessages.playerNameInfo(myPlayerKey)));
    panel.add(colorPanel);
    List<Integer> cards = player.getCards();
    int totalCards = 0;
    if (cards != null && !cards.isEmpty()) {
      totalCards = cards.size();
    }
    panel.add(new HTML(variableMessages.totalCardsInfo(totalCards)));
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
    panel.add(new HTML(variableMessages.unclaimedUnitsInfo(player.getUnclaimedUnits())));
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
