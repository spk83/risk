package org.risk.client;

import org.risk.graphics.GameSounds;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.media.client.Audio;

public class SoundResource {
  
  private Audio diceAudio;
  private Audio deployAudio;
  private Audio attackAudio;
  private GameSounds gameSounds;
  
  public SoundResource(GameSounds gameSounds) {
    this.gameSounds = gameSounds;
    loadDiceAudio();
    loadDeployAudio();
    loadAttackAudio();
  }
  
  private void loadDiceAudio() {
    diceAudio = Audio.createIfSupported();
    if (diceAudio != null) {
      diceAudio.addSource(gameSounds.diceRollMp3().getSafeUri().asString(), AudioElement.TYPE_MP3);
      diceAudio.addSource(gameSounds.diceRollOgg().getSafeUri().asString(), AudioElement.TYPE_OGG);
      diceAudio.load();
    }
  }
  
  private void loadDeployAudio() {
    deployAudio = Audio.createIfSupported();
    if (deployAudio != null) {
      deployAudio.addSource(gameSounds.deployMp3().getSafeUri().asString(), AudioElement.TYPE_MP3);
      deployAudio.addSource(gameSounds.deployOgg().getSafeUri().asString(), AudioElement.TYPE_OGG);
      deployAudio.load();
    }
  }
  
  private void loadAttackAudio() {
    attackAudio = Audio.createIfSupported();
    if (attackAudio != null) {
      attackAudio.addSource(gameSounds.attackMp3().getSafeUri().asString(), AudioElement.TYPE_MP3);
      attackAudio.addSource(gameSounds.attackOgg().getSafeUri().asString(), AudioElement.TYPE_OGG);
      attackAudio.load();
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
  
  public Audio getDeployAudio() {
    return deployAudio;
  }
  
  public void playDeployAudio() {
    if (deployAudio != null) {
      deployAudio.play();
    }
  }
  
  public Audio getAttackAudio() {
    return attackAudio;
  }
  
  public void playAttackAudio() {
    if (attackAudio != null) {
      attackAudio.play();
    }
  }
}
