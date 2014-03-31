package org.risk.graphics;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupPanel extends DialogBox {

  private Button okBtn = new Button("OK");
  private VerticalPanel panel;

  public PopupPanel() {
    super(false, true);
    setAnimationEnabled(true);
    panel = new VerticalPanel();
    okBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    });
    panel.add(okBtn);
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    setWidget(panel);
  }

  public void addPanel(Widget w) {
    panel.remove(okBtn);
    panel.add(w);
    panel.add(okBtn);
  }
  
  public void clearPanel() {
    panel.clear();
  }
  @Override
  public void center() {
    super.center();
    okBtn.setFocus(true);
  }
}
