package org.risk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Card {
  public enum Type { INFANTRY, CAVALRY, ARTILLERY, WILD };
  private Type cardType;
  
  private String cardId;

  public Card(String cardValue, String cardId) {
    this.cardId = cardId;
    switch(cardValue.charAt(0)) {
    case 'I': {
      this.cardType = Type.INFANTRY;
      break;
    }
    case 'C': {
      this.cardType = Type.CAVALRY;
      break;
    }
    case 'A': {
      this.cardType = Type.ARTILLERY;
      break;
    }
    case 'W': {
      this.cardType = Type.WILD;
      break;
    }
    default: throw new IllegalArgumentException("Invalid cardValue !");
    }
  }
  
 public Type getCardType() {
    return cardType;
  }

  public static boolean isTradePossible(List<Card> cards) {
    if (cards.size() >= 5) {
      return true;
    }
    if (cards.size() <= 3) {
      return getUnits(cards, 1) > 0;
    }
    Map<Type, Integer> cardTypeCountMap = getCardTypeCountMap(cards);
    if (cardTypeCountMap.size() == 2 
        && cardTypeCountMap.entrySet().iterator().next().getValue() == 2) {
      return false;
    }
    return true;
  }
  
  private static Map<Type, Integer> getCardTypeCountMap(List<Card> cards) {
    Map<Type, Integer> cardTypeCountMap = new HashMap<Type, Integer>();
    for (Card card : cards) {
      Integer count = cardTypeCountMap.get(card.getCardType());
      if (count == null) {
        count = 0;
      }
      cardTypeCountMap.put(card.getCardType(), count + 1);
    }
    return cardTypeCountMap;
  }
  
  public static int getUnits(List<Card> cards, int tradeNumber) {
    if (cards.size() == 3) {
      Map<Type, Integer> cardTypeCountMap = getCardTypeCountMap(cards);
      int validCount = 0;
      int twoPlusWild = 0;
      for (Entry<Type, Integer> entry : cardTypeCountMap.entrySet()) {
        if ((entry.getKey() != Type.WILD && (entry.getValue() == 1 || entry.getValue() == 3))
            || (entry.getKey() == Type.WILD)) {
          validCount += entry.getValue();
        }
        if ((entry.getKey() != Type.WILD && entry.getValue() == 2) || (
            entry.getKey() == Type.WILD)) {
          twoPlusWild += entry.getValue();
        }
      }
      if (validCount == 3 || twoPlusWild == 3) {
        if (tradeNumber >= 1 && tradeNumber <= 5) {
          return 4 + 2 * (tradeNumber - 1);
        } else if (tradeNumber >= 6) {
          return 15 + (tradeNumber - 6) * 5;
        }
      }
    }
    return 0;
  }
  
  public static List<Card> getCardsById(Map<String, Card> cardMap, List<Integer> cardIds) {
    List<Card> cards = new ArrayList<Card>();
    for (int cardId : cardIds) {
      String cardKey = GameResources.RISK_CARD + cardId;
      Card card = cardMap.get(cardKey);
      if (card != null) {
        cards.add(card);
      }
    }
    return cards;
  }
  
  public static List<Integer> getCardIdsFromCardObjects(List<Card> cardObjects) {
    List<Integer> cards = new ArrayList<Integer>();
    for (Card card : cardObjects) {
      if (card != null) {
        cards.add(Integer.parseInt(card.cardId.substring(2)));
      }
    }
    return cards;
  }
}
