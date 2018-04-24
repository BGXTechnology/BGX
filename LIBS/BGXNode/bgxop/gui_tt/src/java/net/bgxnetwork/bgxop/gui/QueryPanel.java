package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.utils.ChangeEventSupport;
import edu.uci.ics.jung.visualization.Layout;
import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.engine.LayoutType;
import net.bgx.bgxnetwork.bgxop.graph.CustomRenderer;
import net.bgx.bgxnetwork.bgxop.graph.CustomToolTipFunction;
import net.bgx.bgxnetwork.bgxop.graph.CustomViewer;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.FilterPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.TimedDiagramParameterPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.VisibleAttributesDialogPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.object.FieldObjectContainer;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TimeDiagram;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test.TestDataTDModel;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.bgxop.services.audit.AuditManager;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import ru.zsoft.jung.viewer.BufferedViewer;
import ru.zsoft.jung.viewer.GraphScrollPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Class QueryPanel
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class QueryPanel extends JPanel {
    protected static Dimension defaultBounds = new Dimension(800, 800);
    protected static int defaultIterations = 80;
    protected static final String graphCard = "GraphCard";
    protected static final String dataCard = "DataCard";
    protected static final String timeCard = "TimeCard";
    private QueryController controller = null;
    protected LayoutType layoutType;
    protected BufferedViewer graph;
    protected Dimension bounds;
    protected GraphController graphController = null;
    protected CardLayout cards;
    protected FormatTable dataTable = null;
    protected JScrollPane scroll;
    private ArrayList<JDialog> dialogs = new ArrayList<JDialog>();
    private boolean selected = false;
    private boolean loaded = false;
    private JToggleButton filterButton;

    private JPanel dataPanel;
    private JPanel menuPanel;
    private FieldObjectContainer fieldObjectContainer = null;

    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
    protected AbstractSelectableAction showDataA, showGraphA, showTTA, filterA;
    protected AbstractAction managementAction;
    private boolean layoutChanged = false;

    private TimedDiagramParameterPanel tdpm;

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public ArrayList<JDialog> getDialogs() {
        return dialogs;
    }

    public void addDialogs(JDialog dialog) {
        if (!dialogs.contains(dialog))
            dialogs.add(dialog);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean removeDialog(JDialog dialog) {
        return dialogs.remove(dialog);
    }

    public QueryPanel(boolean loaded) {
        super();
        this.loaded = loaded;
        showGraphA = new ShowGraphAction(true);
        showDataA = new ShowDataAction(false);
        showTTA = new ShowTTAction(false);
        managementAction = new ManagementObjectAction();
        filterA = new FilterAction(false);
    }

    public void setQueryPanel(FormatTable table, QueryController controller, LayoutType layoutType, Layout layout) {
        setLayout(new BorderLayout());

        this.controller = controller;
        this.layoutType = layoutType;
        controller.setView(this);
        setName(controller.getQuery().getName());

        dataPanel = new JPanel();
        cards = new CardLayout();
        dataPanel.setLayout(cards);
        dataPanel.removeAll();

        //create graph Layer
        JPanel graphLayer = new JPanel(new GridBagLayout());
        graph = new CustomViewer(layout, new CustomRenderer(), controller);

        // will work only if layout is ChangeEventSupport instance
        if (layout instanceof ChangeEventSupport) {
            ((ChangeEventSupport) layout).addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    layoutChanged = true;
                }
            });
        }

        graphController = new GraphController(controller.getOwner(), graph, controller.getModel());
        graph.addDirectPaintable(graphController);
        graph.addMouseListener(graphController);
        graph.setToolTipFunction(new CustomToolTipFunction());
        GraphScrollPane scrollPane = new GraphScrollPane(graph);
        graphLayer.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //create table Layer
//        JPanel dataTableLayer = new JPanel(new GridBagLayout());
//        dataTable = table;
//        scroll = new FormatScrollPane(dataTable);
//        scroll.setRowHeaderView(dataTable.getRowHeader());
//        dataTableLayer.add(scroll, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
//                new Insets(10, 10, 10, 10), 0, 0));

        //create TimeTable Layer
        JPanel timeTableLayer = new JPanel(new BorderLayout());
        tdpm = new TimedDiagramParameterPanel(getController());

        TimedDiagrammDataSnapshot dataParameters = null;
        try{
            dataParameters = QueryServiceDelegator.getInstance().getTTParameters(controller.getQuery().getId());
            if (dataParameters != null){
                tdpm.setSnapshot(dataParameters);
                LinkedList<TDPair> pairs = QueryServiceDelegator.getInstance().getTTPairs(controller.getQuery().getId());
                if (pairs != null && pairs.size()>0){
                    TestDataTDModel tdModel = new TestDataTDModel(pairs);
                    getTimeDiagram().setModel(tdModel);
                }
            }
        }
        catch(Exception eee){
            eee.printStackTrace();
        }

        timeTableLayer.add(tdpm, BorderLayout.CENTER);
        //add layers to panel
//        dataPanel.add(dataTableLayer, dataCard);
        dataPanel.add(graphLayer, graphCard);
        dataPanel.add(timeTableLayer, timeCard);

        createToolBar();
//        //add menu to top of QueryPanel
//        this.add(menuPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
//                new Insets(5, 0, 0, 0), 0, 0));
//        //add data panel to QueryPanel
//        this.add(dataPanel, new GridBagConstraints(0, 1, 1, GridBagConstraints.RELATIVE, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
//                new Insets(5, 0, 0, 0), 0, 0));
        this.add(menuPanel, BorderLayout.NORTH);
        this.add(dataPanel, BorderLayout.CENTER);

        setDataLevel(QueryPanel.graphCard);
        GlobalPopupUtil.initListeners(this);
    }

    public Set madeGroupGraph(LayoutCoordinates layoutCoordinates) {
        return graphController.madeGroupGraph(controller.getData(), layoutCoordinates);
    }

    private JComponent createToolBar
            () {
        menuPanel = new JPanel(new BorderLayout());
        //menuPanel = new JPanel(new GridBagLayout());
        JToggleButton graphB = (JToggleButton) initToolButton(new JToggleButton(showGraphA), "MainFrame.graphView.img");
        showGraphA.addParent(graphB);
        graphB.setSelected(showGraphA.getState());

//        JToggleButton dataB = (JToggleButton) initToolButton(new JToggleButton(showDataA), "MainFrame.dataView.img");
//        showDataA.addParent(dataB);
//        dataB.setSelected(showDataA.getState());

        JToggleButton ttB = (JToggleButton) initToolButton(new JToggleButton(showTTA), "MainFrame.ttView.img");
        showTTA.addParent(ttB);
        ttB.setSelected(showTTA.getState());

        JToggleButton managementButton = (JToggleButton) initToolButton(new JToggleButton(managementAction), "MainFrame.attributes.img");

        filterButton = (JToggleButton) initToolButton(new JToggleButton(filterA), "MainFrame.filter.img", true);

        ButtonGroup bg = new ButtonGroup();
//        bg.add(dataB);
        bg.add(graphB);
        bg.add(ttB);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(graphB);
        buttonPanel.add(ttB);
        buttonPanel.add(managementButton);
        buttonPanel.add(filterButton);

//        /*
//          menuPanel.add(dataB, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
//                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
//         */
//        menuPanel.add(graphB, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
//                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
//        menuPanel.add(ttB, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
//                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
//
//        menuPanel.add(managementButton , new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
//                GridBagConstraints.NONE, new Insets(0, 15, 0, 0), 0, 0));
//
//        menuPanel.add(filterButton , new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
//                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));

//        menuPanel.add(blankPanel, new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 10.0, 0.0, GridBagConstraints.LINE_START,
//                GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));

        menuPanel.add(buttonPanel, BorderLayout.WEST);
        return menuPanel;
    }

    private AbstractButton initToolButton
            (AbstractButton
                    button, String
                    resource) {
        button.setText(null);
        button.setIcon(ResourceLoader.getInstance().getIconByResource(rb, resource));
        Icon pressed = ResourceLoader.getInstance().getIconByResource(rb, resource + ".pressed");
        button.setPressedIcon(pressed);
        button.setSelectedIcon(pressed);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setMinimumSize(new Dimension(24, 24));
        button.setMaximumSize(new Dimension(24, 24));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private AbstractButton initToolButton
            (AbstractButton
                    button, String
                    resource, Boolean
                    isPressed) {
        button.setText(null);
        button.setIcon(ResourceLoader.getInstance().getIconByResource(rb, resource));
        if (isPressed) {
            Icon pressed = ResourceLoader.getInstance().getIconByResource(rb, resource + ".pressed");
            button.setPressedIcon(pressed);
            button.setSelectedIcon(pressed);
        }
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setMinimumSize(new Dimension(24, 24));
        button.setMaximumSize(new Dimension(24, 24));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    public QueryController getController
            () {
        return controller;
    }

    public Query getQuery
            () {
        return controller.getQuery();
    }

    public LayoutType getLayoutType
            () {
        return layoutType;
    }

    public void setLayoutType
            (LayoutType
                    layoutType) {
        layoutChanged = true;
        this.layoutType = layoutType;
    }

    public GraphNetworkMapper getData
            () {
        return controller.getData();
    }

    public BufferedViewer getGraph
            () {
        return graph;
    }

    public GraphDataModel getGraphModel
            () {
        return controller.getModel();
    }

    public GraphController getGraphController
            () {
        return graphController;
    }

    public FormatTable getDataTable
            () {
        return dataTable;
    }

    public void showCard
            (String
                    cardName) {
        cards.show(dataPanel, cardName);
    }

    public void hideAllDialogs
            () {
        for (JDialog dialog : dialogs) {
            if (dialog == null) continue;
            dialog.setVisible(false);
        }
    }

    public void showAllDialogs
            () {
        for (JDialog dialog : dialogs) {
            if (dialog == null) continue;
            if (dialog instanceof WaitDialog)
                if (((WaitDialog) dialog).isCurrentClosed()) continue;
            dialog.setVisible(true);
        }
    }

    public void closeAllDialogs
            () {
        for (JDialog dialog : dialogs) {
            dialog.dispose();
        }
    }

    public void setDataLevel
            (String
                    mode) {
        if (!mode.equals(QueryPanel.dataCard))
            showDataA.setState(false);
        if (!mode.equals(QueryPanel.graphCard))
            showGraphA.setState(false);
        if (!mode.equals(QueryPanel.timeCard))
            showTTA.setState(false);

        controller.setDataLevel(mode);
    }

    public class ShowGraphAction extends AbstractSelectableAction {
        public ShowGraphAction(boolean state) {
            super(rb.getString("QueryListController.graphView.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.graphView.toolTip"));
            setState(state);
        }

        public void actionPerformed(ActionEvent e) {
            if (isLockrec())
                return;
            if (getState())
                return;
            setState(true);
            setDataLevel(QueryPanel.graphCard);
            super.actionPerformed(e);
        }
    }

    public class ShowDataAction extends AbstractSelectableAction {
        public ShowDataAction(boolean state) {
            super(rb.getString("QueryListController.dataView.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.dataView.toolTip"));
            setState(state);
        }

        public void actionPerformed(ActionEvent e) {
            if (isLockrec())
                return;
            if (getState())
                return;
            setState(true);
            setDataLevel(QueryPanel.dataCard);
            super.actionPerformed(e);
        }
    }

    public class ShowTTAction extends AbstractSelectableAction {
        public ShowTTAction(boolean state) {
            super(rb.getString("QueryListController.ttView.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.ttView.toolTip"));
            setState(state);
        }

        public void actionPerformed(ActionEvent e) {
            if (isLockrec())
                return;
            if (getState())
                return;
            try {
                AuditManager.getInstance().addOpenTemporaryDiagramEvent(controller.getQuery().getId());
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            setState(true);
            setDataLevel(QueryPanel.timeCard);
            super.actionPerformed(e);
        }
    }

    private class ManagementObjectAction extends AbstractAction {
        public ManagementObjectAction() {
            super(rb.getString("Action.name.object.management"));
            putValue(SHORT_DESCRIPTION, rb.getString("Action.name.object.management"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Query query = controller.getQuery();

                HashMap<Long, java.util.List<PropertyType>> visibleAtt = query.getViewedAttributes();

                LVObject objectProperty = Util.getVisibleFieldsForObject(visibleAtt);
                LVObject linkProperty = Util.getVisibleFieldsForLink(visibleAtt);
                if (visibleAtt == null || visibleAtt.size()==0){
                    for (FieldObject f : linkProperty.getFields()){
                        f.setVisible(false);
                    }
                }

                VisibleAttributesDialogPanel dialog = new VisibleAttributesDialogPanel(controller.getOwner(), objectProperty, linkProperty, query);
                addDialogs(dialog);
                dialog.setVisible(true);
            }
            catch (Exception ex) {
                System.out.println(ex);
                ex.printStackTrace();
                MessageDialogs.generalError(null, ex);
            }
            finally{
                if (e.getSource() instanceof JToggleButton){
                    ((JToggleButton)e.getSource()).setSelected(false);
                }
            }
        }
    }

    public class FilterAction extends AbstractSelectableAction {
        public FilterAction(boolean state) {
            super(rb.getString("Action.name.filter"));
            putValue(SHORT_DESCRIPTION, rb.getString("Action.name.filter"));
            setState(state);
        }

        public void actionPerformed(ActionEvent e) {
            Query query = controller.getQuery();

            HashMap<Long, java.util.List<PropertyType>> visibleAtt = query.getViewedAttributes();

            LVObject objectProperty = Util.getVisibleFieldsForObject(visibleAtt);
            LVObject linkProperty = Util.getVisibleFieldsForLink(visibleAtt);

            FilterPanel filterPanel = new FilterPanel(controller.getOwner(), query, objectProperty, linkProperty);
            FieldObjectContainer fieldObjectContainer = getFieldObjectContainer();
            if (fieldObjectContainer != null)
                filterPanel.setFilterParameters(fieldObjectContainer);
            addDialogs(filterPanel);
            filterPanel.setVisible(true);
            super.actionPerformed(e);
        }
    }

    public FieldObjectContainer getFieldObjectContainer
            () {
        return fieldObjectContainer;
    }

    public void setFieldObjectContainer
            (FieldObjectContainer
                    fieldObjectContainer) {
        this.fieldObjectContainer = fieldObjectContainer;
    }

    public void doFilter
            () {
        controller.doFilter();
    }

    public boolean getFilterButtonSelectedState
            () {
        return filterButton.isSelected();
    }

    public void setFilterButtonSelected
            (
                    boolean flag) {
        filterButton.setSelected(flag);
    }

    public void setVisibleState
            () {
        controller.setVisibleStateForObject();
    }

    public void repaintGraph
            () {
        controller.repaintGraph();
    }

    public boolean isLayoutChanged
            () {
        return layoutChanged && loaded;
    }

    public TimeDiagram getTimeDiagram(){
        return tdpm.getTimeDiagram();
    }

    public TimedDiagrammDataSnapshot getDataSnapshot(){
        return tdpm.getSnapshot();
    }
}
