package org.risk.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.client.Card;
import org.risk.client.Attack.AttackResult;
import org.risk.client.GameApi.IteratingPlayerContainer;
import org.risk.client.GameResources;
import org.risk.client.Player;
import org.risk.client.RiskLogic;
import org.risk.client.RiskPresenter;
import org.risk.client.RiskState;
import org.risk.client.Territory;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RiskGraphics extends Composite implements RiskPresenter.View {
    public interface RiskGraphicsUiBinder extends UiBinder<Widget, RiskGraphics> {
    }
    
    public static Map<String, Integer> getTerritories(String playerID) {
      Map<String, Integer> territoryMap = new HashMap<String, Integer>();
      switch(playerID) {
      case "P1":
        for (int i = 0; i < 14; i++) {
          territoryMap.put(i + "", 6);
        }
        break;
      case "P2": 
        for (int i = 14; i < 28; i++) {
          territoryMap.put(i + "", 6);
        }
        break;
      case "P3": 
        for (int i = 28; i < 42; i++) {
          territoryMap.put(i + "", 3);
        }
        break;
      default:
      }
      return territoryMap;
    }
    
    public static List<String> getCardsInRange(int fromInclusive, int toInclusive) {
      List<String> keys = Lists.newArrayList();
      for (int i = fromInclusive; i <= toInclusive; i++) {
        keys.add(GameResources.RISK_CARD + i);
      }
      return keys;
    }
    
    public static boolean isTerritoryInRange(int territoryId) {
      if (territoryId >= 0 && territoryId < 42) {
        return true;
      }
      return false;
    }
    
    public static Map<String, Integer> getTerritoriesInRange(
        int lowestTerritoryIdInclusive, int highestTerritoryIdInclusive, int baseUnits) 
            {
      if (isTerritoryInRange(highestTerritoryIdInclusive) 
          && isTerritoryInRange(lowestTerritoryIdInclusive)
              && lowestTerritoryIdInclusive <= highestTerritoryIdInclusive) {
        Map<String, Integer> territoryMap = new HashMap<String, Integer>();
        for (int i = lowestTerritoryIdInclusive; i <= highestTerritoryIdInclusive; i++) {
          territoryMap.put(i + "", baseUnits);
        }
        return territoryMap;
      } else {
        return null;
        //throw new Exception("Invalid Territory ID");
      }
    }
    String PLAYER_A = "P1";
    String PLAYER_B = "P2";
    String PLAYER_C = "P3";
    int AID = 1;
    int BID = 2;
    int CID = 3;
    
    Map<String, Object> hasToTrade1 = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_TRADE).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2, 3, 4, 5),
            GameResources.TERRITORY, getTerritoriesInRange(0, 12, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, AID)).
        put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(13)).
        put(GameResources.LAST_ATTACKING_TERRITORY, 5).
        put(GameResources.TERRITORY_WINNER, PLAYER_A).
        put(GameResources.DECK, getCardsInRange(6, 43)).
        put("RC0", "I1")
        .put("RC1", "I4")
        .put("RC2", "I7")
        .put("RC3", "W2")
        .put("RC4", "C3")
        .put("RC5", "A8").
        build();
    
    Map<String, Object> hasToTrade = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put(PLAYER_A, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(5),
            GameResources.TERRITORY, getTerritoriesInRange(0, 12, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_B, ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2, 3, 4),
            GameResources.TERRITORY, getTerritoriesInRange(13, 13, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(PLAYER_C, ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(14, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put("RC5", "C1").
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(CID, BID, AID)).
        put(GameResources.DECK, getCardsInRange(6, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_A,
            GameResources.TERRITORY, 5,
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, PLAYER_B,
            GameResources.TERRITORY, 13,
            GameResources.UNITS, 1)).
        build();
    
    Map<String, Object> attackState = ImmutableMap.<String, Object>builder().
        put(GameResources.PHASE, GameResources.ATTACK_RESULT).
        put("P1", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 10, 6),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0"))).
        put("P2", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(11, 29, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("2"))).
        put("P3", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(30, 41, 3),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING)).
        put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(3, 2, 1)).
        put(GameResources.DECK, getCardsInRange(0, 43)).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "1", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "2", 6).
        put(GameResources.ATTACKER + GameResources.DICE_ROLL + "3", 5).
        put(GameResources.ATTACKER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, "P1",
            GameResources.TERRITORY, 5, 
            GameResources.UNITS, 6)).
        put(GameResources.DEFENDER + GameResources.DICE_ROLL + "1", 4).
        put(GameResources.DEFENDER, ImmutableMap.<String, Object>of(
            GameResources.PLAYER, "P2",
            GameResources.TERRITORY, 13, 
            GameResources.UNITS, 1)).build();
    
    
    Map<String, Object> newStateAtA = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.CARD_TRADE)
        .put("P1", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories("P1"),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put("P2", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.TERRITORY, getTerritories("P2"),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put("P3", ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(0, 1, 2),
            GameResources.UNCLAIMED_UNITS, 4,
            GameResources.TERRITORY, getTerritories("P3"),
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(3, 2, 1))
        .put(GameResources.DECK, getCardsInRange(3, 
            GameResources.TOTAL_RISK_CARDS - 1))
        .put("RC0", "I1")
        .put("RC1", "I4")
        .put("RC2", "I7")
        .put(GameResources.UNCLAIMED_TERRITORY, GameResources.EMPTYLISTINT)
        .build();
    
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
  
  private RiskState currentRiskState;
  private List<HandlerRegistration> territoryHandlers = new ArrayList<HandlerRegistration>();
  private List<HandlerRegistration> cardHandlers = new ArrayList<HandlerRegistration>();
  private Map<String, Integer> territoryDelta = new HashMap<String, Integer>();
  private Map<Image, Card> cardImagesOfCurrentPlayer = new HashMap<Image, Card>();
  private List<Card> selectedCards = new ArrayList<Card>();
  private Button selectCardsButton = new Button("Finish Selecting");
  private Button turnOrderButton = new Button("Continue");
  private Button continueAttackButton = new Button("Continue");
  private VerticalPanel attackResultPanel = new VerticalPanel();
  //private HorizontalPanel diceHorizontalPanel = new HorizontalPanel();
  private int unclaimedUnits;
  private Button endPhase;
  private String attackToTerritory;
  private String attackFromTerritory;
  private boolean claimTerritory = false;
  private boolean deployment = false;
  private boolean reinforce = false;
  private boolean attack = false;
  private boolean mandatoryCardSelection = false;
  boolean flag = false;
  
  public RiskGraphics() {
    currentRiskState = new RiskLogic().gameApiStateToRiskState(
        hasToTrade1, 1,  ImmutableList.<Integer>of(1, 2, 3));
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    playerArea.setSpacing(5);
    diceAttackPanel.setSpacing(20);
    //diceHorizontalPanel.setBorderWidth(1);
    /*diceAttackPanel.setBorderWidth(1);
    diceAttackPanel.setBorderWidth(5);*/
    createSelectCardsButtonHandler();
    createTurnOrderButton();
    createContinueAttackButton();
    addMapHandlers();
    flag = false;
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
        int units = Card.getUnits(selectedCards, currentRiskState.getTradeNumber() + 1);
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
        //diceHorizontalPanel.clear();
        diceAttackPanel.clear();
        riskPresenter.setTurnOrderMove();
      }
    });
  }
  
  private void createContinueAttackButton() {
    continueAttackButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        gameStatus.remove(continueAttackButton);
        riskPresenter.attackResultMove();
      }
    });
  }
  
  public void addPlayerSelection(final IteratingPlayerContainer container, int selectedIndex) {
    final ListBox playerSelect = new ListBox();
    for (int i = 1; i <= selectedIndex + 3; ++i) {
      playerSelect.addItem(i + "");
    }
    Label playerSelectLabel = new Label("Select player: ");
    RootPanel.get("mainDiv").add(playerSelectLabel);
    RootPanel.get("mainDiv").add(playerSelect);
    playerSelect.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        container.updateUi(playerSelect.getSelectedIndex() + 1);
      }
    });
  }
  
  @Override
  public void setPresenter(RiskPresenter riskPresenter) {
    this.riskPresenter = riskPresenter;
  }

  @Override
  public void setViewerState(RiskState riskState) {
  }

  @Override
  public void setPlayerState(RiskState riskState) {
    gameStatus.clear();
    playersStatusPanel.clear();
    /*if (!flag) {
      //&& riskPresenter.getMyPlayerId() == 3) {
      //riskState = currentRiskState;
      riskPresenter.setRiskState(riskState);
    } else {
      currentRiskState = riskState;
    }*/
    //currentRiskState = riskState;
    riskState = currentRiskState;
    changeSVGMap(riskState);
    Map<String, Player> playersMap = currentRiskState.getPlayersMap();
    int count = 0;
    int index = 0;
    for (Player player : playersMap.values()) {
      playersStatusPanel.add(PanelHandler.getPlayerPanel(
          cardImages, currentRiskState, player, riskPresenter.getMyPlayerId(),
          cardImagesOfCurrentPlayer), player.getPlayerId());
      if (riskPresenter.getMyPlayerKey().equals(player.getPlayerId())) {
        index = count;
      }
      count++;
    }
    playersStatusPanel.setSize("300px", "450px");
    playersStatusPanel.selectTab(index);
    gameStatus.add(PanelHandler.getGameStatusPanel(riskState));
    diceAttackPanel.clear();
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      for (Map.Entry<String, List<Integer>> entry : riskState.getDiceResult().entrySet()) {
        diceAttackPanel.add(PanelHandler.getNewDicePanel(
            diceImages, entry.getKey(), entry.getValue()));
      }
    }
    /*if (!flag && riskPresenter.getMyPlayerId() == 3) {
      Window.alert("inside player s");
      //chooseCardsForTrading();
      attackResult();
      flag = true;
    }*/
    //attackResult();
    chooseCardsForTrading(true);
    //reinforceTerritories();
    //attack();
  
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
          int playerId = riskPresenter.getMyPlayerId();
          if (currentRiskState.getTurn() == playerId) {
            if (claimTerritory) {
              claimTerritory(territoryId);
            } else if (deployment) {
              deployment(territoryId);
            } else if (reinforce) {
              reinforce(territoryId);
            } else if (attack) {
              attack(territoryId);
            }
          } else {
            Window.alert("Please wait for your turn");
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
  
  private void claimTerritory(String territoryName) {

    String playerKey = riskPresenter.getMyPlayerKey();
    int playerId = riskPresenter.getMyPlayerId();
    OMElement territory = boardElt.getElementById(territoryName);
    OMElement territoryUnits = boardElt.getElementById(territoryName + "_units");
    String territoryId = Territory.SVG_ID_MAP.get(territoryName) + "";
    Territory territorySelected = currentRiskState.getTerritoryMap().get(territoryId);

    if (territorySelected == null) {
      String style = territory.getAttribute("style");
      style = style.replaceFirst("fill:#ffffff", "fill:" + Player.PLAYER_COLOR.get(playerId));
      territory.setAttribute("style", style);
      territoryUnits.getFirstChild().getFirstChild().setNodeValue("1");
      claimTerritory = false;
      riskPresenter.newTerritorySelected(territoryId);
    } else {
      if (territorySelected.getPlayerKey().equals(playerKey)) {
        Window.alert("You already own this territory");
      } else {
        Window.alert("Select an empty territory");
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
    } else {
      Window.alert("Please select your territory");
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
      Window.alert(unclaimedUnits + "");
      if (unclaimedUnits == 0) {
        reinforce = false;
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    } else {
      Window.alert("Please select your territory");
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
            .get(GameResources.playerIdToString(riskPresenter.getMyPlayerId())))
            .getTerritoryUnitMap().get(territoryId);
        if (units < 2) {
          Window.alert("not enough units to attack");
          return;
        }
        attackFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
      } else if (attackFromTerritory.equals(territoryId)) {
        attackFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
      } else {
        Window.alert("ignore : own territory already selected");
        //ignore
      }
      territory.setAttribute("style", style);
      return;
    } else {
      // Defending territory selected
      if (attackFromTerritory == null) {
        Window.alert("ignore : select attacking territory first");
        return;
      }
      attackToTerritory = territoryId;
      if (Territory.CONNECTIONS.get(Integer.parseInt(attackFromTerritory))
          .contains(Integer.parseInt(attackToTerritory))) {
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        style = style.replaceFirst("stroke:#000000", "stroke:red");
        territory.setAttribute("style", style);
        Window.alert("Peform Attack");
        attack = false;
        riskPresenter.performAttack(attackFromTerritory, attackToTerritory);
      } else {
        Window.alert("not adjacent territory");
        return;
      }
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
    //String playingPlayerKey = riskPresenter.getMyPlayerKey();
    this.mandatoryCardSelection = mandatoryCardSelection;
    int playingPlayerId = riskPresenter.getMyPlayerId();
    int turnPlayerId = currentRiskState.getTurn();
    String turnPlayerKey = GameResources.playerIdToString(turnPlayerId);
    if (playingPlayerId == turnPlayerId) {
      Player currentPlayer = currentRiskState.getPlayersMap().get(turnPlayerKey);
      List<Integer> playerCards = currentPlayer.getCards();
      Window.alert(playerCards.size() + "");
      if (playerCards != null && playerCards.size() >= 3) {
        List<Card> cardObjects = Card.getCardsById(currentRiskState.getCardMap(), playerCards);
        if (Card.isTradePossible(cardObjects)) {
          for (Map.Entry<Image, Card> imageCard : cardImagesOfCurrentPlayer.entrySet()) { 
            cardHandlers.add(addCardHandlers(imageCard.getKey(), imageCard.getValue()));
          }
          gameStatus.add(selectCardsButton);
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
    //territoryDelta = new HashMap<String, Integer>();
    if (territoryDelta == null) {
      territoryDelta = new HashMap<String, Integer>();
    }
    territoryDelta.clear();
    unclaimedUnits = ((Player) currentRiskState.getPlayersMap()
        .get(GameResources.playerIdToString(riskPresenter.getMyPlayerId())))
            .getUnclaimedUnits();
    Window.alert(riskPresenter.getMyPlayerKey() + " You got " + unclaimedUnits);
    endPhase = new Button("End Reinforce", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        reinforce = false;
        riskPresenter.territoriesReinforced(territoryDelta);
      }
    });
    playerArea.add(endPhase);
    reinforce = true;
  }
  
  @Override
  public void attack() {
    attackToTerritory = null;
    attackFromTerritory = null;
  
    endPhase.removeFromParent();
    endPhase = new Button("End Attack", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        riskPresenter.endAttack();
      }
    });
    playerArea.add(endPhase);
    attack = true;
    Window.alert("Ready to attack");
  }
  
  @Override
  public void attackResult() {
    //diceHorizontalPanel.clear();
    attackResultPanel.clear();
    diceAttackPanel.clear();
    String attackerKey = currentRiskState.getAttack().getAttackerPlayerId();
    String defenderKey = currentRiskState.getAttack().getDefenderPlayerId();
    /*int attackerId = GameResources.playerIdToInt(attackerKey);
    int defenderId = GameResources.playerIdToInt(defenderKey);*/
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
 /*   diceHorizontalPanel.add(attackerDicePanel);
    diceHorizontalPanel.add(defenderDicePanel);*/
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
      attackResultPanel.add(new HTML("<b>Player " + defenderKey + " out of the game</b>"));
    }
    if (attackResult.isAttackerAWinnerOfGame()) {
      attackResultPanel.add(new HTML("<b>Player " + attackerKey + " wins the game !</b>"));
    } else if (attackResult.isTradeRequired()) {
      attackResultPanel.add(new HTML("<b>Player " + attackerKey + " will have to trade cards</b>"));
    }
    
    //diceAttackPanel.add(diceHorizontalPanel);
    diceAttackPanel.add(attackResultPanel);
    gameStatus.add(continueAttackButton);
  }
   
  @Override
  public void moveUnitsAfterAttack() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fortify() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void endGame() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void turnOrderMove() {
    //gameStatus.add(turnOrderButton);
  }
  
  private void changeSVGMap(RiskState riskState) {
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
          + Player.PLAYER_COLOR.get(GameResources.playerIdToInt(territory.getPlayerKey())));
        territoryElement.setAttribute("style", style);
        territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(
            territory.getCurrentUnits() + "");
      }
    }
  }

}
