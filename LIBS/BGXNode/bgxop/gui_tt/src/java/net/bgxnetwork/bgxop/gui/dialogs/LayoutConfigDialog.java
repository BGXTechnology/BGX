package net.bgx.bgxnetwork.bgxop.gui.dialogs;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.bgx.bgxnetwork.bgxop.engine.DefaultLayoutIterationsFunction;
import net.bgx.bgxnetwork.bgxop.engine.GraphCustoms;
import net.bgx.bgxnetwork.bgxop.engine.ILayoutIterationsFunction;
import net.bgx.bgxnetwork.bgxop.engine.LayoutType;
import net.bgx.bgxnetwork.bgxop.gui.SelectableFormattedField;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.uitools.JComment;

/**
 * Class LayoutConfigDialog
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class LayoutConfigDialog extends JDialog implements ActionListener, CaretListener{
    protected JCheckBox defSizeCB, defIterationsCB;
    protected SelectableFormattedField widthTF, heightTF;
    protected SelectableFormattedField kkIterTF, frIterTF, springIterTF, isomIterTF;
    protected JButton apply, cancel;
    protected LayoutConfigController controller;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    public LayoutConfigDialog(Frame owner, String title, GraphCustoms data, GraphCustoms defaults){
        super(owner, title, true);
        controller = new LayoutConfigController(data, defaults);
        controller.setView(this);
        defSizeCB = new JCheckBox(rb.getString("LayoutConfigDialog.defaultLabel"));
        defSizeCB.addActionListener(this);
        widthTF = new SelectableFormattedField(NumberFormat.getInstance());
        widthTF.addCaretListener(this);
        heightTF = new SelectableFormattedField(NumberFormat.getInstance());
        heightTF.addCaretListener(this);
        JPanel sizePanel = new JPanel(new GridBagLayout());
        sizePanel.setBorder(new TitledBorder(rb.getString("LayoutConfigDialog.sizeLabel")));
        sizePanel.add(defSizeCB, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                0, 5, 5, 5), 0, 0));
        sizePanel.add(new JLabel(rb.getString("LayoutConfigDialog.widthLabel")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        sizePanel.add(widthTF, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 5), 60, 0));
        sizePanel.add(new JLabel(rb.getString("LayoutConfigDialog.heightLabel")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        sizePanel.add(heightTF, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 5), 60, 0));
        defIterationsCB = new JCheckBox(rb.getString("LayoutConfigDialog.defaultLabel"));
        defIterationsCB.addActionListener(this);
        kkIterTF = new SelectableFormattedField(NumberFormat.getInstance());
        kkIterTF.addCaretListener(this);
        frIterTF = new SelectableFormattedField(NumberFormat.getInstance());
        frIterTF.addCaretListener(this);
        springIterTF = new SelectableFormattedField(NumberFormat.getInstance());
        springIterTF.addCaretListener(this);
        isomIterTF = new SelectableFormattedField(NumberFormat.getInstance());
        isomIterTF.addCaretListener(this);
        JLabel layoutL = new JLabel(rb.getString("LayoutConfigDialog.layoutLabel"));
        JLabel kkIterL = new JLabel(rb.getString("LayoutConfigDialog.KK"));
        JLabel frIterL = new JLabel(rb.getString("LayoutConfigDialog.FR"));
        JLabel isomIterL = new JLabel(rb.getString("LayoutConfigDialog.SOM"));
        JLabel springIterL = new JLabel(rb.getString("LayoutConfigDialog.Organic"));
        JComment comment = new JComment(rb.getString("LayoutConfigDialog.layoutComment"));
        JPanel iterPanel = new JPanel(new GridBagLayout());
        iterPanel.setBorder(new TitledBorder(rb.getString("LayoutConfigDialog.iterationsLabel")));
        GridBagConstraints allrow = new GridBagConstraints(0, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
        GridBagConstraints firstcol = new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
        GridBagConstraints secindCol = new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 60, 0);
        iterPanel.add(defIterationsCB, allrow);
        iterPanel.add(layoutL, allrow);
        iterPanel.add(kkIterL, firstcol);
        iterPanel.add(kkIterTF, secindCol);
        iterPanel.add(frIterL, firstcol);
        iterPanel.add(frIterTF, secindCol);
        iterPanel.add(isomIterL, firstcol);
        iterPanel.add(isomIterTF, secindCol);
        iterPanel.add(springIterL, firstcol);
        iterPanel.add(springIterTF, secindCol);
        iterPanel.add(comment, allrow);
        apply = new JButton(rb.getString("LayoutConfigDialog.ok"));
        apply.addActionListener(this);
        cancel = new JButton(rb.getString("LayoutConfigDialog.cancel"));
        cancel.addActionListener(this);
        loadData(controller.getData());
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(
                sizePanel,
                new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10,
                        10, 10), 0, 0));
        getContentPane().add(
                iterPanel,
                new GridBagConstraints(2, 0, 1, 2, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0,
                        10, 10), 0, 0));
        getContentPane().add(
                apply,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(10, 10, 10, 10),
                        0, 0));
        getContentPane().add(
                cancel,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 10,
                        10), 0, 0));
        GlobalPopupUtil.initListeners(this);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(true);
    }
    public void caretUpdate(CaretEvent e){
        checkForReady();
    }
    protected void checkForReady(){
        boolean ready = true;
        if(!defSizeCB.isSelected() && (widthTF.getText().equals("") || heightTF.getText().equals("")))
            ready = false;
        if(!defIterationsCB.isSelected()
                && (kkIterTF.getText().equals("") || frIterTF.getText().equals("") || isomIterTF.getText().equals("") || springIterTF
                        .getText().equals("")))
            ready = false;
        apply.setEnabled(ready);
    }
    protected void loadSize(GraphCustoms data){
        Dimension size = data.getGraphSize();
        if(size == null){
            defSizeCB.setSelected(true);
            size = controller.getDefaults().getGraphSize();
        }else{
            if(data.getGraphSize().equals(controller.getDefaults().getGraphSize()))
                defSizeCB.setSelected(true);
        }
        if(size != null){
            widthTF.setValue(size.width);
            heightTF.setValue(size.height);
        }
    }
    protected void loadIterations(GraphCustoms data){
        ILayoutIterationsFunction f = data.getIterationsFunction();
        if(f == null){
            defIterationsCB.setSelected(true);
            f = controller.getDefaults().getIterationsFunction();
        }else{
            if(data.getIterationsFunction().equals(controller.getDefaults().getIterationsFunction()))
                defIterationsCB.setSelected(true);
        }
        if(f != null){
            kkIterTF.setValue(f.getIterations(LayoutType.KK));
            frIterTF.setValue(f.getIterations(LayoutType.FR));
            isomIterTF.setValue(f.getIterations(LayoutType.ISOM));
            springIterTF.setValue(f.getIterations(LayoutType.Spring));
        }
    }
    public void loadData(GraphCustoms data){
        boolean def = false;
        if(data.getGraphSize() == null){
            def = true;
        }else if(data.getGraphSize().equals(controller.getDefaults().getGraphSize()))
            def = true;
        if(!def)
            loadSize(data);
        else{
            defSizeCB.setSelected(true);
            actionPerformed(new ActionEvent(defSizeCB, ActionEvent.ACTION_PERFORMED, null));
        }
        def = false;
        if(data.getIterationsFunction() == null){
            def = true;
        }else if(data.getIterationsFunction().equals(controller.getDefaults().getIterationsFunction()))
            def = true;
        if(!def)
            loadIterations(data);
        else{
            defIterationsCB.setSelected(true);
            actionPerformed(new ActionEvent(defIterationsCB, ActionEvent.ACTION_PERFORMED, null));
        }
    }
    public void storeData(GraphCustoms data){
        if(defSizeCB.isSelected())
            data.setGraphSize(null);
        else
            data.setGraphSize(new Dimension(parseInt(widthTF), parseInt(heightTF)));
        if(defIterationsCB.isSelected())
            data.setIterationsFunction(null);
        else{
            DefaultLayoutIterationsFunction f = new DefaultLayoutIterationsFunction();
            f.setFrIterations(parseInt(frIterTF));
            f.setIsomIterations(parseInt(isomIterTF));
            f.setKkIterations(parseInt(kkIterTF));
            f.setSpringIterations(parseInt(springIterTF));
            data.setIterationsFunction(f);
        }
    }
    protected int parseInt(JFormattedTextField tf){
        Object o = tf.getValue();
        if(o instanceof Integer)
            return (Integer) o;
        if(o instanceof Long)
            return ((Long) o).intValue();
        if(o instanceof Double)
            return ((Double) o).intValue();
        try{
            return Integer.parseInt(tf.getText());
        }catch (Exception e){
            // parse impossible
            return -1;
        }
    }
    protected void close(){
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    public LayoutConfigController getController(){
        return controller;
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == apply){
            controller.apply();
        }else if(e.getSource() == cancel){
            close();
        }else if(e.getSource() == defSizeCB){
            widthTF.setEnabled(!defSizeCB.isSelected());
            heightTF.setEnabled(!defSizeCB.isSelected());
            if(defSizeCB.isSelected())
                loadSize(controller.getData());
            checkForReady();
        }else if(e.getSource() == defIterationsCB){
            kkIterTF.setEnabled(!defIterationsCB.isSelected());
            frIterTF.setEnabled(!defIterationsCB.isSelected());
            springIterTF.setEnabled(!defIterationsCB.isSelected());
            isomIterTF.setEnabled(!defIterationsCB.isSelected());
            if(defIterationsCB.isSelected())
                loadIterations(controller.getData());
            checkForReady();
        }
    }
}
