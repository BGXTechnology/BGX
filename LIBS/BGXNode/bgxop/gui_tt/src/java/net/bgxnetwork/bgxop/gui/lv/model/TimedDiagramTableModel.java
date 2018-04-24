package net.bgx.bgxnetwork.bgxop.gui.lv.model;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.collections.set.CompositeSet.SetMutator;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;

public class TimedDiagramTableModel extends DefaultTableModel implements ActionListener, ItemListener {
 private ArrayList<ControlObjectPair>tableData;
	private ArrayList<ControlObjectPair> objects;
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private String addPairErrorMessage = rb.getString("TimedDiagramParameterPanel.error.addRowMessage");
    private String removePairMessageString = rb.getString("TimedDiagramParameterPanel.error.removeRowMessage");
    private String removePairErrorMessage = rb.getString("TimedDiagramParameterPanel.error.removePairErrorMessage");
    private String yes = rb.getString("TimedDiagramParameterPanel.error.YES");
    private String no = rb.getString("TimedDiagramParameterPanel.error.NO");
    private String attention = rb.getString("TimedDiagramParameterPanel.error.ATTENTION");


    // private JComboBox stampBox = new JComboBox();
    // private JComboBox dataBox = new JComboBox();
    private JTable table;
    public TimedDiagramTableModel(Object[] header, JTable table) {
        this.table = table;
        init(header);
    }
    private void init(Object[] header) {
        for (Object o : header) {
            addColumn(o);
        }
        table.setModel(this);
    }
    /*
      * public boolean isCellEditable(int row, int column) { return (column ==
      * 0); }
      */
    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }
    public JComboBox setControlObjects(ArrayList<ControlObjectPair> objects) {
        JComboBox stampBox = new JComboBox();
        if (objects.size() > 0) {
            this.objects = objects;
            for (ControlObjectPair co : objects) {
                stampBox.addItem(co);
            }
        //    stampBox.addActionListener(this);
         //   TableColumn tc = table.getColumnModel().getColumn(1);
        //    tc.setCellEditor(new DefaultCellEditor(stampBox));
        }
        return stampBox;
    }
    public void addRow(ControlObjectPair pair, ControlObjectPair linked){
    	addRow(new Object[]{true,pair, linked});
    	table.setModel(this);
    }
    public void addRow(ArrayList<ControlObjectPair> objects) {
        JComboBox stampBox = setControlObjects(objects);
        ControlObjectPair o = (ControlObjectPair) stampBox.getItemAt(0);
        addRow(new Object[] { true, o, createLinkedCombo(o).getItemAt(0) });
        table.setModel(this);
    }
    private JComboBox createLinkedCombo(ControlObjectPair co) {
        Set<ControlObject> so = co.getNeighbors();
        JComboBox dataBox = new JComboBox();
        for (ControlObject c : so) {
            ControlObjectPair p = new ControlObjectPair();
            p.setControlObject(c);
            dataBox.addItem(p);
        }
        TableColumn tc1 = table.getColumnModel().getColumn(2);
        tc1.setCellEditor(new DefaultCellEditor(dataBox));
        return dataBox;
    }
    public void actionPerformed(ActionEvent e) {
        createLinksBox();
    }
    private void createLinksBox() {
        int column = 1;
        int row = table.getSelectedRow();
        if (row != -1) {
            ControlObjectPair co = (ControlObjectPair) getValueAt(row, column);
            try {
                checkExist(co, 1);
            } catch (RuntimeException e) {
                // JOptionPane.showMessageDialog(null, addPairErrorMessage);
                //e.printStackTrace();
            }
            setValueAt(createLinkedCombo(co).getItemAt(0), table.getSelectedRow(), 2);
            table.setModel(this);
        }
    }
    private void checkExist(ControlObjectPair co, int column) {
        int row = table.getSelectedRow();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (i != row) {
                ControlObjectPair o = (ControlObjectPair) getValueAt(row, column);
                if (o.equals(co)) {
                    throw new RuntimeException();
                }
            }
        }
    }
    public void removeCheckedRows() {

        if(!isCheckedRows()){
            MessageDialogs.info(table, removePairErrorMessage, rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
        }
        else{
            boolean res = MessageDialogs.confirm(null, removePairMessageString, attention);
            if (res) {
                int count = getRowCount();
                for (int i = 0; i < count; i++) {
                    String value = getValueAt(i, 0).toString().trim();
                    if (value.equalsIgnoreCase("true")) {
                        removeRow(i);
                        i--;
                        count--;
                    }
                }
                table.setModel(this);
            }

        }


    }
    private boolean isCheckedRows(){


        int count = getRowCount();
        for (int i = 0; i < count; i++) {
            String value = getValueAt(i, 0).toString().trim();
            if (value.equalsIgnoreCase("true")) {
                return true;
            }
        }

        return false;
    }
    public ArrayList<ControlObjectPair> getSelectedData() {
        ArrayList<ControlObjectPair> out = new ArrayList<ControlObjectPair>();
        int count = getRowCount();
        for (int i = 0; i < count; i++) {
            String value = getValueAt(i, 0).toString().trim();
            if (value.equalsIgnoreCase("true")) {
                ControlObjectPair cop = new ControlObjectPair();

                ControlObjectPair co = (ControlObjectPair) getValueAt(i, 1);
                cop.setControlObject(co.getControlObject());

                ControlObjectPair coPair = (ControlObjectPair) getValueAt(i, 2);
                cop.setLinkedObject(coPair.getControlObject());

                out.add(cop);
                //System.out.println(cop.toString() + " pair " + coPair.toString());
            }
        }
        return out;
    }
    public void itemStateChanged(ItemEvent e) {

    }
	public ArrayList<ControlObjectPair> getTableData() {
		tableData=new ArrayList<ControlObjectPair>();
		int rows=getRowCount();
		for(int i=0;i<rows;i++){
			ControlObjectPair cp=(ControlObjectPair)getValueAt(i, 1);

            Object val2 = getValueAt(i, 2);
            if (val2 instanceof ControlObjectPair){
                ControlObjectPair coPair = (ControlObjectPair)val2;
                cp.setLinkedObject(coPair.getControlObject());
            }
            else if (val2 instanceof ControlObject){
                cp.setLinkedObject((ControlObject)val2);
            }

            cp.setSelected(Boolean.valueOf(getValueAt(i, 0).toString()));
			tableData.add(cp);
		}
		return tableData;
	}
	public void setTableData(ArrayList<ControlObjectPair> tableData) {
		this.tableData = tableData;
	}
	public void setSavedData(ArrayList<ControlObjectPair> pairs) {
		int count=getRowCount();
		for(int i=0;i<count;i++){
			removeRow(i);
			i--;
			count--;
		}
		//table.setModel(this);
		for(ControlObjectPair pair:pairs){
			//JComboBox stampBox = setControlObjects(objects);
	        ControlObjectPair linked = new ControlObjectPair();
            linked.setControlObject(pair.getLinkedObject());
            addRow(new Object[] { pair.isSelected().booleanValue(), pair,  linked});
	       
		}
		 table.setModel(this);
		
	}
}
