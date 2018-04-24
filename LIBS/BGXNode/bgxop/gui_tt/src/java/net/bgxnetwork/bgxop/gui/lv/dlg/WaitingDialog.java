/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.dlg;

import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;

import javax.swing.*;
import java.awt.*;

/**
 * User: A.Borisenko
 * Date: 24.07.2007
 * Time: 14:57:22
 */
public class WaitingDialog extends MessageDialogs {
    private JButton button = null;

    public WaitingDialog(Component parent, String msg, String title, String button1, String button2, Icon icon, Throwable ex) {
        super(parent, msg, title, button1, button2, icon, ex);
    }

    public void setControl(JButton button){
        this.button = button;
    }

    public void dispose() {
        if (button != null)
            button.setEnabled(true);
        super.dispose();
    }
}
