package org.risk.graphics;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class DiceAnimation extends Animation {
  private List<ImageResource> diceImageList;
  private String text;
  private List<Integer> rolls;
  private Panel panel;
  private int count = 0;
  private int delay;
  
  public DiceAnimation(DiceImages diceImages, Panel panel, int delay, String text, 
      List<Integer> rolls) {
    diceImageList = new ArrayList<ImageResource>();
    diceImageList.add(diceImages.dice1());
    diceImageList.add(diceImages.dice2());
    diceImageList.add(diceImages.dice3());
    diceImageList.add(diceImages.dice4());
    diceImageList.add(diceImages.dice5());
    diceImageList.add(diceImages.dice6());
    diceImageList.add(diceImages.diceLeft1());
    diceImageList.add(diceImages.diceLeft2());
    diceImageList.add(diceImages.diceLeft3());
    diceImageList.add(diceImages.diceLeft4());
    diceImageList.add(diceImages.diceLeft5());
    diceImageList.add(diceImages.diceLeft6());
    diceImageList.add(diceImages.diceRight1());
    diceImageList.add(diceImages.diceRight2());
    diceImageList.add(diceImages.diceRight3());
    diceImageList.add(diceImages.diceRight4());
    diceImageList.add(diceImages.diceRight5());
    diceImageList.add(diceImages.diceRight6());
    this.delay = delay;
    this.rolls = rolls;
    this.text = text;
    this.panel = panel;
  }
  
  @Override
  protected void onUpdate(double progress) {
    if (count == 0) {
      panel.clear();
      panel.add(new Label(text + "   "));
      for (int i = 0; i < rolls.size(); ++i) {
        panel.add(new Image(diceImageList.get(Random.nextInt(18))));
      }
    }
    count++;
    if (count == delay) {
      count = 0;
    }
  }
  
  @Override
  protected void onComplete() {
      panel.clear();
      panel.add(new Label(text + "   "));
      for (int i = 0; i < rolls.size(); ++i) {
        panel.add(new Image(diceImageList.get(rolls.get(i) - 1)));
      }
  }
}