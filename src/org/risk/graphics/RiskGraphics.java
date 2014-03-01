package org.risk.graphics;

import java.util.List;
import java.util.Map;

import org.risk.client.Card;
import org.risk.client.RiskPresenter;
import org.risk.client.RiskState;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AreaElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.MapElement;
import com.google.gwt.dom.client.TagName;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sun.xml.internal.ws.message.RootElementSniffer;

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
  VerticalPanel diceArea;
  
  @UiField
  HTML mapContainer;
  
  //@UiField
  ImageElement imageRiskMap;
  
  //@UiField
  MapElement riskMap;
  
  //@UiField
  AreaElement indiaArea;
  
  /*@UiField
  SVGImage riskMap;*/
  
  public RiskGraphics() {
    diceImages = GWT.create(DiceImages.class);
    cardImages = GWT.create(CardImages.class);
    riskMapSVG = GWT.create(MapSVG.class);
    RiskGraphicsUiBinder uiBinder = GWT.create(RiskGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    /*imageRiskMap = ImageElement.as(new Image(riskMapSVG.riskMap()).getElement());
    imageRiskMap.setAttribute("usemap", "#riskMap");
    riskMap = Document.get().createMapElement();
    riskMap.setName("riskMap");
    indiaArea = Document.get().createAreaElement();
    indiaArea.setShape("poly");
    indiaArea.setCoords("678,316,686,350,713,386,732,437,740,453,748,456,745,442,759,398,790,342,"
        + "761,328,728,309,730,289,696,294,686,299,690,299");
    indiaArea.setHref("#india");
    riskMap.appendChild(indiaArea);
    playerArea.getElement().appendChild(imageRiskMap);
    playerArea.getElement().appendChild(riskMap);
    Event.sinkEvents(indiaArea, Event.ONCLICK);
    Event.sinkEvents(indiaArea, Event.MOUSEEVENTS);
    Event.setEventListener(indiaArea, new EventListener() {
      
      @Override
      public void onBrowserEvent(Event event) {
        System.out.println("ok");
        if (Event.ONCLICK == event.getTypeInt()) {
          Window.alert("india");
        }
        if (Event.ONMOUSEMOVE == event.getTypeInt()) {
          indiaArea.setTitle("Hey Man... Whats UP");
        }
      }
    });
    Canvas canvas = Canvas.createIfSupported();
    ImageElement card = ImageElement.as(new Image(cardImages.artillery()).getElement());
    ImageElement india = ImageElement.as(new Image(cardImages.()).getElement());
    ImageElement malaysia = ImageElement.as(new Image(cardImages.artillery()).getElement());
    canvas.setCoordinateSpaceHeight(500);
    canvas.setCoordinateSpaceWidth(500);
    canvas.setHeight(500 + "");
    canvas.setWidth(500 + "");
    canvas.getContext2d().drawImage(card, 0, 0);
    canvas.getContext2d().drawImage(india, 100, 100);
    canvas.getContext2d().drawImage(malaysia, 143, 94);
    playerArea.add(canvas);*/
    //canvas.getElement().appendChild(riskMap);
    //Event.setEventListener((Element) indiaArea, new EventListener() {

       /* @Override
        public void onBrowserEvent(Event event) {
            System.out.println("ok");
             if(Event.ONCLICK == event.getTypeInt()) {
                 Window.alert("ok");
                  System.out.println("CLICK");
             }

        }
*/
      /*  @Override
        public void handleEvent(org.w3c.dom.events.Event arg0) {
          // TODO Auto-generated method stub
          
        }
    });*/
    boardElt = OMSVGParser.parse(riskMapSVG.riskMap().getText());
    mapContainer.getElement().appendChild(boardElt.getElement());
    OMElement india = boardElt.getElementById("india");
    System.out.println(india);
    System.out.println(india.getAttribute("d"));
    MouseDownHandler handler = new MouseDownHandler() {
      
      @Override
      public void onMouseDown(MouseDownEvent event) {
        Window.alert("ok");
      }
    };
    ((OMNode) india).addDomHandler(handler, MouseDownEvent.getType());
  }
  
  @Override
  public void setPresenter(RiskPresenter riskPresenter) {
    this.riskPresenter = riskPresenter;
  }

  @Override
  public void setViewerState(RiskState riskState) {
    // TODO Auto-generated method stub
  }

  @Override
  public void setPlayerState(RiskState riskState) {
    diceArea.clear();
    if (riskState.getDiceResult() != null && !riskState.getDiceResult().isEmpty()) {
      for (Map.Entry<String, List<Integer>> entry : riskState.getDiceResult().entrySet()) {
        diceArea.add(getNewDicePanel(entry.getKey(), entry.getValue()));
      }
    }
    /*System.out.println("Current player turn: " + riskState.getTurn());
    System.out.println("Phase: " + riskState.getPhase());
    System.out.println("Turn order: " + riskState.getTurnOrder());*/
  }

  @Override
  public void chooseNewTerritory() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void chooseTerritoryForDeployment() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void chooseCardsForTrading() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void reinforceTerritories() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void attack() {
    // TODO Auto-generated method stub
    
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
  
  private ImageResource getDiceImageResource(int dots) {
    switch(dots) {
      case 1 : return diceImages.dice1();
      case 2 : return diceImages.dice2();
      case 3 : return diceImages.dice3();
      case 4 : return diceImages.dice4();
      case 5 : return diceImages.dice5();
      case 6 : return diceImages.dice6();
      default : return null;
    }
  }
  
  private ImageResource getCardImageResource(Card card) {
    switch(card.getCardType()) {
      case ARTILLERY: return cardImages.artillery();
      case CAVALRY: return cardImages.cavalry();
      case INFANTRY: return cardImages.infantry();
      case WILD: return cardImages.wild();
      default: return null;
    }
  }
  
  private Panel getNewDicePanel(String userId, List<Integer> rolls) {
    FlowPanel imageContainer = new FlowPanel();
    imageContainer.add(new Label(userId));
    for (Integer dots : rolls) {
      Image diceImage = new Image(getDiceImageResource(dots));
      imageContainer.add(diceImage);
    }
    return imageContainer;
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

}
