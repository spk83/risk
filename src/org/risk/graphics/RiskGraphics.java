package org.risk.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.graphics.i18n.messages.ConstantMessages;
import org.risk.graphics.i18n.messages.DialogInstructions;
import org.risk.graphics.i18n.messages.PhaseMessages;
import org.risk.graphics.i18n.messages.VariableMessages;
import org.risk.graphics.i18n.names.ContinentNames;
import org.risk.graphics.i18n.names.TerritoryNames;
import org.risk.logic.Attack;
import org.risk.logic.Attack.AttackResult;
import org.risk.logic.Card;
import org.risk.logic.Continent;
import org.risk.logic.GameApi;
import org.risk.logic.GameResources;
import org.risk.logic.Player;
import org.risk.logic.RiskAI;
import org.risk.logic.RiskState;
import org.risk.logic.Territory;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.ConfirmDialog.ConfirmCallback;
import com.googlecode.mgwt.ui.client.dialog.Dialog;
import com.googlecode.mgwt.ui.client.widget.Carousel;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ActionButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.InfoButton;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

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
  private PhaseMessages phaseMessages;
  private TerritoryNames territoryNames;
  private VariableMessages variableMessages;
  private DialogInstructions dialogInstructions;
  private ConstantMessages constantMessages;
  private ContinentNames continentNames;

  @UiField
  LayoutPanel main;
  
  @UiField
  HeaderPanel headerPanel;
  
  @UiField
  FlowPanel display;

  @UiField
  HTML mapContainer;
  
  @UiField
  ButtonBar footerBar;
  
  @UiField
  InfoButton instructions;
  
  @UiField
  ActionButton playersInfo;
  
  @UiField
  RoundPanel notification;
  
  private RiskState currentRiskState;
  private Map<String, Integer> territoryDelta = new HashMap<String, Integer>();
  private Map<Card, Image> cardImagesOfCurrentPlayer = new HashMap<Card, Image>();
  private List<Card> selectedCards = new ArrayList<Card>();
  
  private List<HandlerRegistration> territoryHandlers = new ArrayList<HandlerRegistration>();
  private List<HandlerRegistration> cardHandlers = new ArrayList<HandlerRegistration>();
  private HeaderButton selectCardsButton = new HeaderButton();
  private HeaderButton autoClaim = new HeaderButton();
  private HeaderButton autoDeploy = new HeaderButton();
  private HeaderButton endAttack = new HeaderButton();
  private HeaderButton endReinforce = new HeaderButton();
  private HeaderButton endFortify = new HeaderButton();
  private HeaderButton backButton = new HeaderButton();
  private HeaderPanel otherHeaderPanel = new HeaderPanel();
  private VerticalPanel attackResultPanel = new VerticalPanel();
  private Carousel playersStatusPanel = new Carousel();
  private RiskAI riskAI = new RiskAI();
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
  private boolean cardTrade = false;
  
  private PopupChoices fortifyOpt;
  private ImageResource attackImageResource;
  private PopupPanel dicePanel;
  
  public RiskGraphics() {
    territoryNames = (TerritoryNames) GWT.create(TerritoryNames.class);
    phaseMessages = (PhaseMessages) GWT.create(PhaseMessages.class);
    variableMessages = (VariableMessages) GWT.create(VariableMessages.class);
    dialogInstructions = (DialogInstructions) GWT.create(DialogInstructions.class);
    constantMessages = (ConstantMessages) GWT.create(ConstantMessages.class);
    continentNames = (ContinentNames) GWT.create(ContinentNames.class);
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    attackImages = GWT.create(AttackImages.class);
    gameSounds = GWT.create(GameSounds.class);
    dicePanel = new PopupPanel(constantMessages);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    soundResource = new SoundResource(gameSounds);
    display.getElement().setAttribute("style", 
        display.getElement().getAttribute("style") + "-webkit-box-flex:1;");
    display.getElement().setId("container");
    mapContainer.getElement().setId("map");
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    display.setStyleName("map");
    attackImageResource = attackImages.tank();
    createSelectCardsButton();
    createEndAttackButton();
    createEndReinforceButton();
    createEndFortifyButton();
    createAutoClaimButton();
    createAutoDeployButton();
    createBackButton();
    addMapHandlers();
    changeSVGLanguage();
    mapContainer.getElement().getStyle().setWidth(100, Unit.PCT);
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        resize();
      }
    });
  }
  
  void resize() {
    mapContainer.getElement().getStyle().setHeight(display.getElement().getClientHeight(), Unit.PX);
  }
  
  private static boolean isSVGLanguageChangeRequired() {
    return !(LocaleInfo.getCurrentLocale().getLocaleName().equals("default") 
        || LocaleInfo.getCurrentLocale().getLocaleName().indexOf("en") != -1);
  }
  
  private void changeSVGLanguage() {
    changeSVGContinentsAndItsUnitsByLocale();
    changeSVGTerritoriesByLocale();
  }
  
  private void changeSVGContinentsAndItsUnitsByLocale() {
    if (isSVGLanguageChangeRequired()) {
      for (String continentId : Continent.CONTINENT_SVG_ID.keySet()) {
        final OMElement continentText = boardElt.getElementById(continentId);
        OMNodeList<OMNode> continentTextChildNodes = continentText.getChildNodes();
        List<String> newContinentNameList = GameResources.getNewTerritoryNameList(
            continentNames.continents().get(continentId), continentTextChildNodes.getLength() - 1);
        int i = 0;
        for (i = 0; i < newContinentNameList.size(); ++i) {
          OMNode continentTextNode = continentTextChildNodes.getItem(i);
          continentTextNode.getFirstChild().setNodeValue(
              newContinentNameList.get(i));
        }
        for (; i < continentTextChildNodes.getLength() - 1; ++i) {
          OMNode continentTextNode = continentTextChildNodes.getItem(i);
          continentTextNode.getFirstChild().setNodeValue("");
        }
        OMNode unitTextNode = continentTextChildNodes.getItem(continentTextChildNodes
            .getLength() - 1);
        unitTextNode.getFirstChild().setNodeValue(variableMessages.nUnits(Continent.UNITS_VALUE.get(
            Continent.CONTINENT_SVG_ID.get(continentId))));
      }
    }
  }
  
  private void changeSVGTerritoriesByLocale() {
    if (isSVGLanguageChangeRequired()) {
      for (String territoryId : Territory.SVG_ID_MAP.keySet()) {
        final OMElement territoryText = boardElt.getElementById(territoryId + "_text");
        OMNodeList<OMNode> territoryTextChildNodes = territoryText.getChildNodes();
        int newTextPointer = 0;
        List<String> newTerritoryNameList = GameResources.getNewTerritoryNameList(
            territoryNames.countries().get(territoryId), territoryTextChildNodes.getLength());
        
        for (OMNode territoryTextNode : territoryTextChildNodes) {
          if (newTextPointer < newTerritoryNameList.size()) {
            territoryTextNode.getFirstChild().setNodeValue(
                newTerritoryNameList.get(newTextPointer));
          } else {
            territoryText.removeChild(territoryTextNode);
          }
          newTextPointer++;
        }
      }
    }
  }
  
  private void createBackButton() {
    backButton.setBackButton(true);
    backButton.setText(constantMessages.back());
    backButton.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        playersStatusPanel.removeFromParent();
        backButton.removeFromParent();
        otherHeaderPanel.removeFromParent();
        playersInfo.setVisible(true);
        footerBar.removeFromParent();
        
        headerPanel.setVisible(true);
        display.setVisible(true);
        main.add(footerBar);
      }
    });
  }
  
  private void cardsCleanup() {
    selectedCards.clear();
    removeHandlers(cardHandlers);
    cardImagesOfCurrentPlayer.clear();
    selectCardsButton.removeFromParent();
    playersStatusPanel.removeFromParent();
    otherHeaderPanel.removeFromParent();
    playersInfo.setVisible(true);
    footerBar.removeFromParent();
    
    headerPanel.setVisible(true);
    display.setVisible(true);
    main.add(footerBar);
  }
  
  private void createSelectCardsButton() {
    selectCardsButton.setRoundButton(true);
    selectCardsButton.setText(constantMessages.done());
    selectCardsButton.addTapHandler(new TapHandler() {
      
      @Override
      public void onTap(TapEvent event) {
        int units = Card.getUnits(selectedCards, currentRiskState.getTradeNumber());
        if (mandatoryCardSelection && units == 0) {
          CustomDialogPanel.alert(constantMessages.invalidSelection(), 
              constantMessages.mandatoryCardSelection(), null, constantMessages.ok());
          return;
        }
        if (selectedCards.size() == 0) {
          cardsCleanup();
          riskPresenter.cardsTraded(null);
        } else if (units > 0) {
          List<Integer> selectedIntCards = Card.getCardIdsFromCardObjects(selectedCards);
          cardsCleanup();
          riskPresenter.cardsTraded(selectedIntCards);
        } else {
          ConfirmCallback callback = new ConfirmCallback() {
            @Override
            public void onOk() {
              cardsCleanup();
              riskPresenter.cardsTraded(null);
            }
            @Override
            public void onCancel() {
            }
          };
          
          CustomDialogPanel.confirm(constantMessages.invalidSelection(), 
              constantMessages.confirmDialog(), callback, constantMessages.ok(), 
              constantMessages.cancel());
        }
      }
    });
  }
  
  private void createAutoClaimButton() {
    autoClaim.setRoundButton(true);
    autoClaim.setText(constantMessages.auto());
    autoClaim.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        autoClaim.removeFromParent();
        currentRiskState.getPlayersMap().get(riskPresenter.getMyPlayerId()).setAutoClaim(true);
        chooseNewTerritory();
      }
    });
  }
  
  private void createAutoDeployButton() {
    autoDeploy.setRoundButton(true);
    autoDeploy.setText(constantMessages.auto());
    autoDeploy.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        autoDeploy.removeFromParent();
        currentRiskState.getPlayersMap().get(riskPresenter.getMyPlayerId()).setAutoDeploy(true);
        chooseTerritoryForDeployment();
      }
    });
  }
  
  private void createEndAttackButton() {
    endAttack.setRoundButton(true);
    endAttack.setText(constantMessages.end());
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
    endReinforce.setText(constantMessages.end());
    endReinforce.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        endReinforce.removeFromParent();
        reinforce = false;
        notification.clear();
        notification.setVisible(false);
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    });
  }
  
  private void createEndFortifyButton() {
    endFortify.setRoundButton(true);
    endFortify.setText(constantMessages.end());
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
              fortify(territoryId, null);
            }
          } else if (playerId.equals(GameApi.VIEWER_ID)) {
            CustomDialogPanel.alert(constantMessages.notAllowed(), 
                constantMessages.viewerNotAllowed(), null, constantMessages.ok());
          } else {
            CustomDialogPanel.alert(constantMessages.notAllowed(), 
                constantMessages.playerNotAllowed(), null, constantMessages.ok());
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
    currentRiskState = riskState;
    changeSVGMap(riskState);
    dicePanel.clearPanel();
    dicePanel.hide();
    if (fortifyOpt != null) {
      fortifyOpt.hide();
    }
    HTML phase = new HTML("<b>" + phaseMessages.uiPhaseMap().get(riskState.getPhase()) 
        + "</b>");
    phase.getElement().getStyle().setTop(10, Unit.PX);
    headerPanel.setLeftWidget(phase);
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      List<String> diceList = Lists.newArrayList(riskState.getDiceResult().keySet());
      Collections.sort(diceList);
      soundResource.playDiceAudio();
      dicePanel.getDialogPanel().getDialogTitle().setText(phaseMessages.turnOrder());
      for (String dice: diceList) {
        FlowPanel diceFlowPanel = new FlowPanel();
        String playerName = currentRiskState.getPlayersMap().get(dice).getPlayerName();
        if (playerName == null || playerName.isEmpty()) {
          if (dice.equals(GameApi.AI_PLAYER_ID)) {
            playerName = constantMessages.computer();
          } else {
            playerName = variableMessages.playerName(dice);
          }
        }
        DiceAnimation diceAnimation = new DiceAnimation(
            diceImages, diceFlowPanel, 3, playerName, riskState.getDiceResult().get(dice));
        dicePanel.addPanel(diceFlowPanel);
        diceAnimation.run(1000);
      }
      String playingPlayerId = riskPresenter.getMyPlayerId();
      String turnPlayerId = currentRiskState.getTurn();
      if (playingPlayerId.equals(turnPlayerId)) {
        dicePanel.setOkBtnHandler(riskPresenter, 1, riskPresenter.isAIPresent());
      } else {
        dicePanel.setOkBtnHandler(riskPresenter, 0, riskPresenter.isAIPresent());
      }
      dicePanel.center();
   }
    claimTerritory = false;
    deployment = false;
    reinforce = false;
    attack = false;
    fortify = false;
    mandatoryCardSelection = false;
    cardTrade = false;
    setStyle();
    if (riskPresenter.isAIPresent()) {
      instructions.setVisible(false);
      playersInfo.setVisible(false);
    } else {
      instructions.setVisible(true);
      playersInfo.setVisible(true);
    }
    if (mapContainer.getElement().getClientHeight() != display.getElement().getClientHeight()) {
      resize();
    }
  }

  private void setStyle() {
    headerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    mapContainer.getElement().getStyle().setPosition(Position.ABSOLUTE);
    mapContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
  }

  private void claimTerritory(String territoryName) {

    String playerId = riskPresenter.getMyPlayerId();
    OMElement territory = boardElt.getElementById(territoryName);
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    if (territorySelected == null) {
      String style = territory.getAttribute("style");
      style = style.replaceFirst("fill:#ffffff", "fill:" 
            + currentRiskState.getPlayersMap().get(playerId).getPlayerColor());
      territory.setAttribute("style", style);
      territoryUnits.getFirstChild().getFirstChild().setNodeValue("1");
      claimTerritory = false;
      riskPresenter.newTerritorySelected(territoryId, 
          currentRiskState.getPlayersMap().get(playerId).isAutoClaim());
      soundResource.playDeployAudio();
    } else {
      if (territorySelected.getPlayerKey().equals(playerId)) {
        CustomDialogPanel.alert(constantMessages.notAllowed(), 
            constantMessages.alreadyOwnTerritory(), null, constantMessages.ok());
      } else {
        CustomDialogPanel.alert(constantMessages.notAllowed(), 
            constantMessages.emptyTerritory(), null, constantMessages.ok());
      }
    }
  }
  
  private void deployment(String territoryName) {
    String playerId = riskPresenter.getMyPlayerId();
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);

    if (territorySelected.getPlayerKey().equals(playerId)) {
      int units = Integer.parseInt(territoryUnits.getFirstChild().getFirstChild().getNodeValue());
      territoryUnits.getFirstChild().getFirstChild().setNodeValue((units + 1) + "");
      deployment = false;
      riskPresenter.territoryForDeployment(territoryId, 
          currentRiskState.getPlayersMap().get(playerId).isAutoDeploy());
      soundResource.playDeployAudio();
    } else {
      CustomDialogPanel.alert(constantMessages.notAllowed(), 
          constantMessages.selectYourTerritory(), null, constantMessages.ok());
    }
  }
  
  private void reinforce(String territoryName) {
    String playerId = riskPresenter.getMyPlayerId();
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    
    if (territorySelected.getPlayerKey().equals(playerId)) {
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
      notification.add(new HTML("<b>" + variableMessages.unclaimedUnitsLeft(unclaimedUnits)
          + "</b>"));
      notification.getElement().getStyle().setFontSize(12, Unit.PX);
      notification.getElement().getStyle().setPadding(7, Unit.PX);
      
      territorySelected.setCurrentUnits(territorySelected.getCurrentUnits() + 1);
      if (unclaimedUnits == 0) {
        reinforce = false;
        notification.clear();
        notification.setVisible(false);
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    } else {
      CustomDialogPanel.alert(constantMessages.notAllowed(), 
          constantMessages.selectYourTerritory(), null, constantMessages.ok());
    }
  }
  
  private void attack(String territoryName) {
    String playerId = riskPresenter.getMyPlayerId();
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    OMElement territory = boardElt.getElementById(territoryName);
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    String style = territory.getAttribute("style");
    
    if (territorySelected.getPlayerKey().equals(playerId)) {
      // Attacking territory selected
      if (attackFromTerritory == null) {
        int units = ((Player) currentRiskState.getPlayersMap().get(playerId))
            .getTerritoryUnitMap().get(territoryId);
        if (units < 2) {
          CustomDialogPanel.alert(constantMessages.notAllowed(), 
              constantMessages.notEnoughUnits(), null, constantMessages.ok());
          return;
        }
        attackFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
      } else if (attackFromTerritory.equals(territoryId)) {
        attackFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
      } else {
        CustomDialogPanel.alert(constantMessages.notAllowed(), 
            constantMessages.selectOpponentTerritory(), null, constantMessages.ok());
      }
      territory.setAttribute("style", style);
      
      return;
    } else {
      // Defending territory selected
      if (attackFromTerritory == null) {
        CustomDialogPanel.alert(constantMessages.notAllowed(), 
            constantMessages.selectOwnTerritoryAttack(), null, constantMessages.ok());
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
        CustomDialogPanel.alert(constantMessages.notAllowed(),  
            constantMessages.selectAdjacentTerritory(), null, constantMessages.ok());
        return;
      }
    }
  } 
  
  private void fortify(String territoryName, Integer unitsToMove) {
    String playerId = riskPresenter.getMyPlayerId();
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    OMElement territory = boardElt.getElementById(territoryName);
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);
    String style = territory.getAttribute("style");

    if (territorySelected.getPlayerKey().equals(playerId)) {
      if (fortifyFromTerritory == null) {
        int units = ((Player) currentRiskState.getPlayersMap().get(playerId))
            .getTerritoryUnitMap().get(territoryId);
        if (units < 2) {
          CustomDialogPanel.alert(constantMessages.notAllowed(), 
              constantMessages.fortifyNotPossible(), null, constantMessages.ok());
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
          CustomDialogPanel.alert(constantMessages.notAllowed(), 
              constantMessages.selectOwnTerritoryFortify(), null, constantMessages.ok());
          return;
        }
        fortifyToTerritory = territoryId;
        int fromTerritory = Integer.parseInt(fortifyFromTerritory);
        int toTerritory = Integer.parseInt(fortifyToTerritory);
        List<String> territoryList = Lists.newArrayList(
            currentRiskState.getPlayersMap().get(playerId).getTerritoryUnitMap().keySet());
        if (Territory.isFortifyPossible(fromTerritory, toTerritory, territoryList)) {
          style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
          territory.setAttribute("style", style);
          int unitsOnFromTerritory = currentRiskState.getTerritoryMap()
              .get(fortifyFromTerritory).getCurrentUnits();
          if (riskPresenter.isAIPresent()) {
            unitsOnFromTerritory += unitsToMove.intValue();
          }
          List<String> options = Lists.newArrayList();
          for (int i = 1; i <= unitsOnFromTerritory - 1; i++) {
            options.add(i + "");
          }
          fortifyOpt = new PopupChoices(constantMessages.chooseUnitsToMove(),
              options, new PopupChoices.OptionChosen() {
            @Override
            public void optionChosen(String option) {
              territoryDelta = new HashMap<String, Integer>();
              territoryDelta.put(fortifyFromTerritory, -Integer.parseInt(option));
              territoryDelta.put(fortifyToTerritory, Integer.parseInt(option));
              fortify = false;
              endFortify.removeFromParent();
              riskPresenter.fortifyMove(territoryDelta);
            }
          }, constantMessages, riskPresenter.isAIPresent());
          fortifyOpt.center();
        } else {
          CustomDialogPanel.alert(constantMessages.notAllowed(), 
              constantMessages.selectOwnTerritoryFortify(), null, constantMessages.ok());
          return;
        }
      }
    } else {
      CustomDialogPanel.alert(constantMessages.notAllowed(), 
          constantMessages.selectOwnTerritoryFortify(), null, constantMessages.ok());
    }
  }
  
  @Override
  public void chooseNewTerritory() {
    if (riskPresenter.isAIPresent()) {
      claimTerritory(riskAI.getNewTerritory(
          currentRiskState.getPlayersMap().get(GameApi.AI_PLAYER_ID), 
          currentRiskState.getUnclaimedTerritory()));
    } else if (currentRiskState.getPlayersMap().get(riskPresenter.getMyPlayerId()).isAutoClaim()) {
      claimTerritory(riskAI.getNewTerritory(
          currentRiskState.getPlayersMap().get(riskPresenter.getMyPlayerId()), 
          currentRiskState.getUnclaimedTerritory()));
    } else {
      headerPanel.setRightWidget(autoClaim);
      claimTerritory = true;
    }
  }
  
  @Override
  public void chooseTerritoryForDeployment() {
    if (riskPresenter.isAIPresent()) {
      deployment(riskAI.getTerritoryForDeployment(
          currentRiskState.getPlayersMap().get(GameApi.AI_PLAYER_ID).getTerritoryUnitMap(), 
          currentRiskState.getTerritoryMap()));
    } else if (currentRiskState.getPlayersMap().get(riskPresenter.getMyPlayerId()).isAutoDeploy()) {
      deployment(riskAI.getTerritoryForDeployment(
          currentRiskState.getPlayersMap().get(riskPresenter.getMyPlayerId()).getTerritoryUnitMap(),
          currentRiskState.getTerritoryMap()));
    } else {
      headerPanel.setRightWidget(autoDeploy);
      deployment = true;
    }
  }
  
  @Override
  public void chooseCardsForTrading(boolean mandatoryCardSelection) {
    this.mandatoryCardSelection = mandatoryCardSelection;
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      Player currentPlayer = currentRiskState.getPlayersMap().get(turnPlayerId);
      List<Integer> playerCards = currentPlayer.getCards();
      if (playerCards != null && playerCards.size() >= 3) {
        List<Card> cardObjects = Card.getCardsById(currentRiskState.getCardMap(), playerCards);
        if (Card.isTradePossible(cardObjects)) {
          if (riskPresenter.isAIPresent()) {
            final List<Card> selectedCardsByAI = riskAI.chooseCardsForTrading(
                mandatoryCardSelection, cardObjects, currentPlayer.getTerritoryUnitMap().size(), 
                currentRiskState.getPlayersMap().size());
            if (selectedCardsByAI.size() == 3) {
              cardTrade = true;
              playersInfo.fireEvent(new TapEvent(playersInfo, playersInfo.getElement(), 0, 0));
              for (Card aiCard : selectedCardsByAI) { 
                selectCard(cardImagesOfCurrentPlayer.get(aiCard), aiCard);
              }
              new Timer() {
                @Override
                public void run() {
                  cardsCleanup();
                  riskPresenter.cardsTraded(Card.getCardIdsFromCardObjects(selectedCardsByAI));
                }
              } .schedule(2000);
            } else {
              riskPresenter.cardsTraded(null);
            }
          } else {
            cardTrade  = true;
            playersInfo.fireEvent(new TapEvent(playersInfo, playersInfo.getElement(), 0, 0));
            return;
          }
        } else {
            riskPresenter.cardsTraded(null);
        }
      } else {
        riskPresenter.cardsTraded(null);
      }
    }
  }
  
  private void registerCardHandlers() {
    if (!riskPresenter.isAIPresent()) {
      for (Map.Entry<Card, Image> imageCard : cardImagesOfCurrentPlayer.entrySet()) { 
        cardHandlers.add(addCardHandlers(imageCard.getValue(), imageCard.getKey()));
      }
      otherHeaderPanel.setRightWidget(selectCardsButton);
    }
    otherHeaderPanel.setCenterWidget(new HTML("<b>Trade Cards</b>"));
    backButton.removeFromParent();
  }
  
  private HandlerRegistration addCardHandlers(final Image image, final Card card) {
    TouchDelegate i = new TouchDelegate(image);
    return i.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        selectCard(image, card);
      }
    });
  }
  
  private void selectCard(final Image image, final Card card) {
    if (image.getStyleName().equals("risk-cards")) {
      image.setStyleName("risk-cards-selected");
      soundResource.playCardAudio();
      selectedCards.add(card);
    } else {
      image.setStyleName("risk-cards");
      selectedCards.remove(card);
    }
  }

  @Override
  public void reinforceTerritories() {
    if (territoryDelta == null) {
      territoryDelta = new HashMap<String, Integer>();
    }
    territoryDelta.clear();
    Player player = (Player) currentRiskState.getPlayersMap()
        .get(riskPresenter.getMyPlayerId());
    unclaimedUnits = player.getUnclaimedUnits();
    soundResource.playAddUnitsAudio();
    if (riskPresenter.isAIPresent()) {
      while (unclaimedUnits > 0) {
        String territoryToReinforce = null;
        territoryToReinforce = riskAI.getWeakestTerritory(player.getTerritoryUnitMap(), 
              currentRiskState.getTerritoryMap());
        if (territoryToReinforce == null) {
          territoryToReinforce = riskAI.getTerritoryForDeployment(player.getTerritoryUnitMap(), 
                currentRiskState.getTerritoryMap());
        }
        reinforce(territoryToReinforce); 
      }
    } else {
      CustomDialogPanel.alert(constantMessages.info(), 
          variableMessages.unclaminedUnits(unclaimedUnits), null, constantMessages.ok());
      headerPanel.setRightWidget(endReinforce);
      reinforce = true;
    }
  }
  
  @Override
  public void attack() {
    attackToTerritory = null;
    attackFromTerritory = null;
    if (riskPresenter.isAIPresent()) {
      final List<String> attack = riskAI.performAttack(
          currentRiskState.getPlayersMap().get(GameApi.AI_PLAYER_ID).getTerritoryUnitMap(),
          currentRiskState.getTerritoryMap());
      if (attack == null || attack.isEmpty()) {
        riskPresenter.endAttack();
      } else {
        new Timer() {
          
          @Override
          public void run() {
            attack(attack.get(0));
            attack(attack.get(1));
          }
        } .schedule(500);
      }
    } else {
      headerPanel.setRightWidget(endAttack);
      attack = true;
    }
  }
  
  @Override
  public void attackResult() {
    attackResultPanel.clear();
    String attackerId =  currentRiskState.getAttack().getAttackerPlayerId();
    String attackerKey = currentRiskState.getPlayersMap().get(attackerId).getPlayerName();
    String defenderId = currentRiskState.getAttack().getDefenderPlayerId();
    String defenderKey = currentRiskState.getPlayersMap().get(defenderId).getPlayerName();
    
    if (attackerKey == null || attackerKey.isEmpty()) {
      if (attackerId.equals(GameApi.AI_PLAYER_ID)) {
        attackerKey = constantMessages.computer();
      } else {
        attackerKey = variableMessages.playerName(attackerId);
      }
    }
    
    if (defenderKey == null || defenderKey.isEmpty()) {
      if (defenderId.equals(GameApi.AI_PLAYER_ID)) {
        defenderKey = constantMessages.computer();
      } else {
        defenderKey = variableMessages.playerName(defenderId);
      }
    }
    
    String attackingTerritorySVG = Territory.SVG_NAME_MAP.get(
        currentRiskState.getAttack().getAttackerTerritoryId());
    String defendingTerritorySVG = Territory.SVG_NAME_MAP.get(
        currentRiskState.getAttack().getDefenderTerritoryId());
    
    String attackSVG = 
        Territory.SVG_NAME_MAP.get(currentRiskState.getAttack().getAttackerTerritoryId());
    String defendSVG = 
        Territory.SVG_NAME_MAP.get(currentRiskState.getAttack().getDefenderTerritoryId());
    String attackerText = variableMessages.playerTextAttackResult(attackerKey, 
        territoryNames.countries().get(attackSVG));
    String defenderText = variableMessages.playerTextAttackResult(defenderKey, 
        territoryNames.countries().get(defendSVG));
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    int attackUnits = currentRiskState.getAttack().getAttackUnits();
    int defendUnits = currentRiskState.getAttack().getDefendUnits();
    final AttackResult attackResult = currentRiskState.getAttack().getAttackResult();

    
    int tankSize = (int) (Math.min(mapContainer.getOffsetHeight(), mapContainer.getOffsetWidth()) 
        * 0.08);
    OMElement territoryUnitsElement = boardElt.getElementById(attackingTerritorySVG + "_units");
    territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(attackUnits + "");
    int startAttackXCords = territoryUnitsElement.getElement().getAbsoluteLeft()
        - tankSize / 2;
    int startAttackYCords = territoryUnitsElement.getElement().getAbsoluteTop() 
        - headerPanel.getElement().getClientHeight() - tankSize / 2;
    
    territoryUnitsElement = boardElt.getElementById(defendingTerritorySVG + "_units");
    territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(defendUnits + "");
    int endAttackXCords = territoryUnitsElement.getElement().getAbsoluteLeft()
        - tankSize / 2;
    int endAttackYCords = territoryUnitsElement.getElement().getAbsoluteTop() 
        - headerPanel.getElement().getClientHeight() - tankSize / 2;
    
    TankMovingAnimation animation = new TankMovingAnimation(
        mapContainer, startAttackXCords, startAttackYCords, endAttackXCords, endAttackYCords, 
        attackImageResource, soundResource.getAttackAudio(), tankSize);
    
    FlowPanel attackerDicePanel = new FlowPanel();
    final DiceAnimation diceAnimationAttacker = new DiceAnimation(diceImages, attackerDicePanel, 3, 
        attackerText, currentRiskState.getAttack().getAttackerDiceRolls());
    
    FlowPanel defenderDicePanel = new FlowPanel();
    final DiceAnimation diceAnimationDefender = new DiceAnimation(diceImages, defenderDicePanel, 3, 
        defenderText, currentRiskState.getAttack().getDefenderDiceRolls());
    dicePanel.clearPanel();
    dicePanel.getDialogPanel().getDialogTitle().setText(phaseMessages.attackResult());
    dicePanel.setPanelSize("200px", "200px");
    if (playingPlayerId.equals(turnPlayerId)) {
      dicePanel.setOkBtnHandler(riskPresenter, 2, riskPresenter.isAIPresent());
    } else {
      dicePanel.setOkBtnHandler(riskPresenter, 0, riskPresenter.isAIPresent());
    }
    dicePanel.addPanel(attackerDicePanel);
    dicePanel.addPanel(defenderDicePanel);
  
    attackResultPanel.add(new HTML(variableMessages.attackerLost(-1 * attackResult.getDeltaAttack()
        )));
    attackResultPanel.add(new HTML(variableMessages.defenderLost(-1 * attackResult.getDeltaDefend()
        )));
    
    if (attackResult.isAttackerATerritoryWinner()) {
      attackResultPanel.add(new HTML(variableMessages.playerCaptures(attackerKey, 
          territoryNames.countries().get(defendSVG))));
    }
    if (attackResult.isDefenderOutOfGame()) {
      attackResultPanel.add(new HTML(variableMessages.defenderOut(defenderKey, attackerKey)));
    }
    if (attackResult.isAttackerAWinnerOfGame()) {
      attackResultPanel.add(new HTML(variableMessages.attackerWinner(attackerKey)));
      
    } else if (attackResult.isTradeRequired()) {
      attackResultPanel.add(new HTML(variableMessages.attackerTradeCards(attackerKey)));
    }
    animation.run(2000);
    
    Timer attackAnimationTimer = new Timer() {
      @Override
      public void run() {
        diceAnimationAttacker.run(1000);
        diceAnimationDefender.run(1000);
        soundResource.playDiceAudio();
        dicePanel.center();
        new Timer() {
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
        } .schedule(1200);
        if (riskPresenter.isAIPresent()) {
          new Timer() {
            @Override
            public void run() {
              dicePanel.hide();
              riskPresenter.attackResultMove();
            }
          } .schedule(3500);
        }
      }
    };
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
      if (riskPresenter.isAIPresent()) {
        riskPresenter.moveUnitsAfterAttack(riskAI.moveUnitsAfterAttack(minUnitsToNewTerritory, 
            unitsOnAttackingTerritory - 1, currentRiskState.getTerritoryMap(), 
            currentRiskState.getLastAttackingTerritory().intValue(), 
            currentRiskState.getUnclaimedTerritory().get(0).intValue(),
            Sets.newHashSet(currentRiskState.getPlayersMap()
                .get(GameApi.AI_PLAYER_ID).getTerritoryUnitMap().keySet())));
      } else {
        new PopupChoices(constantMessages.chooseUnitsToMove(),
            options, new PopupChoices.OptionChosen() {
          @Override
          public void optionChosen(String option) {
            riskPresenter.moveUnitsAfterAttack(Integer.parseInt(option));
          }
        }, constantMessages, false).center();
      }
    }
  }

  @Override
  public void fortify() {
    fortifyFromTerritory = null;
    fortifyToTerritory = null;
    if (riskPresenter.isAIPresent()) {
      fortify = true;
      Map<String, Territory> territoryMap = currentRiskState.getTerritoryMap();
      Map<String, Integer> deltaFortify = riskAI.fortify(territoryMap, 
          Maps.newHashMap(currentRiskState.getPlayersMap()
              .get(GameApi.AI_PLAYER_ID).getTerritoryUnitMap()));
      String source = null;
      String destination = null;
      Integer unitsToMove = null;
      if (deltaFortify != null && deltaFortify.size() == 2) {
        for (Map.Entry<String, Integer> deltaTerritoryEntry : deltaFortify.entrySet()) {
          if (deltaTerritoryEntry.getValue() < 0) {
            source = deltaTerritoryEntry.getKey();
          } else if (deltaTerritoryEntry.getValue() > 0) {
            destination = deltaTerritoryEntry.getKey();
            unitsToMove = deltaTerritoryEntry.getValue();
          }
        }
      }
      if (source != null && destination != null) {
        fortify(Territory.SVG_NAME_MAP.get(Integer.parseInt(source)), unitsToMove);
        final Integer units = unitsToMove;
        final Integer destinationTerritory = Integer.parseInt(destination);
        Timer destinationTimer = new Timer() {
          @Override
          public void run() {
            fortify(Territory.SVG_NAME_MAP.get(destinationTerritory), units);
            this.cancel();
          }
        };
        destinationTimer.schedule(1000);
        Timer selectUnitsToMove = new Timer() {
          @Override
          public void run() {
            ((PopupChoices) fortifyOpt).fireValueChange(units);
            this.cancel();
          }
        };
        selectUnitsToMove.schedule(2000);
        
        Timer tapEventTimer = new Timer() {
          @Override
          public void run() {
            ((PopupChoices) fortifyOpt).fireTapEvent();
            this.cancel();
          }
        };
        tapEventTimer.schedule(3000);
      } else {
        riskPresenter.fortifyMove(null);
      }
    } else {
      String playingPlayerId = riskPresenter.getMyPlayerId();
      String turnPlayerId = currentRiskState.getTurn();
      if (playingPlayerId.equals(turnPlayerId)) {
        headerPanel.setRightWidget(endFortify);
      }
      fortify = true;
    }
  }

  @Override
  public void endGame() {
    String playingPlayerId = riskPresenter.getMyPlayerId();
    String turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId.equals(turnPlayerId)) {
      final Dialog dialog = CustomDialogPanel.alert(phaseMessages.gameEnded(), 
          constantMessages.gameWon(), null, constantMessages.ok());
      if (riskPresenter.isAIPresent()) {
        new Timer() {
          @Override
          public void run() {
            dialog.hide();
          }
        } .schedule(1500);
      }
      riskPresenter.endGame();
    } else {
      String playerName = currentRiskState.getPlayersMap().get(turnPlayerId).getPlayerName();
      if (playerName == null || playerName.isEmpty()) {
        if (turnPlayerId.equals(GameApi.AI_PLAYER_ID)) {
          playerName = constantMessages.computer();
        } else {
          playerName = variableMessages.playerName(turnPlayerId);
        }
      }
      final Dialog dialog = CustomDialogPanel.alert(phaseMessages.gameEnded(), 
          variableMessages.playerWon(playerName), null,
          constantMessages.ok());
      if (riskPresenter.isAIPresent()) {
        new Timer() {
          @Override
          public void run() {
            dialog.hide();
          }
        } .schedule(1500);
      }
    }
  }
  
  @Override
  public void invalidNumberOfPlayers(int size) {
    CustomDialogPanel.alert(constantMessages.invalidNumberOfPlayers(), 
        variableMessages.invalidNumberOfPlayers(size), null,
        constantMessages.ok());
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
        CustomDialogPanel.alert(dialogInstructions.instructions(), 
            dialogInstructions.setTurnOrder(), null, constantMessages.ok());
      } else if (phase.equals(GameResources.CLAIM_TERRITORY)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), 
            dialogInstructions.claimTerritory(), null, constantMessages.ok());
      } else if (phase.equals(GameResources.DEPLOYMENT)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.deployment(), 
            null, constantMessages.ok());
      } else if (phase.equals(GameResources.CARD_TRADE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), variableMessages.cardTrade(4),
            null, constantMessages.ok());
      } else if (phase.equals(GameResources.ATTACK_TRADE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), variableMessages.cardTrade(5),
            null, constantMessages.ok());
      } else if (phase.equals(GameResources.REINFORCE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.reinforce(),
            null, constantMessages.ok());
      } else if (phase.equals(GameResources.ATTACK_PHASE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.attack(),
            null, constantMessages.ok());
      } else if (phase.equals(GameResources.ATTACK_RESULT)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(),
            dialogInstructions.attackResult(), null, constantMessages.ok());
      } else if (phase.equals(GameResources.ATTACK_REINFORCE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(),
            dialogInstructions.attackReinforce(), null, constantMessages.ok());
      } else if (phase.equals(GameResources.ATTACK_OCCUPY)) {
        String instruction = "";
        if (currentRiskState.getTerritoryWinner().equals(playingPlayerId)) {
          instruction += dialogInstructions.territoryWinner();
        }
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.attackOccupy()
            + instruction, null, constantMessages.ok());
      } else if (phase.equals(GameResources.FORTIFY)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.fortify(), 
            null, constantMessages.ok());
      } else if (phase.equals(GameResources.END_GAME) || phase.equals(GameResources.GAME_ENDED)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), constantMessages.endGame(),
            null, constantMessages.ok());
      } else {
        CustomDialogPanel.alert(dialogInstructions.instructions(), phase, null,
            constantMessages.ok());
      }
    } else if (playingPlayerId.equals(GameApi.VIEWER_ID)) {
      CustomDialogPanel.alert(dialogInstructions.instructions(), constantMessages.viewer(),
          null, constantMessages.ok());
    } else {
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), constantMessages.turnOrder(),
            null, constantMessages.ok());
      } else if (playerLost || phase.equals(GameResources.END_GAME)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), constantMessages.playerLost(),
            null, constantMessages.ok());
      }  else {
        CustomDialogPanel.alert(dialogInstructions.instructions(), constantMessages.notYourTurn(),
            null, constantMessages.ok());
      }
    }
  }
  
  
  @UiHandler("playersInfo")
  public void onTapPlayersInfoButton(TapEvent e) {
    playersStatusPanel = new Carousel();
    display.setVisible(false);
    playersInfo.setVisible(false);
    headerPanel.setVisible(false);
    footerBar.removeFromParent();
    
    main.add(otherHeaderPanel);
    main.add(playersStatusPanel);
    main.add(footerBar);
    otherHeaderPanel.setLeftWidget(backButton);
    otherHeaderPanel.setCenterWidget(new HTML("<b>" + constantMessages.playersInfo() + "</b>"));
    
    Map<String, Player> playersMap = currentRiskState.getPlayersMap();
    int count = 0;
    int index = 0;
    List<String> playerIds = Lists.newArrayList(currentRiskState.getPlayersMap().keySet());
    Collections.sort(playerIds);
    
    for (String id : playerIds) {
      ScrollPanel scrollPanel2 = new ScrollPanel();
      scrollPanel2.setScrollingEnabledX(false);
      
      Player player = playersMap.get(id);
      scrollPanel2.setWidget(PanelHandler.getPlayerPanel(
          cardImages, currentRiskState, player, riskPresenter.getMyPlayerId(),
          cardImagesOfCurrentPlayer, variableMessages, constantMessages));
      if (riskPresenter.getMyPlayerId().equals(player.getPlayerId())) {
        index = count;
      }
      count++;
      playersStatusPanel.add(scrollPanel2);
    }
    if (cardTrade) {
      registerCardHandlers();
    }
    playersStatusPanel.refresh();
    playersStatusPanel.setSelectedPage(index);
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
          + currentRiskState.getPlayersMap().get(territory.getPlayerKey()).getPlayerColor());
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
          + currentRiskState.getPlayersMap().get(riskState.getTerritoryWinner()).getPlayerColor()); 
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
