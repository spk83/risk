package org.risk.graphics.i18n.messages;

import com.google.gwt.i18n.client.Constants;

public interface DialogInstructions extends Constants {
  
  @DefaultStringValue("Instructions")
  String instructions();
  
  @DefaultStringValue("Turn Order will be decided by rolling dice for all players. \n\n"
      + "Press OK to continue.")
  String setTurnOrder();
  
  @DefaultStringValue("Select a empty territory to claim. One territory at a time."
      + "\n\nPhase will end when all the territories are claimed.")
  String claimTerritory();
  
  @DefaultStringValue("Deploy your remaining army units to territory you own. "
      + "One unit at a time.\n\nPhase will end when you have placed your "
      + "all unclaimed units")
  String deployment();
  
  @DefaultStringValue("Reinforce your territories by putting units on territories "
      + "you own. You got these unclaimed units based on territories and continents you own "
      + "and cards traded, if any.\n\n Phase will end when you have 0 unclaimed units or "
      + "if you choose to end phase.")
  String reinforce();
  
  @DefaultStringValue("Attack on opponent's territory by selecting your territory "
      + "first and then opponent's territory. Make sure opponent's territory is adjacent "
      + "to your attacking territory and you have at least 2 units on your "
      + "attacking territory.\n\nYou can also attack by dragging your tank on to "
      + "opponent's tank. Opponent's tank will be visible once you start dragging."
      + "\n\n You can end this phase by clicking End.")
  String attack();
  
  @DefaultStringValue("Check the result of your attack. "
      + "Compare the highest die of attacker and defender. If attacker's is higher, "
      + "the defender loses one army from the territory under attack. But if "
      + "the defender's die is higher than yours, you lose one army from the territory you "
      + "attacked from. If each of you rolled more than one die, now compare the two "
      + "next-highest dice and repeat the process. In case of a tie, the defender always "
      + "wins.\n\nPress OK to go back to attack phase.")
  String attackResult();
  
  @DefaultStringValue("Reinforce your territories by putting units on territories "
      + "you own. You got these units based on cards you traded. "
      + "Phase will end when you have 0 unclaimed units or "
      + "if you choose to end phase")
  String attackReinforce();
  
  @DefaultStringValue("You will get a risk card at the end of attack phase, "
      + "because you won a territory in attack phase. "
      + "You will get only one risk card even if you won more than one territory.")
  String territoryWinner();
  
  @DefaultStringValue("Move units to new territory you just won from your"
      + " attacking territory. You must leave at least one unit behind on your attacking "
      + "territory. Also, you have to move units at least equal to the number of dice rolled "
      + "in last attack.")
  String attackOccupy();
  
  @DefaultStringValue("Move units from one territory you own to other territory "
      + "you own. You must leave at least one unit on territory. "
      + "\n\nYou can skip this phase by clicking End.")
  String fortify();
  
 }
