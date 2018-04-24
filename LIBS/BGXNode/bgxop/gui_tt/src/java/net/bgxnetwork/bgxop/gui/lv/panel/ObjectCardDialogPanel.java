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
import javax.swing.ListSelectionModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.ObjectCardTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.gui.table.FlexibleTable;
import net.bgx.bgxnetwork.bgxop.gui.table.TableHelper;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.PropertyVal;
import net.bgx.bgxnetwork.transfer.data.LVObject;

public class ObjectCardDialogPanel extends JDialog {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private FlexibleTable objectCardOblectTable = new FlexibleTable();
    private JButton closeButton = new JButton();
    private MainFrame owner;
    private ControlObject object;
    private String[] columnNames = new String[2];
    private DialogAction da = new DialogAction();
    private ObjectCardTableModel model;
    public ObjectCardDialogPanel(MainFrame owner, ControlObject object) {
        super(owner);
        this.owner = owner;
        this.object = object;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initDialog();
    }
    private void initDialog() {
        String title = rb.getString("ObjectCardDialog.title");
        // String title="test";
        setTitle(title);
        setResizable(false);
        String header = rb.getString("ObjectCardDialog.table.header");
        // String header = "Name, parameter";
        StringTokenizer st = new StringTokenizer(header, ",");
        columnNames[0] = st.nextToken();
        columnNames[1] = st.nextToken();
        List<PropertyVal> list = getList();

        LVObject lvObject = Util.getVisibleFieldsForObject(null);

        model = new ObjectCardTableModel(columnNames, list, lvObject.getFields());
        objectCardOblectTable.setModel(model);
        getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        GlobalPopupUtil.initListeners(this);
        pack();
        setSize(400, 200);
        setLocationRelativeTo(getOwner());
        // objectCardOblectTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
    private List<PropertyVal> getList() {
        List<PropertyType> listType = object.getTypeObject().getPropertyTypes();
        List<PropertyVal> out = new ArrayList<PropertyVal>();
        for (PropertyType t : listType) {
            if (t != null) {
                int id = t.getPropertyTypeId().intValue();
                out.add(getValue(id));
            }
        }
        return out;
    }
    private PropertyVal getValue(int id) {
        Set<PropertyVal> values = object.getPropertyVals();
        for (PropertyVal v : values) {
            if (v.getPropertyType().getPropertyTypeId().intValue() == id) {
                return v;
            }
        }
        return null;
    }
    private Component getButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(closeButton);
        String buttonText = rb.getString("ObjectCardDialog.closeButton");
        // String buttonText = "close";
        closeButton.setText(buttonText);
        closeButton.addActionListener(da);
        return panel;
    }
    private Component getCenterPanel() {
        DefaultListSelectionModel smodel = new DefaultListSelectionModel();
        smodel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // objectCardOblectTable.sizeColumnsToFit(JTable.AUTO_RESIZE_OFF);
        objectCardOblectTable.setSelectionModel(smodel);
        TableHelper helper = new TableHelper(objectCardOblectTable, columnNames, model, false);
        return helper.getScrollWithRowHeader();
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
