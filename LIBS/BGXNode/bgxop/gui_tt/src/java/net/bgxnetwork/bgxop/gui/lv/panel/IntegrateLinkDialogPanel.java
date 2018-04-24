package net.bgx.bgxnetwork.bgxop.gui.lv.panel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.CardIntegrateLinkTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.meta.Manager;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkValue;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class IntegrateLinkDialogPanel extends JDialog {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private JLabel firstObjectLabel = new JLabel();
    private JLabel firstObjectValueLabel = new JLabel();
    private String objectOneName;
    private String objectTwoName;
    private JLabel secondObjectLabel = new JLabel();
    private JLabel secondObjectValueLabel = new JLabel();
    private JTable table = new JTable();
    private JButton applyButton = new JButton();
    private JButton cancelButton = new JButton();
    private MainFrame owner;
    private List<LinkObject> list;
    private DialogAction da = new DialogAction();
    private CardIntegrateLinkTableModel model;
    private Long idQuery;
    public IntegrateLinkDialogPanel(MainFrame owner, List<LinkObject> list, String objectOneName, String objectTwoName, Long idQuery) {
        super(owner);
        this.owner = owner;
        Collections.sort(list, new Comparator<LinkObject>(){
            public int compare(LinkObject o1, LinkObject o2) {
                long timestamp1 = Util.getTimeStampByDate(o1);
                long timestamp2 = Util.getTimeStampByDate(o2);
                Date dt1 = new Date(timestamp1);
                Date dt2 = new Date(timestamp2);
                return dt1.compareTo(dt2);
            }
        });
        this.list = list;
        this.idQuery = idQuery;
        this.objectOneName = objectOneName;
        this.objectTwoName = objectTwoName;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initDialog();
    }
    private void initDialog() {
        setTitle(rb.getString("IntegrateLinkDialog.title"));
        getContentPane().add(getTopPanel(), BorderLayout.NORTH);
        getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        GlobalPopupUtil.initListeners(this);
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }
    private Component getCenterPanel() {
        LVObject linkProperty = Util.getVisibleFieldsForLink(null);
        ArrayList<FieldObject> fields = new ArrayList<FieldObject>();
        FieldObject fo = new FieldObject();
        fo.setCaption(rb.getString("IntegrateLinkDialog.firstColumn"));
        fields.add(fo);
        fields.addAll(linkProperty.getFields());


        model = new CardIntegrateLinkTableModel(fields, list, table);
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoscrolls(true);
        
        JScrollPane scroll = new JScrollPane(table);
        //scroll.setPreferredSize();
        //scroll.getViewport().add(table);
        return scroll;
    }
    private Component getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(2, 2));
        topPanel.add(parametersPanel, BorderLayout.WEST);
        firstObjectLabel.setText(rb.getString("IntegrateLinkDialog.firstObjectLabel"));
        secondObjectLabel.setText(rb.getString("IntegrateLinkDialog.secondObjectLabel"));
        parametersPanel.add(firstObjectLabel);
        firstObjectValueLabel.setText(objectOneName);
        secondObjectValueLabel.setText(objectTwoName);
        parametersPanel.add(firstObjectValueLabel);
        parametersPanel.add(secondObjectLabel);
        parametersPanel.add(secondObjectValueLabel);
        return topPanel;
    }
    private Component getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JPanel underButtonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        applyButton.setText(rb.getString("IntegrateLinkDialog.applyButton"));
        cancelButton.setText(rb.getString("IntegrateLinkDialog.cancelButton"));
        applyButton.addActionListener(da);
        cancelButton.addActionListener(da);
        underButtonPanel.add(applyButton);
        underButtonPanel.add(cancelButton);
        buttonPanel.add(underButtonPanel, BorderLayout.EAST);
        return buttonPanel;
    }
    private List<LinkValue> getList(LinkObject object) {
        List<PropertyType> listType = object.getLinkType().getPropertyTypes();
        List<LinkValue> out = new ArrayList<LinkValue>();
        for (PropertyType t : listType) {
            if (t != null) {
                int id = t.getPropertyTypeId().intValue();
                out.add(getValue(id, object));
            }
        }
        return out;
    }
    private LinkValue getValue(int id, LinkObject object) {
        Set<LinkValue> values = object.getPropertyVals();
        for (LinkValue v : values) {
            if (v.getPropertyType().getPropertyTypeId().intValue() == id) {
                return v;
            }
        }
        return null;
    }
    private class DialogAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == applyButton) {
                setReturnResult();
            }
            if (source == cancelButton) {
                dispose();
            }
        }
        private void setReturnResult() {
            ArrayList<LinkObject> out = (ArrayList<LinkObject>)model.getCheckedObjects();
            owner.updateChangesForLink(idQuery, out);
            dispose();
        }
    }

    public void dispose() {
        QueryPanel queryPanel = owner.getQueryListController().getQueryPanel(idQuery);
        if (queryPanel != null){
            queryPanel.removeDialog(this);
        }
        super.dispose();
    }
}
