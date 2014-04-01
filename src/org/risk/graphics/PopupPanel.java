package org.risk.graphics;

import org.risk.client.RiskPresenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupPanel extends DialogBox {

  private Button okBtn = new Button("OK");
  private VerticalPanel panel;
  private HandlerRegistration regHandler;

  public PopupPanel() {
    super(false, true);
    setAnimationEnabled(true);
    panel = new VerticalPanel();
    regHandler = okBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    });
    panel.add(okBtn);
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    setWidget(panel);
  }

  public void setOkBtnHandler(final RiskPresenter riskPresenter, final int i) {
    // i = 0 -> riskPresenter.setTurnOrderMove();
    // i = 1 -> riskPresenter.attackResultMove();
    regHandler.removeHandler();
    regHandler = okBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (i == 1) {
          riskPresenter.setTurnOrderMove();
        } else if (i == 2) {
          riskPresenter.attackResultMove();
        }
        hide();
      }
    });
  }
  public void addPanel(Widget w) {
    panel.remove(okBtn);
    panel.add(w);
    panel.add(okBtn);
  }
  
  public void clearPanel() {
    panel.clear();
  }
  
  public void setPanelSize(String width, String height) {
    panel.setSize(width, height);
  }
  @Override
  public void center() {
    super.center();
    okBtn.setFocus(true);
  }
}
