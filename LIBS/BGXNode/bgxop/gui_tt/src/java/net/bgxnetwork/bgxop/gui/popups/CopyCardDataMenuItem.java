/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui_tt/src/java/net/bgx/bgxnetwork/bgxop/gui/popups/CopyCardDataMenuItem.java#1 $
$DateTime: 2007/08/06 17:28:33 $
$Change: 20537 $
$Author: a.borisenko $
*/

package net.bgx.bgxnetwork.bgxop.gui.popups;

import java.awt.event.ActionEvent;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.bgx.bgxnetwork.bgxop.gui.dialogs.CardDialog;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;

public class CopyCardDataMenuItem extends JMenuItem {

    protected ResourceBundle rb = null;

    public CopyCardDataMenuItem(CardDialog cardDialog) {
        rb = PropertyResourceBundle.getBundle("gui");
        String label = rb.getString("PopupMenu.items.copycarddata");
        AbstractAction action = new CopyCardAction(cardDialog);
        action.putValue(AbstractAction.NAME, label);
        action.putValue(AbstractAction.SHORT_DESCRIPTION, label);
        setAction(action);
    }

    public class CopyCardAction extends AbstractAction {
        protected CardDialog dialog = null;

        public CopyCardAction(CardDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent e) {
            if(dialog != null) {
                dialog.copyCardToClipboard();
            }
        }
    }
}