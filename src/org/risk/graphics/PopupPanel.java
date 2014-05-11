package org.risk.graphics;

import org.risk.graphics.i18n.messages.ConstantMessages;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;

public class PopupPanel extends PopinDialog {
  
  private VerticalPanel panel;
  private HandlerRegistration regHandler;
  private DialogPanel dialogPanel;
  
  public DialogPanel getDialogPanel() {
    return dialogPanel;
  }

  public PopupPanel(ConstantMessages constantMessages) {
    super();
    dialogPanel = new DialogPanel();
    dialogPanel.showCancelButton(false);
    panel = new VerticalPanel();
    dialogPanel.setOkButtonText(constantMessages.ok());
    regHandler = dialogPanel.getOkButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.setWidth("200px");
    dialogPanel.getContent().add(panel);
    add(dialogPanel);
  }

  public void setOkBtnHandler(final RiskPresenter riskPresenter, final int i, 
      boolean isAIPresent) {
    // i = 0 -> riskPresenter.setTurnOrderMove();
    // i = 1 -> riskPresenter.attackResultMove();
    if (regHandler != null) {
      regHandler.removeHandler();
    }
    if (!isAIPresent) {
      dialogPanel.showOkButton(true);
      regHandler = dialogPanel.getOkButton().addTapHandler(new TapHandler() {
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
    } else {
      dialogPanel.showOkButton(false);
      hide();
    }
  }
  public void addPanel(Widget w) {
    panel.add(w);
  }
  
  public void clearPanel() {
    panel.clear();
    dialogPanel.getDialogTitle().setText("");
  }
  
  public void setPanelSize(String width, String height) {
    panel.setSize(width, height);
  }
  
}
