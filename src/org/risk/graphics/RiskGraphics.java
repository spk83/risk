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
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.ConfirmDialog.ConfirmCallback;
import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.InfoButton;

public class RiskGraphics extends Composite implements RiskPresenter.View {
    public interface RiskGraphicsUiBinder extends UiBinder<Widget, RiskGraphics> {
    }
  
  private final GameSounds gameSounds;
  private final DiceImages diceImages;
  private final CardImages cardImages;
  private final MapSVG riskMapSVG;
  private final AttackImages attackImages;
  private final SoundResource soundResource;
  private RiskPresenter riskPresenter;
  private OMSVGSVGElement boardElt;

  @UiField
  HTML mapContainer;
  
  @UiField
  HeaderPanel headerPanel;
  
  @UiField
  ButtonBar footerBar;
  
  //@UiField
  TabPanel playersStatusPanel = new TabPanel();
  
  @UiField
  InfoButton instructions = new InfoButton();
  
  @UiField
  VerticalPanel mapWrapper;
  
  RoundPanel notification = new RoundPanel();
  
  private RiskState currentRiskState;
  private Map<String, Integer> territoryDelta = new HashMap<String, Integer>();
  private Map<Image, Card> cardImagesOfCurrentPlayer = new HashMap<Image, Card>();
  private List<Card> selectedCards = new ArrayList<Card>();
  
  private List<HandlerRegistration> territoryHandlers = new ArrayList<HandlerRegistration>();
  private List<HandlerRegistration> cardHandlers = new ArrayList<HandlerRegistration>();
  private HeaderButton selectCardsButton = new HeaderButton();
  private HeaderButton endAttack = new HeaderButton();
  private HeaderButton endReinforce = new HeaderButton();
  private HeaderButton endFortify = new HeaderButton();
  private VerticalPanel attackResultPanel = new VerticalPanel();
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
  
  private PopupPanel dicePanel;
  private PopupChoices fortifyOpt;
  private ImageResource attackImageResource;
 
  public RiskGraphics() {
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    attackImages = GWT.create(AttackImages.class);
    gameSounds = GWT.create(GameSounds.class);
    soundResource = new SoundResource(gameSounds);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    attackImageResource = attackImages.tank();
    dicePanel = new PopupPanel();
    dicePanel.hide();
    createSelectCardsButtonHandler();
    createEndAttackButton();
    createEndReinforceButton();
    createEndFortifyButton();
    addMapHandlers();
  }
  
  private void createSelectCardsButtonHandler() {
    selectCardsButton.setRoundButton(true);
    selectCardsButton.setText("Done");
    selectCardsButton.addTapHandler(new TapHandler() {
      
      private void cleanup() {
        selectedCards.clear();
        removeHandlers(cardHandlers);
        cardImagesOfCurrentPlayer.clear();
        selectCardsButton.removeFromParent();
      }
      
      @Override
      public void onTap(TapEvent event) {
        int units = Card.getUnits(selectedCards, currentRiskState.getTradeNumber());
        if (mandatoryCardSelection && units == 0) {
          Dialogs.alert("Invalid selection", "Card selection is mandatory, please select again !", 
              null);
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
          Dialogs.alert("Invalid selection", "Card selection is mandatory, please select again !", 
              null);
          ConfirmCallback callback = new ConfirmCallback() {
            @Override
            public void onOk() {
              cleanup();
              riskPresenter.cardsTraded(null);
            }
            @Override
            public void onCancel() {
              
            }
          };
          Dialogs.confirm("Invalid selection", 
              "Invalid selection: press OK to continue or Cancel to select " + "again", callback);
        }
      }
    });
  }
    
  private void createEndAttackButton() {
    endAttack.setRoundButton(true);
    endAttack.setText("End");
    endAttack.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        endAttack.removeFromParent();
        attack = false;
        riskPresenter.endAttack();
      }
    });
  }
  
  private void createEndReinforceButton() {
    endReinforce.setRoundButton(true);
    endReinforce.setText("End");
    endReinforce.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        endReinforce.removeFromParent();
        reinforce = false;
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    });
  }
  
  private void createEndFortifyButton() {
    endFortify.setRoundButton(true);
    endFortify.setText("End");
    endFortify.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        endFortify.removeFromParent();
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
            Dialogs.alert("Not Allowed", "You can only view the game", null);
          } else {
            Dialogs.alert("Not Allowed", "Please wait for your turn", null);
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
    selectCardsButton.removeFromParent();
    endAttack.removeFromParent();
    endReinforce.removeFromParent();
    endFortify.removeFromParent();
    playersStatusPanel.clear();
    notification.clear();
    currentRiskState = riskState;
    changeSVGMap(riskState);
    dicePanel.clearPanel();
    dicePanel.hide();
    if (fortifyOpt != null) {
      fortifyOpt.hide();
    }
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
    HTML phase = new HTML("<b>" + GameResources.UI_PHASE_MAPPING.get(riskState.getPhase()) 
        + "</b>");
    phase.getElement().getStyle().setTop(10, Unit.PX);
    headerPanel.setLeftWidget(phase);
    headerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      List<String> diceList = Lists.newArrayList(riskState.getDiceResult().keySet());
      Collections.sort(diceList);
      soundResource.playDiceAudio();
      dicePanel.addPanel(new HTML("<b>Turn Order</b>"));
      for (String dice: diceList) {
        FlowPanel diceFlowPanel = new FlowPanel();
        DiceAnimation diceAnimation = new DiceAnimation(
            diceImages, diceFlowPanel, 3, dice, riskState.getDiceResult().get(dice));
        dicePanel.addPanel(diceFlowPanel);
        diceAnimation.run(1000);
      }
      String playingPlayerId = riskPresenter.getMyPlayerId();
      String turnPlayerId = currentRiskState.getTurn();
      if (playingPlayerId.equals(turnPlayerId)) {
        dicePanel.setOkBtnHandler(riskPresenter, 1);
      } else {
        dicePanel.setOkBtnHandler(riskPresenter, 0);
      }
      dicePanel.center();
   }
    claimTerritory = false;
    deployment = false;
    reinforce = false;
    attack = false;
    fortify = false;
    mandatoryCardSelection = false;
    notification.getElement().getStyle().setFontSize(12, Unit.PX);
    notification.getElement().getStyle().setOverflow(Overflow.VISIBLE);
    notification.getElement().getStyle().setPosition(Position.ABSOLUTE);
    mapWrapper.getElement().getStyle().setPosition(Position.ABSOLUTE);
    mapWrapper.getElement().getStyle().setOverflow(Overflow.VISIBLE);
    mapContainer.getElement().getStyle().setPosition(Position.ABSOLUTE);
    mapContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
    mapWrapper.add(notification);
    notification.setVisible(false);
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
      riskPresenter.newTerritorySelected(territoryId);
      soundResource.playDeployAudio();
    } else {
      if (territorySelected.getPlayerKey().equals(playerKey)) {
        Dialogs.alert("Not Allowed", "You already own this territory", null);
      } else {
        Dialogs.alert("Not Allowed", "Select an empty territory", null);
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
      riskPresenter.territoryForDeployment(territoryId);
      soundResource.playDeployAudio();
    } else {
      Dialogs.alert("Not Allowed", "Please select your territory", null);
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
      notification.clear();
      notification.setVisible(true);
      notification.add(new HTML("<b>Unclaimed Units left : " + unclaimedUnits + "</b>"));
      if (unclaimedUnits == 0) {
        reinforce = false;
        notification.clear();
        notification.setVisible(false);
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    } else {
      Dialogs.alert("Not Allowed", "Please select your territory", null);
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
          Dialogs.alert("Not Allowed", "Not enough units to attack", null);
          return;
        }
        attackFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
      } else if (attackFromTerritory.equals(territoryId)) {
        attackFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
      } else {
        Dialogs.alert("Not Allowed", "Select opponent's territory to attack", null);
      }
      territory.setAttribute("style", style);
      
      return;
    } else {
      // Defending territory selected
      if (attackFromTerritory == null) {
        Dialogs.alert("Not Allowed", "Select own territory first to attack from", null);
        return;
      }
      attackToTerritory = territoryId;
      if (Territory.CONNECTIONS.get(Integer.parseInt(attackFromTerritory))
          .contains(Integer.parseInt(attackToTerritory))) {
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        style = style.replaceFirst("stroke:#000000", "stroke:red");
        territory.setAttribute("style", style);
        attack = false;
        riskPresenter.performAttack(attackFromTerritory, attackToTerritory);
      } else {
        Dialogs.alert("Not Allowed",  
            "Select opponent's territory that is adjacent to your territory for attack", null);
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
          Dialogs.alert("Not Allowed", "Not enough units to fortify", null);
          return;
        }
        fortifyFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        territory.setAttribute("style", style);
        return;
      } else if (fortifyFromTerritory.equals(territoryId)) {
        fortifyFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
        territory.setAttribute("style", style);
        return;
      } else {
        if (fortifyFromTerritory == null) {
          Dialogs.alert("Not Allowed", "Select own territory first to fortify from", null);
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
          fortifyOpt = new PopupChoices("Choose number of units to move on the new territory",
              options, new PopupChoices.OptionChosen() {
            @Override
            public void optionChosen(String option) {
              territoryDelta = new HashMap<String, Integer>();
              territoryDelta.put(fortifyFromTerritory, -Integer.parseInt(option));
              territoryDelta.put(fortifyToTerritory, Integer.parseInt(option));
              fortify = false;
              riskPresenter.fortifyMove(territoryDelta);
            }
          });
          fortifyOpt.center();
        } else {
          Dialogs.alert("Not Allowed", 
              "Select your own territory that is connected to your territory for fortify", null);
          return;
        }
      }
    } else {
      Dialogs.alert("Not Allowed", "Select own territory to fortify", null);
    }
  }
  
  @Override
  public void chooseNewTerritory() {
    claimTerritory = true;
  }
  
  @Override
  public void chooseTerritoryForDeployment() {
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
          headerPanel.setRightWidget(selectCardsButton);
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
          soundResource.playCardAudio();
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
    if (territoryDelta == null) {
      territoryDelta = new HashMap<String, Integer>();
    }
    territoryDelta.clear();
    unclaimedUnits = ((Player) currentRiskState.getPlayersMap()
        .get(GameResources.playerIdToKey(riskPresenter.getMyPlayerId())))
            .getUnclaimedUnits();
    Dialogs.alert("Info", "You got " + unclaimedUnits + " units for reinforcement!", null);
    headerPanel.setRightWidget(endReinforce);
    reinforce = true;
    soundResource.playAddUnitsAudio();
  }
  
  @Override
  public void attack() {
    attackToTerritory = null;
    attackFromTerritory = null;
    headerPanel.setRightWidget(endAttack);
    attack = true;
  }
  
  @Override
  public void attackResult() {
    attackResultPanel.clear();
    String attackerKey = currentRiskState.getAttack().getAttackerPlayerId();
    String defenderKey = currentRiskState.getAttack().getDefenderPlayerId();
    String attackingTerritory = Territory.TERRITORY_NAME.get(
        currentRiskState.getAttack().getAttackerTerritoryId());
    String defendingTerritory = Territory.TERRITORY_NAME.get(
        currentRiskState.getAttack().getDefenderTerritoryId());
    String attackingTerritorySVG = Territory.SVG_NAME_MAP.get(
        currentRiskState.getAttack().getAttackerTerritoryId());
    String defendingTerritorySVG = Territory.SVG_NAME_MAP.get(
        currentRiskState.getAttack().getDefenderTerritoryId());
    
    String attackerText = attackerKey + " (" + attackingTerritory + ")";
    String defenderText = defenderKey + " (" + defendingTerritory + ")";
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    int attackUnits = currentRiskState.getAttack().getAttackUnits();
    int defendUnits = currentRiskState.getAttack().getDefendUnits();
    final AttackResult attackResult = currentRiskState.getAttack().getAttackResult();

    OMElement territoryUnitsElement = boardElt.getElementById(attackingTerritorySVG + "_units");
    territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(attackUnits + "");
    int startAttackXCords = territoryUnitsElement.getElement().getAbsoluteLeft() 
        - territoryUnitsElement.getElement().getParentElement().getAbsoluteLeft();
    int startAttackYCords = territoryUnitsElement.getElement().getAbsoluteTop() 
        - territoryUnitsElement.getElement().getParentElement().getAbsoluteTop();
    
    territoryUnitsElement = boardElt.getElementById(defendingTerritorySVG + "_units");
    territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(defendUnits + "");
    int endAttackXCords = territoryUnitsElement.getElement().getAbsoluteLeft() 
        - territoryUnitsElement.getElement().getParentElement().getAbsoluteLeft();
    int endAttackYCords = territoryUnitsElement.getElement().getAbsoluteTop() 
        - territoryUnitsElement.getElement().getParentElement().getAbsoluteTop();
    
    TankMovingAnimation animation = new TankMovingAnimation(
        mapContainer, startAttackXCords, startAttackYCords, endAttackXCords, endAttackYCords, 
        attackImageResource, soundResource.getAttackAudio());
    
    FlowPanel attackerDicePanel = new FlowPanel();
    final DiceAnimation diceAnimationAttacker = new DiceAnimation(diceImages, attackerDicePanel, 3, 
        attackerText, currentRiskState.getAttack().getAttackerDiceRolls());
    
    FlowPanel defenderDicePanel = new FlowPanel();
    final DiceAnimation diceAnimationDefender = new DiceAnimation(diceImages, defenderDicePanel, 3, 
        defenderText, currentRiskState.getAttack().getDefenderDiceRolls());
    dicePanel.clearPanel();
    dicePanel.addPanel(new HTML("<b>Attack Result</b>"));
    dicePanel.setPanelSize("200px", "200px");
    if (playingPlayerId.equals(turnPlayerId)) {
      dicePanel.setOkBtnHandler(riskPresenter, 2);
    } else {
      dicePanel.setOkBtnHandler(riskPresenter, 0);
    }
    dicePanel.addPanel(attackerDicePanel);
    dicePanel.addPanel(defenderDicePanel);
  
    attackResultPanel.add(new HTML("Attacker lost <b>" + (-1 * attackResult.getDeltaAttack()) 
        + " units</b>"));
    attackResultPanel.add(new HTML("Defender lost <b>" + (-1 * attackResult.getDeltaDefend()) 
        + " units</b>"));
    
    if (attackResult.isAttackerATerritoryWinner()) {
      attackResultPanel.add(new HTML("Player " + attackerKey + " captures " + defendingTerritory));
    }
    if (attackResult.isDefenderOutOfGame()) {
      attackResultPanel.add(new HTML("Player " + defenderKey + " out of the game. "
          + "Player " + attackerKey + " will get cards owned by Player " + defenderKey 
          + ", if any."));
    }
    if (attackResult.isAttackerAWinnerOfGame()) {
      attackResultPanel.add(new HTML("Player " + attackerKey + " wins the game !"));
      
    } else if (attackResult.isTradeRequired()) {
      attackResultPanel.add(new HTML("Player " + attackerKey + " will have to trade cards"));
    }
    
    final Timer diceAnimationTimer = new Timer() {
      @Override
      public void run() {
        dicePanel.addPanel(attackResultPanel);
        if (attackResult.isAttackerAWinnerOfGame()) {
          soundResource.playGameWonAudio();
        } else if (attackResult.getDeltaAttack() > attackResult.getDeltaDefend()) {
          soundResource.playAttackWonAudio();
        } else {
          soundResource.playAttackLostAudio();
        }
      }
    };
    
    Timer attackAnimationTimer = new Timer() {
      @Override
      public void run() {
        diceAnimationAttacker.run(1000);
        diceAnimationDefender.run(1000);
        dicePanel.center();
        diceAnimationTimer.schedule(1200);
        soundResource.playDiceAudio();
      }
    };
    animation.run(2000);
    attackAnimationTimer.schedule(2100);
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
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      headerPanel.setRightWidget(endFortify);
    }
    fortifyFromTerritory = null;
    fortifyToTerritory = null;
    fortify = true;
  }

  @Override
  public void endGame() {
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      Dialogs.alert("Game Ended", "You won the game!", null);
      riskPresenter.endGame();
    } else {
      Dialogs.alert("Game Ended", "Player-" + GameResources.playerIdToKey(turnPlayerId) 
          + " won the game!", null);
    }
  }
  
  @UiHandler("instructions")
  public void onTapInfoButton(TapEvent e) {
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    String phase = (String) currentRiskState.getPhase();
    boolean playerLost = false;
    if (currentRiskState.getTurnOrder() != null) {
      playerLost = !currentRiskState.getTurnOrder().contains(playingPlayerId);
    }
    
    if (playingPlayerId.equals(turnPlayerId)) {
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        Dialogs.alert("Instructions", "Turn Order will be decided by rolling dice for all players."
            + "\n\nPress OK.", null);
      } else if (phase.equals(GameResources.CLAIM_TERRITORY)) {
        Dialogs.alert("Instructions", "Select a empty territory to claim. One territory at a time."
            + "\n\nPhase will end when all the territories are claimed.", null);
      } else if (phase.equals(GameResources.DEPLOYMENT)) {
        Dialogs.alert("Instructions", "Deploy your remaining army units to territory you own. "
            + "One unit at a time.\n\nPhase will end when you have placed your "
            + "all unclaimed units", null);
      } else if (phase.equals(GameResources.CARD_TRADE) 
          || phase.equals(GameResources.ATTACK_TRADE)) {
        Dialogs.alert("Instructions", "Select cards to trade. You must trade if you have more than "
            + "4 cards or you are in attack phase and you have more than 5 cards. "
            + "\n\nYou'll get units for this card trade. "
            + "Number of units you get for trading cards will"
            + " increase as number of trades performed in the game increases.", null);
      } else if (phase.equals(GameResources.REINFORCE)) {
        Dialogs.alert("Instructions", "Reinforce your territories by putting units on territories "
            + "you own. You got these unclaimed units based on territories and continents you own "
            + "and cards traded, if any.\n\n Phase will end when you have 0 unclaimed units or "
            + "if you choose to end phase", null);
      } else if (phase.equals(GameResources.ATTACK_PHASE)) {
        Dialogs.alert("Instructions", "Attack on opponent's territory by selecting your territory "
            + "first and then opponent's territory. Make sure opponent's territory is adjacent "
            + "to your attacking territory and you have at least 2 units on your "
            + "attacking territory.\n\nYou can also attack by dragging your tank on to "
            + "opponent's tank. Opponent's tank will be visible once you start dragging."
            + "\n\n You can end this phase by clicking End Attack.", null);
      } else if (phase.equals(GameResources.ATTACK_RESULT)) {
        Dialogs.alert("Instructions", "Check the result of your attack. "
            + "Compare the highest die of attacker and defender. If attacker's is higher, "
            + "the defender loses one army from the territory under attack. But if "
            + "the defender's die is higher than yours, you lose one army from the territory you "
            + "attacked from. If each of you rolled more than one die, now compare the two "
            + "next-highest dice and repeat the process. In case of a tie, the defender always "
            + "wins.\n\nPress OK to go back to attack phase.", null);
      } else if (phase.equals(GameResources.ATTACK_REINFORCE)) {
        Dialogs.alert("Instructions", "Reinforce your territories by putting units on territories "
            + "you own. You got these units based on cards you traded. "
            + "Phase will end when you have 0 unclaimed units or "
            + "if you choose to end phase", null);
      } else if (phase.equals(GameResources.ATTACK_OCCUPY)) {
        String instruction = "";
        if (currentRiskState.getTerritoryWinner().equals(riskPresenter.getMyPlayerKey())) {
          instruction += "\n\nAnd now you'll get a risk card at end of attack phase, "
              + "because you won a territory in attack phase. "
              + "You'll get only one risk card even if you won more than one territory.";
        }
        Dialogs.alert("Instructions", "Move units to new territory you just won from your"
            + " attacking territory. You must leave at least one unit behind on your attacking "
            + "territory. Also, you have to move units at least equal to the number of dice rolled "
            + "in last attack." + instruction, null);
      } else if (phase.equals(GameResources.FORTIFY)) {
        Dialogs.alert("Instructions", "Move units from one territory you own to other territory "
            + "you own. You must leave at least one unit on territory. "
            + "\n\nYou can skip this phase by clicking End Fortify.", null);
   
      } else if (phase.equals(GameResources.END_GAME) || phase.equals(GameResources.GAME_ENDED)) {
        Dialogs.alert("Instructions", "You won all the territories. "
            + "Congratulations, you are the winner!", null);
      } else {
        Dialogs.alert("Instructions", phase, null);
      }
    } else {
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        Dialogs.alert("Instructions", "Turn Order will be decided by rolling dice for all players",
            null);
      } else if (playingPlayerId.equals(GameApi.VIEWER_ID)) {
        Dialogs.alert("Instructions", "Watch the game! Hope you enjoy!", null);
      } else if (playerLost || phase.equals(GameResources.END_GAME)) {
        Dialogs.alert("Instructions", "You lost. Better luck next time.", null);
      }  else {
        Dialogs.alert("Instructions", "Wait for your turn! Watch what your enemy is doing!", 
            null);
      }
    }
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
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
        style = style.replaceFirst("stroke:red", "stroke:#000000");
        territoryElement.setAttribute("style", style);
        territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(0 + "");
      }
    }
    
  }
}
