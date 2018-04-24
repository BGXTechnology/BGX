package net.bgx.bgxnetwork.bgxop.gui.query;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.wizard.WizardQueryPanel;
import net.bgx.bgxnetwork.transfer.query.Query;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class QueryDialog
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public abstract class QueryDialog extends JDialog implements IReadinessListener, ActionListener, ChangeListener{
    //protected ArrayList<AbstractQueryTypePanel> tabs = new ArrayList<AbstractQueryTypePanel>();
    protected ArrayList<WizardQueryPanel> tabs = new ArrayList<WizardQueryPanel>();
    protected WizardQueryPanel queryPanel;

    protected boolean action = false;
    protected JButton saveB, executeB, cancelB;
    protected JScrollPane content;
    protected QueryDialogController controller;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_query");
    public QueryDialog(QueryDialogController controller, Frame owner){
        super(owner, true);
        this.controller = controller;
        controller.setView(this);
        saveB = new JButton(rb.getString("QueryDialog.save"));
        saveB.addActionListener(this);
        saveB.setEnabled(false);
        executeB = new JButton(rb.getString("QueryDialog.execute"));
        executeB.addActionListener(this);
        executeB.setEnabled(false);
        cancelB = new JButton(rb.getString("QueryDialog.cancel"));
        cancelB.addActionListener(this);
        getContentPane().setLayout(new GridBagLayout());
    }
    public void showDialog(){
        content = new JScrollPane((JPanel)queryPanel);
        content.setPreferredSize(new Dimension(650, (int)super.getOwner().getSize().getHeight()-120));
        stateChanged(null);
        getContentPane().add(
                content,
                new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 10),
                        0, 0));
        getContentPane().add(
                saveB,
                new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 5), 0,
                        0));
        getContentPane().add(
                executeB,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 10, 5),
                        0, 0));
        getContentPane().add(
                cancelB,
                new GridBagConstraints(2, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 10, 10), 0,
                        0));
        GlobalPopupUtil.initListeners(this);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
    public QueryDialogController getController(){
        return controller;
    }
    public void stateChanged(ChangeEvent e){
//        AbstractQueryTypePanel panel = (AbstractQueryTypePanel) content.getSelectedComponent();
//        if(panel != null)
//            panel.checkForReady();
    }
    public void setReady(boolean ready){
        saveB.setEnabled(ready);
        executeB.setEnabled(ready);
    }
    public boolean isActionDone(){
        return action;
    }
    public void close(){
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelB){
            close();
        }
    }
}
