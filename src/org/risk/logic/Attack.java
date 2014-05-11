package org.risk.logic;

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
  private AttackResult attackResult;

  public Attack() {
  }
  
  public Attack(Map<String, Object> attacker, Map<String, Object> defender,
      List<Integer> attackerDiceRolls, List<Integer> defenderDiceRolls,
      int totalDefenderTerritories, int totalPlayers, int totalAttackerCards,
      int totalDefenderCards) {
    attackUnits = (Integer) attacker.get(GameResources.UNITS);
    defendUnits = (Integer) defender.get(GameResources.UNITS);
    attackerPlayerId = (String) attacker.get(GameResources.PLAYER);
    defenderPlayerId = (String) defender.get(GameResources.PLAYER);
    attackerTerritoryId = (Integer) attacker.get(GameResources.TERRITORY);
    defenderTerritoryId = (Integer) defender.get(GameResources.TERRITORY);
    this.attackerDiceRolls = attackerDiceRolls;
    this.defenderDiceRolls = defenderDiceRolls;
    
    //sorting the dice rolls so that the comparison becomes easy
    Collections.sort(attackerDiceRolls, Collections.reverseOrder());
    Collections.sort(defenderDiceRolls, Collections.reverseOrder());
    attackResult = new AttackResult(
        attackerDiceRolls, defenderDiceRolls, attackUnits, defendUnits, totalDefenderTerritories, 
        totalPlayers, totalAttackerCards, totalDefenderCards);
  }
  
  public final class AttackResult {
    private int deltaAttack;
    private int deltaDefend;
    private boolean isAttackerATerritoryWinner;
    private boolean isDefenderOutOfGame;  //can be true only if isAttackerATerritoryWinner is true
    private boolean isAttackerAWinnerOfGame;  //can be true only if isDefenderOutOfGame is true
    private boolean isTradeRequired;  //can be true only if isDefenderOutOfGame is true
    
    private AttackResult(List<Integer> attackerDiceRolls, List<Integer> defenderDiceRolls,
        int attackUnits, int defendUnits, int totalDefenderTerritories, int totalPlayers, 
        int totalAttackerCards, int totalDefenderCards) {
      calculateUnitLoss(attackerDiceRolls, defenderDiceRolls);
      setTerritoryWinner(defendUnits);
      setDefenderOutOfGame(totalDefenderTerritories);
      setAttackerAWinnerOfGame(totalPlayers);
      setTradeRequired(totalAttackerCards, totalDefenderCards);
    }
    
    public int getDeltaAttack() {
      return deltaAttack;
    }

    public int getDeltaDefend() {
      return deltaDefend;
    }

    private void calculateUnitLoss(
        List<Integer> attackerDiceRolls, List<Integer> defenderDiceRolls) {
      int count = 0;
      deltaAttack = 0;
      deltaDefend = 0;
      while (count < attackerDiceRolls.size() && count < defenderDiceRolls.size()) {
        if (defenderDiceRolls.get(count) >= attackerDiceRolls.get(count)) {
          deltaAttack--;
        } else {
          deltaDefend--;
        }
        count++;
      }
    }
    
    private void setTerritoryWinner(int defendUnits) {
      isAttackerATerritoryWinner = deltaDefend + defendUnits == 0;
    }
    
    private void setDefenderOutOfGame(int totalDefenderTerritories) {
      isDefenderOutOfGame = isAttackerATerritoryWinner && totalDefenderTerritories == 1;
    }
    
    private void setAttackerAWinnerOfGame(int totalPlayers) {
      isAttackerAWinnerOfGame = isDefenderOutOfGame && totalPlayers == 2;
    }
    
    public void setTradeRequired(int totalAttackerCards, int totalDefenderCards) {
      isTradeRequired = isDefenderOutOfGame 
          && (totalAttackerCards + totalDefenderCards) >= GameResources.MAX_CARDS_IN_ATTACK_TRADE;
    }


    public boolean isAttackerATerritoryWinner() {
      return isAttackerATerritoryWinner;
    }

    public boolean isDefenderOutOfGame() {
      return isDefenderOutOfGame;
    }

    public boolean isAttackerAWinnerOfGame() {
      return isAttackerAWinnerOfGame;
    }

    public boolean isTradeRequired() {
      return isTradeRequired;
    }
    
  }

  
  public AttackResult getAttackResult() {
    return attackResult;
  }

  public void setAttackResult(AttackResult attackResult) {
    this.attackResult = attackResult;
  }

  public List<Integer> getAttackerDiceRolls() {
    return attackerDiceRolls;
  }

  public List<Integer> getDefenderDiceRolls() {
    return defenderDiceRolls;
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
