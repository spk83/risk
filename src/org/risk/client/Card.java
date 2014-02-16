package org.risk.client;

import java.util.List;

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
  public static List<Card> getCardsFromList(List<Integer> cardsList) {
      return null;
  }
}
