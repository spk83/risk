package org.risk.graphics;

import org.risk.client.RiskPresenter;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class PopupPanel extends PopinDialog {

  private Button okBtn = new Button("OK");
  private VerticalPanel panel;
  private HandlerRegistration regHandler;

  public PopupPanel() {
    super();
    RoundPanel rpanel = new RoundPanel();
    panel = new VerticalPanel();
    regHandler = okBtn.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    panel.add(okBtn);
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.setWidth("200px");
    rpanel.add(panel);
    add(rpanel);
  }

  public void setOkBtnHandler(final RiskPresenter riskPresenter, final int i) {
    // i = 0 -> riskPresenter.setTurnOrderMove();
    // i = 1 -> riskPresenter.attackResultMove();
    regHandler.removeHandler();
    regHandler = okBtn.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
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
    okBtn.setImportant(false);
  }
}
