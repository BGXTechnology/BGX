/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.background.lv;

import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test.TestDataTDModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TimeDiagram;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TDModel;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.transfer.tt.TDPair;

import javax.swing.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.LinkedList;
import java.awt.*;

/**
 * User: A.Borisenko
 * Date: 24.07.2007
 * Time: 11:21:35
 */
public class CreateTDThread extends Thread {
    protected ResourceBundle rb_gui_query = PropertyResourceBundle.getBundle("gui_query");
    private TDModel localtdModel = null;
    private TDModel copytdModel = null;
    private TimeDiagram timeDiagram = null;
    private JDialog dialog = null;
    private LinkedList<TDPair> pairs = null;
    private QueryPanel panel = null;

    public CreateTDThread(JDialog dlg, TestDataTDModel tdModel, TimeDiagram timeDiagram, LinkedList<TDPair> pairs, QueryPanel queryPanel) {
        this.dialog = dlg;
        this.localtdModel = tdModel;
        this.timeDiagram = timeDiagram;
        this.pairs = pairs;
        this.panel = queryPanel;
    }

    public void run() {
        try{
            localtdModel = new TestDataTDModel(pairs);
            timeDiagram.setModel(localtdModel);
        }
        catch(Exception e){
            MessageDialogs.generalError(timeDiagram.getParent(), e);
        }
        finally{
            this.dialog.setVisible(false);
            this.dialog.dispose();
            panel.removeDialog(dialog);
        }
    }
}
