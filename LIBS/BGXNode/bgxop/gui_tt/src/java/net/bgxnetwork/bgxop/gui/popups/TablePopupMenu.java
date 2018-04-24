package net.bgx.bgxnetwork.bgxop.gui.popups;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

public class TablePopupMenu extends JPopupMenu{
    private static final String DELIM = ",";
    private static final String TRUE_VALUE = "YES";
    private static final String FALSE_VALUE = "NO";
    private JTable table;
    private ResourceBundle rb = null;
    private Action copyA;
    private Action copyCellA;
    private Action copyToCsvA;
    private Action selectAllA;
    private JMenuItem copyItem;
    private JMenuItem copyCellItem;
    private JMenuItem copyToCsvItem;
    private JMenuItem selectAllItem;
    private String labelCopy;
    private String labelCopyCell;
    private String labelCopyToCsv;
    private String labelSelectAll;
    private String labelIconCell;
    public TablePopupMenu(){
        initResourceBundle();
        copyA = new CopyAction();
        copyCellA = new CopyCellAction();
        copyToCsvA = new CopyToCsvAction();
        selectAllA = new SelectAllAction();
        copyItem = new JMenuItem(copyA);
        copyCellItem = new JMenuItem(copyCellA);
        copyToCsvItem = new JMenuItem(copyToCsvA);
        selectAllItem = new JMenuItem(selectAllA);
        this.add(copyItem);
        this.add(copyCellItem);
        this.add(copyToCsvItem);
        this.add(selectAllItem);
    }
    private void initResourceBundle(){
        if(rb == null){
            rb = PropertyResourceBundle.getBundle("gui");
            labelCopy = rb.getString("PopupMenu.items.copy");
            labelCopyCell = rb.getString("PopupMenu.items.copyCell");
            labelCopyToCsv = rb.getString("PopupMenu.items.copyToCsv");
            labelSelectAll = rb.getString("PopupMenu.items.selectAll");
            labelIconCell = rb.getString("PopupMenu.items.labelIconCell");
        }
    }
    public void show(Component invoker, int x, int y){
        super.show(invoker, x, y);
        table = (JTable) invoker;
        setModifiable();
    }
    public void setModifiable(){
        boolean rowSelected = (table.getSelectedRow() != -1);
        boolean singleSelected = table.getSelectedRowCount() == 1;
        copyItem.setEnabled(rowSelected);
        copyCellItem.setEnabled(rowSelected && table.getSelectedColumn() != -1 && singleSelected);
        copyToCsvItem.setEnabled(rowSelected);
        selectAllItem.setEnabled(table.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION);
    }
    public class CopyAction extends AbstractAction{
        public CopyAction(){
            super(labelCopy);
            putValue(SHORT_DESCRIPTION, labelCopy);
        }
        public void actionPerformed(ActionEvent e){
            copy();
        }
        private void copy(){
            TransferHandler th = table.getTransferHandler();
            if(th != null){
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                th.exportToClipboard(table, clipboard, TransferHandler.COPY);
            }
        }
    }
    public class SelectAllAction extends AbstractAction{
        public SelectAllAction(){
            super(labelSelectAll);
            putValue(SHORT_DESCRIPTION, labelSelectAll);
        }
        public void actionPerformed(ActionEvent e){
            table.selectAll();
        }
    }
    public class CopyCellAction extends AbstractAction{
        public CopyCellAction(){
            super(labelCopyCell);
            putValue(SHORT_DESCRIPTION, labelCopyCell);
        }
        public void actionPerformed(ActionEvent e){
            Object cell = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
            String selection = "";
            if(cell != null){
                if(cell instanceof ImageIcon){
                    selection = labelIconCell;
                }else{
                    selection = cell.toString();
                }
            }
            StringSelection data = new StringSelection(selection);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(data, data);
        }
    }
    public class CopyToCsvAction extends AbstractAction{
        public CopyToCsvAction(){
            super(labelCopyToCsv);
            putValue(SHORT_DESCRIPTION, labelCopyToCsv);
        }
        public void actionPerformed(ActionEvent e){
            copyToCsv();
        }
        private void copyToCsv(){
            StringBuffer selection = new StringBuffer();
            int numcols = table.getColumnCount();
            int[] rowsselected = table.getSelectedRows();
            
            for(int i = -1; i < rowsselected.length; i++){
                for(int j = 0; j < numcols; j++){
                    String currentCell = "";
                    if(i == -1){
                        TableModel model = table.getModel();
                        if(model != null){
                            currentCell = cellValueToCsvFormat(model.getColumnName(j));
                        }
                    }else{
                        Object cell = table.getValueAt(rowsselected[i], j);
                        if(cell != null){
                            if(cell instanceof ImageIcon){
                                currentCell = labelIconCell;
                            }else if(cell instanceof Boolean){
                                currentCell = ((Boolean) cell) ? TRUE_VALUE : FALSE_VALUE;
                            }else{
                                currentCell = cellValueToCsvFormat(cell.toString());
                            }
                        }
                    }
                    selection.append(currentCell);
                    if(j < numcols - 1)
                        selection.append(DELIM);
                }
                
                selection.append("\n");
            }
            StringSelection data = new StringSelection(selection.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(data, data);
        }
        private String cellValueToCsvFormat(String currentCell){
            if(currentCell.indexOf('#') != 0){
                if(currentCell.indexOf('\"') != -1){
                    currentCell = currentCell.replace("\"", "\"\"");
                    currentCell = "\"" + currentCell + "\"";
                }else if(currentCell.indexOf(DELIM) != -1){
                    currentCell = "\"" + currentCell + "\"";
                }
                if(currentCell.indexOf('\n') != -1){
                    currentCell = currentCell.replace("\r\n", " | ");
//                    currentCell = currentCell.replace("\n", " | ");
                }
                currentCell.trim();
            }else{
                currentCell = "";
            }
            return currentCell;
        }
    }
}
