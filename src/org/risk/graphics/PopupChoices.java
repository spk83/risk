package org.risk.graphics;

import java.util.List;

import org.risk.graphics.i18n.messages.ConstantMessages;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.slider.Slider;

public class PopupChoices extends PopinDialog {
  
  public interface OptionChosen {
    void optionChosen(String option);
  }
  
  final OptionChosen optionChosen;
  final HTML valueField;
  final List<String> options;
  final Slider slider;
  
  public PopupChoices(String mainText, final List<String> options, 
      final OptionChosen optionChosen, final ConstantMessages constantMessages,
      final boolean isAIPresent) {
    super();
    this.options = options;
    this.optionChosen = optionChosen;
    this.valueField = new HTML(options.get(0));
    this.slider = new Slider();
    DialogPanel dialogPanel = new DialogPanel();
    dialogPanel.showCancelButton(false);
    dialogPanel.getDialogTitle().setText(mainText);
    
    dialogPanel.setOkButtonText(constantMessages.ok());
    dialogPanel.getContent().add(slider);
    slider.setMax(options.size());
    slider.getElement().setAttribute("style", "width: 200px; position:relative; left: 50%; "
        + "margin-left: -100px");
    
    valueField.getElement().setAttribute("style", "text-align: center;");
    dialogPanel.getContent().add(valueField);
    if (!isAIPresent) {
      slider
          .addValueChangeHandler(new ValueChangeHandler<Integer>() {
        @Override
        public void onValueChange(ValueChangeEvent<Integer> event) {
          valueField.setHTML(
              new SafeHtmlBuilder().appendEscaped(options.get(event.getValue())).toSafeHtml());
        }
      });
      dialogPanel.getOkButton().addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
          hide();
          optionChosen.optionChosen(valueField.getHTML());
        }
      });
    } else {
      dialogPanel.showOkButton(false);
    }
    add(dialogPanel);
  }
  
  void fireTapEvent() {
    hide();
    optionChosen.optionChosen(valueField.getHTML());
  }
  
  void fireValueChange(int value) {
    slider.setValue(value - 1);
    valueField.setHTML(
        new SafeHtmlBuilder().appendEscaped(options.get(value - 1)).toSafeHtml());
  }

}
