package org.risk.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.client.Attack;
import org.risk.client.Attack.AttackResult;
import org.risk.client.Card;
import org.risk.client.GameApi;
import org.risk.client.GameResources;
import org.risk.client.Player;
import org.risk.client.RiskPresenter;
import org.risk.client.RiskState;
import org.risk.client.Territory;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RiskGraphics extends Composite implements RiskPresenter.View {
    public interface RiskGraphicsUiBinder extends UiBinder<Widget, RiskGraphics> {
    }
    
  private final DiceImages diceImages;
  private final CardImages cardImages;
  private final MapSVG riskMapSVG;
  private RiskPresenter riskPresenter;
  private OMSVGSVGElement boardElt;
  
  @UiField
  HorizontalPanel playerArea;
  
  @UiField
  HorizontalPanel diceAttackPanel;
  
  @UiField
  HTML mapContainer;
  
  @UiField
  VerticalPanel gameStatus;
  
  @UiField
  TabPanel playersStatusPanel;
  
  @UiField
  TabPanel instructions;
  
  private RiskState currentRiskState;
  private Map<String, Integer> territoryDelta = new HashMap<String, Integer>();
  private Map<Image, Card> cardImagesOfCurrentPlayer = new HashMap<Image, Card>();
  private List<Card> selectedCards = new ArrayList<Card>();
  
  private List<HandlerRegistration> territoryHandlers = new ArrayList<HandlerRegistration>();
  private List<HandlerRegistration> cardHandlers = new ArrayList<HandlerRegistration>();
  private Button selectCardsButton = new Button("Finish Selecting");
  private Button turnOrderButton = new Button("Continue");
  private Button continueToAttackPhaseButton = new Button("Continue");
  private Button endAttack = new Button("End Attack Phase");
  private Button endReinforce = new Button("End Reinforce Phase");
  private Button endFortify = new Button("End Fortify");
  private VerticalPanel attackResultPanel = new VerticalPanel();
  private Label errorLabel = new Label();
  private Label reinforceLabel = new Label();
  
  private int unclaimedUnits;
  private String attackToTerritory;
  private String attackFromTerritory;
  private String fortifyToTerritory;
  private String fortifyFromTerritory;
  private boolean claimTerritory = false;
  private boolean deployment = false;
  private boolean reinforce = false;
  private boolean attack = false;
  private boolean fortify = false;
  private boolean mandatoryCardSelection = false;
  
  public RiskGraphics() {
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    playerArea.setSpacing(5);
    diceAttackPanel.setSpacing(20);
    createSelectCardsButtonHandler();
    createTurnOrderButton();
    createContinueToAttackPhaseButton();
    createEndAttackButton();
    createEndReinforceButton();
    createEndFortifyButton();
    addMapHandlers();
  }
  
  private void createSelectCardsButtonHandler() {
    selectCardsButton.addClickHandler(new ClickHandler() {
      
      private void cleanup() {
        selectedCards.clear();
        removeHandlers(cardHandlers);
        cardImagesOfCurrentPlayer.clear();
        gameStatus.remove(selectCardsButton);
      }
      
      @Override
      public void onClick(ClickEvent event) {
        int units = Card.getUnits(selectedCards, currentRiskState.getTradeNumber());
        if (mandatoryCardSelection && units == 0) {
          Window.alert("Invalid selection: Card selection is mandatory, please select again !");
          return;
        }
        if (selectedCards.size() == 0) {
          cleanup();
          riskPresenter.cardsTraded(null);
        } else if (units > 0) {
          List<Integer> selectedIntCards = Card.getCardIdsFromCardObjects(selectedCards);
          cleanup();
          riskPresenter.cardsTraded(selectedIntCards);
        } else {
          if (Window.confirm("Invalid selection: press OK to continue or Cancel to select "
              + "again")) {
            cleanup();
            riskPresenter.cardsTraded(null);
          }
        }
      }
    });
  }
  
  private void createTurnOrderButton() {
    turnOrderButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        gameStatus.remove(turnOrderButton);
        attackResultPanel.clear();
        diceAttackPanel.clear();
        riskPresenter.setTurnOrderMove();
      }
    });
  }
  
  private void createContinueToAttackPhaseButton() {
    continueToAttackPhaseButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        gameStatus.remove(continueToAttackPhaseButton);
        riskPresenter.attackResultMove();
      }
    });
  }
  
  private void createEndAttackButton() {
    endAttack.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        gameStatus.remove(endAttack);
        attack = false;
        riskPresenter.endAttack();
      }
    });
  }
  
  private void createEndReinforceButton() {
    endReinforce.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        gameStatus.remove(endReinforce);
        reinforce = false;
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    });
  }
  
  private void createEndFortifyButton() {
    endFortify.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        gameStatus.remove(endFortify);
        fortify = false;
        riskPresenter.fortifyMove(null);
      }
    });
  }

  private void addMapHandlers() {
    if (territoryHandlers.isEmpty()) {
      for (String territoryId : Territory.SVG_ID_MAP.keySet()) {
        territoryHandlers.addAll(addTerritoryHandlers(territoryId));
      }
    }
  }
  
  private void removeHandlers(List<HandlerRegistration> handlerRegistrations) {
    if (handlerRegistrations != null) {
      for (HandlerRegistration registration : handlerRegistrations) {
        registration.removeHandler();
      }
      handlerRegistrations.clear();
    }
  }

  private List<HandlerRegistration> addTerritoryHandlers(final String territoryId) {
    try {
      List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
      final OMElement territory = boardElt.getElementById(territoryId);
      final OMElement territoryText = boardElt.getElementById(territoryId + "_text");
      final OMElement territoryUnits = boardElt.getElementById(territoryId + "_units");
      MouseDownHandler handler = new MouseDownHandler() {
        @Override
        public void onMouseDown(MouseDownEvent event) {
          String playerId = riskPresenter.getMyPlayerId();
          gameStatus.remove(errorLabel);
          
          if (currentRiskState.getTurn().equals(playerId)) {
            if (claimTerritory) {
              claimTerritory(territoryId);
            } else if (deployment) {
              deployment(territoryId);
            } else if (reinforce) {
              reinforce(territoryId);
            } else if (attack) {
              attack(territoryId);
            } else if (fortify) {
              fortify(territoryId);
            }
          } else if (playerId.equals(GameApi.VIEWER_ID)) {
            errorLabel = new Label("You can only view the game");
            gameStatus.add(errorLabel);
          } else {
            errorLabel = new Label("Please wait for your turn");
            gameStatus.add(errorLabel);
          }
        }
      };
      handlerRegistrations.add(((OMNode) territory)
          .addDomHandler(handler, MouseDownEvent.getType()));
      handlerRegistrations.add(((OMNode) territoryText)
          .addDomHandler(handler, MouseDownEvent.getType()));
      handlerRegistrations.add(((OMNode) territoryUnits)
          .addDomHandler(handler, MouseDownEvent.getType()));
      return handlerRegistrations;
    } catch (Exception e) {
      System.out.println(territoryId);
      e.printStackTrace();
    }
    return new ArrayList<HandlerRegistration>();
  }

  @Override
  public void setPresenter(RiskPresenter riskPresenter) {
    this.riskPresenter = riskPresenter;
  }

  @Override
  public void setViewerState(RiskState riskState) {
    setPlayerState(riskState);
  }

  @Override
  public void setPlayerState(RiskState riskState) {
    gameStatus.clear();
    playersStatusPanel.clear();
    currentRiskState = riskState;
    changeSVGMap(riskState);
    Map<String, Player> playersMap = currentRiskState.getPlayersMap();
    int count = 0;
    int index = 0;
    List<String> playerIds = Lists.newArrayList(currentRiskState.getPlayersMap().keySet());
    Collections.sort(playerIds);
    
    for (String id : playerIds) {
      Player player = playersMap.get(id);
      playersStatusPanel.add(PanelHandler.getPlayerPanel(
          cardImages, currentRiskState, player, riskPresenter.getMyPlayerId(),
          cardImagesOfCurrentPlayer), player.getPlayerId());
      if (riskPresenter.getMyPlayerKey().equals(player.getPlayerId())) {
        index = count;
      }
      count++;
    }
    playersStatusPanel.setWidth("300px");
    playersStatusPanel.selectTab(index);
    gameStatus.add(PanelHandler.getGameStatusPanel(riskState));
    diceAttackPanel.clear();
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      List<String> diceList = Lists.newArrayList(riskState.getDiceResult().keySet());
      Collections.sort(diceList);
      for (String dice: diceList) {
        diceAttackPanel.add(PanelHandler.getNewDicePanel(
            diceImages, dice, riskState.getDiceResult().get(dice)));
        }
   }
    setInstructionPanel();
  }

  private void claimTerritory(String territoryName) {

    String playerKey = riskPresenter.getMyPlayerKey();
    String playerId = riskPresenter.getMyPlayerId();
    OMElement territory = boardElt.getElementById(territoryName);
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);

    if (territorySelected == null) {
      String style = territory.getAttribute("style");
      style = style.replaceFirst("fill:#ffffff", "fill:" + Player.getPlayerColor(playerId));
      territory.setAttribute("style", style);
      territoryUnits.getFirstChild().getFirstChild().setNodeValue("1");
      claimTerritory = false;
      gameStatus.remove(errorLabel);
      riskPresenter.newTerritorySelected(territoryId);
    } else {
      if (territorySelected.getPlayerKey().equals(playerKey)) {
        gameStatus.remove(errorLabel);
        errorLabel = new Label("You already own this territory");
        gameStatus.add(errorLabel);
      } else {
        gameStatus.remove(errorLabel);
        errorLabel = new Label("Select an empty territory");
        gameStatus.add(errorLabel);
      }
    }
  }
  
  private void deployment(String territoryName) {
    String playerKey = riskPresenter.getMyPlayerKey();
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);

    if (territorySelected.getPlayerKey().equals(playerKey)) {
      int units = Integer.parseInt(territoryUnits.getFirstChild().getFirstChild().getNodeValue());
      territoryUnits.getFirstChild().getFirstChild().setNodeValue((units + 1) + "");
      deployment = false;
      gameStatus.remove(errorLabel);
      riskPresenter.territoryForDeployment(territoryId);
    } else {
      gameStatus.remove(errorLabel);
      errorLabel = new Label("Please select your territory");
      gameStatus.add(errorLabel);
    }
  }
  
  private void reinforce(String territoryName) {
    String playerKey = riskPresenter.getMyPlayerKey();
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    
    if (territorySelected.getPlayerKey().equals(playerKey)) {
      int units = Integer.parseInt(territoryUnits.getFirstChild().getFirstChild().getNodeValue());
      territoryUnits.getFirstChild().getFirstChild().setNodeValue((units + 1) + "");
      Integer deltaUnits = territoryDelta.get(territoryId);
      if (deltaUnits == null) {
        territoryDelta.put(territoryId, 1);
      } else {
        territoryDelta.put(territoryId, deltaUnits + 1);
      }
      unclaimedUnits--;
      gameStatus.remove(reinforceLabel);
      reinforceLabel = new Label("Unclaimed Units left : " + unclaimedUnits);
      gameStatus.add(reinforceLabel);
      if (unclaimedUnits == 0) {
        reinforce = false;
        gameStatus.remove(errorLabel);
        gameStatus.remove(reinforceLabel);
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    } else {
      gameStatus.remove(errorLabel);
      errorLabel = new Label("Please select your territory");
      gameStatus.add(errorLabel);
    }
  }
  
  private void attack(String territoryName) {
    String playerKey = riskPresenter.getMyPlayerKey();
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    OMElement territory = boardElt.getElementById(territoryName);
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    String style = territory.getAttribute("style");

    if (territorySelected.getPlayerKey().equals(playerKey)) {
      // Attacking territory selected
      if (attackFromTerritory == null) {
        int units = ((Player) currentRiskState.getPlayersMap()
            .get(GameResources.playerIdToKey(riskPresenter.getMyPlayerId())))
            .getTerritoryUnitMap().get(territoryId);
        if (units < 2) {
          gameStatus.remove(errorLabel);
          errorLabel = new Label("Not enough units to attack");
          gameStatus.add(errorLabel);
          return;
        }
        attackFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        gameStatus.remove(errorLabel);
      } else if (attackFromTerritory.equals(territoryId)) {
        attackFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
        gameStatus.remove(errorLabel);
      } else {
        gameStatus.remove(errorLabel);
        errorLabel = new Label("Select opponent's territory to attack");
        gameStatus.add(errorLabel);
      }
      territory.setAttribute("style", style);
      return;
    } else {
      // Defending territory selected
      if (attackFromTerritory == null) {
        gameStatus.remove(errorLabel);
        errorLabel = new Label("Select own territory first to attack from");
        gameStatus.add(errorLabel);
        return;
      }
      attackToTerritory = territoryId;
      if (Territory.CONNECTIONS.get(Integer.parseInt(attackFromTerritory))
          .contains(Integer.parseInt(attackToTerritory))) {
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        style = style.replaceFirst("stroke:#000000", "stroke:red");
        territory.setAttribute("style", style);
        attack = false;
        gameStatus.remove(errorLabel);
        riskPresenter.performAttack(attackFromTerritory, attackToTerritory);
      } else {
        gameStatus.remove(errorLabel);
        errorLabel = new Label(
            "Select opponent's territory that is adjacent to your territory for attack");
        gameStatus.add(errorLabel);
        return;
      }
    }
  } 
  
  private void fortify(String territoryName) {
    String playerKey = riskPresenter.getMyPlayerKey();
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    OMElement territory = boardElt.getElementById(territoryName);
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    String style = territory.getAttribute("style");

    if (territorySelected.getPlayerKey().equals(playerKey)) {
      if (fortifyFromTerritory == null) {
        int units = ((Player) currentRiskState.getPlayersMap()
            .get(GameResources.playerIdToKey(riskPresenter.getMyPlayerId())))
            .getTerritoryUnitMap().get(territoryId);
        if (units < 2) {
          gameStatus.remove(errorLabel);
          errorLabel = new Label("Not enough units to fortify");
          gameStatus.add(errorLabel);
          return;
        }
        fortifyFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        territory.setAttribute("style", style);
        gameStatus.remove(errorLabel);
        return;
      } else if (fortifyFromTerritory.equals(territoryId)) {
        fortifyFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
        territory.setAttribute("style", style);
        gameStatus.remove(errorLabel);
        return;
      } else {
        if (fortifyFromTerritory == null) {
          gameStatus.remove(errorLabel);
          errorLabel = new Label("Select own territory first to fortify from");
          gameStatus.add(errorLabel);
          return;
        }
        fortifyToTerritory = territoryId;
        int fromTerritory = Integer.parseInt(fortifyFromTerritory);
        int toTerritory = Integer.parseInt(fortifyToTerritory);
        List<String> territoryList = Lists.newArrayList(
            currentRiskState.getPlayersMap().get(playerKey).getTerritoryUnitMap().keySet());
        if (Territory.isFortifyPossible(fromTerritory, toTerritory, territoryList)) {
          style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
          territory.setAttribute("style", style);
          int unitsOnFromTerritory = currentRiskState.getTerritoryMap()
              .get(fortifyFromTerritory).getCurrentUnits();
          List<String> options = Lists.newArrayList();
          for (int i = 1; i <= unitsOnFromTerritory - 1; i++) {
            options.add(i + "");
          }
          new PopupChoices("Choose number of units to move on the new territory",
              options, new PopupChoices.OptionChosen() {
            @Override
            public void optionChosen(String option) {
              territoryDelta = new HashMap<String, Integer>();
              territoryDelta.put(fortifyFromTerritory, -Integer.parseInt(option));
              territoryDelta.put(fortifyToTerritory, Integer.parseInt(option));
              fortify = false;
              gameStatus.remove(errorLabel);
              riskPresenter.fortifyMove(territoryDelta);
            }
          }).center();
        } else {
          gameStatus.remove(errorLabel);
          errorLabel = new Label(
              "Select your own territory that is connected to your territory for fortify");
          gameStatus.add(errorLabel);
          return;
        }
      }
    } else {
      gameStatus.remove(errorLabel);
      errorLabel = new Label("Select own territory to fortify");
      gameStatus.add(errorLabel);
    }    
  }
  
  @Override
  public void chooseNewTerritory() {
    gameStatus.remove(errorLabel);
    gameStatus.remove(reinforceLabel);
    claimTerritory = true;
  }
  
  @Override
  public void chooseTerritoryForDeployment() {
    gameStatus.remove(errorLabel);
    gameStatus.remove(reinforceLabel);
    deployment = true;
  }
  
  @Override
  public void chooseCardsForTrading(boolean mandatoryCardSelection) {
    this.mandatoryCardSelection = mandatoryCardSelection;
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    String turnPlayerKey = GameResources.playerIdToKey(turnPlayerId);
    if (playingPlayerId.equals(turnPlayerId)) {
      Player currentPlayer = currentRiskState.getPlayersMap().get(turnPlayerKey);
      List<Integer> playerCards = currentPlayer.getCards();
      if (playerCards != null && playerCards.size() >= 3) {
        List<Card> cardObjects = Card.getCardsById(currentRiskState.getCardMap(), playerCards);
        if (Card.isTradePossible(cardObjects)) {
          for (Map.Entry<Image, Card> imageCard : cardImagesOfCurrentPlayer.entrySet()) { 
            cardHandlers.add(addCardHandlers(imageCard.getKey(), imageCard.getValue()));
          }
          gameStatus.add(selectCardsButton);
        } else {
          riskPresenter.cardsTraded(null);
        }
      } else {
        riskPresenter.cardsTraded(null);
      }
    }
  }
  
  private HandlerRegistration addCardHandlers(final Image image, final Card card) {
    return image.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        if (image.getStyleName().equals("risk-cards")) {
          image.setStyleName("risk-cards-selected");
          selectedCards.add(card);
        } else {
          image.setStyleName("risk-cards");
          selectedCards.remove(card);
        }
      }
    });
  }

  @Override
  public void reinforceTerritories() {
    gameStatus.remove(errorLabel);
    //gameStatus.remove(reinforceLabel);
    
    if (territoryDelta == null) {
      territoryDelta = new HashMap<String, Integer>();
    }
    territoryDelta.clear();
    unclaimedUnits = ((Player) currentRiskState.getPlayersMap()
        .get(GameResources.playerIdToKey(riskPresenter.getMyPlayerId())))
            .getUnclaimedUnits();
    reinforceLabel = new Label("You got " + unclaimedUnits + " for reinforce!");
    gameStatus.add(endReinforce);
    reinforce = true;
  }
  
  @Override
  public void attack() {
    gameStatus.remove(errorLabel);
    gameStatus.remove(reinforceLabel);
    attackToTerritory = null;
    attackFromTerritory = null;
    gameStatus.add(endAttack);
    attack = true;
  }
  
  @Override
  public void attackResult() {
    attackResultPanel.clear();
    diceAttackPanel.clear();
    gameStatus.remove(errorLabel);
    gameStatus.remove(reinforceLabel);

    String attackerKey = currentRiskState.getAttack().getAttackerPlayerId();
    String defenderKey = currentRiskState.getAttack().getDefenderPlayerId();
    String attackingTerritory = Territory.TERRITORY_NAME.get(
        currentRiskState.getAttack().getAttackerTerritoryId());
    String defendingTerritory = Territory.TERRITORY_NAME.get(
        currentRiskState.getAttack().getDefenderTerritoryId());
    String attackerText = attackerKey + " (" + attackingTerritory + ")";
    String defenderText = defenderKey + " (" + defendingTerritory + ")";
    
    Panel attackerDicePanel = PanelHandler.getNewDicePanel(
        diceImages, attackerText, currentRiskState.getAttack().getAttackerDiceRolls());
    Panel defenderDicePanel = PanelHandler.getNewDicePanel(
        diceImages, defenderText, currentRiskState.getAttack().getDefenderDiceRolls());
    diceAttackPanel.add(attackerDicePanel);
    diceAttackPanel.add(defenderDicePanel);
    AttackResult attackResult = currentRiskState.getAttack().getAttackResult();
    attackResultPanel.add(new HTML("Attacker lost <b>" + (-1 * attackResult.getDeltaAttack()) 
        + " units</b>"));
    attackResultPanel.add(new HTML("Defender lost <b>" + (-1 * attackResult.getDeltaDefend()) 
        + " units</b>"));
    
    if (attackResult.isAttackerATerritoryWinner()) {
      attackResultPanel.add(new HTML("<b>Player " + attackerKey + " captures " + defendingTerritory
          + " </b>"));
    }
    if (attackResult.isDefenderOutOfGame()) {
      attackResultPanel.add(new HTML("<b>Player " + defenderKey + " out of the game. "
          + "Player " + attackerKey + " will get cards owned by Player " + defenderKey 
          + ", if any.</b>"));
    }
    if (attackResult.isAttackerAWinnerOfGame()) {
      attackResultPanel.add(new HTML("<b>Player " + attackerKey + " wins the game !</b>"));
    } else if (attackResult.isTradeRequired()) {
      attackResultPanel.add(new HTML("<b>Player " + attackerKey + " will have to trade cards</b>"));
    }
    
    diceAttackPanel.add(attackResultPanel);
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      gameStatus.add(continueToAttackPhaseButton);
    }
  }
   
  @Override
  public void moveUnitsAfterAttack() {
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      int unitsOnAttackingTerritory = currentRiskState.getTerritoryMap()
          .get(currentRiskState.getLastAttackingTerritory() + "").getCurrentUnits();
      int minUnitsToNewTerritory = GameResources.getMinUnitsToNewTerritory(
          unitsOnAttackingTerritory);
      List<String> options = Lists.newArrayList();
      for (int i = minUnitsToNewTerritory; i <= unitsOnAttackingTerritory - 1; i++) {
        options.add(i + "");
      }
      new PopupChoices("Choose number of units to move on the new territory",
          options, new PopupChoices.OptionChosen() {
        @Override
        public void optionChosen(String option) {
          riskPresenter.moveUnitsAfterAttack(Integer.parseInt(option));
        }
      }).center();
    }
  }

  @Override
  public void fortify() {
    gameStatus.remove(errorLabel);
    gameStatus.remove(reinforceLabel);
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      gameStatus.add(endFortify);
    }
    fortifyFromTerritory = null;
    fortifyToTerritory = null;
    fortify = true;
  }

  @Override
  public void endGame() {
    gameStatus.remove(errorLabel);
    gameStatus.remove(reinforceLabel);
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    gameStatus.add(new Label("Game Ended"));
    if (playingPlayerId.equals(turnPlayerId)) {
      Window.alert("You won the game!");
      riskPresenter.endGame();
    } else {
      Window.alert("Player-" + GameResources.playerIdToKey(turnPlayerId) + " won the game!");
    }
  }
  
  @Override
  public void turnOrderMove() {
    gameStatus.add(turnOrderButton);
  }
  
  private void setInstructionPanel() {
    instructions.clear();
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    String phase = (String) currentRiskState.getPhase();
    boolean playerLost = false;
    if (currentRiskState.getTurnOrder() != null) {
      playerLost = !currentRiskState.getTurnOrder().contains(playingPlayerId);
    }
    
    if (playingPlayerId.equals(turnPlayerId)) {
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        instructions.add(new HTML("Turn Order will be decided by rolling dice for all players."
            + "<br><br>Press continue."), 
            "Instructions");
      } else if (phase.equals(GameResources.CLAIM_TERRITORY)) {
        instructions.add(new HTML("Select a empty territory to claim. One territory at a time."
            + "<br><br>Phase will end when all the territories are claimed."), 
            "Instructions");
      } else if (phase.equals(GameResources.DEPLOYMENT)) {
        instructions.add(new HTML("Deploy your remaining army units to territory you own. "
            + "One unit at a time.<br><br>Phase will end when you have placed your "
            + "all unclaimed units"), "Instructions");
      } else if (phase.equals(GameResources.CARD_TRADE) 
          || phase.equals(GameResources.ATTACK_TRADE)) {
        instructions.add(new HTML("Select cards to trade. You must trade if you have more than "
            + "4 cards or you are in attack phase and you have more than 5 cards. "
            + "<br><br>You'll get units for this card trade. "
            + "Number of units you get for trading cards will"
            + " increase as number of trades performed in the game increases."), "Instructions");
      } else if (phase.equals(GameResources.REINFORCE)) {
        instructions.add(new HTML("Reinforce your territories by putting units on territories "
            + "you own. You got these unclaimed units based on territories and continents you own "
            + "and cards traded, if any.<br><br> Phase will end when you have 0 unclaimed units or "
            + "if you choose to end phase"), "Instructions");
      } else if (phase.equals(GameResources.ATTACK_PHASE)) {
        instructions.add(new HTML("Attack on opponent's territory by selecting your territory "
            + "first and then opponent's territory. Make sure opponent's territory is adjacent "
            + "to your attacking territory and you have at least 2 units on your "
            + "attacking territory.<br><br> You can end this phase by clicking End Attack.")
        , "Instructions");
      } else if (phase.equals(GameResources.ATTACK_RESULT)) {
        instructions.add(new HTML("Check the result of your attack. "
            + "Compare the highest die of attacker and defender. If attacker's is higher, "
            + "the defender loses one army from the territory under attack. But if "
            + "the defender's die is higher than yours, you lose one army from the territory you "
            + "attacked from. If each of you rolled more than one die, now compare the two "
            + "next-highest dice and repeat the process. In case of a tie, the defender always "
            + "wins.<br><br>Press Continue to go back to attack phase."), "Instructions");
      } else if (phase.equals(GameResources.ATTACK_REINFORCE)) {
        instructions.add(new HTML("Reinforce your territories by putting units on territories "
            + "you own. You got these units based on cards you traded. "
            + "Phase will end when you have 0 unclaimed units or "
            + "if you choose to end phase"), "Instructions");
      } else if (phase.equals(GameResources.ATTACK_OCCUPY)) {
        String instruction = "";
        if (currentRiskState.getTerritoryWinner().equals(riskPresenter.getMyPlayerKey())) {
          instruction += "<br><br>And now you'll get a risk card at end of attack phase, "
              + "because you won a territory in attack phase. "
              + "You'll get only one risk card even if you won more than one territory.";
        }
        instructions.add(new HTML("Move units to new territory you just won from your attacking "
            + "territory. You must leave at least one unit behind on your attacking territory. "
            + "Also, you have to move units at least equal to the number of dice rolled in "
            + "last attack." + instruction), "Instructions");
      } else if (phase.equals(GameResources.FORTIFY)) {
        instructions.add(new HTML("Move units from one territory you own to other territory "
            + "you own. You must leave at least one unit on territory. "
            + "<br><br>You can skip this phase by clicking End Fortify."), "Instructions");
   
      } else if (phase.equals(GameResources.END_GAME) || phase.equals(GameResources.GAME_ENDED)) {
        instructions.add(new Label("You won all the territories. "
            + "Congratulations, you are the winner!"), 
            "Instructions");
      } else {
        instructions.add(new Label(phase), "Instructions");
      }
    } else {
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        instructions.add(new Label("Turn Order will be decided by rolling dice for all players"), 
            "Instructions");
      } else if (playingPlayerId.equals(GameApi.VIEWER_ID)) {
        instructions.add(new Label("Watch the game! Hope you enjoy!"), "Instructions");
      } else if (playerLost || phase.equals(GameResources.END_GAME)) {
        instructions.add(new Label("You lost. Better luck next time."), "Instructions");
      }  else {
        instructions.add(new Label("Wait for your turn! Watch what your enemy is doing!"), 
            "Instructions");
      }
    }
    instructions.setWidth("300px");
    instructions.selectTab(0);
  }
  
  private void changeSVGMap(RiskState riskState) {
    Attack attack = riskState.getAttack();
    int deltaAttack = 0;
    int deltaDefend = 0;
    String attackTerritoryId = null;
    String defendTerritoryId = null;
    
    if (attack != null) {
      deltaAttack = attack.getAttackResult().getDeltaAttack();
      deltaDefend = attack.getAttackResult().getDeltaDefend();
      attackTerritoryId = attack.getAttackerTerritoryId() + "";
      defendTerritoryId = attack.getDefenderTerritoryId() + "";
    }
    
    if (riskState.getLastAttackingTerritory() != null) {
      attackTerritoryId = riskState.getLastAttackingTerritory() + "";
    }
    
    for (Map.Entry<String, Integer> territoryIdNum : Territory.SVG_ID_MAP.entrySet()) {
      String territoryId = territoryIdNum.getKey();
      int territoryNum = territoryIdNum.getValue();
      String territoryKey = territoryNum + "";
      final OMElement territoryElement = boardElt.getElementById(territoryId);
      final OMElement territoryUnitsElement = boardElt.getElementById(territoryId + "_units");
      Territory territory = riskState.getTerritoryMap().get(territoryKey);
      
      if (territory != null) {
        String style = territoryElement.getAttribute("style");
        style = style.replaceFirst("fill:[^;]+", "fill:"
          + Player.getPlayerColor(GameResources.playerKeyToId(territory.getPlayerKey())));
        int units = territory.getCurrentUnits();
        if (territoryKey.equals(attackTerritoryId)) {
          style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
          units += deltaAttack;
        } else if (territoryKey.equals(defendTerritoryId)) {
          style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
          style = style.replaceFirst("stroke:#000000", "stroke:red");
          units += deltaDefend;
        } else {
          style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
          style = style.replaceFirst("stroke:red", "stroke:#000000");
        }
        territoryElement.setAttribute("style", style);
        territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(units + "");
      } else if (riskState.getTerritoryWinner() != null) {
        String style = territoryElement.getAttribute("style");
        style = style.replaceFirst("fill:[^;]+", "fill:"
          + Player.getPlayerColor(GameResources.playerKeyToId(riskState.getTerritoryWinner())));
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        style = style.replaceFirst("stroke:red", "stroke:#000000");
        territoryElement.setAttribute("style", style);
        territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(0 + "");
      } else {
        String style = territoryElement.getAttribute("style");
        style = style.replaceFirst("fill:[^;]+", "fill:white");
        territoryElement.setAttribute("style", style);
        territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(0 + "");
      }
    }
    
  }
}
