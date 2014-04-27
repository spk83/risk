package org.risk.graphics.i18n.messages;

import com.google.gwt.i18n.client.Constants;

public interface ConstantMessages extends Constants {
  
  @DefaultStringValue("You won all the territories. Congratulations, you are the winner!")
  String endGame();
  
  @DefaultStringValue("Turn Order will be decided by rolling dice for all players.")
  String turnOrder();
  
  @DefaultStringValue("Watch the game! Hope you enjoy!")
  String viewer();
  
  @DefaultStringValue("You lost. Better luck next time.")
  String playerLost();
  
  @DefaultStringValue("Wait for your turn! Watch what your enemy is doing!")
  String notYourTurn();
  
  @DefaultStringValue("Players Info")
  String playersInfo();
  
  @DefaultStringValue("Back")
  String back();
 
  @DefaultStringValue("Ok")
  String ok();
  
  @DefaultStringValue("End")
  String end();
  
  @DefaultStringValue("Cancel")
  String cancel();
  
  @DefaultStringValue("Info")
  String info();
  
  @DefaultStringValue("Card selection is mandatory, please select again !")
  String mandatoryCardSelection();
  
  @DefaultStringValue("Invalid selection")
  String invalidSelection();
  
  @DefaultStringValue("Invalid selection: Press OK to continue or Cancel to select again.")
  String confirmDialog();
  
  @DefaultStringValue("Not allowed")
  String notAllowed();
  
  @DefaultStringValue("You can only view the game.")
  String viewerNotAllowed();
  
  @DefaultStringValue("Please wait for your turn.")
  String playerNotAllowed();
  
  @DefaultStringValue("You already own this territory.")
  String alreadyOwnTerritory();
  
  @DefaultStringValue("Select an empty territory.")
  String emptyTerritory();
  
  @DefaultStringValue("Please select your territory.")
  String selectYourTerritory();
  
  @DefaultStringValue("Not enough units to attack.")
  String notEnoughUnits();
  
  @DefaultStringValue("Select opponent's territory to attack.")
  String selectOpponentTerritory();
  
  @DefaultStringValue("Select your own territory first to attack from.")
  String selectOwnTerritoryAttack();
  
  @DefaultStringValue("Select opponent's territory that is adjacent to your territory for attack.")
  String selectAdjacentTerritory();
  
  @DefaultStringValue("Not enough units to fortify.")
  String fortifyNotPossible();
  
  @DefaultStringValue("Select own territory first to fortify from.")
  String selectOwnTerritoryFortify();
  
  @DefaultStringValue("Select your own territory that is connected to your territory for fortify")
  String selectConnectedTerritoryFortify();
  
  @DefaultStringValue("You won the game!")
  String gameWon();
  
  @DefaultStringValue("Player")
  String player();
  
  @DefaultStringValue("Choose number of units to move on the new territory")
  String chooseUnitsToMove();
  
  @DefaultStringValue("Units")
  String units();
  
  @DefaultStringValue("Color")
  String color();
}
