package org.risk.graphics;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.risk.client.Card;
import org.risk.client.GameResources;
import org.risk.client.Player;
import org.risk.client.RiskState;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class PanelHandler {
  
  private PanelHandler() {
  }
  
  public static Panel getPlayerPanel(CardImages cardImages, RiskState state, 
      Player player, int currentPlayerId, Map<Image, Card> currentPlayerCardImages) {
    String myPlayerKey = player.getPlayerId();
    int myPlayerId = GameResources.playerIdToInt(myPlayerKey);
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(5);
    HorizontalPanel colorPanel = new HorizontalPanel();
    colorPanel.setSpacing(5);
    colorPanel.add(new Label("Color: "));
    colorPanel.add(new HTML("<div style='background-color: " + Player.PLAYER_COLOR.get(myPlayerId) 
        + "; height: 25px; width: 25px;'>" + "</div>"));
    panel.add(new HTML("Player: <b>" + myPlayerKey + "</b>"));
    panel.add(colorPanel);
    List<Integer> cards = player.getCards();
    int totalCards = 0;
    if (cards != null && !cards.isEmpty()) {
      totalCards = cards.size();
    }
    panel.add(new HTML("Total cards: <b>" + totalCards + "</b>"));
    if (myPlayerId == currentPlayerId) {
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
  
  public static Panel getGameStatusPanel(RiskState state) {
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(5);
    panel.add(new HTML("Turn: " + "<b>" + state.getTurn() + "</b>"));
    if (state.getTurnOrder() != null) {
      panel.add(new HTML("Turn Order: " + "<b>" + state.getTurnOrder() + "</b>"));
    }
    panel.add(new HTML("Phase: " + "<b>" + state.getPhase() + "</b>"));
    return panel;
  }
  
  public static Panel getNewDicePanel(DiceImages diceImages, String text, List<Integer> rolls) {
    Collections.sort(rolls);
    FlowPanel imageContainer = new FlowPanel();
    imageContainer.add(new Label(text + "   "));
    for (Integer dots : rolls) {
      Image diceImage = new Image(ImagesHandler.getDiceImageResource(diceImages, dots));
      imageContainer.add(diceImage);
    }
    return imageContainer;
  }

}
