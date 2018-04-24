package net.bgx.bgxnetwork.bgxop.gui.lv.panel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.LinkCardTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkValue;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.transfer.data.LVObject;

public class CardLinkDialogPanel extends JDialog {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private JTable objectCardOblectTable = new JTable();
    private JButton closeButton = new JButton();
    private MainFrame owner;
    private LinkObject object;
    private String[] columnNames = new String[2];
    private DialogAction da = new DialogAction();
    private LinkCardTableModel model;
    public CardLinkDialogPanel(MainFrame owner, LinkObject object) {
        super(owner);
        this.owner = owner;
        this.object = object;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initDialog();
    }
    private void initDialog() {
        String title = rb.getString("LinkCardDialog.title");
        setTitle(title);
        setResizable(false);

        String header = rb.getString("LinkCardDialog.table.header");
        StringTokenizer st = new StringTokenizer(header, ",");
        columnNames[0] = st.nextToken();
        columnNames[1] = st.nextToken();

        LVObject fields = Util.getVisibleFieldsForLink(null);

        model = new LinkCardTableModel(columnNames, getList(), fields.getFields());
        
        objectCardOblectTable.setModel(model);
        getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        GlobalPopupUtil.initListeners(this);
        pack();
        setLocationRelativeTo(getOwner());
        // objectCardOblectTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
    private Component getButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(closeButton);
        String buttonText = rb.getString("LinkCardDialog.closeButton");
        closeButton.setText(buttonText);
        closeButton.addActionListener(da);
        return panel;
    }
    private Component getCenterPanel() {
        DefaultListSelectionModel smodel = new DefaultListSelectionModel();
        smodel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        objectCardOblectTable.sizeColumnsToFit(JTable.AUTO_RESIZE_OFF);
        objectCardOblectTable.setSelectionModel(smodel);

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(objectCardOblectTable);
        return scroll;
    }
    private List<LinkValue> getList() {
        List<PropertyType> listType = object.getLinkType().getPropertyTypes();
        List<LinkValue> out = new ArrayList<LinkValue>();
        for (PropertyType t : listType) {
            if (t != null) {
                int id = t.getPropertyTypeId().intValue();
                out.add(getValue(id));
            }
        }
        return out;
    }
    private LinkValue getValue(int id) {
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
            if (source == closeButton) {
                dispose();
            }
        }
    }

    public void dispose() {
        Long qId = object.getPk().getQueryId();
        QueryPanel queryPanel = owner.getQueryListController().getQueryPanel(qId);
        if (queryPanel != null){
            queryPanel.removeDialog(this);
        }
        super.dispose();
    }
}
