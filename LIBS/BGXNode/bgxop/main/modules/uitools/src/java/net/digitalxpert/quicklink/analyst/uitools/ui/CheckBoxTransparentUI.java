package net.bgx.bgxnetwork.bgxop.uitools.ui;

import javax.swing.plaf.metal.MetalCheckBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.*;

/**
 * Class CheckBoxTransparentUI
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CheckBoxTransparentUI extends MetalCheckBoxUI {

  private static CheckBoxTransparentUI ui = new CheckBoxTransparentUI();

  public static ComponentUI createUI(JComponent c) {
    return ui;
  }

  // ********************************
//          Defaults
// ********************************
  public void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
  }
}
