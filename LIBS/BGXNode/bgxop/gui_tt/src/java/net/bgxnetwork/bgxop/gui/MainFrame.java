package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import net.bgx.bgxnetwork.bgxop.engine.AlgorithmsHolder;
import net.bgx.bgxnetwork.bgxop.gui.background.BackgroundLayouter;
import net.bgx.bgxnetwork.bgxop.gui.background.RefreshThread;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.ExportDialog;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.LayerDialog;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.LayoutConfigDialog;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.object.FieldObjectContainer;
import net.bgx.bgxnetwork.bgxop.gui.lv.request.RequestListController;
import net.bgx.bgxnetwork.bgxop.gui.lv.request.RequestTabbedPane;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.bgxop.services.audit.AuditManager;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.uitools.ActorInfo;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.query.LinkType;
import net.bgx.bgxnetwork.transfer.query.Query;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class MainFrame
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class MainFrame extends JFrame {
    private static Logger log = Logger.getLogger(MainFrame.class.getName());
    private static final Dimension toolButtonSize = new Dimension(47, 47);
    // settings
    public static PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
    public static String currentDir = "";
    private ProfilePanel profilePanel = null;
    private AppAction _actions = AppAction.getInstance();
    private RequestTabbedPane requestTabbedPane = null;
    private QueryListController queryListController = null;
    private StatusBar status = new StatusBar();
    protected Action curLayoutCfgA, layoutCfgA;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
    private Vertex lastSelectedVertex = null;
    private Edge lastSelectedEdge;

    public MainFrame() {
        setTitle(rb.getString("MainFrame.title"));
        ImageIcon appIcon = (ImageIcon) ResourceLoader.getInstance().getIconByResource(rb, "icon");
        setIconImage(appIcon.getImage());
        profilePanel = new ProfilePanel(new ProfilePanelController(this));
        profilePanel.getController().refresh();
        JTabbedPane sidePanel = new JTabbedPane();
        sidePanel.addTab(rb.getString("MainFrame.profileTab"), profilePanel);
        queryListController = new QueryListController(this);
        //queryListPanel = new QueryListPanel(queryListController);

        RequestListController requestListController = new RequestListController();
        requestTabbedPane = new RequestTabbedPane(requestListController, this);

        JSplitPane generalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        generalSplit.setLeftComponent(sidePanel);
        //generalSplit.setRightComponent(queryListPanel);
        generalSplit.setRightComponent(requestTabbedPane);
        sidePanel.setPreferredSize(new Dimension(250, 500));
        generalSplit.resetToPreferredSizes();
        generalSplit.setDividerSize(9);
        generalSplit.setOneTouchExpandable(true);
        createActions();
        getContentPane().add(createToolBar(), BorderLayout.NORTH);
        getContentPane().add(generalSplit, BorderLayout.CENTER);
        getContentPane().add(status, BorderLayout.SOUTH);
        setJMenuBar(createMenu());
        enableControls(false);
        addWindowListener(new Closer());
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        setBounds(env.getMaximumWindowBounds());
        GlobalPopupUtil.initListeners(this);
        _actions.setOwner(this);
        pack();
        setVisible(true);
        setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public ProfilePanel getProfilePanel() {
        return profilePanel;
    }

    public StatusBar getStatusBar() {
        return status;
    }

    private void createActions() {
        // Disable at 2006.12.22
        //layersA = new LayersAction();
        //filterA = _actions.getAction(AppAction.FILTER_ACTION);
        //expA = new ExportAction();

        layoutCfgA = new LayoutConfigAction();
        curLayoutCfgA = new CurrentLayoutConfigAction();
    }

    private JMenuBar createMenu() {
        JMenu file = new JMenu(rb.getString("MainFrame.Menu.File")),
                edit = new JMenu(rb.getString("MainFrame.Menu.Edit")),
                parameters = new JMenu(rb.getString("MainFrame.Menu.Parameters")),
                view = new JMenu(rb.getString("MainFrame.Menu.View")),
                layout = new JMenu(rb.getString("MainFrame.Menu.Layout")),
                help = new JMenu(rb.getString("MainFrame.Menu.Help"));

        // File menu
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CREATE_ACTION))));
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.OPEN_ACTION))));
        _actions.getAction(AppAction.OPEN_ACTION).setEnabled(false);
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.EDIT_ACTION))));
        _actions.getAction(AppAction.EDIT_ACTION).setEnabled(false);
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.COPY_ACTION))));
        _actions.getAction(AppAction.COPY_ACTION).setEnabled(false);
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.REMOVE_ACTION))));
        _actions.getAction(AppAction.REMOVE_ACTION).setEnabled(false);
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CLOSE_ACTION))));
        _actions.getAction(AppAction.CLOSE_ACTION).setEnabled(false);
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.SAVE_ACTION))));
        _actions.getAction(AppAction.SAVE_ACTION).setEnabled(false);
        file.addSeparator();
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.SAVE_IMG_ACTION))));
        _actions.getAction(AppAction.SAVE_IMG_ACTION).setEnabled(false);
        file.addSeparator();
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.PAGE_PROPERTY_ACTION))));
        //_actions.getAction(AppAction.PAGE_PROPERTY_ACTION).setEnabled(false);
        file.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.PRINT_ACTION))));
        _actions.getAction(AppAction.PRINT_ACTION).setEnabled(false);
        file.addSeparator();
        JMenuItem exitMenu = new JMenuItem(_actions.getAction(AppAction.EXIT_ACTION));
        exitMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.processWindowEvent(new WindowEvent(MainFrame.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        file.add(initActionMenuItem(exitMenu));

        // Edit menu
        edit.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.OPEN_NODE_GRP_ACTION))));
        edit.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.REMOVE_GROUP_ACTION))));
        edit.addSeparator();
        edit.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.SET_INVISIBLE_OBJECT_ACTION))));
        edit.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.SHOW_INVISIBLE_OBJECT_ACTION))));
        _actions.getAction(AppAction.SHOW_INVISIBLE_OBJECT_ACTION).setEnabled(false);
        edit.addSeparator();
        edit.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.FILTER_ACTION))));
        _actions.getAction(AppAction.FILTER_ACTION).setEnabled(false);

        //parameters menu
        parameters.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CARD_OBJECT_ACTION))));
        _actions.getAction(AppAction.CARD_OBJECT_ACTION).setEnabled(false);
        parameters.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CARD_LINK_ACTION))));
        _actions.getAction(AppAction.CARD_LINK_ACTION).setEnabled(false);
        parameters.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.MANEGEMENT_ATR_ACTION))));
        parameters.addSeparator();
        parameters.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION))));
        _actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION).setEnabled(false);

        // view menu
//        view.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.TABLE_VIEW_ACTION))));
//        _actions.getAction(AppAction.TABLE_VIEW_ACTION).setEnabled(false);
        view.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.GRAPH_VIEW_ACTION))));
        _actions.getAction(AppAction.GRAPH_VIEW_ACTION).setEnabled(false);
        view.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.TIMETABLE_VIEW_ACTION))));
        _actions.getAction(AppAction.TIMETABLE_VIEW_ACTION).setEnabled(false);
        view.addSeparator();
        view.add(initActionMenuItem(new JMenuItem(queryListController.showNA)));
        _actions.getAction(AppAction.SHOW_NEIGHBOUR_ACTION).setEnabled(false);

        // layout menu
//        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.SAVE_LAYOUT_ACTION))));
//        _actions.getAction(AppAction.SAVE_LAYOUT_ACTION).setEnabled(false);
//        layout.addSeparator();
        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CIRCLE_LAYOUT_ACTION))));
        _actions.getAction(AppAction.CIRCLE_LAYOUT_ACTION).setEnabled(false);
        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.KK_LAYOUT_ACTION))));
        _actions.getAction(AppAction.KK_LAYOUT_ACTION).setEnabled(false);
        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.FR_LAYOUT_ACTION))));
        _actions.getAction(AppAction.FR_LAYOUT_ACTION).setEnabled(false);
        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.AUTO_LAYOUT_ACTION))));
        _actions.getAction(AppAction.AUTO_LAYOUT_ACTION).setEnabled(false);
        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.STATIC_LAYOUT_ACTION))));
        _actions.getAction(AppAction.STATIC_LAYOUT_ACTION).setEnabled(false);
        layout.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.CUSTOM_LAYOUT_ACTION))));
        _actions.getAction(AppAction.CUSTOM_LAYOUT_ACTION).setEnabled(false);

        // help menu
        help.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.ABOUT_ACTION))));
        help.add(initActionMenuItem(new JMenuItem(_actions.getAction(AppAction.INSTRUCTION_ACTION))));

        JCheckBoxMenuItem neiMI = (JCheckBoxMenuItem) initActionMenuItem(new JCheckBoxMenuItem(queryListController.showNA));
        queryListController.showNA.addParent(neiMI);
        neiMI.setSelected(queryListController.showNA.getState());

        JMenuBar res = new JMenuBar();
        res.add(file);
        res.add(edit);
        res.add(parameters);
        res.add(view);
        res.add(layout);
        res.add(help);
        return res;
    }

    private JMenuItem initActionMenuItem(JMenuItem src) {
        src.setIcon(null);
        src.setToolTipText(null);
        return src;
    }

    private JComponent createToolBar() {
        JPanel toolbar = new JPanel(new GridBagLayout());
        Insets normal = new Insets(0, 0, 0, 0);
        GridBagConstraints gbc = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
        GridBagConstraints separated = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0);
        GridBagConstraints last = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        JToggleButton neiB = (JToggleButton) initToolButton(new JToggleButton(queryListController.showNA), "MainFrame.neighbours.img");
        queryListController.showNA.addParent(neiB);
        neiB.setSelected(queryListController.showNA.getState());

        toolbar.add(initToolButton(new JButton(profilePanel.createA), "ProfilePanel.new.img"), gbc);
        gbc.insets = normal;
        toolbar.add(initToolButton(new JButton(profilePanel.openA), "ProfilePanel.open.img"), gbc);
        toolbar.add(initToolButton(new JButton(profilePanel.editA), "ProfilePanel.edit.img"), gbc);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.COPY_ACTION)), "MainFrame.copy.img"), gbc);
        toolbar.add(initToolButton(new JButton(profilePanel.refreshA), "ProfilePanel.refresh.img"), gbc);
        toolbar.add(initToolButton(new JButton(profilePanel.executeA), "ProfilePanel.execute.img"), gbc);
        toolbar.add(initToolButton(new JButton(profilePanel.removeA), "ProfilePanel.remove.img"), gbc);
        toolbar.add(initToolButton(new JButton(queryListController.closeA), "MainFrame.closeGraph.img"), gbc);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.SAVE_ACTION)), "MainFrame.saveImage.img"), gbc);

        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.PRINT_ACTION)), "MainFrame.print.img"), separated);

        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.OPEN_NODE_GRP_ACTION)), "MainFrame.createGrp.img"), separated);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.REMOVE_GROUP_ACTION)), "MainFrame.removeGrp.img"), gbc);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.SHOW_INVISIBLE_OBJECT_ACTION)), "MainFrame.showInvisible.img"), gbc);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.SET_INVISIBLE_OBJECT_ACTION)), "MainFrame.removeObject.img"), gbc);

        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.CARD_OBJECT_ACTION)), "MainFrame.objectCard.img"), separated);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.CARD_LINK_ACTION)), "MainFrame.linkCard.img"), gbc);
//        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.MANEGEMENT_ATR_ACTION)), "MainFrame.attributes.img", false), gbc);
        toolbar.add(initToolButton(new JButton(_actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION)), "MainFrame.integratedLink.img"), gbc);

        toolbar.add(neiB, separated);
//        toolbar.add(initToolButton(new JButton(filterA), "MainFrame.filter.img", false), gbc);
//        toolbar.add(initToolButton(new JButton(queryListController.saveLayoutA), "MainFrame.saveLayout.img", false), gbc);

//        toolbar.add(graphB, separated);
//        toolbar.add(dataB, gbc);
        toolbar.add(initToolButton(new JButton(queryListController.zoomIn), "MainFrame.zoomIn.img"), separated);
        toolbar.add(initToolButton(new JButton(queryListController.zoomOut), "MainFrame.zoomOut.img"), gbc);
        toolbar.add(new JLabel(), last);
        // add logo
        JLabel logo = new JLabel(ResourceLoader.getInstance().getIconByResource(rb, "logo"));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 10);
        toolbar.add(logo, gbc);
        return toolbar;
    }

    private AbstractButton initToolButton(AbstractButton button, String resource) {
        button.setText(null);
        button.setIcon(ResourceLoader.getInstance().getIconByResource(rb, resource));
        Icon pressed = ResourceLoader.getInstance().getIconByResource(rb, resource + ".pressed");
        button.setPressedIcon(pressed);
        button.setSelectedIcon(pressed);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setMinimumSize(toolButtonSize);
        button.setMaximumSize(toolButtonSize);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private AbstractButton initToolButton(AbstractButton button, String resource, Boolean isPressed) {
        button.setText(null);
        button.setIcon(ResourceLoader.getInstance().getIconByResource(rb, resource));
        if (isPressed) {
            Icon pressed = ResourceLoader.getInstance().getIconByResource(rb, resource + ".pressed");
            button.setPressedIcon(pressed);
            button.setSelectedIcon(pressed);
        }
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setMinimumSize(toolButtonSize);
        button.setMaximumSize(toolButtonSize);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private JButton createToolButton(Action a, String resource) {
        JButton res = new JButton(a);
        res.setIcon(ResourceLoader.getInstance().getIconByResource(rb, resource));
        res.setText(null);
        res.setMinimumSize(toolButtonSize);
        res.setMaximumSize(toolButtonSize);
        return res;
    }

    public void setDisabledOpenCard() {
        _actions.getAction(AppAction.CARD_OBJECT_ACTION).setEnabled(false);
    }

    public void setVertexEnableControl(boolean enable) {
        if (enable) {
            if (!(lastSelectedVertex instanceof GraphCollapser.CollapsedVertex)) {
                _actions.getAction(AppAction.OPEN_NODE_GRP_ACTION).setEnabled(getQueryListController().
                        getCurrentGraph().getPickedState().getPickedVertices().size() > 1);
                _actions.getAction(AppAction.SET_INVISIBLE_OBJECT_ACTION).setEnabled(true);
                _actions.getAction(AppAction.CARD_OBJECT_ACTION).setEnabled(true);
                _actions.getAction(AppAction.REMOVE_GROUP_ACTION).setEnabled(false);

            } else {
                _actions.getAction(AppAction.OPEN_NODE_GRP_ACTION).setEnabled(getQueryListController().
                        getCurrentGraph().getPickedState().getPickedVertices().size() > 1);
                _actions.getAction(AppAction.SET_INVISIBLE_OBJECT_ACTION).setEnabled(false);
                _actions.getAction(AppAction.CARD_OBJECT_ACTION).setEnabled(false);
                _actions.getAction(AppAction.REMOVE_GROUP_ACTION).setEnabled(true);
            }

        } else {
            _actions.getAction(AppAction.OPEN_NODE_GRP_ACTION).setEnabled(enable);
            _actions.getAction(AppAction.SET_INVISIBLE_OBJECT_ACTION).setEnabled(enable);
            _actions.getAction(AppAction.CARD_OBJECT_ACTION).setEnabled(enable);
            _actions.getAction(AppAction.REMOVE_GROUP_ACTION).setEnabled(enable);
        }
    }

    public void setEdgeEnableControl(boolean enable) {
        _actions.getAction(AppAction.CARD_LINK_ACTION).setEnabled(enable);
        //_actions.getAction(AppAction.REMOVE_LINK_ACTION).setEnabled(enable);
        if (!enable)
            _actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION).setEnabled(enable);
    }

    public void enableControls(boolean enable) {
        //?????? ?????? Action ??????? ??????? ?? ?????? ???????, ?????????? ??????????????
        setVertexEnableControl(false);
        //?????? ?????? Action ??????? ??????? ?? ?????? ?????, ?????????? ??????????????
        setEdgeEnableControl(false);

        //file
        _actions.getAction(AppAction.CLOSE_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.SAVE_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.SAVE_IMG_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.PRINT_ACTION).setEnabled(enable);

        //edit
        _actions.getAction(AppAction.SHOW_INVISIBLE_OBJECT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.FILTER_ACTION).setEnabled(enable);

        //view
        _actions.getAction(AppAction.GRAPH_VIEW_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.TABLE_VIEW_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.TIMETABLE_VIEW_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.SHOW_NEIGHBOUR_ACTION).setEnabled(enable);

        //layout
        _actions.getAction(AppAction.SAVE_LAYOUT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.CIRCLE_LAYOUT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.KK_LAYOUT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.FR_LAYOUT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.AUTO_LAYOUT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.STATIC_LAYOUT_ACTION).setEnabled(enable);
        _actions.getAction(AppAction.CUSTOM_LAYOUT_ACTION).setEnabled(enable);

        if (enable) {
            status.addMessage(MessageFormat.format(rb.getString("MainFrame.openGraph.status"), queryListController.getCurrentComponent()
                    .getName()));
        }
    }

    public void setEnabledOtherButton(boolean enable){
        queryListController.showNA.setEnabled(enable);
        queryListController.zoomIn.setEnabled(enable);
        queryListController.zoomOut.setEnabled(enable);

        for (QueryListController.DoLayoutAction action : queryListController.getLayoutActions()){
            action.setEnabled(enable);
        }
    }

    public void disableControlsForTTView() {
        //?????? ?????? Action ??????? ??????? ?? ?????? ???????, ?????????? ??????????????
        setVertexEnableControl(false);
        //?????? ?????? Action ??????? ??????? ?? ?????? ?????, ?????????? ??????????????
        setEdgeEnableControl(false);

        //edit
        _actions.getAction(AppAction.SHOW_INVISIBLE_OBJECT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.FILTER_ACTION).setEnabled(false);

        _actions.getAction(AppAction.SHOW_NEIGHBOUR_ACTION).setEnabled(false);

        //layout
        _actions.getAction(AppAction.SAVE_LAYOUT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.CIRCLE_LAYOUT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.KK_LAYOUT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.FR_LAYOUT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.AUTO_LAYOUT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.STATIC_LAYOUT_ACTION).setEnabled(false);
        _actions.getAction(AppAction.CUSTOM_LAYOUT_ACTION).setEnabled(false);

        queryListController.showNA.setEnabled(false);
        queryListController.zoomIn.setEnabled(false);
        queryListController.zoomOut.setEnabled(false);
    }

    // ********************** Nested classes
    // *************************************
    private class Closer extends WindowAdapter {
        private WaitDialog waitDialog;

        public void windowClosing(WindowEvent e) {
            waitDialog = new WaitDialog(MainFrame.this);
            CloserThread thread = new CloserThread();
            thread.start();
            waitDialog.showDialog();
            System.exit(0);
        }

        private class CloserThread extends Thread {
            public void run() {
                RefreshThread refresher = profilePanel.getController().getRefresher();
                refresher.setStop();
                try {
                    refresher.join();
                }
                catch (InterruptedException e) {
                    MessageDialogs.generalError(MainFrame.this, e);
                }
                finally {
                    /* implementation saving data move to saveAllLayouts() start comment
                    for (QueryPanel queryPanel : queryListController.getQueriesList()) {
                        ArrayList<ControlObject> objects = new ArrayList<ControlObject>();
                        for (Node node : queryPanel.getController().getData().getNetwork().getNodeArray()) {
                            if (node == null) continue;
                            ControlObject co = GraphNetworkUtil.getControlObject(node);
                            if (co == null) continue;
                            objects.add(co);
                        }
                        try {
                            QueryServiceDelegator.getInstance().updateControlObjects(objects);
                        }
                        catch (Exception e) {
                            //todo
                            e.printStackTrace();
                        }
                        //save link state
                        ArrayList<LinkObject> links = new ArrayList<LinkObject>();
                        for (oracle.spatial.network.Link link : queryPanel.getController().getData().getNetwork().getLinkArray()) {
                            if (link == null) continue;
                            LinkObject lo = GraphNetworkUtil.getLinkObject(link);
                            if (lo == null) continue;
                            links.add(lo);
                        }
                        try {
                            QueryServiceDelegator.getInstance().updateLinkObjects(links);
                        }
                        catch (Exception ex) {
                            //todo
                            ex.printStackTrace();
                        }
                    }
                    /* implementation saving data move to saveAllLayouts() end comment */
                    queryListController.saveAllLayouts();
                    try {
                        AuditManager auditManager = new AuditManager();
                        auditManager.addExitEvent();
                    } catch (Exception ex) {
                        MessageDialogs.generalError(MainFrame.this, ex);
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        waitDialog.close();
                    }
                });
            }
        }
    }

    public class LayersAction extends AbstractAction {
        public LayersAction() {
            super(rb.getString("MainFrame.layers.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("MainFrame.layers.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            new LayerDialog(MainFrame.this, getValue(NAME).toString(), queryListController.getCurrentGraph());
        }
    }

    public class ExportAction extends AbstractAction {
        public ExportAction() {
            super(rb.getString("MainFrame.exportData.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("MainFrame.exportData.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            ArrayList<String> roles = new ArrayList<String>();
            roles.add("QueryResultExporting");
            try {
                if (!ActorInfo.getInstance().permitRulles(roles)) {
                    throw new QueryBusinesException("Permit no denide");
                }
                try {
                    String queryName = queryListController.getCurrentComponent().getName();
                    AuditManager auditManager = new AuditManager();
                    auditManager.addExportEvent(queryListController.getCurrentComponent().getQuery().getId());
                } catch (Exception ex) {
                    MessageDialogs.generalError(MainFrame.this, ex);
                }
                QueryPanel dataPanel = queryListController.getCurrentComponent();
                if (dataPanel.getController().isVerticesExportable() || dataPanel.getController().isEdgesExportable())
                    new ExportDialog(getValue(NAME).toString(), dataPanel, MainFrame.this);
                else
                    MessageDialogs.warning(MainFrame.this, rb.getString("error.exportNotSupported"),
                            rb.getString("error.operationInpossibleTitle"));
            } catch (QueryBusinesException ex) {
                String note = rb.getString("MainFrame.exportData.permissionLogNote");
                //permissionDeniedInLog(note);
                MessageDialogs.warning(MainFrame.this, rb.getString("MainFrame.exportData.noPermission"), null);
            }
        }
    }

    public class CurrentLayoutConfigAction extends AbstractAction {
        public CurrentLayoutConfigAction() {
            super(rb.getString("MainFrame.currentLayoutConfig.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("MainFrame.currentLayoutConfig.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = queryListController.getCurrentQuery();
            LayoutConfigDialog d = new LayoutConfigDialog(MainFrame.this, (String) getValue(NAME), AlgorithmsHolder.getInstance()
                    .getGraphCustoms(q.getId()), AlgorithmsHolder.getInstance().createCommonCustoms());
            if (!d.getController().dataModified())
                return;
            AlgorithmsHolder.getInstance().setGraphCustoms(q.getId(), d.getController().getData());
            queryListController.relayoutCurrentGraph();
        }
    }

    public class LayoutConfigAction extends AbstractAction {
        public LayoutConfigAction() {
            super(rb.getString("MainFrame.layoutConfig.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("MainFrame.layoutConfig.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            LayoutConfigDialog d = new LayoutConfigDialog(MainFrame.this, (String) getValue(NAME), AlgorithmsHolder.getInstance()
                    .createCommonCustoms(), AlgorithmsHolder.getInstance().getDefaultCustoms());
            if (!d.getController().dataModified())
                return;
            AlgorithmsHolder.getInstance().setCommonCustoms(d.getController().getData());
            queryListController.relayoutCurrentGraph();
            ArrayList<String> queries = new ArrayList<String>();
            for (QueryPanel q : queryListController.getQueriesList())
                queries.add(q.getName());
            BackgroundLayouter newThread = new BackgroundLayouter(queryListController, queries);
            newThread.start();
        }
    }

    public void permissionDeniedInLog(String note) {
        try {
            AuditManager auditManager = new AuditManager();
            auditManager.addAccesDeniedEvent(note);
        } catch (Exception ex) {
            MessageDialogs.generalError(MainFrame.this, ex);
        }
    }

    public RequestTabbedPane getRequestTabbedPane() {
        return requestTabbedPane;
    }

    public QueryListController getQueryListController() {
        return queryListController;
    }

    public void applyVisibleChanges(Long id, ArrayList<ControlObject> objects) {
        try {
            //QueryServiceDelegator.getInstance().updateControlObjects(objects);
            QueryPanel queryPanel = this.getQueryListController().getQueryPanel(id);
            queryPanel.setVisibleState();
        }
        catch (Exception e) {
            //todo
            e.printStackTrace();
        }
    }

    public void updateChangesForLink(Long id, ArrayList<LinkObject> objects) {
        try {
            //QueryServiceDelegator.getInstance().updateLinkObjects(objects);
            QueryPanel queryPanel = this.getQueryListController().getQueryPanel(id);
            queryPanel.setVisibleState();
        }
        catch (Exception e) {
            //todo
            e.printStackTrace();
        }
    }

    public void applyChangesByPropertyPanel(Long id, Object obj) {
        //
    }

    public void applyChangesByPropertyPanel(Object obj) {

    }

    public void applyFilter(Query query, Object obj) {
        QueryPanel queryPanel = this.getQueryListController().getQueryPanel(query);
        queryPanel.setFieldObjectContainer((FieldObjectContainer) obj);
        queryPanel.doFilter();
    }

    public void setListVisiblePropertyFoQuery(Query q, Object obj) {
        FieldObjectContainer visibleParamsContainer = (FieldObjectContainer) obj;
        ArrayList<Long> codes = new ArrayList<Long>();
        for (FieldObject fieldObject : visibleParamsContainer.getObjectProperties()) {
            if (fieldObject == null) continue;
            codes.add(fieldObject.getCode());
        }

        for (FieldObject fieldObject : visibleParamsContainer.getLinkProperties()) {
            if (fieldObject == null) continue;
            codes.add(fieldObject.getCode());
        }

        try {
            HashMap<Long, List<PropertyType>> visibleAtt =
                    (HashMap<Long, List<PropertyType>>) QueryServiceDelegator.getInstance().setVisibleAttributes(q.getId(), codes);
            q.setViewedAttributes(visibleAtt);
            getQueryListController().getQueryPanel(q).repaintGraph();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentSelectedVertex(Vertex v) {
        if (v instanceof GraphCollapser.CollapsedVertex)
            _actions.getAction(AppAction.REMOVE_GROUP_ACTION).setEnabled(true);
        else
            _actions.getAction(AppAction.REMOVE_GROUP_ACTION).setEnabled(false);

        lastSelectedVertex = v;
    }

    public Vertex getCurrentSelectedVertex() {
        return lastSelectedVertex;
    }

    public void setCurrentSelectedEdge(Edge e) {
        if (GraphNetworkUtil.getType(e) != null &&
                GraphNetworkUtil.getType(e).equals(LinkType.IntegrationLink)) {
            _actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION).setEnabled(true);
            _actions.getAction(AppAction.CARD_LINK_ACTION).setEnabled(false);
        } else {
            _actions.getAction(AppAction.CARD_INTEGRATEDLINK_ACTION).setEnabled(false);
            _actions.getAction(AppAction.CARD_LINK_ACTION).setEnabled(true);
        }
        lastSelectedEdge = e;
    }

    public Edge getCurrentSelectedEdge() {
        return lastSelectedEdge;
    }

}
