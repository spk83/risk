package org.risk.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.risk.client.GameApi.Operation;
import org.risk.client.GameApi.Set;
import org.risk.client.GameApi.SetRandomInteger;
import org.risk.client.GameApi.SetTurn;
import org.risk.client.GameApi.Shuffle;
import org.risk.client.GameApi.VerifyMove;
import org.risk.client.GameApi.VerifyMoveDone;
import org.risk.client.GameApi.SetVisibility;
import org.risk.client.GameApi.Delete;
import org.risk.client.GameApi.EndGame;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class RiskLogic {
  
  public VerifyMoveDone verify(VerifyMove verifyMove) {
    try {
      checkMoveIsLegal(verifyMove);
      return new VerifyMoveDone();
    } catch (Exception e) {
      return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
    }
  }
  
  void checkMoveIsLegal(VerifyMove verifyMove) {
    List<Operation> lastMove = verifyMove.getLastMove();
    Map<String, Object> lastState = verifyMove.getLastState();
    // Checking the operations are as expected.
    List<Operation> expectedOperations = getExpectedOperations(
        lastState, lastMove, verifyMove.getPlayerIds(), verifyMove.getLastMovePlayerId(),
        verifyMove.state);
    check(expectedOperations.equals(lastMove), expectedOperations, lastMove);
    // We use SetTurn, so we don't need to check that the correct player did the move.
    // However, we do need to check the first move is done by the white player (and then in the
    // first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
    if (lastState.isEmpty() || lastState.get(GameResources.PHASE).toString()
        .equals(GameResources.SET_TURN_ORDER)) {
      check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
    }
  }

  @SuppressWarnings("unchecked")
  List<Operation> getExpectedOperations(
      Map<String, Object> lastApiState, List<Operation> lastMove, List<Integer> playerIds,
      int lastMovePlayerId, Map<String, Object> newState) {
    
    // Initial Operations from empty state
    if (lastApiState.isEmpty()) {
      return getInitialOperations(GameResources.getPlayerKeys(playerIds));
    }
    
    RiskState lastState = gameApiStateToRiskState(lastApiState, lastMovePlayerId, playerIds);
    
    if (lastState.getPhase().equals(GameResources.SET_TURN_ORDER)) {
      return setTurnOrderMove(lastState);
    }
    else if (lastState.getPhase().equals(GameResources.CLAIM_TERRITORY)) {
      Map<String, Object> playerValue = (Map<String, Object>) ((Set) lastMove.get(1)).getValue();
      Map<String, Integer> territoryUnitMap = 
          (Map<String, Integer>) playerValue.get(GameResources.TERRITORY);
      return performClaimTerritory(
          lastState, territoryUnitMap, GameResources.playerIdToString(lastMovePlayerId));
    }
    else if (lastState.getPhase().equals(GameResources.DEPLOYMENT)) {
      Map<String, Object> playerValue = (Map<String, Object>) ((Set) lastMove.get(1)).getValue();
      Map<String, Integer> territoryUnitMap = 
          (Map<String, Integer>) playerValue.get(GameResources.TERRITORY);
      return performDeployment(
          lastState, territoryUnitMap, GameResources.playerIdToString(lastMovePlayerId));
    }
    else if (lastState.getPhase().equals(GameResources.CARD_TRADE)) {
      if (lastMove.size() > 3) {
        boolean isCardTrade = ((Set)lastMove.get(lastMove.size() - 3)).getKey().equals(
            GameResources.CARDS_BEING_TRADED);
        if (isCardTrade) {
          Map<String, Object> playerValue = (Map<String, Object>)((Set) lastMove.get(1)).getValue();
          return performTrade(
              lastState, playerValue, GameResources.playerIdToString(lastMovePlayerId),
              gameApiStateToRiskState(newState, lastMovePlayerId, playerIds));
        }
      }
      else {
        return skipCardTrade(GameResources.playerIdToString(lastMovePlayerId));
      }
    }
    else if (lastState.getPhase().equals(GameResources.ADD_UNITS)) {
      boolean isCardTraded = false;
      if (lastState.getCardsTraded() != null) {
        isCardTraded = lastState.getCardsTraded().size() > 0; 
      }
      if (isCardTraded) { 
        Map<String, Object> playerValue = (Map<String, Object>)((Set) lastMove.get(1)).getValue();
        return performAddUnitsWithTrade(
            lastState, playerValue, GameResources.playerIdToString(lastMovePlayerId));
      }
      else {
        Map<String, Object> playerValue = (Map<String, Object>)((Set) lastMove.get(1)).getValue();
        return performAddUnitsWithOutTrade(
            lastState, playerValue, GameResources.playerIdToString(lastMovePlayerId));
      }
    }
    else if (lastState.getPhase().equals(GameResources.REINFORCE)) {
      Map<String, Object> playerValue = (Map<String, Object>)((Set) lastMove.get(1)).getValue();
      return performReinforce(
          lastState, playerValue, GameResources.playerIdToString(lastMovePlayerId));
    }
    else if (lastState.getPhase().equals(GameResources.ATTACK_PHASE)) {
      String nextPhase = ((Set) lastMove.get(lastMove.size() - 1)).getValue().toString();
      if (nextPhase.equals(GameResources.ATTACK_RESULT)) {
        Map<String, Object> attacker = (Map<String, Object>) ((Set) lastMove.get(1)).getValue();
        Map<String, Object> defender = (Map<String, Object>) ((Set) lastMove.get(2)).getValue();
        return performAttack(
            lastState, attacker, defender, GameResources.playerIdToString(lastMovePlayerId));
      }
      else if (nextPhase.equals(GameResources.FORTIFY)) {
        if (lastState.getTerritoryWinner() != null) {
            return performEndAttack(lastState, GameResources.playerIdToString(lastMovePlayerId));
        } else {
            return performEndAttackWithNoCard(lastMovePlayerId);
        }
      }
    }
    else if (lastState.getPhase().equals(GameResources.ATTACK_RESULT)) {
      String nextPhase = ((Set) lastMove.get(lastMove.size() - 1)).getValue().toString();
      if (nextPhase.equals(GameResources.ATTACK_OCCUPY)) {
          return performAttackResultOnTerritoryWin(lastState,
              GameResources.playerIdToString(lastMovePlayerId));
      }
      if (nextPhase.equals(GameResources.ATTACK_PHASE)) {
        return performAttackResultOnLoss(lastState, 
            GameResources.playerIdToString(lastMovePlayerId));
      }
      if(nextPhase.equals(GameResources.END_GAME)) {
        return performAttackResultOnEndGame(lastState,
            GameResources.playerIdToString(lastMovePlayerId));
        
      }
    }
    else if (lastState.getPhase().equals(GameResources.ATTACK_OCCUPY)) {
      return performAttackOccupy(lastState, (Map<String, Object>)((Set)lastMove.get(1)).getValue(),
          GameResources.playerIdToString(lastMovePlayerId));
    }
    else if (lastState.getPhase().equals(GameResources.FORTIFY)) {
      String playerIdString = GameResources.playerIdToString(lastMovePlayerId);
      if (((Set)lastMove.get(1)).getKey().equals(playerIdString)) {
        return performFortify(lastState, (Map<String, Object>)((Set)lastMove.get(1)).getValue(),
            GameResources.playerIdToString(lastMovePlayerId));
      }
      else {
        return performFortify(lastState, null, GameResources.playerIdToString(lastMovePlayerId));
      }
    }
    else if(lastState.getPhase().equals(GameResources.END_GAME)) {
      return performEndGame(lastState, GameResources.playerIdToString(lastMovePlayerId));
    }
    return null;
  }

  private List<Operation> performEndGame(RiskState lastState,
      String playerIdToString) {
    List<Operation> endGameOperations = Lists.newArrayList();
    check(lastState.getTurnOrder().size() == 1);
    check(lastState.getTerritoryWinner().equals(playerIdToString));
    check(lastState.getPlayersMap().size() == 1);
    check(lastState.getUnclaimedTerritory().size() == 1);
    Player player = lastState.getPlayersMap().get(playerIdToString);
    check(player.getContinent().size() == Continent.continentName.size() - 1);
    check(player.getTerritoryUnitMap().size() == GameResources.TOTAL_TERRITORIES - 1);
    endGameOperations.add(new EndGame(GameResources.playerIdToInt(playerIdToString)));
    return endGameOperations;
  }

  @SuppressWarnings("unchecked")
  private List<Operation> performFortify(RiskState lastState,
      Map<String, Object> value, String playerIdToString) {
    List<Operation> attackOperations = Lists.newArrayList();
    List<Integer> turnOrder = lastState.getTurnOrder();
    int nextPlayerId = turnOrder.get((turnOrder.indexOf(
        GameResources.playerIdToInt(playerIdToString)) + 1)%turnOrder.size());
    attackOperations.add(new SetTurn(nextPlayerId));
    if (value != null) {
      Player player = lastState.getPlayersMap().get(playerIdToString);
      Map<String, Integer> oldTerritoryMap = player.getTerritoryUnitMap();
      Map<String, Integer> territoryMap = (Map<String, Integer>) value.get(GameResources.TERRITORY);
      check(oldTerritoryMap.size() == territoryMap.size(), oldTerritoryMap, territoryMap);
      Map<String, Integer> differenceMap = differenceTerritoryMap(oldTerritoryMap, territoryMap);
      check(differenceMap.size() == 2, oldTerritoryMap, territoryMap, differenceMap);
      Iterator<Entry<String, Integer>> iterator = differenceMap.entrySet().iterator();
      Entry<String, Integer> territoryEntry1 = iterator.next();
      Entry<String, Integer> territoryEntry2 = iterator.next();
      String territory1 = territoryEntry1.getKey();
      int territoryDelta1 = territoryEntry1.getValue();
      String territory2 = territoryEntry2.getKey();
      int territoryDelta2 = territoryEntry2.getValue();
      check((territoryDelta1 + territoryDelta2) == 0, oldTerritoryMap, territoryMap, differenceMap);
      check(Territory.isFortifyPossible(Integer.parseInt(territory1), Integer.parseInt(territory2), 
          new ArrayList<String>(oldTerritoryMap.keySet())));
      oldTerritoryMap.put(territory1, territoryDelta1 + oldTerritoryMap.get(territory1));
      oldTerritoryMap.put(territory2, territoryDelta2 + oldTerritoryMap.get(territory2));
      attackOperations.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
          GameResources.CARDS, player.getCards(),
          GameResources.TERRITORY, player.getTerritoryUnitMap(),
          GameResources.UNCLAIMED_UNITS, 0,
          GameResources.CONTINENT, player.getContinent())));
    }
    attackOperations.add(new Set(GameResources.PHASE, GameResources.CARD_TRADE));
    return attackOperations;
  }

  private List<Operation> performAttackResultOnEndGame(RiskState lastState,
      String playerIdToString) {
    List<Operation> attackOperations = Lists.newArrayList();
    Attack attack = lastState.getAttack();
    Player attacker = lastState.getPlayersMap().get(playerIdToString);
    Player defender = lastState.getPlayersMap().get(attack.getDefenderPlayerId());
    Map<String, Integer> attackerTerritoryMap = attacker.getTerritoryUnitMap();
    Map<String, Integer> defenderTerritoryMap = defender.getTerritoryUnitMap();
    int attackerUnits = attack.getAttackUnits();
    int defenderUnits = attack.getDefendUnits();
    int deltaAttack = attack.getDeltaAttack();
    int deltaDefend = attack.getDeltaDefend();
    int resultAttack = attackerUnits + deltaAttack;
    int resultDefend = defenderUnits + deltaDefend;
    check(resultAttack >= 1, attack.getAttackUnits(), deltaAttack);
    check(resultDefend == 0, attack.getDefendUnits(), deltaDefend);
    attackerTerritoryMap.put(attack.getAttackerTerritoryId()+"", resultAttack);
    defenderTerritoryMap.put(attack.getDefenderTerritoryId()+"", resultDefend);
    defenderTerritoryMap.remove(attack.getDefenderTerritoryId()+"");
    check(defenderTerritoryMap.size() == 0);
    attacker.getCards().addAll(defender.getCards());
    check(attackerTerritoryMap.size() == GameResources.TOTAL_TERRITORIES - 1);
    List<Integer> turnOrder = Lists.newArrayList(lastState.getTurnOrder());
    turnOrder.remove(turnOrder.indexOf(GameResources.playerIdToInt(attack.getDefenderPlayerId())));
    check(turnOrder.size() == 1, turnOrder);
  
    attackOperations.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    attackOperations.add(new Set(attack.getAttackerPlayerId(), ImmutableMap.<String, Object>of(
        GameResources.CARDS, attacker.getCards(),
        GameResources.TERRITORY, attacker.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.CONTINENT, attacker.getContinent())));
    attackOperations.add(new Delete(attack.getDefenderPlayerId()));
    attackOperations.add(new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of
       (attack.getDefenderTerritoryId())));
    attackOperations.add(new Set(GameResources.LAST_ATTACKING_TERRITORY, 
        attack.getAttackerTerritoryId()));
    attackOperations.add(new Set(GameResources.TERRITORY_WINNER, attack.getAttackerPlayerId()));
    for (int dice = 0; dice < attack.getAttackerDiceRolls().size(); ++dice) {
      attackOperations.add(new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + (dice+1)));
    }
    attackOperations.add(new Delete(GameResources.ATTACKER));
    for (int dice = 0; dice < attack.getDefenderDiceRolls().size(); ++dice) {
      attackOperations.add(new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + (dice+1)));
    }
    attackOperations.add(new Delete(GameResources.DEFENDER));
    attackOperations.add(new Set(GameResources.TURN_ORDER, turnOrder));
    attackOperations.add(new Set(GameResources.PHASE, GameResources.END_GAME)); 
    return attackOperations;
  }

  private List<Operation> performEndAttackWithNoCard(int playerId) {
    List<Operation> endAttackOperations = Lists.newArrayList();
    endAttackOperations.add(new SetTurn(playerId));
    endAttackOperations.add(new Set(GameResources.PHASE, GameResources.FORTIFY));
    return endAttackOperations;
  }

  @SuppressWarnings("unchecked")
  private List<Operation> performAttackOccupy(RiskState lastState,
      Map<String, Object> playerMap, String playerIdToString) {
    int unclaimedTerritory = lastState.getUnclaimedTerritory().get(0);
    int attackingTerritory = lastState.getLastAttackingTerritory();
    Map<String, Integer> territoryMap = 
        (Map<String, Integer>)playerMap.get(GameResources.TERRITORY);
    Player player = lastState.getPlayersMap().get(playerIdToString);
    Map<String, Integer> oldTerritoryMap = player.getTerritoryUnitMap();
    int minMovingUnits = GameResources.getMinUnitsToNewTerritory
        (oldTerritoryMap.get(attackingTerritory+""));
    check(minMovingUnits != 0, oldTerritoryMap);
    int newUnitsAtUnclaimed = territoryMap.get(unclaimedTerritory+"");
    int newUnitsAtAttacking = territoryMap.get(attackingTerritory+"");
    int oldUnitsAtUnclaimed = 0;
    int oldUnitsAtAttacking = oldTerritoryMap.get(attackingTerritory+"");
    check(newUnitsAtAttacking >= 1, territoryMap, lastState);
    check(newUnitsAtUnclaimed >= minMovingUnits, territoryMap, lastState);
    check( newUnitsAtAttacking + newUnitsAtUnclaimed 
        == oldUnitsAtAttacking + oldUnitsAtUnclaimed, territoryMap, oldTerritoryMap);
    oldTerritoryMap.put(attackingTerritory+"", newUnitsAtAttacking);
    oldTerritoryMap.put(unclaimedTerritory+"", newUnitsAtUnclaimed);
    java.util.Set<String> oldTerritorySet = oldTerritoryMap.keySet();
    for (int i = 0; i < GameResources.TOTAL_CONTINENTS; i++) {
      if (oldTerritorySet.containsAll(Continent.territorySet.get(i + ""))) {
        if (!player.getContinent().contains(i + "")) {
          player.getContinent().add(i + "");
        }
      }
    }
    List<Operation> attackOperations = Lists.newArrayList();
    attackOperations.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    attackOperations.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
        GameResources.CARDS, player.getCards(),
        GameResources.TERRITORY, player.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.CONTINENT, player.getContinent())));
    attackOperations.add(new Set(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT));
    attackOperations.add(new Delete(GameResources.LAST_ATTACKING_TERRITORY));
    attackOperations.add(new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    return attackOperations;
  }

  private List<Operation> performAttackResultOnLoss(RiskState lastState,
      String playerIdToString) {
    Attack attack = lastState.getAttack();
    Player attacker = lastState.getPlayersMap().get(playerIdToString);
    Player defender = lastState.getPlayersMap().get(attack.getDefenderPlayerId());
    Map<String, Integer> attackerTerritoryMap = attacker.getTerritoryUnitMap();
    Map<String, Integer> defenderTerritoryMap = defender.getTerritoryUnitMap();
    int attackerUnits = attackerTerritoryMap.get(attack.getAttackerTerritoryId()+"");
    int defenderUnits = defenderTerritoryMap.get(attack.getDefenderTerritoryId()+"");
    attackerTerritoryMap.put(
        attack.getAttackerTerritoryId()+"", attackerUnits + attack.getDeltaAttack());
    defenderTerritoryMap.put(
        attack.getDefenderTerritoryId()+"", defenderUnits + attack.getDeltaDefend());
    List<Operation> attackOperations = Lists.newArrayList();
    attackOperations.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    attackOperations.add(new Set(attack.getAttackerPlayerId(), ImmutableMap.<String, Object>of(
        GameResources.CARDS, attacker.getCards(),
        GameResources.TERRITORY, attacker.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.CONTINENT, attacker.getContinent())));
    attackOperations.add(new Set(attack.getDefenderPlayerId(), ImmutableMap.<String, Object>of(
        GameResources.CARDS, defender.getCards(),
        GameResources.TERRITORY, defender.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.CONTINENT, defender.getContinent())));
    for (int dice = 0; dice < attack.getAttackerDiceRolls().size(); ++dice) {
      attackOperations.add(new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + (dice+1)));
    }
    attackOperations.add(new Delete(GameResources.ATTACKER));
    for (int dice = 0; dice < attack.getDefenderDiceRolls().size(); ++dice) {
      attackOperations.add(new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + (dice+1)));
    }
    attackOperations.add(new Delete(GameResources.DEFENDER));
    attackOperations.add(new Set(GameResources.PHASE, GameResources.ATTACK_PHASE)); 
    return attackOperations;
  }

  private List<Operation> performAttackResultOnTerritoryWin(RiskState lastState,
      String playerIdToString) {
    Attack attack = lastState.getAttack();
    Player attacker = lastState.getPlayersMap().get(playerIdToString);
    Player defender = lastState.getPlayersMap().get(attack.getDefenderPlayerId());
    Map<String, Integer> attackerTerritoryMap = attacker.getTerritoryUnitMap();
    Map<String, Integer> defenderTerritoryMap = defender.getTerritoryUnitMap();
    int attackerUnits = attackerTerritoryMap.get(attack.getAttackerTerritoryId()+"");
    int defenderUnits = defenderTerritoryMap.get(attack.getDefenderTerritoryId()+"");
    attackerTerritoryMap.put(
        attack.getAttackerTerritoryId()+"", attackerUnits + attack.getDeltaAttack());
    defenderTerritoryMap.put(
        attack.getDefenderTerritoryId()+"", defenderUnits + attack.getDeltaDefend());
    check(defenderUnits + attack.getDeltaDefend() == 0, attacker, defender, attack);
    defenderTerritoryMap.remove(attack.getDefenderTerritoryId()+"");
    java.util.Set<String> defenderTerritorySet = defenderTerritoryMap.keySet();
    List<String> defenderContinentList = Lists.newArrayList(defender.getContinent());
    for (String continentId : defender.getContinent()) {
      if (!defenderTerritorySet.containsAll(Continent.territorySet.get(continentId))) {
        defenderContinentList.remove(continentId);
      }
    }
    defender.setContinent(defenderContinentList);
    List<Operation> attackOperations = Lists.newArrayList();
    attackOperations.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    attackOperations.add(new Set(attack.getAttackerPlayerId(), ImmutableMap.<String, Object>of(
        GameResources.CARDS, attacker.getCards(),
        GameResources.TERRITORY, attacker.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.CONTINENT, attacker.getContinent())));
    attackOperations.add(new Set(attack.getDefenderPlayerId(), ImmutableMap.<String, Object>of(
        GameResources.CARDS, defender.getCards(),
        GameResources.TERRITORY, defender.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, 0,
        GameResources.CONTINENT, defender.getContinent())));
    attackOperations.add(new Set(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of
       (attack.getDefenderTerritoryId())));
    attackOperations.add(new Set(GameResources.LAST_ATTACKING_TERRITORY, 
        attack.getAttackerTerritoryId()));
    attackOperations.add(new Set(GameResources.TERRITORY_WINNER, attack.getAttackerPlayerId()));
    for (int dice = 0; dice < attack.getAttackerDiceRolls().size(); ++dice) {
      attackOperations.add(new Delete(GameResources.ATTACKER + GameResources.DICE_ROLL + (dice+1)));
    }
    attackOperations.add(new Delete(GameResources.ATTACKER));
    for (int dice = 0; dice < attack.getDefenderDiceRolls().size(); ++dice) {
      attackOperations.add(new Delete(GameResources.DEFENDER + GameResources.DICE_ROLL + (dice+1)));
    }
    attackOperations.add(new Delete(GameResources.DEFENDER));
    attackOperations.add(new Set(GameResources.PHASE, GameResources.ATTACK_OCCUPY)); 
    return attackOperations;
  }

  private List<Operation> performEndAttack(RiskState lastState,
      String playerIdToString) {
    List<Operation> endAttackOperations = Lists.newArrayList();
    int playerId = GameResources.playerIdToInt(playerIdToString);
    check(lastState.getTerritoryWinner().equals(playerIdToString), lastState.getTerritoryWinner(), 
        playerIdToString);
    endAttackOperations.add(new SetTurn(playerId));
    Player player = lastState.getPlayersMap().get(playerIdToString);
    List<String> deck = lastState.getDeck();
    int card = Integer.parseInt(deck.get(0).substring(2));
    player.getCards().add(card);
    deck.remove(0);
    endAttackOperations.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
        GameResources.CARDS, player.getCards(),
        GameResources.TERRITORY, player.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, player.getUnclaimedUnits(),
        GameResources.CONTINENT, player.getContinent())));
    endAttackOperations.add(new Set(GameResources.DECK, deck));
    endAttackOperations.add(new SetVisibility(GameResources.RISK_CARD + card,
        ImmutableList.<Integer>of(playerId)));
    endAttackOperations.add(new Delete(GameResources.TERRITORY_WINNER));
    endAttackOperations.add(new Set(GameResources.PHASE, GameResources.FORTIFY));
    return endAttackOperations;
  }

  private List<Operation> performAttack(RiskState lastState,
      Map<String, Object> attacker, Map<String, Object> defender,
      String playerIdToString) {
    String playerIdOfAttacker = attacker.get(GameResources.PLAYER).toString();
    Integer attackTerritory = (Integer)attacker.get(GameResources.TERRITORY);
    Integer attackUnits = (Integer)attacker.get(GameResources.UNITS);
    check(attackUnits >= 2, attackUnits);
    String playerIdOfDefender = defender.get(GameResources.PLAYER).toString();
    Integer defendTerritory = (Integer)defender.get(GameResources.TERRITORY);
    Integer defendUnits = (Integer)defender.get(GameResources.UNITS);
    check(Territory.isAttackPossible(attackTerritory, defendTerritory), attacker, defender);
    check(playerIdOfAttacker.equals(playerIdToString));
    Player attackerPlayer = lastState.getPlayersMap().get(playerIdOfAttacker);
    Player defenderPlayer = lastState.getPlayersMap().get(playerIdOfDefender);
    check(playerIdOfAttacker.equals(attackerPlayer.getPlayerId()));
    Integer lastStateAttackUnits = attackerPlayer.getTerritoryUnitMap().get(attackTerritory+"");
    Integer lastStateDefendUnits = defenderPlayer.getTerritoryUnitMap().get(defendTerritory+"");
    check(lastStateAttackUnits == attackUnits,attackerPlayer, attacker);
    check(lastStateDefendUnits == defendUnits, defenderPlayer, defender);
    check((lastStateAttackUnits != null) && 
        (lastStateAttackUnits == attackUnits),attackerPlayer, attacker);
    check((lastStateDefendUnits != null) && 
        (lastStateDefendUnits == defendUnits), defenderPlayer, defender);
    int totalDiceAttacker = GameResources.getMaxDiceRollsForAttacker(attackUnits);
    int totalDiceDefender = GameResources.getMaxDiceRollsForDefender(defendUnits);
   
    List<Operation> attackOperations = Lists.newArrayList();
    attackOperations.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    
    attackOperations.add(new Set(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, playerIdOfAttacker,
            GameResources.TERRITORY, attackTerritory,
            GameResources.UNITS, attackUnits)));
    attackOperations.add(new Set(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, playerIdOfDefender,
            GameResources.TERRITORY, defendTerritory, 
            GameResources.UNITS, defendUnits)));
    for (int dice = 0; dice < totalDiceAttacker; ++dice) {
      attackOperations.add(new SetRandomInteger(GameResources.ATTACKER + GameResources.DICE_ROLL + 
          (dice+1), GameResources.MIN_DICE_ROLL, GameResources.MAX_DICE_ROLL + 1));
    }
    for (int dice = 0; dice < totalDiceDefender; ++dice) {
      attackOperations.add(new SetRandomInteger(GameResources.DEFENDER + GameResources.DICE_ROLL + 
          (dice+1), GameResources.MIN_DICE_ROLL, GameResources.MAX_DICE_ROLL + 1));
    }
    attackOperations.add(new Set(GameResources.PHASE, GameResources.ATTACK_RESULT)); 
    return attackOperations;
  }

  @SuppressWarnings("unchecked")
  private List<Operation> performReinforce(RiskState lastState,
      Map<String, Object> playerValue, String playerIdToString) {
    List<Operation> move = Lists.newArrayList();
    Player player = lastState.getPlayersMap().get(playerIdToString);
    int oldUnclaimedUnits = player.getUnclaimedUnits();
    check(oldUnclaimedUnits >= GameResources.MIN_ALLOCATED_UNITS, oldUnclaimedUnits);
    int differenceUnclaimedUnits = oldUnclaimedUnits - 
        Integer.parseInt(playerValue.get(GameResources.UNCLAIMED_UNITS).toString());
    int currentUnclaimedUnits = Integer.parseInt(
        playerValue.get(GameResources.UNCLAIMED_UNITS).toString());
    check(currentUnclaimedUnits == 0, playerValue);
    Map<String, Integer> territoryUnitMap = 
        (Map<String, Integer>) playerValue.get(GameResources.TERRITORY);
    java.util.Set<String> newTerritorySet = new HashSet<String>(territoryUnitMap.keySet());
    Map<String, Integer> oldTerritoryMap = player.getTerritoryUnitMap();
    java.util.Set<String> oldTerritorySet = oldTerritoryMap.keySet();
    check(newTerritorySet.size() == oldTerritorySet.size(), newTerritorySet, oldTerritorySet);
    newTerritorySet.removeAll(oldTerritorySet);
    check(newTerritorySet.size() == 0, newTerritorySet, oldTerritorySet);
    Map<String, Integer> differenceTerritoryMap = differenceTerritoryMap
        (oldTerritoryMap, territoryUnitMap);
    int reinforcedUnits = 0;
    for ( Entry<String, Integer> entry : differenceTerritoryMap.entrySet()) {
      check(entry.getValue() > 0, entry);
      reinforcedUnits += entry.getValue();
      oldTerritoryMap.put(entry.getKey(), oldTerritoryMap.get(entry.getKey()) + entry.getValue());
    }
    check(reinforcedUnits <= differenceUnclaimedUnits, reinforcedUnits, differenceUnclaimedUnits);
    player.setUnclaimedUnits(0);
    move.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    move.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
        GameResources.CARDS, player.getCards(),
        GameResources.TERRITORY, player.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, player.getUnclaimedUnits(),
        GameResources.CONTINENT, player.getContinent())));
    move.add(new Set(GameResources.PHASE, GameResources.ATTACK_PHASE));
    return move;
  }

  private List<Operation> performAddUnitsWithOutTrade(RiskState lastState,
      Map<String, Object> playerValue, String playerIdToString) {
    List<Operation> move = Lists.newArrayList();
    int unclaimedTerritoryNew = Integer.parseInt(
        playerValue.get(GameResources.UNCLAIMED_UNITS).toString());
    Player player = lastState.getPlayersMap().get(playerIdToString);
    int unclaimedTerritoryOld = player.getUnclaimedUnits();
    int addUnits = calculateUnits(player.getTerritoryUnitMap().size(), player.getContinent());
    check(unclaimedTerritoryNew == (unclaimedTerritoryOld + addUnits), unclaimedTerritoryNew,
        unclaimedTerritoryOld, addUnits);
    player.setUnclaimedUnits(unclaimedTerritoryNew); 
    move.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    move.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
        GameResources.CARDS, player.getCards(),
        GameResources.TERRITORY, player.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, player.getUnclaimedUnits(),
        GameResources.CONTINENT, player.getContinent())));
    move.add(new Set(GameResources.PHASE, GameResources.REINFORCE));
    return move;
  }

  private List<Operation> performAddUnitsWithTrade(RiskState lastState,
      Map<String, Object> playerValue, String playerIdToString) {
    List<Operation> move = Lists.newArrayList();
    int unclaimedTerritoryNew = Integer.parseInt(
        playerValue.get(GameResources.UNCLAIMED_UNITS).toString());
    Player player = lastState.getPlayersMap().get(playerIdToString);
    int unclaimedTerritoryOld = player.getUnclaimedUnits();
    int addUnits = calculateUnits(player.getTerritoryUnitMap().size(), player.getContinent());
    check(unclaimedTerritoryNew == (unclaimedTerritoryOld + addUnits), unclaimedTerritoryNew,
        unclaimedTerritoryOld, addUnits);
    player.setUnclaimedUnits(unclaimedTerritoryNew);
    List<String> deck = lastState.getDeck();
    deck.add(GameResources.RISK_CARD + lastState.getCardsTraded().get(0));
    deck.add(GameResources.RISK_CARD + lastState.getCardsTraded().get(1));
    deck.add(GameResources.RISK_CARD + lastState.getCardsTraded().get(2));
    
    move.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    move.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
        GameResources.CARDS, player.getCards(),
        GameResources.TERRITORY, player.getTerritoryUnitMap(),
        GameResources.UNCLAIMED_UNITS, player.getUnclaimedUnits(),
        GameResources.CONTINENT, player.getContinent())));
    move.add(new Delete(GameResources.CARDS_BEING_TRADED));
    move.add(new SetVisibility(GameResources.RISK_CARD + lastState.getCardsTraded().get(0),
        GameResources.EMPTYLISTINT));
    move.add(new SetVisibility(GameResources.RISK_CARD + lastState.getCardsTraded().get(1),
        GameResources.EMPTYLISTINT));
    move.add(new SetVisibility(GameResources.RISK_CARD + lastState.getCardsTraded().get(2),
        GameResources.EMPTYLISTINT));    
    move.add(new Shuffle(deck));
    move.add(new Set(GameResources.DECK, deck));
    move.add(new Set(GameResources.PHASE, GameResources.REINFORCE));
    return move;
  }

  private int calculateUnits(int size, List<String> continent) {
    int newUnits = size / 3;
    if (newUnits < GameResources.MIN_ALLOCATED_UNITS) {
      newUnits = GameResources.MIN_ALLOCATED_UNITS;
    }
    for (String continentId : continent) {
      newUnits += Continent.unitsValue.get(continentId);
    }
    return newUnits;
  }

  private List<Operation> skipCardTrade(String playerIdToString) {
    List<Operation> move = Lists.newArrayList();
    move.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    move.add(new Set(GameResources.PHASE, GameResources.ADD_UNITS));
    return move;
  }

  private List<Operation> performTrade(RiskState lastState,
      Map<String, Object> playerValue, String playerIdToString,
      RiskState newState) {
    List<Integer> playerCards = lastState.getPlayersMap().get(playerIdToString).getCards();
    Map<String, Card> visibleCards = newState.getCardMap();
    List<Card> tradedCards = Lists.newArrayList();
    List<Integer> tradedCardsInt = Lists.newArrayList();
    List<String> tradedCardsString = Lists.newArrayList();
    Integer tradeNumber = lastState.getTradeNumber();
    check(playerCards.size() >= 3,
        lastState.getPlayersMap().get(playerIdToString));
    for (Integer cardId : playerCards) {
      Card card = visibleCards.get(GameResources.RISK_CARD + cardId);
      if (card != null) {
        tradedCards.add(card);
        tradedCardsInt.add(cardId);
        tradedCardsString.add(GameResources.RISK_CARD + cardId);
      }
    }
    int unclaimedUnits = Card.getUnits(tradedCards, tradeNumber);
    Player player = lastState.getPlayersMap().get(playerIdToString);
    player.setUnclaimedUnits(unclaimedUnits);
    player.getCards().removeAll(tradedCardsInt);
    Collections.sort(player.getCards());
    Collections.sort(tradedCardsInt);
    Collections.sort(tradedCardsString);
    List<Operation> move = Lists.newArrayList();
    move.add(new SetTurn(GameResources.playerIdToInt(playerIdToString)));
    move.add(new Set(playerIdToString, ImmutableMap.<String, Object>of(
        GameResources.CARDS, player.getCards(),
        GameResources.UNCLAIMED_UNITS, player.getUnclaimedUnits(),
        GameResources.TERRITORY, player.getTerritoryUnitMap(),
        GameResources.CONTINENT, player.getContinent())));
    for (String tradedCardString : tradedCardsString) {
      move.add(new SetVisibility(tradedCardString));
    }
    move.add(new Set(GameResources.CARDS_BEING_TRADED, tradedCardsInt));
    move.add(new Set(GameResources.TRADE_NUMBER, lastState.getTradeNumber()));
    move.add(new Set(GameResources.PHASE, GameResources.ADD_UNITS));
    return move;
  }

  private List<Operation> performClaimTerritory(RiskState lastState,
      Map<String, Integer> territoryUnitMap, String playerKey) {
    java.util.Set<String> newTerritorySet = new HashSet<String>(territoryUnitMap.keySet());
    Player playerMap = lastState.getPlayersMap().get(playerKey);
    Map<String, Integer> oldTerritoryMap = playerMap.getTerritoryUnitMap();
    java.util.Set<String> oldTerritorySet = oldTerritoryMap.keySet();
    newTerritorySet.removeAll(oldTerritorySet);
    check(newTerritorySet.size() == 1, newTerritorySet, oldTerritorySet);
    String newTerritory = newTerritorySet.iterator().next();
    List<Integer> unclaimedTerritory = Lists.newArrayList(lastState.getUnclaimedTerritory());
    check(unclaimedTerritory.contains(Integer.parseInt(newTerritory)), unclaimedTerritory, newTerritory);
    unclaimedTerritory.remove(unclaimedTerritory.indexOf(Integer.parseInt(newTerritory)));
    check(territoryUnitMap.get(newTerritory) == 1, territoryUnitMap, newTerritory);
    oldTerritoryMap.put(newTerritory, 1);
    oldTerritorySet = oldTerritoryMap.keySet();
    for (int i = 0; i < GameResources.TOTAL_CONTINENTS; i++) {
      if (oldTerritorySet.containsAll(Continent.territorySet.get(i + ""))) {
        if (!playerMap.getContinent().contains(i + "")) {
          playerMap.getContinent().add(i + "");
        }
      }
    }
    playerMap.setTerritoryUnitMap(oldTerritoryMap);
    playerMap.setUnclaimedUnits(playerMap.getUnclaimedUnits() - 1);
    check(playerMap.getUnclaimedUnits() > 0, playerMap.getUnclaimedUnits());
    List<Operation> move = Lists.newArrayList();
    List<Integer> turnOrder = lastState.getTurnOrder();
    int index = turnOrder.indexOf(GameResources.playerIdToInt(playerKey));
    
    if (unclaimedTerritory.size() == 0 || index == (turnOrder.size() - 1)) {
      index = 0;
    } else {
      index++;
    }
    move.add(new SetTurn(turnOrder.get(index)));
    move.add(new Set(playerKey, ImmutableMap.<String, Object>of(
        GameResources.CARDS, playerMap.getCards(),
        GameResources.UNCLAIMED_UNITS, playerMap.getUnclaimedUnits(),
        GameResources.TERRITORY, playerMap.getTerritoryUnitMap(),
        GameResources.CONTINENT, playerMap.getContinent())));
    move.add(new Set(GameResources.UNCLAIMED_TERRITORY, unclaimedTerritory));
    if (unclaimedTerritory.size() == 0) {
      move.add(new Set(GameResources.PHASE, GameResources.DEPLOYMENT));
    }
    return move;
  }
  
  //Assumes both map have equal size
  private Map<String, Integer> differenceTerritoryMap
      (Map<String, Integer> oldTerritories, Map<String, Integer> newTerritories) {
    Map<String, Integer> differenceMap = new HashMap<String, Integer>();
    for (Map.Entry<String, Integer> oldEntry : oldTerritories.entrySet()) {
      int difference = newTerritories.get(oldEntry.getKey()) - oldEntry.getValue();
      if (difference != 0) {
        differenceMap.put(oldEntry.getKey(), difference);
      }
    }
    return differenceMap;
  }
  
  private List<Operation> performDeployment(RiskState lastState,
      Map<String, Integer> territoryUnitMap, String playerKey) {
    java.util.Set<String> newTerritorySet = new HashSet<String>(territoryUnitMap.keySet());
    Player playerMap = lastState.getPlayersMap().get(playerKey);
    Map<String, Integer> oldTerritoryMap = playerMap.getTerritoryUnitMap();
    java.util.Set<String> oldTerritorySet = oldTerritoryMap.keySet();
    check(newTerritorySet.size() == oldTerritorySet.size(), newTerritorySet, oldTerritorySet);
    newTerritorySet.removeAll(oldTerritorySet);
    check(newTerritorySet.size() == 0, newTerritorySet, oldTerritorySet);
    playerMap.setTerritoryUnitMap(oldTerritoryMap);
    playerMap.setUnclaimedUnits(playerMap.getUnclaimedUnits() - 1);
    Map<String, Integer> differenceTerritoryMap = differenceTerritoryMap
        (oldTerritoryMap, territoryUnitMap);
    check(differenceTerritoryMap.size() == 1, differenceTerritoryMap);
    check(differenceTerritoryMap.entrySet().iterator().next().getValue() == 1, 
        differenceTerritoryMap);
    oldTerritoryMap.put(differenceTerritoryMap.entrySet().iterator().next().getKey(), 
        oldTerritoryMap.entrySet().iterator().next().getValue() + 1);
    boolean isDeploymentDone = true;
    for (Entry<String, Player> playerStateMap : lastState.getPlayersMap().entrySet()) {
      isDeploymentDone = isDeploymentDone && playerStateMap.getValue().getUnclaimedUnits() == 0;
    }
    List<Operation> move = Lists.newArrayList();
    List<Integer> turnOrder = lastState.getTurnOrder();
    int nextPlayerIndex = turnOrder.indexOf(GameResources.playerIdToInt(playerKey));
    
    if (isDeploymentDone || nextPlayerIndex == (turnOrder.size() - 1)) {
      nextPlayerIndex = 0;
    } else {
      nextPlayerIndex++;
    }
    move.add(new SetTurn(turnOrder.get(nextPlayerIndex)));
    move.add(new Set(playerKey, ImmutableMap.<String, Object>of(
        GameResources.CARDS, playerMap.getCards(),
        GameResources.UNCLAIMED_UNITS, playerMap.getUnclaimedUnits(),
        GameResources.TERRITORY, playerMap.getTerritoryUnitMap(),
        GameResources.CONTINENT, playerMap.getContinent())));
    if (isDeploymentDone) {
      move.add(new Set(GameResources.PHASE, GameResources.CARD_TRADE));
    }
    return move;
  }


  @SuppressWarnings("unchecked")
  private RiskState gameApiStateToRiskState(Map<String, Object> lastApiState,
      int lastMovePlayerId, List<Integer> playerIds) {
   
    RiskState riskState = new RiskState();
    riskState.setTurn(GameResources.playerIdToString(lastMovePlayerId));
    riskState.setPhase(lastApiState.get(GameResources.PHASE).toString());
    
    Map<String, Player> playersMap = new HashMap<String, Player>();
    for (String playerId : GameResources.getPlayerKeys(playerIds)) {
       Map<String, Object> tempPlayersMap = (Map<String, Object>) lastApiState.get(playerId);
       if (tempPlayersMap != null) {
         playersMap.put(playerId, new Player(playerId, tempPlayersMap));         
       }
    }
    riskState.setPlayersMap(playersMap);
    Map<String, Card> cardsMap = new HashMap<String, Card>();
    for (int i = 0; i <= 43; i++) { 
      Object cardValue = lastApiState.get(GameResources.RISK_CARD + i);
      if ( cardValue != null) {
        cardsMap.put(
            GameResources.RISK_CARD + i, 
            new Card(cardValue.toString(), 
            GameResources.RISK_CARD + i));
      }
    }
    Integer tradeNumber = (Integer)lastApiState.get(GameResources.TRADE_NUMBER);
    if (tradeNumber == null) {
      tradeNumber = 0;
    }
    riskState.setTradeNumber(tradeNumber.intValue() + 1);
    riskState.setCardMap(cardsMap);
    riskState.setDeck((List<String>) lastApiState.get(GameResources.DECK));
    riskState.setUnclaimedTerritory((List<Integer>) lastApiState.get(
        GameResources.UNCLAIMED_TERRITORY));
    
    List<String> diceRolls = getDiceRolls(GameResources.getPlayerKeys(playerIds));
    Map<String, List<Integer>> diceResultMap = new HashMap<String, List<Integer>>();
    int j = 0;
    for (String playerId : GameResources.getPlayerKeys(playerIds)) {
      List<Integer> results = Lists.newArrayList();
      for (int i = 0; i < GameResources.TOTAL_INITIAL_DICE_ROLL; i++) {
        Object tempDiceRoll = lastApiState.get(diceRolls.get(j));
        if (tempDiceRoll != null) {
          results.add(Integer.parseInt(tempDiceRoll.toString()));
          j++;
        }
      }
      diceResultMap.put(playerId, results);
    }
    riskState.setDiceResult(diceResultMap);
    
    riskState.setTurnOrder((List<Integer>) lastApiState.get(GameResources.TURN_ORDER));
    riskState.setCardsTraded((List<Integer>) lastApiState.get(GameResources.CARDS_BEING_TRADED));
    Map<String, Object> attacker = (Map<String, Object>) lastApiState.get(GameResources.ATTACKER);
    Map<String, Object> defender = (Map<String, Object>) lastApiState.get(GameResources.DEFENDER);
    if (attacker != null && defender != null) {
      List<Integer> attackerDiceRolls = getDiceRolls(lastApiState, GameResources.ATTACKER);
      List<Integer> defenderDiceRolls = getDiceRolls(lastApiState, GameResources.DEFENDER);
      riskState.setAttack(new Attack(attacker, defender, attackerDiceRolls, defenderDiceRolls));
    }
    Integer lastAttackingTerritory = (Integer)lastApiState.get(
        GameResources.LAST_ATTACKING_TERRITORY);
    riskState.setLastAttackingTerritory(lastAttackingTerritory);
    
    String territoryWinner = (String) lastApiState.get(GameResources.TERRITORY_WINNER);    
    riskState.setTerritoryWinner(territoryWinner);
    return riskState;
  }
  
  private List<Integer> getDiceRolls(Map<String, Object> lastApiState, String type) {
    List<Integer> diceRolls = new ArrayList<Integer>();
    boolean rolls = true;
    int count = 0;
    while(rolls) {
      Integer diceRoll = (Integer)lastApiState.get(
          type + GameResources.DICE_ROLL + (++count));
      if (diceRoll != null) {
        diceRolls.add(diceRoll);
      }
      else {
        rolls = false;
      }
    }
    return diceRolls;
  }
  
  private List<Operation> setTurnOrderMove(RiskState lastState) {
    List<Operation> turnOrderMove = Lists.newArrayList();
    int nextTurn;
    ImmutableSortedSet<Integer> turnOrder;
    Map<String, List<Integer>> diceResult = lastState.getDiceResult();
    Map<Integer, Integer> result = new HashMap<Integer, Integer>();
    for (Map.Entry<String, List<Integer>> entry : diceResult.entrySet()) {
      int total = 0;
      for (int i : entry.getValue()) {
        total += i;
      }
      result.put(GameResources.playerIdToInt(entry.getKey()), total);
    }
    
    Ordering<Integer> valueComparator = Ordering.natural().onResultOf(
        Functions.forMap(result)).compound(Ordering.natural());
    ImmutableSortedMap<Integer, Integer> resultMap = ImmutableSortedMap.copyOf(
        result, valueComparator);
    turnOrder = resultMap.descendingKeySet();
    nextTurn = turnOrder.first();
    turnOrderMove.add(new SetTurn(nextTurn));
    turnOrderMove.add(new Set(GameResources.PHASE, GameResources.CLAIM_TERRITORY));
    turnOrderMove.add(new Set(GameResources.TURN_ORDER, turnOrder.asList()));
    return turnOrderMove;
  }

  public List<Operation> getInitialOperations(List<String> playerIds) {
    List<Operation> operations = Lists.newArrayList();

    // set TURN to first player 
    operations.add(new SetTurn(GameResources.START_PLAYER_ID));
    
    // set PHASE to SET_TURN_ORDER
    operations.add(new Set(GameResources.PHASE, GameResources.SET_TURN_ORDER));
    
    for (String playerId : playerIds) {
      operations.add(new Set(playerId, setInitialPlayerState(playerIds.size())));
    }
    
    // sets all 44 cards: set(RC0,A0),set(RC1,I1),set(RC2,C2),..,set(RC43,W43)
    for (int i = 0; i < 44; i++) {
        operations.add(
            new Set(GameResources.RISK_CARD + i,
            GameResources.cardIdToString(i),
            GameResources.EMPTYLISTINT));
    }
    
    // Shuffle all the RISK cards in the deck
    operations.add(
        new Shuffle(GameResources.getCardsInRange(0, GameResources.TOTAL_RISK_CARDS - 1)));
 
    // Add cards to deck
    operations.add(new Set(GameResources.DECK, GameResources.getCardsInRange(
        0, GameResources.TOTAL_RISK_CARDS - 1)));
    
    // Create a list of unclaimed territories
    operations.add(
        new Set(GameResources.UNCLAIMED_TERRITORY,
        GameResources.getTerritoriesInRange(0,GameResources.TOTAL_TERRITORIES - 1)));
    
    // Roll dice to decide turn order
    List<String> diceRolls = getDiceRolls(playerIds);
    for (String diceRoll : diceRolls) {
      operations.add(
          new SetRandomInteger
              (diceRoll, GameResources.MIN_DICE_ROLL, GameResources.MAX_DICE_ROLL + 1));
    }
    
    return operations;
  }
    
  public static List<String> getDiceRolls(List<String> playerIds){
    List<String> diceRollList = new ArrayList<String>();
    for (String playerId : playerIds) {
      for (int i = 0; i< GameResources.TOTAL_INITIAL_DICE_ROLL; i++) {
        diceRollList.add(GameResources.DICE_ROLL + "_" + playerId + "_" + i);
      }
    }
   return diceRollList;
  }
  
  private static Map<String, Object> setInitialPlayerState(int totalPlayers){
    Map<String, Object> playerState = ImmutableMap.<String, Object>of(
        GameResources.CARDS, GameResources.EMPTYLISTINT,
        GameResources.UNCLAIMED_UNITS, GameResources.PLAYERS_UNIT_MAP.get(totalPlayers),
        GameResources.TERRITORY, GameResources.EMPTYMAP,
        GameResources.CONTINENT, GameResources.EMPTYLISTSTRING);
    return playerState;
  }  

  private void check(boolean val, Object... debugArguments) {
    if (!val) {
      throw new RuntimeException("Hacker found");
          //+ Arrays.toString(debugArguments));
    }
  }

}
