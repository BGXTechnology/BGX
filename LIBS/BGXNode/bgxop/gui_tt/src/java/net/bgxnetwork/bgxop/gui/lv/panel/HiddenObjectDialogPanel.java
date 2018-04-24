package net.bgx.bgxnetwork.bgxop.gui.lv.panel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.HiddenObjectTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.PropertyVal;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class HiddenObjectDialogPanel extends JDialog {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");

    private JTable table = new JTable();
    private JButton applyButton = new JButton();
    private JButton cancelButton = new JButton();
    private MainFrame owner;
    private List<ControlObject> list;
    private DialogAction da = new DialogAction();
    private HiddenObjectTableModel model;
    private Long idQuery;
    public HiddenObjectDialogPanel(MainFrame owner, List<ControlObject> list, Long idQuery) {
        super(owner);
        this.owner = owner;
        this.list = list;
        this.idQuery = idQuery;
        initDialog();
    }
    private void initDialog() {
        setTitle(rb.getString("HiddenObjectDialog.title"));
        getContentPane().add(getTopPanel(), BorderLayout.NORTH);
        getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        GlobalPopupUtil.initListeners(this);
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    private Component getCenterPanel() {
        ArrayList<FieldObject> columnNames = new ArrayList<FieldObject>();
        FieldObject firstColumn = new FieldObject();
        firstColumn.setCaption(rb.getString("HiddenObjectDialog.firstColumn"));
        columnNames.add(firstColumn);
        ControlObject object = list.get(0);

        List<PropertyVal> valuesList = getList(object);
        for (PropertyVal propertyVal : valuesList) {
            if (propertyVal == null) continue;
            String strCode = propertyVal.getPropertyType().getCodePropertyType();
            if (strCode == null) continue;
            FieldObject fo = Util.getAttributesByCode((long)ObjectType.SIMPLE_OBJECT.getValue(), Long.parseLong(strCode));
            columnNames.add(fo);
        }
        Object[] header = columnNames.toArray();
        model = new HiddenObjectTableModel(header, list, table);
        table.setModel(model);
//        for (Object o : header) {
//            table.getColumn(((FieldObject)o).getCaption()).setMinWidth(75);
//        }
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scroll = new JScrollPane(table);
        //scroll.getViewport().add(table);
        return scroll;
    }
    private Component getTopPanel() {
        JPanel topPanel = new JPanel();

        return topPanel;
    }
    private Component getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JPanel underButtonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        applyButton.setText(rb.getString("HiddenObjectDialog.applyButton"));
        cancelButton.setText(rb.getString("HiddenObjectDialog.cancelButton"));
        applyButton.addActionListener(da);
        cancelButton.addActionListener(da);
        underButtonPanel.add(applyButton);
        underButtonPanel.add(cancelButton);
        buttonPanel.add(underButtonPanel, BorderLayout.EAST);
        return buttonPanel;
    }
    private List<PropertyVal> getList(ControlObject object) {
        List<PropertyType> listType = object.getTypeObject().getPropertyTypes();
        List<PropertyVal> out = new ArrayList<PropertyVal>();
        for (PropertyType t : listType) {
            if (t != null) {
                int id = t.getPropertyTypeId().intValue();
                out.add(getValue(id, object));
            }
        }
        return out;
    }
    private PropertyVal getValue(int id, ControlObject object) {
        Set<PropertyVal> values = object.getPropertyVals();
        for (PropertyVal v : values) {
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
                dispose();
            }
            if (source == cancelButton) {
                dispose();
            }
        }
        private void setReturnResult() {
            //List<ControlObject> out = model.getCheckedObjects();
            List<ControlObject> out = model.getAllObjects();
            owner.applyVisibleChanges(idQuery, (ArrayList<ControlObject>)out);
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
