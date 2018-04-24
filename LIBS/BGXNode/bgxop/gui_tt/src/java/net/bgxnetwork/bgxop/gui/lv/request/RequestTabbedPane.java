/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.request;

import net.bgx.bgxnetwork.bgxop.gui.PopupMenuListener;
import net.bgx.bgxnetwork.bgxop.gui.AppAction;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseEvent;

/**
 * User: A.Borisenko
 * Date: 19.06.2007
 * Time: 9:49:14
 */
public class RequestTabbedPane extends JTabbedPane implements ChangeListener {
    private RequestListController requestListController = null;
    private MainFrame mainFrame;

    public RequestTabbedPane(RequestListController requestListController, MainFrame mainFrame) {
        super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        this.requestListController = requestListController;
        this.mainFrame = mainFrame;
        addChangeListener(this);
        JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem(AppAction.getInstance().getAction(AppAction.CLOSE_REQUEST_TAB)));
        addMouseListener(new RequestPopupListener(menu));
    }

    public void stateChanged(ChangeEvent e) {
        mainFrame.getQueryListController().tabSelected();
    }

    public RequestListController getRequestListController() {
        return requestListController;
    }

    class RequestPopupListener extends PopupMenuListener {
        public RequestPopupListener(JPopupMenu popup){
            super(popup);
        }
        protected void maybeShowPopup(MouseEvent e){
            int index = RequestTabbedPane.this.getUI().tabForCoordinate(RequestTabbedPane.this, e.getX(), e.getY());
            if(index < 0)
                return;
            super.maybeShowPopup(e);
        }
    }
}
