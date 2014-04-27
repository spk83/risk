package org.risk.graphics;

import java.util.List;

import org.risk.graphics.i18n.messages.ConstantMessages;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.slider.Slider;

public class PopupChoices extends PopinDialog {
  
  private final List<Widget> widgetsToHide;
  
  public interface OptionChosen {
    void optionChosen(String option);
  }
  
  public PopupChoices(String mainText, final List<String> options, 
      final OptionChosen optionChosen, final List<Widget> widgetsToHide,
      final ConstantMessages constantMessages) {
    super();
    this.widgetsToHide = widgetsToHide;
    RoundPanel panel = new RoundPanel();
    HTML label = new HTML("<b>" + mainText + "</b>");
    label.getElement().setAttribute("style", "text-align: center;");
    panel.add(label);
    Slider slider = new Slider();
    Button okBtn = new Button(constantMessages.ok());
    final HTML valueField;
    panel.add(slider);
    slider.setMax(options.size());
    slider.getElement().setAttribute("style", "width: 200px; position:relative; left: 50%; "
        + "margin-left: -100px");
    
    valueField = new HTML(options.get(0));
    valueField.getElement().setAttribute("style", "text-align: center;");
    panel.add(valueField);
    
    slider.addValueChangeHandler(new ValueChangeHandler<Integer>() {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> event) {
        valueField.setHTML(
            new SafeHtmlBuilder().appendEscaped(options.get(event.getValue())).toSafeHtml());
      }
    });
    
    panel.add(okBtn);
    
    okBtn.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
        optionChosen.optionChosen(valueField.getHTML());
        RiskGraphics.setVisible(widgetsToHide, true);
      }
    });
    add(panel);
  }

  @Override
  public void center() {
    super.center();
    RiskGraphics.setVisible(widgetsToHide, false);
  }
}
