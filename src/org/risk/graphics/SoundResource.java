package org.risk.graphics;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.media.client.Audio;

public class SoundResource {
  
  private Audio diceAudio;
  private Audio deployAudio;
  private Audio attackAudio;
  private Audio cardAudio;
  private Audio attackWonAudio;
  private Audio attackLostAudio;
  private Audio gameWonAudio;
  private Audio addUnitsAudio;
  private GameSounds gameSounds;
  
  public SoundResource(GameSounds gameSounds) {
    this.gameSounds = gameSounds;
    loadDiceAudio();
    loadDeployAudio();
    loadAttackAudio();
    loadCardAudio();
    loadAttackWonAudio();
    loadAttackLostAudio();
    loadGameWonAudio();
    loadAddUnitsAudio();
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
  
  private void loadCardAudio() {
    cardAudio = Audio.createIfSupported();
    if (cardAudio != null) {
      cardAudio.addSource(gameSounds.cardMp3().getSafeUri().asString(), AudioElement.TYPE_MP3);
      cardAudio.addSource(gameSounds.cardOgg().getSafeUri().asString(), AudioElement.TYPE_OGG);
      cardAudio.load();
    }
  }
  
  private void loadAttackWonAudio() {
    attackWonAudio = Audio.createIfSupported();
    if (attackWonAudio != null) {
      attackWonAudio.addSource(gameSounds.attackWonMp3().getSafeUri().asString(), 
          AudioElement.TYPE_MP3);
      attackWonAudio.addSource(gameSounds.attackWonOgg().getSafeUri().asString(), 
          AudioElement.TYPE_OGG);
      attackWonAudio.addSource(gameSounds.attackWonWav().getSafeUri().asString(), 
          AudioElement.TYPE_WAV);
      attackWonAudio.load();
    }
  }
  
  private void loadAttackLostAudio() {
    attackLostAudio = Audio.createIfSupported();
    if (attackLostAudio != null) {
      attackLostAudio.addSource(gameSounds.attackLostMp3().getSafeUri().asString(), 
          AudioElement.TYPE_MP3);
      attackLostAudio.addSource(gameSounds.attackLostOgg().getSafeUri().asString(), 
          AudioElement.TYPE_OGG);
      attackLostAudio.load();
    }
  }
  
  private void loadGameWonAudio() {
    gameWonAudio = Audio.createIfSupported();
    if (gameWonAudio != null) {
      gameWonAudio.addSource(gameSounds.gameWonMp3().getSafeUri().asString(), 
          AudioElement.TYPE_MP3);
      gameWonAudio.addSource(gameSounds.gameWonOgg().getSafeUri().asString(), 
          AudioElement.TYPE_OGG);
      gameWonAudio.load();
    }
  }
  
  private void loadAddUnitsAudio() {
    addUnitsAudio = Audio.createIfSupported();
    if (addUnitsAudio != null) {
      addUnitsAudio.addSource(gameSounds.addUnitsMp3().getSafeUri().asString(), 
          AudioElement.TYPE_MP3);
      addUnitsAudio.addSource(gameSounds.addUnitsOgg().getSafeUri().asString(), 
          AudioElement.TYPE_OGG);
      addUnitsAudio.load();
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
  
  public Audio getCardAudio() {
    return cardAudio;
  }
  
  public void playAttackAudio() {
    if (attackAudio != null) {
      attackAudio.play();
    }
  }
  
  public void playCardAudio() {
    if (cardAudio != null) {
      cardAudio.play();
    }
  }
  
  public Audio getAttackWonAudio() {
    return attackWonAudio;
  }
  
  public void playAttackWonAudio() {
    if (attackWonAudio != null) {
      attackWonAudio.play();
    }
  }
  
  public Audio getAttackLostAudio() {
    return attackLostAudio;
  }
  
  public void playAttackLostAudio() {
    if (attackLostAudio != null) {
      attackLostAudio.play();
    }
  }
  
  public Audio getGameWonAudio() {
    return gameWonAudio;
  }
  
  public void playGameWonAudio() {
    if (gameWonAudio != null) {
      gameWonAudio.play();
    }
  }
  
  public Audio getAddUnitsAudio() {
    return addUnitsAudio;
  }
  
  public void playAddUnitsAudio() {
    if (addUnitsAudio != null) {
      addUnitsAudio.play();
    }
  }
}
