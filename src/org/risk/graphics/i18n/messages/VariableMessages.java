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
}
