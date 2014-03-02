package org.risk.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface MapSVG extends ClientBundle {

  /*@Source("images/map/Riskgameboard.svg")
  TextResource riskMap();*/
  
  @Source("images/map/Risk_board.svg")
  TextResource riskMap();
  
  /*@Source("images/map/riskboard.jpg")
  ImageResource riskMap();*/
}
