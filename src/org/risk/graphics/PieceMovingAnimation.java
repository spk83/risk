package org.risk.graphics;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class PieceMovingAnimation extends Animation {
  
  AbsolutePanel panel;
  Image moving;
  ImageResource piece;
  int startX, startY, startWidth, startHeight;
  int endX, endY;
  //Audio soundAtEnd;
  boolean cancelled;

  public PieceMovingAnimation(HTML mapContainer, int startX, int startY, int endX, int endY,
      ImageResource resource, Audio sfx) {
    piece = resource;
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
    this.startWidth = 20;
    this.startHeight = 20;
    //soundAtEnd = sfx;
    cancelled = false;

    panel = new AbsolutePanel();
    moving = new Image(resource);
    moving.setPixelSize(startWidth, startHeight);
    panel.add(moving, startX, startY);
    panel.getElement().getStyle().setOverflow(Overflow.VISIBLE);
    panel.getElement().getStyle().setPosition(Position.RELATIVE);
    mapContainer.getElement().insertFirst(panel.getElement());
  }

  @Override
  protected void onUpdate(double progress) {
    int x = (int) (startX + (endX - startX) * progress);
    int y = (int) (startY + (endY - startY) * progress);
    //double scale = 1 + 0.5 * Math.sin(progress * Math.PI);
    //int width = (int) (20 * scale);
    //int height = (int) (20 * scale);
    //moving.setPixelSize(width, height);
    //x -= (width - 20) / 2;
    //y -= (height - 20) / 2;

    panel.remove(moving);
    //moving = new Image(piece.getSafeUri());
    //moving.setPixelSize(20, 20);
    panel.add(moving, x, y);
  }

  @Override
  protected void onCancel() {
    cancelled = true;
    panel.remove(moving);
  }

  @Override
  protected void onComplete() {
    if (!cancelled) {
      //if (soundAtEnd != null) {
      //  soundAtEnd.play();
      //}
      panel.remove(moving);
    }
  }
}