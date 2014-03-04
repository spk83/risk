package org.risk.graphics;

import org.risk.client.Card;

import com.google.gwt.resources.client.ImageResource;

public final class ImagesHandler {
  
  private ImagesHandler() {
    
  }
  
  public static ImageResource getDiceImageResource(DiceImages diceImages, int dots) {
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
  
  public static ImageResource getCardImageResource(CardImages cardImages, Card card) {
    switch(card.getCardType()) {
      case ARTILLERY: return cardImages.artillery();
      case CAVALRY: return cardImages.cavalry();
      case INFANTRY: return cardImages.infantry();
      case WILD: return cardImages.wild();
      default: return null;
    }
  }
  
}
