package org.risk.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {
  
  @Source("sounds/dice_rolling2.mp3")
  DataResource diceRollMp3();
  
  @Source("sounds/dice_rolling2.ogg")
  DataResource diceRollOgg();
}
