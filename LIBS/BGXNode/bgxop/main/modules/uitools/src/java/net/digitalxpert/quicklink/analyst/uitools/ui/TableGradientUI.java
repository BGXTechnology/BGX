package net.bgx.bgxnetwork.bgxop.uitools.ui;

import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.*;
import java.awt.*;

/**
 * Class TableGradientUI
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class TableGradientUI extends BasicTableUI {

  public static ComponentUI createUI(JComponent c) {
      return new TableGradientUI();
  }

  private final static Color defaultTarget = new Color(222,241,255);

  public void update(Graphics g, JComponent c) {
    Color target = UIManager.getColor("Table.background.gradient");
    if (target==null) target = defaultTarget;
    if (c.isOpaque()) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(new GradientPaint(0,0,c.getBackground(),0,c.getHeight(),target));
      g2d.fill(new Rectangle(0, 0, c.getWidth(),c.getHeight()));
    }
    paint(g, c);
  }
}
