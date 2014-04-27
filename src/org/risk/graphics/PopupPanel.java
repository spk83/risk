package org.risk.graphics;

import java.util.List;

import org.risk.client.RiskPresenter;
import org.risk.graphics.i18n.messages.DialogInstructions;

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
  
  private final List<Widget> widgetsToHide;

  private Button okBtn;
  private VerticalPanel panel;
  private HandlerRegistration regHandler;

  public PopupPanel(final List<Widget> widgetsToHide, DialogInstructions dialogInstructions) {
    super();
    okBtn = new Button(dialogInstructions.ok());
    this.widgetsToHide = widgetsToHide;
    RoundPanel rpanel = new RoundPanel();
    panel = new VerticalPanel();
    regHandler = okBtn.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
        RiskGraphics.setVisible(widgetsToHide, true);
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
        RiskGraphics.setVisible(widgetsToHide, true);
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
    RiskGraphics.setVisible(widgetsToHide, false);
  }
}
