package org.risk.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Attack {
  
  private int attackUnits;
  private int defendUnits;
  private String attackerPlayerId;
  private String defenderPlayerId;
  private int attackerTerritoryId;
  private int defenderTerritoryId;
  private List<Integer> attackerDiceRolls;
  private List<Integer> defenderDiceRolls;
  private int deltaAttack;
  private int deltaDefend;

  public Attack() {}
  
  public Attack(Map<String, Object> attacker, Map<String, Object> defender,
      List<Integer> attackerDiceRolls, List<Integer> defenderDiceRolls) {
    attackUnits = (int)attacker.get(GameResources.UNITS);
    defendUnits = (int)defender.get(GameResources.UNITS);
    attackerPlayerId = (String)attacker.get(GameResources.PLAYER);
    defenderPlayerId = (String)defender.get(GameResources.PLAYER);
    attackerTerritoryId = (int)attacker.get(GameResources.TERRITORY);
    defenderTerritoryId = (int)defender.get(GameResources.TERRITORY);
    this.attackerDiceRolls = attackerDiceRolls;
    this.defenderDiceRolls = defenderDiceRolls;
    Collections.sort(attackerDiceRolls, Collections.reverseOrder());
    Collections.sort(defenderDiceRolls, Collections.reverseOrder());
    calculateAttackResults();
   }
  
  private void calculateAttackResults() {
    int count = 0;
    while (count < attackerDiceRolls.size() && count < defenderDiceRolls.size()) {
      if (defenderDiceRolls.get(count) >= attackerDiceRolls.get(count)) {
        deltaAttack--;
      }
      else {
        deltaDefend--;
      }
      count++;
    }
  }
  
  public List<Integer> getAttackerDiceRolls() {
    return attackerDiceRolls;
  }

  public List<Integer> getDefenderDiceRolls() {
    return defenderDiceRolls;
  }

  public int getDeltaAttack() {
    return deltaAttack;
  }

  public int getDeltaDefend() {
    return deltaDefend;
  }

  public int getAttackUnits() {
    return attackUnits;
  }

  public int getDefendUnits() {
    return defendUnits;
  }

  public String getAttackerPlayerId() {
    return attackerPlayerId;
  }

  public String getDefenderPlayerId() {
    return defenderPlayerId;
  }
  
  public int getAttackerTerritoryId() {
    return attackerTerritoryId;
  }
  
  public int getDefenderTerritoryId() {
    return defenderTerritoryId;
  }
}
