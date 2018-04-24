package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Class TablePopupListener
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class TablePopupListener extends PopupMenuListener {
  private JTable tab;

  public TablePopupListener(JTable tab, JPopupMenu popup) {
    super(popup);
    this.tab = tab;
  }

  protected void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      int row = tab.rowAtPoint(e.getPoint());
      if (row>=0) {
        tab.getSelectionModel().setSelectionInterval(row, row);
        super.maybeShowPopup(e);
      }
    }
  }
}
