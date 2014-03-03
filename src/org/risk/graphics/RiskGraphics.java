package org.risk.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.client.Card;
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
  VerticalPanel diceArea;
  
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
  private int unclaimedUnits;
  private Button endPhase;
  private String attackToTerritory;
  private String attackFromTerritory;
  private boolean claimTerritory = false;
  private boolean deployment = false;
  private boolean reinforce = false;
  private boolean attack = false;
  boolean flag = false;
  
  public RiskGraphics() {
    currentRiskState = new RiskLogic().gameApiStateToRiskState(
        newStateAtA, 3,  ImmutableList.<Integer>of(1, 2, 3));
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    playerArea.setSpacing(5);
    createSelectCardsButtonHandler();
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
        if (selectedCards.size() == 0) {
          cleanup();
          riskPresenter.cardsTraded(null);
        } else if (Card.getUnits(selectedCards, currentRiskState.getTradeNumber() + 1) > 0) {
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
    riskState = currentRiskState;
    //currentRiskState = riskState;
    riskPresenter.setRiskState(riskState);
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
    diceArea.clear();
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      for (Map.Entry<String, List<Integer>> entry : riskState.getDiceResult().entrySet()) {
        diceArea.add(PanelHandler.getNewDicePanel(diceImages, entry.getKey(), entry.getValue()));
      }
    }
    if (!flag && riskPresenter.getMyPlayerId() == 3) {
      Window.alert("inside player s");
      chooseCardsForTrading();
      flag = true;
    }
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
  public void chooseCardsForTrading() {
    //String playingPlayerKey = riskPresenter.getMyPlayerKey();
    int playingPlayerId = riskPresenter.getMyPlayerId();
    int turnPlayerId = currentRiskState.getTurn();
    String turnPlayerKey = GameResources.playerIdToString(turnPlayerId);
    if (playingPlayerId == turnPlayerId) {
      Player currentPlayer = currentRiskState.getPlayersMap().get(turnPlayerKey);
      List<Integer> playerCards = currentPlayer.getCards();
      if (playerCards != null && playerCards.size() >= 3) {
        List<Card> cardObjects = Card.getCardsById(currentRiskState.getCardMap(), playerCards);
        int units = Card.getUnits(cardObjects, currentRiskState.getTradeNumber() + 1);
        if (units > 0) {
          for (Map.Entry<Image, Card> imageCard : cardImagesOfCurrentPlayer.entrySet()) { 
            cardHandlers.add(addCardHandlers(imageCard.getKey(), imageCard.getValue()));
          }
        }
        gameStatus.add(selectCardsButton);
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
    territoryDelta = new HashMap<String, Integer>();
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
  public void moveUnitsAfterAttack() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void tradeCardsInAttackPhase() {
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
    Button button;
    ClickHandler handler = new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        riskPresenter.setTurnOrderMove();
      }
    };
    button = new Button("Continue", handler);
    diceArea.add(button);
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
