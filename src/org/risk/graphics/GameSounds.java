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
  
  @Source("sounds/cards.mp3")
  DataResource cardMp3();
  
  @Source("sounds/cards.ogg")
  DataResource cardOgg();
  
  @Source("sounds/cards.wav")
  DataResource cardWav();
  
  @Source("sounds/attackWon.mp3")
  DataResource attackWonMp3();
  
  @Source("sounds/attackWon.ogg")
  DataResource attackWonOgg();
  
  @Source("sounds/attackWon.wav")
  DataResource attackWonWav();
  
  @Source("sounds/attackLost.mp3")
  DataResource attackLostMp3();
  
  @Source("sounds/attackLost.ogg")
  DataResource attackLostOgg();
  
  @Source("sounds/attackLost.wav")
  DataResource attackLostWav();
  
  @Source("sounds/gameWon.mp3")
  DataResource gameWonMp3();
  
  @Source("sounds/gameWon.ogg")
  DataResource gameWonOgg();
  
  @Source("sounds/gameWon.wav")
  DataResource gameWonWav();
  
  @Source("sounds/addUnits.wav")
  DataResource addUnitsMp3();

  @Source("sounds/addUnits.ogg")
  DataResource addUnitsOgg();
  
  @Source("sounds/addUnits.wav")
  DataResource addUnitsWav();
  
}
