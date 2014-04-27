package org.risk.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.client.Attack;
import org.risk.client.Attack.AttackResult;
import org.risk.client.Card;
import org.risk.client.Continent;
import org.risk.client.GameApi;
import org.risk.client.GameResources;
import org.risk.client.Player;
import org.risk.client.RiskPresenter;
import org.risk.client.RiskState;
import org.risk.client.Territory;
import org.risk.graphics.i18n.messages.ConstantMessages;
import org.risk.graphics.i18n.messages.DialogInstructions;
import org.risk.graphics.i18n.messages.VariableMessages;
import org.risk.graphics.i18n.names.TerritoryNames;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
  private ConstantMessages constantMessages;
  private TerritoryNames territoryNames;
  private VariableMessages variableMessages;
  private DialogInstructions dialogInstructions;

  @UiField
  LayoutPanel main;
  
  @UiField
  HeaderPanel headerPanel;
  
  @UiField
  ScrollPanel display;
  
  @UiField
  VerticalPanel mapWrapper;

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
  private Map<Image, Card> cardImagesOfCurrentPlayer = new HashMap<Image, Card>();
  private List<Card> selectedCards = new ArrayList<Card>();
  
  private List<HandlerRegistration> territoryHandlers = new ArrayList<HandlerRegistration>();
  private List<HandlerRegistration> cardHandlers = new ArrayList<HandlerRegistration>();
  private HeaderButton selectCardsButton = new HeaderButton();
  private HeaderButton endAttack = new HeaderButton();
  private HeaderButton endReinforce = new HeaderButton();
  private HeaderButton endFortify = new HeaderButton();
  private HeaderButton backButton = new HeaderButton();
  private HeaderPanel otherHeaderPanel = new HeaderPanel();
  private VerticalPanel attackResultPanel = new VerticalPanel();
  private Carousel playersStatusPanel = new Carousel();
  
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
  private List<Widget> widgetsToHide = new ArrayList<Widget>();
  private PopupPanel dicePanel = new PopupPanel(widgetsToHide);
  
  public RiskGraphics() {
    territoryNames = (TerritoryNames) GWT.create(TerritoryNames.class);
    constantMessages = (ConstantMessages) GWT.create(ConstantMessages.class);
    variableMessages = (VariableMessages) GWT.create(VariableMessages.class);
    dialogInstructions = (DialogInstructions) GWT.create(DialogInstructions.class);
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    attackImages = GWT.create(AttackImages.class);
    gameSounds = GWT.create(GameSounds.class);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    soundResource = new SoundResource(gameSounds);
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    mapContainer.setStyleName("map");
    attackImageResource = attackImages.tank();
    createSelectCardsButton();
    createEndAttackButton();
    createEndReinforceButton();
    createEndFortifyButton();
    createBackButton();
    addMapHandlers();
    renameContinents();
    widgetsToHide.add(display);
    widgetsToHide.add(footerBar);
  }
  
  private void renameContinents() {
    if (!(LocaleInfo.getCurrentLocale().getLocaleName().equals("default") 
        || LocaleInfo.getCurrentLocale().getLocaleName().indexOf("en") != -1)) {
      for (String continentId : Continent.CONTINENT_SVG_ID.keySet()) {
        final OMElement continentText = boardElt.getElementById(continentId);
        OMNodeList<OMNode> continentTextChildNodes = continentText.getChildNodes();
        List<String> newContinentNameList = GameResources.getNewTerritoryNameList(
            territoryNames.continents().get(continentId), continentTextChildNodes.getLength() - 1);
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
  
  private void createBackButton() {
    backButton.setBackButton(true);
    backButton.setText(dialogInstructions.back());
    backButton.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        playersStatusPanel.removeFromParent();
        backButton.removeFromParent();
        otherHeaderPanel.removeFromParent();
        footerBar.add(playersInfo);
        footerBar.removeFromParent();
        
        headerPanel.setVisible(true);
        display.setVisible(true);
        main.add(footerBar);
      }
    });
  }
  
  public static void setVisible(List<Widget> widgetsToHide, boolean isVisible) {
    for (Widget widget : widgetsToHide) {
      widget.setVisible(isVisible);
    }
  }
  
  private void createSelectCardsButton() {
    selectCardsButton.setRoundButton(true);
    selectCardsButton.setText("Done");
    selectCardsButton.addTapHandler(new TapHandler() {
      
      private void cleanup() {
        selectedCards.clear();
        removeHandlers(cardHandlers);
        cardImagesOfCurrentPlayer.clear();
        selectCardsButton.removeFromParent();
        playersStatusPanel.removeFromParent();
        otherHeaderPanel.removeFromParent();
        footerBar.add(playersInfo);
        footerBar.removeFromParent();
        
        headerPanel.setVisible(true);
        display.setVisible(true);
        main.add(footerBar);
      }
      
      @Override
      public void onTap(TapEvent event) {
        int units = Card.getUnits(selectedCards, currentRiskState.getTradeNumber());
        if (mandatoryCardSelection && units == 0) {
          CustomDialogPanel.alert(dialogInstructions.invalidSelection(), 
              dialogInstructions.mandatoryCardSelection(), null, dialogInstructions.ok());
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
          
          CustomDialogPanel.confirm(dialogInstructions.invalidSelection(), 
              dialogInstructions.confirmDialog(), callback, dialogInstructions.ok(), 
              dialogInstructions.cancel());
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
        notification.clear();
        notification.setVisible(false);
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
      
      if (!(LocaleInfo.getCurrentLocale().getLocaleName().equals("default") 
          || LocaleInfo.getCurrentLocale().getLocaleName().indexOf("en") != -1)) {
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
            CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
                dialogInstructions.viewerNotAllowed(), null, dialogInstructions.ok());
          } else {
            CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
                dialogInstructions.playerNotAllowed(), null, dialogInstructions.ok());
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
    setVisible(widgetsToHide, true);
    currentRiskState = riskState;
    changeSVGMap(riskState);
    dicePanel.clearPanel();
    dicePanel.hide();
    if (fortifyOpt != null) {
      fortifyOpt.hide();
    }
    HTML phase = new HTML("<b>" + constantMessages.uiPhaseMap().get(riskState.getPhase()) 
        + "</b>");
    phase.getElement().getStyle().setTop(10, Unit.PX);
    headerPanel.setLeftWidget(phase);
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      List<String> diceList = Lists.newArrayList(riskState.getDiceResult().keySet());
      Collections.sort(diceList);
      soundResource.playDiceAudio();
      dicePanel.addPanel(new HTML("<b>" + constantMessages.turnOrder() + "</b>"));
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
    cardTrade = false;
    setStyle();
  }

  private void setStyle() {
    headerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    mapWrapper.getElement().getStyle().setPosition(Position.ABSOLUTE);
    mapWrapper.getElement().getStyle().setOverflow(Overflow.VISIBLE);
    mapContainer.getElement().getStyle().setPosition(Position.ABSOLUTE);
    mapContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
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
        CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
            dialogInstructions.alreadyOwnTerritory(), null, dialogInstructions.ok());
      } else {
        CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
            dialogInstructions.emptyTerritory(), null, dialogInstructions.ok());
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
      CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
          dialogInstructions.selectYourTerritory(), null, dialogInstructions.ok());
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
      notification.getElement().getStyle().setFontSize(12, Unit.PX);
      notification.getElement().getStyle().setPadding(7, Unit.PX);
      if (unclaimedUnits == 0) {
        reinforce = false;
        notification.clear();
        notification.setVisible(false);
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    } else {
      CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
          dialogInstructions.selectYourTerritory(), null, dialogInstructions.ok());
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
          CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
              dialogInstructions.notEnoughUnits(), null, dialogInstructions.ok());
          return;
        }
        attackFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
      } else if (attackFromTerritory.equals(territoryId)) {
        attackFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
      } else {
        CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
            dialogInstructions.selectOpponentTerritory(), null, dialogInstructions.ok());
      }
      territory.setAttribute("style", style);
      
      return;
    } else {
      // Defending territory selected
      if (attackFromTerritory == null) {
        CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
            dialogInstructions.selectOwnTerritoryAttack(), null, dialogInstructions.ok());
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
        CustomDialogPanel.alert(dialogInstructions.notAllowed(),  
            dialogInstructions.selectAdjacentTerritory(), null, dialogInstructions.ok());
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
          CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
              dialogInstructions.fortifyNotPossible(), null, dialogInstructions.ok());
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
          CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
              dialogInstructions.selectOwnTerritoryFortify(), null, dialogInstructions.ok());
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
          fortifyOpt = new PopupChoices(dialogInstructions.chooseUnitsToMove(),
              options, new PopupChoices.OptionChosen() {
            @Override
            public void optionChosen(String option) {
              territoryDelta = new HashMap<String, Integer>();
              territoryDelta.put(fortifyFromTerritory, -Integer.parseInt(option));
              territoryDelta.put(fortifyToTerritory, Integer.parseInt(option));
              fortify = false;
              riskPresenter.fortifyMove(territoryDelta);
            }
          }, widgetsToHide);
          fortifyOpt.center();
        } else {
          CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
              dialogInstructions.selectOwnTerritoryFortify(), null, dialogInstructions.ok());
          return;
        }
      }
    } else {
      CustomDialogPanel.alert(dialogInstructions.notAllowed(), 
          dialogInstructions.selectOwnTerritoryFortify(), null, dialogInstructions.ok());
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
            cardTrade  = true;
            playersInfo.fireEvent(new TapEvent(playersInfo, playersInfo.getElement(), 0, 0));
            return;
        } else {
          riskPresenter.cardsTraded(null);
        }
      } else {
        riskPresenter.cardsTraded(null);
      }
    }
  }
  
  private void registerCardHandlers() {
    for (Map.Entry<Image, Card> imageCard : cardImagesOfCurrentPlayer.entrySet()) { 
      cardHandlers.add(addCardHandlers(imageCard.getKey(), imageCard.getValue()));
    }
    otherHeaderPanel.setCenterWidget(new HTML("<b>Trade Cards</b>"));
    otherHeaderPanel.setRightWidget(selectCardsButton);
    backButton.removeFromParent();
  }
  
  private HandlerRegistration addCardHandlers(final Image image, final Card card) {
    TouchDelegate i = new TouchDelegate(image);
    return i.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
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
    CustomDialogPanel.alert(dialogInstructions.info(), 
        variableMessages.unclaminedUnits(unclaimedUnits), null, dialogInstructions.ok());
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
    dicePanel.addPanel(new HTML("<b>" + constantMessages.attackResult() + "</b>"));
    dicePanel.setPanelSize("200px", "200px");
    if (playingPlayerId.equals(turnPlayerId)) {
      dicePanel.setOkBtnHandler(riskPresenter, 2);
    } else {
      dicePanel.setOkBtnHandler(riskPresenter, 0);
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
      new PopupChoices(dialogInstructions.chooseUnitsToMove(),
          options, new PopupChoices.OptionChosen() {
        @Override
        public void optionChosen(String option) {
          riskPresenter.moveUnitsAfterAttack(Integer.parseInt(option));
        }
      }, widgetsToHide).center();
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
      CustomDialogPanel.alert(constantMessages.gameEnded(), dialogInstructions.gameWon(), null, 
          dialogInstructions.ok());
      riskPresenter.endGame();
    } else {
      CustomDialogPanel.alert(constantMessages.gameEnded(), variableMessages.playerWon(
          dialogInstructions.player() + GameResources.playerIdToKey(turnPlayerId)), null,
          dialogInstructions.ok());
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
        CustomDialogPanel.alert(dialogInstructions.instructions(), 
            dialogInstructions.setTurnOrder(), null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.CLAIM_TERRITORY)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), 
            dialogInstructions.claimTerritory(), null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.DEPLOYMENT)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.deployment(), 
            null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.CARD_TRADE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), variableMessages.cardTrade(4),
            null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.ATTACK_TRADE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), variableMessages.cardTrade(5),
            null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.REINFORCE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.reinforce(),
            null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.ATTACK_PHASE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.attack(),
            null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.ATTACK_RESULT)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(),
            dialogInstructions.attackResult(), null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.ATTACK_REINFORCE)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(),
            dialogInstructions.attackReinforce(), null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.ATTACK_OCCUPY)) {
        String instruction = "";
        if (currentRiskState.getTerritoryWinner().equals(riskPresenter.getMyPlayerKey())) {
          instruction += dialogInstructions.territoryWinner();
        }
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.attackOccupy()
            + instruction, null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.FORTIFY)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.fortify(), 
            null, dialogInstructions.ok());
      } else if (phase.equals(GameResources.END_GAME) || phase.equals(GameResources.GAME_ENDED)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.endGame(),
            null, dialogInstructions.ok());
      } else {
        CustomDialogPanel.alert(dialogInstructions.instructions(), phase, null,
            dialogInstructions.ok());
      }
    } else if (playingPlayerId.equals(GameApi.VIEWER_ID)) {
      CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.viewer(),
          null, dialogInstructions.ok());
    } else {
      if (phase.equals(GameResources.SET_TURN_ORDER)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.turnOrder(),
            null, dialogInstructions.ok());
      } else if (playerLost || phase.equals(GameResources.END_GAME)) {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.playerLost(),
            null, dialogInstructions.ok());
      }  else {
        CustomDialogPanel.alert(dialogInstructions.instructions(), dialogInstructions.notYourTurn(),
            null, dialogInstructions.ok());
      }
    }
  }
  
  
  @UiHandler("playersInfo")
  public void onTapPlayersInfoButton(TapEvent e) {
    playersStatusPanel.clear();
    display.setVisible(false);
    playersInfo.removeFromParent();
    headerPanel.setVisible(false);
    footerBar.removeFromParent();
    
    main.add(otherHeaderPanel);
    main.add(playersStatusPanel);
    main.add(footerBar);
    otherHeaderPanel.setLeftWidget(backButton);
    otherHeaderPanel.setCenterWidget(new HTML("<b>" + dialogInstructions.playersInfo() + "</b>"));
    
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
          cardImagesOfCurrentPlayer, variableMessages));
      if (riskPresenter.getMyPlayerKey().equals(player.getPlayerId())) {
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
