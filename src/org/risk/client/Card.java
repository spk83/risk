package org.risk.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Card {
  private String cardValue;
  private enum Type { INFANTRY, CAVALRY, ARTILLERY, WILD };
  private Type cardType;
  private String cardId;

  public Card(String cardValue, String cardId) {
    this.cardValue = cardValue;
    this.cardId = cardId;
    switch(cardValue.charAt(0)){
    case 'I':{
        this.cardType = Type.INFANTRY;
        break;
      }
    case 'C':{
      this.cardType = Type.CAVALRY;
      break;
    }
    case 'A':{
      this.cardType = Type.ARTILLERY;
      break;
    }
    case 'W':{
      this.cardType = Type.WILD;
      break;
    }
    default: throw new IllegalArgumentException("Invalid cardValue !");
    }
  }
  
  public String getCardValue() {
    return cardValue;
  }
  public void setCardValue(String cardValue) {
    this.cardValue = cardValue;
  }
  public Type getCardType() {
    return cardType;
  }
  public void setCardType(Type cardType) {
    this.cardType = cardType;
  }
  public String getCardId() {
    return cardId;
  }
  public void setCardId(String cardId) {
    this.cardId = cardId;
  }
  /*public static List<Card> getCardsFromList(List<Integer> cardsList) {
      return null;
  }*/
  public static int getUnits(List<Card> cards, int tradeNumber) {
    if( cards.size() == 3 ) {
      Map<Type, Integer> cardTypeCountMap = new HashMap<Type, Integer>();
      for (Card card : cards) {
        Integer count = cardTypeCountMap.get(card.getCardType());
        if (count == null) {
          count = 0;
        }
        cardTypeCountMap.put(card.getCardType(), count + 1);
      }
      int validCount = 0;
      for (Entry<Type, Integer> entry : cardTypeCountMap.entrySet()) {
        if( (entry.getKey() != Type.WILD && (entry.getValue() == 1 || entry.getValue() == 3))
            || (entry.getKey() == Type.WILD)) {
          validCount+=entry.getValue();
        }
      }
      if (validCount == 3) {
        if (tradeNumber >= 1 && tradeNumber <= 5) {
          return 4 + 2 * (tradeNumber - 1);
        }
        else if (tradeNumber >= 6) {
          return 15 + (tradeNumber - 6) * 5;
        }
      }
    }
    return 0;
  }
}