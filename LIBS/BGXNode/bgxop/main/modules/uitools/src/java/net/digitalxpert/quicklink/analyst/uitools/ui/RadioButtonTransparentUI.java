package net.bgx.bgxnetwork.bgxop.uitools.ui;

import javax.swing.plaf.metal.MetalRadioButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.*;

/**
 * Class RadioButtonTransparentUI
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class RadioButtonTransparentUI extends MetalRadioButtonUI {

  private static final RadioButtonTransparentUI metalRadioButtonUI = new RadioButtonTransparentUI();

  public static ComponentUI createUI(JComponent c) {
      return metalRadioButtonUI;
  }


  // ********************************
//        Install Defaults
// ********************************
  public void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
  }

}
