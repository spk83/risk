package org.risk.client;

import org.risk.graphics.GameSounds;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.media.client.Audio;

public class SoundResource {
  
  private Audio diceAudio;
  private GameSounds gameSounds;
  
  public SoundResource(GameSounds gameSounds) {
    this.gameSounds = gameSounds;
    loadDiceAudio();
  }
  
  private void loadDiceAudio() {
    diceAudio = Audio.createIfSupported();
    if (diceAudio != null) {
      diceAudio.addSource(gameSounds.diceRollMp3().getSafeUri().asString(), AudioElement.TYPE_MP3);
      diceAudio.addSource(gameSounds.diceRollOgg().getSafeUri().asString(), AudioElement.TYPE_OGG);
      diceAudio.load();
    }
  }
  
  public Audio getDiceAudio() {
    return diceAudio;
  }
  
  public void playDiceAudio() {
    if (diceAudio != null) {
      diceAudio.play();
    }
  }
}
