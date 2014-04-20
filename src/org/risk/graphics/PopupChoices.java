package org.risk.graphics;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class PopupChoices extends PopinDialog {
  public interface OptionChosen {
    void optionChosen(String option);
  }

  public PopupChoices(String mainText, List<String> options, final OptionChosen optionChosen) {
    super();
    RoundPanel panel = new RoundPanel();
    panel.add(new HTML("<b>" + mainText + "</b>"));
    HorizontalPanel buttons = new HorizontalPanel();
    for (String option : options) {
      final String optionF = option;
      Button btn = new Button(option);
      btn.addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
          hide();
          optionChosen.optionChosen(optionF);
        }
      });
      buttons.add(btn);
      // adding separator space
      if (option != options.get(options.size() - 1)) {
        Label label = new Label();
        label.setStyleName("withMargin");
        buttons.add(label);
      }
    }
    panel.add(buttons);
    add(panel);
  }

  @Override
  public void center() {
    super.center();
  }
}
