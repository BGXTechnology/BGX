package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;

/**
 * Class TreePopupListener
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class TreePopupListener extends PopupMenuListener {
  JTree tree;
  private ProfilePanel view;

  public TreePopupListener(JPopupMenu popup, JTree tree, ProfilePanel view) {
    super(popup);
    this.view = view;  
    this.tree = tree;
  }
  protected void maybeShowPopup(MouseEvent e) {
    if (e.getButton()==MouseEvent.BUTTON3){
        try {
            tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            Query q = null;
            Object o = path.getLastPathComponent();
            if (o instanceof ProfilePanel.QueryTreeNode) q = ((ProfilePanel.QueryTreeNode)o).getQuery();
            if (q != null && q.getId() != -1) super.maybeShowPopup(e);
        }
        catch (Exception e1) {
            System.out.println("Handled exception: look at: "+e1);
        }
    }
  }
}
