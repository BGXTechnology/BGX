package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import net.bgx.bgxnetwork.bgxop.gui.background.RemoveVerticesThread;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.ObjectCardDialogPanel;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.transfer.query.Query;
import ru.zsoft.jung.viewer.BufferedViewer;

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
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class VertexPopupListener
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class VertexPopupListener extends MouseAdapter {
    private static final String DELIM = ",";
    private static final String TRUE_VALUE = "YES";
    private static final String FALSE_VALUE = "NO";
    private static Logger log = Logger.getLogger(VertexPopupListener.class.getName());
    private DefPopupMenu expandableListMenu = null;
    private DefPopupMenu removeOnlyMenu = null;
    private DefPopupMenu objectMenu = null;
    private DefPopupMenu objectCollapseMenu = null;
    private IVertexContainer container;
    private Vertex currentVertex = null;
    private MainFrame owner;
    protected ResourceBundle rb = ResourceBundle.getBundle("gui_dialogs");
    protected ResourceBundle guirb = ResourceBundle.getBundle("gui");
    protected ResourceBundle qrb = ResourceBundle.getBundle("query");
    protected OpenListAction openListAction = new OpenListAction();
    protected Action collapseVertexAction;
    protected Action expandVertexAction;
    protected Action invisibleVertexAction;
    //protected OpenCardAction openCardAction = new OpenCardAction();
    //protected RemoveVertexAction removeAction = new RemoveVertexAction();
    //protected NewQueryAction newAction = new NewQueryAction();
    //protected ExecuteQueryAction executeAction = new ExecuteQueryAction();
    private ResourceBundle rbCopyPaste = null;
    private Action copyA;
    private Action copyCellA;
    private Action copyToCsvA;
    private Action selectAllA;
    private String labelCopy;
    private String labelCopyCell;
    private String labelCopyToCsv;
    private String labelSelectAll;
    private String labelIconCell;
    private AppAction _actions = AppAction.getInstance();

    public VertexPopupListener(MainFrame owner, IVertexContainer c) {
        container = c;
        this.owner = owner;

        collapseVertexAction = _actions.getAction(AppAction.OPEN_NODE_GRP_ACTION);
        expandVertexAction = _actions.getAction(AppAction.REMOVE_GROUP_ACTION);
        invisibleVertexAction = _actions.getAction(AppAction.SET_INVISIBLE_OBJECT_ACTION);
        initCopyPasteMenuItem();
        expandableListMenu = new DefPopupMenu();
        expandableListMenu.addDefault(new JMenuItem(openListAction));
        //expandableListMenu.add(new JMenuItem(_actions.getAction(AppAction.REMOVE_OBJECT_ACTION)));
        addNewMenuItem(expandableListMenu);
        removeOnlyMenu = new DefPopupMenu();
        //removeOnlyMenu.add(new JMenuItem(_actions.getAction(AppAction.REMOVE_OBJECT_ACTION)));
        addNewMenuItem(removeOnlyMenu);
        objectMenu = new DefPopupMenu();
        objectMenu.addDefault(new JMenuItem(_actions.getAction(AppAction.CARD_OBJECT_ACTION)));
        //objectMenu.add(new JMenuItem(_actions.getAction(AppAction.REMOVE_OBJECT_ACTION)));
        objectMenu.add(new JMenuItem(collapseVertexAction));
        objectMenu.add(new JMenuItem(invisibleVertexAction));

        addNewMenuItem(objectMenu);

        objectCollapseMenu = new DefPopupMenu();
        objectCollapseMenu.addDefault(new JMenuItem(collapseVertexAction));
        objectCollapseMenu.addDefault(new JMenuItem(expandVertexAction));
    }

    private void initCopyPasteMenuItem() {
        initResourceBundle();
        copyA = new CopyAction();
        copyCellA = new CopyCellAction();
        copyToCsvA = new CopyToCsvAction();
        selectAllA = new SelectAllAction();
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

    private void addNewMenuItem(DefPopupMenu menu) {
        if (container instanceof JTable) {
            menu.addSeparator();
            menu.add(new JMenuItem(copyA));
            menu.add(new JMenuItem(copyCellA));
            menu.add(new JMenuItem(copyToCsvA));
            menu.add(new JMenuItem(selectAllA));
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            Vertex v = container.getVertexByPoint(e.getPoint());
            if (v == null) return;
            if (!GraphDataUtil.getVisible(v)) return;
            if (v != null) {
                setCurrentVertex(v);
                owner.setVertexEnableControl(true);
                showPopup(e);
            }
        } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
            Vertex v = container.getVertexByPoint(e.getPoint());
            fireDefaultAction(v);
        }
    }

    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    public void setCurrentVertex(Vertex currentVertex) {
        this.currentVertex = currentVertex;
        owner.setCurrentSelectedVertex(currentVertex);
    }

    public void fireDefaultAction(Vertex v) {
        setCurrentVertex(v);
        if (v == null || v instanceof GraphCollapser.CollapsedVertex)
            return;
        DefPopupMenu menu = getPopupMenu();
        if (menu != null)
            if (menu.getDefaultAction() != null)
                menu.getDefaultAction().actionPerformed(null);
    }

    protected DefPopupMenu getPopupMenu() {
        return objectMenu;
    }

    protected DefPopupMenu getCollapsePopupMenu() {
        return objectCollapseMenu;
    }

    protected void showPopup(MouseEvent e) {
        // container.selectByPoint(e.getPoint());
        currentVertex = container.getVertexByPoint(e.getPoint());
        if (currentVertex == null)
            return;
        if (!(currentVertex instanceof GraphCollapser.CollapsedVertex)) {
            DefPopupMenu menu = getPopupMenu();
            setModifiable(menu);
            if (menu != null) {
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        } else {
            DefPopupMenu menu = getCollapsePopupMenu();
            setModifiable(menu);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void setModifiable(DefPopupMenu menu) {
        if (container instanceof JTable) {
            JTable table = (JTable) container;
            boolean rowSelected = (table.getSelectedRow() != -1);
            boolean singleSelected = table.getSelectedRowCount() == 1;
            Component[] comps = menu.getComponents();
            for (int i = 0; i < comps.length; i++) {
                Component comp = comps[i];
                if (comp instanceof JMenuItem) {
                    Action action = ((JMenuItem) comp).getAction();
                    if (action instanceof CopyAction || action instanceof CopyToCsvAction) {
                        ((JMenuItem) comp).setEnabled(rowSelected);
                    } else if (action instanceof SelectAllAction) {
                        ((JMenuItem) comp).setEnabled(table.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION);
                    } else if (action instanceof CopyCellAction) {
                        ((JMenuItem) comp).setEnabled(rowSelected && table.getSelectedColumn() != -1 && singleSelected);
                    } else {
                        ((JMenuItem) comp).setEnabled(singleSelected);
                    }

                }
            }
        }
        if (container instanceof GraphController) {
            Component[] comps = menu.getComponents();
            for (int i = 0; i < comps.length; i++) {
                Component comp = comps[i];
                if (comp instanceof JMenuItem) {
                    Action action = ((JMenuItem) comp).getAction();
                    if (action instanceof AppAction.OpenNodeGroupAction) {
                        ((JMenuItem) comp).setEnabled(
                                owner.getQueryListController().getCurrentComponent().getGraph().getPickedState() != null &&
                                        owner.getQueryListController().getCurrentComponent().getGraph().
                                                getPickedState().getPickedVertices() != null &&
                                        owner.getQueryListController().getCurrentComponent().getGraph().
                                                getPickedState().getPickedVertices().size() > 1);

                    } else {
                        ((JMenuItem) comp).setEnabled(true);

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
            if (itemFont == null)
                itemFont = UIManager.getFont("PopupMenu.font");
            if (defaultItemFont == null)
                defaultItemFont = itemFont.deriveFont(Font.BOLD);
        }

        public void addDefault(JMenuItem item) {
            if (defaultItem != null)
                defaultItem.setFont(itemFont);
            defaultItem = item;
            defaultItem.setFont(defaultItemFont);
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

    // ********************* actions ***************************
    public class OpenListAction extends AbstractAction {
        public OpenListAction() {
            this.putValue(NAME, rb.getString("VertexPopupMenuListener.openList.label"));
        }

        public OpenListAction(String title) {
            this.putValue(NAME, title);
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("This is stub. look at 2.1 for detailes");
        }
    }

    public class OpenCardAction extends AbstractAction {
        public OpenCardAction() {
            this.putValue(NAME, rb.getString("VertexPopupMenuListener.openCard.label"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = owner.getQueryListController().getCurrentQuery();
            assert q != null;
            assert currentVertex != null;
            try {
                ControlObject controlObject = GraphNetworkUtil.getControlObject(currentVertex);
                ObjectCardDialogPanel objectCardDialogPanel = new ObjectCardDialogPanel(owner, controlObject);
                objectCardDialogPanel.setVisible(true);
            } catch (Throwable ex) {
                MessageDialogs.generalError(owner, ex, guirb.getString("error.cardFetch"), guirb.getString("error.commonTitle"));
            }
        }
    }

    public class RemoveVertexAction extends AbstractAction {
        public RemoveVertexAction() {
            this.putValue(NAME, rb.getString("VertexPopupMenuListener.removeVertex.label"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = owner.getQueryListController().getCurrentQuery();
            Graph g = owner.getQueryListController().getCurrentData().getGraph();
            assert q != null;
            assert g != null;
            assert currentVertex != null;
            Set<Edge> edges = currentVertex.getIncidentEdges();
            WaitDialog wait = new WaitDialog(owner);
            RemoveVerticesThread th = new RemoveVerticesThread(wait, q, currentVertex, edges);
            th.start();
            wait.showDialog();
            if (th.getException() == null) {
                BufferedViewer view = owner.getQueryListController().getCurrentGraph();
                owner.getQueryListController().getCurrentComponent().getGraphController().removeVertex(currentVertex);
                view.unlock();
                view.repaint();
                owner.getQueryListController().
                        setStatusBar(owner.getQueryListController().getCurrentData());
            }
        }
    }

    /*   public class OpenNodeGroupAction extends AbstractAction {
        public OpenNodeGroupAction()

        {
            super(guirb.getString("Action.name.createGroup"));
            putValue(SHORT_DESCRIPTION, guirb.getString("Action.name.createGroup"));
        }


        public void actionPerformed(ActionEvent e) {
            if (currentVertex instanceof GraphCollapser.CollapsedVertex) {

                NodeGroupDialog dialog = new NodeGroupDialog(owner,
                        rb.getString("NodeGroupDialog.Edit.title"),
                        GraphNetworkUtil.getName(currentVertex));
            } else {
                NodeGroupDialog dialog = new NodeGroupDialog(owner,
                        rb.getString("NodeGroupDialog.Add.title"),
                        null);

            }
        }
    }*/

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
