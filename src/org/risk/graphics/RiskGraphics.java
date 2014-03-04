package org.risk.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.risk.client.Attack;
import org.risk.client.Attack.AttackResult;
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
<<<<<<< HEAD
=======
    
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
>>>>>>> branch 'HW4' of ssh://git@github.com/spk83/risk.git
    
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
    
    Map<String, Object> fortifyState = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.FORTIFY)
        .put("P1", ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(),
            GameResources.TERRITORY, getTerritories("P1"),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put("P2", ImmutableMap.<String, Object>of(
            GameResources.CARDS, ImmutableList.<Integer>of(),
            GameResources.TERRITORY, getTerritories("P2"),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put("P3", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritories("P3"),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, GameResources.EMPTYLISTSTRING))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(3, 2, 1))
        .put(GameResources.DECK, getCardsInRange(2, 43))
        .build();
    
    Map<String, Object> endGameState = ImmutableMap.<String, Object>builder()
        .put(GameResources.PHASE, GameResources.END_GAME)
        .put("P3", ImmutableMap.<String, Object>of(
            GameResources.CARDS, GameResources.EMPTYLISTINT,
            GameResources.TERRITORY, getTerritoriesInRange(0, 40, 1),
            GameResources.UNCLAIMED_UNITS, 0,
            GameResources.CONTINENT, ImmutableList.<String>of("0", "1", "2", "3", "4")))
        .put(GameResources.TURN_ORDER, ImmutableList.<Integer>of(3))
        .put(GameResources.DECK, getCardsInRange(0, 43))
        .put(GameResources.UNCLAIMED_TERRITORY, ImmutableList.<Integer>of(41))
        .put(GameResources.LAST_ATTACKING_TERRITORY, 40)
        .put(GameResources.TERRITORY_WINNER, "P3")
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
  private Button continueToAttackPhaseButton = new Button("Continue");
  private Button endAttack = new Button("End Attack Phase");
  private Button endReinforce = new Button("End Reinforce Phase");
  private Button endFortify = new Button("End Fortify");
  
  private VerticalPanel attackResultPanel = new VerticalPanel();
  //private HorizontalPanel diceHorizontalPanel = new HorizontalPanel();
  private int unclaimedUnits;
  private String attackToTerritory;
  private String attackFromTerritory;
  private String fortifyToTerritory;
  private String fortifyFromTerritory;
  private Label errorLabel = new Label();
  private Label reinforceLabel = new Label();
  private boolean claimTerritory = false;
  private boolean deployment = false;
  private boolean reinforce = false;
  private boolean attack = false;
<<<<<<< HEAD
  private boolean fortify = false;
=======
>>>>>>> branch 'HW4' of ssh://git@github.com/spk83/risk.git
  private boolean mandatoryCardSelection = false;
  boolean flag = false;
  
  public RiskGraphics() {
<<<<<<< HEAD
    currentRiskState = new RiskLogic().gameApiStateToRiskState(
        fortifyState, 3,  ImmutableList.<Integer>of(1, 2, 3));
=======
    /*currentRiskState = new RiskLogic().gameApiStateToRiskState(
        hasToTrade1, 1,  ImmutableList.<Integer>of(1, 2, 3));*/
>>>>>>> branch 'HW4' of ssh://git@github.com/spk83/risk.git
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
    createContinueToAttackPhaseButton();
    createEndAttackButton();
    createEndReinforceButton();
    createEndFortifyButton();
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
      riskState = currentRiskState;
      riskPresenter.setRiskState(riskState);
    } else {
      currentRiskState = riskState;
    }*/
<<<<<<< HEAD
    //currentRiskState = riskState;
    riskState = currentRiskState;
=======
    currentRiskState = riskState;
    //riskState = currentRiskState;
>>>>>>> branch 'HW4' of ssh://git@github.com/spk83/risk.git
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
      //attack();
      attackResult();
      flag = true;
    }*/
    //attackResult();
<<<<<<< HEAD
    //moveUnitsAfterAttack();
=======
    //chooseCardsForTrading(true);
>>>>>>> branch 'HW4' of ssh://git@github.com/spk83/risk.git
    //reinforceTerritories();
    //attack();
    fortify();
    //endGame();
  
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
            } else if (fortify) {
              fortify(territoryId);
            }
          } else {
            gameStatus.remove(errorLabel);
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
            .get(GameResources.playerIdToString(riskPresenter.getMyPlayerId())))
            .getTerritoryUnitMap().get(territoryId);
        if (units < 2) {
          gameStatus.remove(errorLabel);
          errorLabel = new Label("Not enough units to attack");
          gameStatus.add(errorLabel);
          return;
        }
        attackFromTerritory = territoryId;
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
      } else if (attackFromTerritory.equals(territoryId)) {
        attackFromTerritory = null;
        style = style.replaceFirst("stroke-width:5", "stroke-width:1.20000005");
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
            .get(GameResources.playerIdToString(riskPresenter.getMyPlayerId())))
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
              riskPresenter.fortifyMove(territoryDelta);
            }
          }).center();
        } else {
          gameStatus.remove(errorLabel);
          errorLabel = new Label(
              "Select own territory that is connected to your territory for fortify");
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
    if (territoryDelta == null) {
      territoryDelta = new HashMap<String, Integer>();
    }
    territoryDelta.clear();
    unclaimedUnits = ((Player) currentRiskState.getPlayersMap()
        .get(GameResources.playerIdToString(riskPresenter.getMyPlayerId())))
            .getUnclaimedUnits();
    gameStatus.remove(reinforceLabel);
    reinforceLabel = new Label("You got " + unclaimedUnits + " for reinforce!");
    gameStatus.add(errorLabel);
    gameStatus.add(endReinforce);
    reinforce = true;
  }
  
  @Override
  public void attack() {
    attackToTerritory = null;
    attackFromTerritory = null;
    gameStatus.add(endAttack);
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
    int playingPlayerId = riskPresenter.getMyPlayerId();
    int turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId == turnPlayerId) {
      gameStatus.add(continueToAttackPhaseButton);
    }
  }
   
  @Override
  public void moveUnitsAfterAttack() {
<<<<<<< HEAD
    int playingPlayerId = riskPresenter.getMyPlayerId();
    int turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId == turnPlayerId) {
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
=======
    // TODO Auto-generated method stub
    
>>>>>>> branch 'HW4' of ssh://git@github.com/spk83/risk.git
  }

  @Override
  public void fortify() {
    int playingPlayerId = riskPresenter.getMyPlayerId();
    int turnPlayerId = currentRiskState.getTurn();
    if (playingPlayerId == turnPlayerId) {
      gameStatus.add(endFortify);
    }
    fortify = true;
  }

  @Override
  public void endGame() {
    int playingPlayerId = riskPresenter.getMyPlayerId();
    int turnPlayerId = currentRiskState.getTurn();
    gameStatus.add(new Label("Game Ended"));
    if (playingPlayerId == turnPlayerId) {
      Window.alert("You won the game!");
      riskPresenter.endGame();
    } else {
      Window.alert("Player-" + GameResources.playerIdToString(turnPlayerId) + " won the game!");
    }
  }
  
  @Override
  public void turnOrderMove() {
    //gameStatus.add(turnOrderButton);
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
          + Player.PLAYER_COLOR.get(GameResources.playerIdToInt(territory.getPlayerKey())));
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
          + Player.PLAYER_COLOR.get(GameResources.playerIdToInt(riskState.getTerritoryWinner())));
        style = style.replaceFirst("stroke-width:1.20000005", "stroke-width:5");
        style = style.replaceFirst("stroke:red", "stroke:#000000");
        territoryElement.setAttribute("style", style);
        territoryUnitsElement.getFirstChild().getFirstChild().setNodeValue(0 + "");
      }
    }
    
  }
}
