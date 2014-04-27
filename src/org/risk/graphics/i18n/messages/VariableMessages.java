package org.risk.graphics.i18n.messages;

import com.google.gwt.i18n.client.Messages;

public interface VariableMessages extends Messages {
  
  @DefaultMessage("Select cards to trade. You must trade if you have more than "
    + "{0} cards. You will get units for this card trade."
    + "\n\nNumber of units you get for trading cards will"
    + " increase as number of trades performed in the game increases.")
  String cardTrade(int maxCards);
  
  @DefaultMessage("You got {0} units for reinforcement.")
  String unclaminedUnits(int units);
  
  @DefaultMessage("{0} won the game!")
  String playerWon(String playerName);
  
  @DefaultMessage("Player: {0}")
  String playerNameInfo(String playerName);
  
  @DefaultMessage("Unclaimed Units: {0}")
  String unclaimedUnitsInfo(int units);
  
  @DefaultMessage("Total cards: {0}")
  String totalCardsInfo(int totalCards);
  
  @DefaultMessage("Color: {0}")
  String colorInfo(String htmlColor);
  
  @DefaultMessage("Attacker lost <b>{0} units</b>")
  String attackerLost(int deltaUnits);
  
  @DefaultMessage("Defender lost <b>{0} units</b>")
  String defenderLost(int deltaUnits);
  
  @DefaultMessage("Player {0} captures {1}")
  String playerCaptures(String player, String territory);
  
  @DefaultMessage("Player {0} out of the game. Player {1} will get cards owned by Player {0}, if "
      + "any.")
  String defenderOut(String defender, String attacker);
  
  @DefaultMessage("Player {0} wins the game !")
  String attackerWinner(String player);
  
  @DefaultMessage("Player {0} will have to trade cards")
  String attackerTradeCards(String player);
  
  @DefaultMessage("{0} ({1})")
  String playerTextAttackResult(String playerKey, String territory);
  
  @DefaultMessage("{0} UNITS")
  String nUnits(int n);
  
  @DefaultMessage("{0}>>>>{1}")
  String colorOrder(String colorText, String colorHtml);
  
  @DefaultMessage("Unclaimed Units Left: {0}")
  String unclaimedUnitsLeft(int unclaimedUnits);
}