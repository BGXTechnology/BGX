package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * Class PopupMenuListener
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class PopupMenuListener extends MouseAdapter {
  private JPopupMenu popup;

  public PopupMenuListener(JPopupMenu popup) {
    this.popup = popup;
  }

  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }

  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }

  protected void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger())
      popup.show(e.getComponent(), e.getX(), e.getY());
  }
}
