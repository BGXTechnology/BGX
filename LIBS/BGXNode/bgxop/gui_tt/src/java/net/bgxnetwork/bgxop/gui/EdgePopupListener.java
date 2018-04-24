package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.graph.Edge;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.transfer.query.LinkType;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Class EdgePopupListener
 *
 * @author Gerasimenko
 * @version 1.0
 */
public class EdgePopupListener extends MouseAdapter {
    private static final String DELIM = ",";
    private static final String TRUE_VALUE = "YES";
    private static final String FALSE_VALUE = "NO";
    private static Logger log = Logger.getLogger(EdgePopupListener.class.getName());
    private EdgePopupListener.DefPopupMenu removeOnlyMenu = null;
    private EdgePopupListener.DefPopupMenu objectMenu = null;
    private EdgePopupListener.DefPopupMenu objectListMenu;
    private IEdgeContainer container;
    private Edge currentEdge = null;
    private MainFrame owner;
    protected ResourceBundle rb = ResourceBundle.getBundle("gui_dialogs");
    protected ResourceBundle guirb = ResourceBundle.getBundle("gui");
    protected ResourceBundle qrb = ResourceBundle.getBundle("query");
    protected Action openCardAction;
    protected Action removeAction;
    protected Action openCardIntegrationLinkAction;
    private ResourceBundle rbCopyPaste = null;
    private AppAction _actions = AppAction.getInstance();
    private Action copyA;
    private Action copyCellA;
    private Action copyToCsvA;
    private Action selectAllA;
    private String labelCopy;
    private String labelCopyCell;
    private String labelCopyToCsv;
    private String labelSelectAll;
    private String labelIconCell;

    public EdgePopupListener(MainFrame owner, IEdgeContainer c) {
        container = c;
        this.owner = owner;
        initCopyPasteMenuItem();
        openCardAction = _actions.getAction(AppAction.CARD_LINK_ACTION);
        removeAction = _actions.getAction(AppAction.REMOVE_LINK_ACTION);
        openCardIntegrationLinkAction = _actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION);

        objectMenu = new DefPopupMenu();
        objectMenu.addDefault(new JMenuItem(openCardAction));
        addNewMenuItem(objectMenu);

        objectListMenu = new DefPopupMenu();
        objectListMenu.addDefault(new JMenuItem(openCardIntegrationLinkAction));
        addNewMenuItem(objectListMenu);

        removeOnlyMenu = new DefPopupMenu();
        removeOnlyMenu = new EdgePopupListener.DefPopupMenu();
        removeOnlyMenu.add(new JMenuItem(removeAction));
        addNewMenuItem(removeOnlyMenu);

    }

    private void initCopyPasteMenuItem() {
        initResourceBundle();
        copyA = new EdgePopupListener.CopyAction();
        copyCellA = new EdgePopupListener.CopyCellAction();
        copyToCsvA = new EdgePopupListener.CopyToCsvAction();
        selectAllA = new EdgePopupListener.SelectAllAction();
    }

    private void initResourceBundle() {
        if (rbCopyPaste == null) {
            rbCopyPaste = PropertyResourceBundle.getBundle("gui");
            labelCopy = rbCopyPaste.getString("PopupMenu.items.copy");
            labelCopyCell = rbCopyPaste.getString("PopupMenu.items.copyCell");
            labelCopyToCsv = rbCopyPaste.getString("PopupMenu.items.copyToCsv");
            labelSelectAll = rbCopyPaste.getString("PopupMenu.items.selectAll");
            labelIconCell = rbCopyPaste.getString("PopupMenu.items.labelIconCell");
        }
    }

    private void addNewMenuItem(EdgePopupListener.DefPopupMenu menu) {
        if (container instanceof JTable) {
            menu.addSeparator();
            menu.add(new JMenuItem(copyA));
            menu.add(new JMenuItem(copyCellA));
            menu.add(new JMenuItem(copyToCsvA));
            menu.add(new JMenuItem(selectAllA));
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (container.getVertexByPoint(e.getPoint()) == null) {
            Edge edge = container.getEdgeByPoint(e.getPoint());
            if (edge != null && GraphDataUtil.getVisible(edge)) {
                if (e.getButton() == MouseEvent.BUTTON3)
                    showPopup(e);
                else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    fireDefaultAction(edge);
                }
            }
        }
    }

    public Edge getCurrentEdge() {
        return currentEdge;
    }

    public void setCurrentEdge(Edge currentEdge) {
        this.currentEdge = currentEdge;
    }

    public void fireDefaultAction(Edge e) {
        setCurrentEdge(e);
        if (e == null)
            return;
        EdgePopupListener.DefPopupMenu menu = getPopupMenu();
        if (menu != null)
            if (menu.getDefaultAction() != null)
                menu.getDefaultAction().actionPerformed(null);
    }

    protected EdgePopupListener.DefPopupMenu getPopupMenu() {
        if (currentEdge != null) {
            if (GraphNetworkUtil.getType((Edge) currentEdge) != null &&
                    GraphNetworkUtil.getType(currentEdge).equals(LinkType.IntegrationLink))
                return objectListMenu;
            else
                return objectMenu;
        } else
            return null;
    }

    protected void showPopup(MouseEvent e) {
        // container.selectVertexByPoint(e.getPoint());

        currentEdge = container.getEdgeByPoint(e.getPoint());
        if (currentEdge != null) {
            EdgePopupListener.DefPopupMenu menu = getPopupMenu();
            setModifiable(menu);
            if (menu != null) {
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        } else {
        }
    }

    private void setModifiable(EdgePopupListener.DefPopupMenu menu) {
        if (container instanceof JTable) {
            JTable table = (JTable) container;
            boolean rowSelected = (table.getSelectedRow() != -1);
            boolean singleSelected = table.getSelectedRowCount() == 1;
            Component[] comps = menu.getComponents();
            for (int i = 0; i < comps.length; i++) {
                Component comp = comps[i];
                if (comp instanceof JMenuItem) {
                    Action action = ((JMenuItem) comp).getAction();
                    if (action instanceof EdgePopupListener.CopyAction || action instanceof EdgePopupListener.CopyToCsvAction) {
                        ((JMenuItem) comp).setEnabled(rowSelected);
                    } else if (action instanceof EdgePopupListener.SelectAllAction) {
                        ((JMenuItem) comp).setEnabled(table.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION);
                    } else if (action instanceof EdgePopupListener.CopyCellAction) {
                        ((JMenuItem) comp).setEnabled(rowSelected && table.getSelectedColumn() != -1 && singleSelected);
                    } else {
                        ((JMenuItem) comp).setEnabled(singleSelected);
                    }
                }
            }
        }
    }

    public static class DefPopupMenu extends JPopupMenu {
        private JMenuItem defaultItem = null;
        private static Font itemFont = null;
        private static Font defaultItemFont = null;

        public DefPopupMenu() {
            defineFont();
        }

        public DefPopupMenu(String label) {
            super(label);
            defineFont();
        }

        private void defineFont() {
            if (EdgePopupListener.DefPopupMenu.itemFont == null)
                EdgePopupListener.DefPopupMenu.itemFont = UIManager.getFont("PopupMenu.font");
            if (EdgePopupListener.DefPopupMenu.defaultItemFont == null)
                EdgePopupListener.DefPopupMenu.defaultItemFont = EdgePopupListener.DefPopupMenu.itemFont.deriveFont(Font.BOLD);
        }

        public void addDefault(JMenuItem item) {
            if (defaultItem != null)
                defaultItem.setFont(EdgePopupListener.DefPopupMenu.itemFont);
            defaultItem = item;
            defaultItem.setFont(EdgePopupListener.DefPopupMenu.defaultItemFont);
            add(item);
        }

        public JMenuItem getDefaultItem() {
            return defaultItem;
        }

        public Action getDefaultAction() {
            if (defaultItem == null)
                return null;
            return defaultItem.getAction();
        }
    }


    // ********************* new actions ***************************
    public class CopyAction extends AbstractAction {
        public CopyAction() {
            super(labelCopy);
            putValue(SHORT_DESCRIPTION, labelCopy);
        }

        public void actionPerformed(ActionEvent e) {
            copy();
        }

        private void copy() {
            TransferHandler th = ((JTable) container).getTransferHandler();
            if (th != null) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                th.exportToClipboard(((JTable) container), clipboard, TransferHandler.COPY);
            }
        }
    }

    public class SelectAllAction extends AbstractAction {
        public SelectAllAction() {
            super(labelSelectAll);
            putValue(SHORT_DESCRIPTION, labelSelectAll);
        }

        public void actionPerformed(ActionEvent e) {
            ((JTable) container).selectAll();
        }
    }

    public class CopyCellAction extends AbstractAction {
        public CopyCellAction() {
            super(labelCopyCell);
            putValue(SHORT_DESCRIPTION, labelCopyCell);
        }

        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) container;
            Object cell = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
            String selection = "";
            if (cell != null) {
                if (cell instanceof ImageIcon) {
                    selection = labelIconCell;
                } else {
                    selection = cell.toString();
                }
            }
            StringSelection data = new StringSelection(selection);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(data, data);
        }
    }

    public class CopyToCsvAction extends AbstractAction {
        public CopyToCsvAction() {
            super(labelCopyToCsv);
            putValue(SHORT_DESCRIPTION, labelCopyToCsv);
        }

        public void actionPerformed(ActionEvent e) {
            copyToCsv();
        }

        private void copyToCsv() {
            JTable table = (JTable) container;
            StringBuffer selection = new StringBuffer();
            int numcols = table.getColumnCount();
            int[] rowsselected = table.getSelectedRows();

            for (int i = -1; i < rowsselected.length; i++) {
                for (int j = 0; j < numcols; j++) {
                    String currentCell = "";
                    if (i == -1) {
                        TableModel model = table.getModel();
                        if (model != null) {
                            currentCell = cellValueToCsvFormat(model.getColumnName(j));
                        }
                    } else {
                        Object cell = table.getValueAt(rowsselected[i], j);
                        if (cell != null) {
                            if (cell instanceof ImageIcon) {
                                currentCell = labelIconCell;
                            } else if (cell instanceof Boolean) {
                                currentCell = ((Boolean) cell) ? TRUE_VALUE : FALSE_VALUE;
                            } else {
                                currentCell = cellValueToCsvFormat(cell.toString());
                            }
                        }
                    }
                    selection.append(currentCell);
                    if (j < numcols - 1)
                        selection.append(DELIM);
                }

                selection.append("\n");
            }
            StringSelection data = new StringSelection(selection.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(data, data);
        }

        private String cellValueToCsvFormat(String currentCell) {
            if (currentCell.indexOf('#') != 0) {
                if (currentCell.indexOf('\"') != -1) {
                    currentCell = currentCell.replace("\"", "\"\"");
                    currentCell = "\"" + currentCell + "\"";
                } else if (currentCell.indexOf(DELIM) != -1) {
                    currentCell = "\"" + currentCell + "\"";
                }
                if (currentCell.indexOf('\n') != -1) {
                    currentCell = currentCell.replace("\r\n", " | ");
//                    currentCell = currentCell.replace("\n", " | ");
                }
                currentCell.trim();
            } else {
                currentCell = "";
            }
            return currentCell;
        }
    }
}
