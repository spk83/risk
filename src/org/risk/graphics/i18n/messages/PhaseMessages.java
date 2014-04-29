package org.risk.graphics.i18n.messages;

import java.util.Map;

import com.google.gwt.i18n.client.Constants;

public interface PhaseMessages extends Constants {
  
  @DefaultStringValue("Turn Order")
  String turnOrder();
  
  @DefaultStringValue("Decide Turn Order")
  String setTurnOrder();
  
  @DefaultStringValue("Claim Territory")
  String claimTerritory();
  
  @DefaultStringValue("Deploy Units")
  String deployment();
  
  @DefaultStringValue("Trade Cards")
  String cardTrade();
  
  @DefaultStringValue("Add units for Reinforcement")
  String addUnits();
  
  @DefaultStringValue("Reinforce Territories")
  String reinforce();
  
  @DefaultStringValue("Attack")
  String attackPhase();
  
  @DefaultStringValue("Trade Cards in Attack Phase")
  String attackTrade();
  
  @DefaultStringValue("Reinforce Territory in Attack Phase")
  String attackReinforce();
  
  @DefaultStringValue("Result of Attack")
  String attackResult();
  
  @DefaultStringValue("Occupy new territory")
  String attackOccupy();
  
  @DefaultStringValue("End of attack")
  String endAttack();
  
  @DefaultStringValue("Fortify Territory")
  String fortify();
  
  @DefaultStringValue("End of Game")
  String endGame();
  
  @DefaultStringValue("End of Game")
  String gameEnded();
  
  Map<String, String> uiPhaseMap();
 }

