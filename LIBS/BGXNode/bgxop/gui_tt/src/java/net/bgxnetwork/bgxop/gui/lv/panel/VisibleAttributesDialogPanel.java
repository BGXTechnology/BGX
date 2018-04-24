package net.bgx.bgxnetwork.bgxop.gui.lv.panel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.AttributeOverviewTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.object.FieldObjectContainer;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.query.Query;

public class VisibleAttributesDialogPanel extends JDialog {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private FieldObjectContainer container;
    private DialogAction da = new DialogAction();
    private JTabbedPane objectTab = new JTabbedPane();
    private JPanel buttonPanel = new JPanel();
    private JButton applyButton = new JButton();
    private JButton cancelButton = new JButton();
    private JTable objectTable = new JTable();
    private JTable linkTable = new JTable();
    private AttributeOverviewTableModel objectTableModel;
    private AttributeOverviewTableModel linkTableModel;
    private LVObject objectProperty;
    private LVObject linkPropetry;
    private JLabel tableObjectLabel = new JLabel();
    private JLabel tableLinkLabel = new JLabel();
    private String[] columnNames = new String[2];
    private List<FieldObject> objectProperties = new ArrayList<FieldObject>();
    private List<FieldObject> linkProperties = new ArrayList<FieldObject>();
    private MainFrame owner;
    private Query query;
    public VisibleAttributesDialogPanel(MainFrame owner, LVObject objectProperty, LVObject linkPropetry, Query query) {
        super(owner);
        this.owner = owner;
        this.query = query;
        this.objectProperty = objectProperty;
        this.linkPropetry = linkPropetry;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initDialog();
    }

    private void initDialog() {
        setResizable(false);
        columnNames[0] = rb.getString("LinkObjectDialog.table.header.AtrName");
        columnNames[1] = rb.getString("LinkObjectDialog.table.header.Visible");
        objectTableModel = new AttributeOverviewTableModel(columnNames, objectProperty.getFields());
        linkTableModel = new AttributeOverviewTableModel(columnNames, linkPropetry.getFields());
        setTitle(rb.getString("LinkObjectDialog.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JLabel leftSpace = new JLabel();
        leftSpace.setPreferredSize(new Dimension(10, 0));
        JLabel rightSpace = new JLabel();
        rightSpace.setPreferredSize(new Dimension(10, 0));
        getContentPane().add(leftSpace, BorderLayout.WEST);
        getContentPane().add(rightSpace, BorderLayout.EAST);
        getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        GlobalPopupUtil.initListeners(this);
        pack();
        setLocationRelativeTo(getOwner());
    }
    private Component getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JPanel underButtonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        applyButton.setText(rb.getString("LinkObjectDialog.applyButton"));
        cancelButton.setText(rb.getString("LinkObjectDialog.cancelButton"));
        applyButton.addActionListener(da);
        cancelButton.addActionListener(da);
        underButtonPanel.add(applyButton);
        underButtonPanel.add(cancelButton);
        buttonPanel.add(underButtonPanel, BorderLayout.EAST);
        return buttonPanel;
    }
    private Component getTabbedPane() {
         objectTab.addTab(rb.getString("LinkObjectDialog.linkTab"), getLinkAttributePanel());
         objectTab.addTab(rb.getString("LinkObjectDialog.attributeTab"), getObjectAttributePanel());
        return objectTab;
    }
    private Component getObjectAttributePanel() {
        tableLinkLabel.setText(rb.getString("ObjectDialog.tabTitle"));
        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(objectTable);
        objectTable.setModel(objectTableModel);
        linkPanel.add(tableLinkLabel, BorderLayout.NORTH);
        linkPanel.add(scroll, BorderLayout.CENTER);
        return linkPanel;
    }
    private Component getLinkAttributePanel() {
        tableObjectLabel.setText(rb.getString("LinkDialog.tabTitle"));

        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(linkTable);
        linkTable.setModel(linkTableModel);
        attributePanel.add(tableObjectLabel, BorderLayout.NORTH);
        attributePanel.add(scroll, BorderLayout.CENTER);
        return attributePanel;
    }
    private class DialogAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == applyButton) {
                setReturnResult();
                dispose();
            }
            if (source == cancelButton) {
                dispose();
            }
        }
    }
    public void setReturnResult() {
        setReturnData(objectTableModel, objectProperties);
        setReturnData(linkTableModel, linkProperties);
        container = new FieldObjectContainer();
        container.setLinkProperties(linkProperties);
        container.setObjectProperties(objectProperties);
        owner.setListVisiblePropertyFoQuery(query, container);
    }
    private void setReturnData(AttributeOverviewTableModel model, List<FieldObject> list) {
        int count = model.getRowCount();
        for (int i = 0; i < count; i++) {
            if (model.getValueAt(i, 1).toString().equalsIgnoreCase("true")) {
                try {
                    FieldObject field = model.getRowData(i);
                    list.add(field);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dispose() {
        QueryPanel queryPanel = owner.getQueryListController().getQueryPanel(query);
        if (queryPanel != null){
            queryPanel.removeDialog(this);
        }
        super.dispose();    
    }
}
