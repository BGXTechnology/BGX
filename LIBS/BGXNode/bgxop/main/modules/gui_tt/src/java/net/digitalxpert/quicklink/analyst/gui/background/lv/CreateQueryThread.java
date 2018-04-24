/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.background.lv;

import net.bgx.bgxnetwork.bgxop.gui.ProfilePanelController;
import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardPanelNotEnoughParameters;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;

import java.awt.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * User: A.Borisenko
 * Date: 05.07.2007
 * Time: 21:50:46
 */
public class CreateQueryThread extends Thread{
    protected ResourceBundle rb_gui_query = PropertyResourceBundle.getBundle("gui_query");
    ProfilePanelController controller;
    Object obj;
    public CreateQueryThread(ProfilePanelController controller, Object query) {
        this.controller = controller;
        this.obj = query;
    }

    public void run() {
        try{
            controller.finishAction(obj);
        }
        catch (WizardPanelNotEnoughParameters nep) {
            MessageDialogs.generalError((Frame) null, null, rb_gui_query.getString("warning.msg.requiredParameter.isAbsent"), rb_gui_query.getString("warning.title.requiredParameter.isAbsent"));
        }
    }
}
