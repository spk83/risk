package org.risk.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface DiceImages extends ClientBundle {
  
  @Source("images/dice/dice-1.png")
  ImageResource dice1();
  
  @Source("images/dice/dice-2.png")
  ImageResource dice2();

  @Source("images/dice/dice-3.png")
  ImageResource dice3();
  
  @Source("images/dice/dice-4.png")
  ImageResource dice4();
  
  @Source("images/dice/dice-5.png")
  ImageResource dice5();
  
  @Source("images/dice/dice-6.png")
  ImageResource dice6();
}
