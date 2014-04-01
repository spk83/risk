package org.risk.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {
  
  @Source("sounds/dice_rolling2.mp3")
  DataResource diceRollMp3();
  
  @Source("sounds/dice_rolling2.ogg")
  DataResource diceRollOgg();
  
  @Source("sounds/dice_rolling2.wav")
  DataResource diceRollWav();
  
  @Source("sounds/deploy.mp3")
  DataResource deployMp3();
  
  @Source("sounds/deploy.ogg")
  DataResource deployOgg();
  
  @Source("sounds/deploy.wav")
  DataResource deployWav();
  
  @Source("sounds/attack.mp3")
  DataResource attackMp3();
  
  @Source("sounds/attack.ogg")
  DataResource attackOgg();
  
  @Source("sounds/attack.wav")
  DataResource attackWav();
  
}
