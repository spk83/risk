package org.risk.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CardImages extends ClientBundle {

  @Source("images/cards/infantry.png")
  ImageResource infantry();
  
  @Source("images/cards/cavalry.png")
  ImageResource cavalry();
  
  @Source("images/cards/artillery.png")
  ImageResource artillery();
  
  @Source("images/cards/wild.png")
  ImageResource wild();
  
}
