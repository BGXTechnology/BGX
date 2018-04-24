package net.bgx.bgxnetwork.bgxop.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.tree.DefaultMutableTreeNode;

import net.bgx.bgxnetwork.bgxop.engine.AlgorithmsHolder;
import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.engine.LayoutType;
import net.bgx.bgxnetwork.bgxop.gui.background.OpenQueryThread;
import net.bgx.bgxnetwork.bgxop.gui.lv.request.RequestPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test.TestDataTDModel;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.bgxop.graph.CustomRenderer;
import net.bgx.bgxnetwork.bgxop.graph.CustomParallelEdgeIndexFunction;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import ru.zsoft.jung.viewer.BufferedViewer;
import edu.uci.ics.jung.visualization.Layout;
import oracle.spatial.network.Node;
import org.jboss.util.Base64;

/**
 * Class QueryListController
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class QueryListController extends QueryState {
    private static Logger log = Logger.getLogger(QueryListController.class.getName());
    protected static final Set emptySet = new HashSet();
    private HashMap<Long, QueryListPanel> views = new HashMap<Long, QueryListPanel>();
    private HashMap<QueryPanel, Query> queriesList = new HashMap<QueryPanel, Query>();
    private HashMap<Query, QueryPanel> invQueriesList = new HashMap<Query, QueryPanel>();
    private MainFrame owner;
    private HashMap<Query, LayoutCoordinates> layouts = new HashMap<Query, LayoutCoordinates>();
    private ArrayList<DoLayoutAction> layoutActions = new ArrayList<DoLayoutAction>();
    protected Action closeA, saveLayoutA, zoomIn, zoomOut;
    protected AbstractSelectableAction showNA, showDataA, showGraphA;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

    public QueryListController(MainFrame owner) {
        this.owner = owner;
        layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.Circle"), LayoutType.Circle));
        layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.KK"), LayoutType.KK));
        layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.FR"), LayoutType.FR));
        layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.SOM"), LayoutType.ISOM));
        //layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.Organic"), LayoutType.Spring));
        layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.Static"), LayoutType.Static));
        layoutActions.add(new DoLayoutAction(rb.getString("QueryListController.layout.Custom"), LayoutType.Custom));
        closeA = new CloseTabAction();
        AppAction.getInstance().setOwner(owner);

        //saveLayoutA = new SaveLayoutAction();
        saveLayoutA = AppAction.getInstance().getAction(AppAction.SAVE_ACTION);
        zoomIn = new ZoomInAction();
        zoomOut = new ZoomOutAction();

//        showNA = (AbstractSelectableAction) AppAction.getInstance().getAction(AppAction.SHOW_NEIGHBOUR_ACTION);
        showNA = new ShowNeighboursAction(false);
//        showGraphA = new ShowGraphAction(true);
//        showDataA = new ShowDataAction(false);
        setDataLevel(DataLevel.GraphCard);
        enableControls();
    }

    public QueryListPanel getView(Long id) {
        return views.get(id);
    }

    public MainFrame getOwner() {
        return owner;
    }

    public DoLayoutAction getLayoutAction(LayoutType type) {
        for (DoLayoutAction ac : layoutActions)
            if (ac.getLayout() == type)
                return ac;
        return null;
    }

    public ArrayList<DoLayoutAction> getLayoutActions() {
        return layoutActions;
    }

    public boolean saveAllLayouts() {
        boolean operationStatus = true;
        Set<QueryPanel> panels = getQueriesList();
        for (QueryPanel panel : panels) {
            Query q = panel.getQuery();
            Long parentId = q.getParent();
            RequestPanel requestPanel = owner.getRequestTabbedPane().getRequestListController().getPanelByQuestId(parentId);
            if (requestPanel == null) continue;
            owner.getRequestTabbedPane().setSelectedComponent(requestPanel);
            requestPanel.getQueryListPanel().setSelectedComponent(panel);

            boolean res = MessageDialogs.confirm(owner, rb.getString("QueryListController.saveLayout.confirm2") + "\n" + q.getName() + "?",
                    rb.getString("confirm.title"));
            if (res) {
                ArrayList<ControlObject> objects = new ArrayList<ControlObject>();
                for (Node node : panel.getController().getData().getNetwork().getNodeArray()) {
                    if (node == null) continue;
                    ControlObject co = GraphNetworkUtil.getControlObject(node);
                    if (co == null) continue;
                    objects.add(co);
                }
                try {
                    QueryServiceDelegator.getInstance().updateControlObjects(objects);
                }
                catch (Exception ex) {
                    //todo
                    ex.printStackTrace();
                }
                //save link state
                ArrayList<LinkObject> links = new ArrayList<LinkObject>();
                for (oracle.spatial.network.Link link : panel.getController().getData().getNetwork().getLinkArray()) {
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

                try {
                    TestDataTDModel tdModel = (TestDataTDModel)panel.getTimeDiagram().getModel();
                    LinkedList<TDPair> pairs = new LinkedList<TDPair>();
                    for (int i = 0; i < tdModel.getPairsCount(); i++) {
                        pairs.add(tdModel.getPairAt(i));
                    }
                    QueryServiceDelegator.getInstance().saveTTPairs(q.getId(), pairs);
                }
                catch(Exception e3){
                    e3.printStackTrace();
                }

                try {
                    TimedDiagrammDataSnapshot data = panel.getDataSnapshot();
                    //if (data.getDateFrom() != null && data.getDateTo() != null && data.getPairs()!=null && data.getPairs().size() != 0){
                    //    String strData = serializeObject(data);
                    //    QueryServiceDelegator.getInstance().updateTTParams(panel.getController().getQuery().getId(), strData);
                    //}
//
                    QueryServiceDelegator.getInstance().saveTTParameters(q.getId(), panel.getDataSnapshot());
                }
                catch(Exception e4){
                    e4.printStackTrace();
                }

                saveLayoutByPanel(panel);
            }
            //operationStatus = operationStatus && saveLayoutByPanel(panel);

        }
        return operationStatus;
    }

    private String serializeObject(Object obj){
        String serializeObj = "";
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(obj);
            out.flush();
            out.close();
            serializeObj = Base64.encodeBytes(baos.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
        }
        return serializeObj;
    }

    public boolean saveLayoutByPanel(QueryPanel panel) {
        boolean operationStatus = false;
        if (panel.isLoaded()) {
            LayoutCoordinates coordinates;
            Map<Long, String> groupNames = panel.getGraphController().getGroupNames(panel.getGraph().getGraphLayout().getGraph());
            if (groupNames.size() > 0)
                coordinates = AlgorithmsHolder.storeCoordinates(panel.getGraphController().getOriginalLayout(), groupNames);
            else
                coordinates = AlgorithmsHolder.storeCoordinates(panel.getGraph().getGraphLayout());

            Query query = panel.getQuery();
            try {
                operationStatus = QueryServiceDelegator.getInstance().saveLayout(query.getId(), coordinates);
            } catch (Exception e) {
                MessageDialogs.generalError(owner, e);
            }
            if (operationStatus) {
                layouts.put(query, coordinates);
            }
        }
        return operationStatus;
    }


    public boolean saveCurrentLayout() {
        boolean operationStatus = false;
        QueryPanel panel = getCurrentComponent();
        if (panel != null) {
            operationStatus = saveLayoutByPanel(panel);
        }
        return operationStatus;
    }

    private HashMap<Query, OpenQueryThread> threads = new HashMap<Query, OpenQueryThread>();

    public void openQuery(Query q) {
        WaitDialog dialog = new WaitDialog(owner);
        dialog.setModal(false);

        QueryPanel newQueryPanel = new QueryPanel(false);
        newQueryPanel.setName(q.getName());

        Long questId = q.getParent();
        RequestPanel requestPanel = owner.getRequestTabbedPane().getRequestListController().getPanelByQuestId(questId);
        if (requestPanel == null) {
            DefaultMutableTreeNode dmtn = owner.getProfilePanel().controller.findNodeById(questId);
            Query requestQuery = (Query) (((DefaultMutableTreeNode) dmtn.getParent()).getUserObject());
            requestPanel = new RequestPanel(requestQuery.getId());
            requestPanel.setName(requestQuery.getName() + " - " + ((Query) dmtn.getUserObject()).getName());
            owner.getRequestTabbedPane().getRequestListController().addRequestPanel(questId, requestPanel);
            owner.getRequestTabbedPane().addTab(requestPanel.getName(), requestPanel);
            QueryListPanel queryListPanel = new QueryListPanel(owner.getQueryListController());
            requestPanel.setQueryListPanel(queryListPanel);
            views.put(q.getId(), queryListPanel);
        }
        QueryListPanel queryListPanel = requestPanel.getQueryListPanel();
        views.put(q.getId(), queryListPanel);
        queryListPanel.addTab(newQueryPanel.getName(), newQueryPanel);
        setForegroundByComponent(queryListPanel, newQueryPanel, Color.RED);
        queryListPanel.setSelectedComponent(newQueryPanel);
        queriesList.put(newQueryPanel, q);
        invQueriesList.put(q, newQueryPanel);
        newQueryPanel.addDialogs(dialog);
        OpenQueryThread opener = new OpenQueryThread(dialog, owner, q, layouts.get(q), newQueryPanel, this);
        threads.put(q, opener);
        owner.getRequestTabbedPane().setSelectedComponent(requestPanel);

        opener.start();
        dialog.showDialog();
    }

    public void setForegroundByComponent(QueryListPanel queryListPanel, QueryPanel panel, Color color) {
        int tabIndex = queryListPanel.indexOfComponent(panel);
        if (tabIndex != -1) {
            queryListPanel.setForegroundAt(tabIndex, color);
        }
    }

    public void removeQuery(Query q) {
        OpenQueryThread opener = threads.get(q);
        if (opener != null) {
            opener.interrupt();
            threads.remove(q);
        }
        QueryPanel p = invQueriesList.get(q);
        if (p != null) {
            removeQuery(p);
        }
        removeLayout(q);
    }

    public void removeQuery(QueryPanel p) {
        Query q = queriesList.get(p);
        queriesList.remove(p);
        invQueriesList.remove(q);
        p.closeAllDialogs();
        QueryListPanel view = getView(q.getId());
        if (view == null) return;

        view.remove(p);
        if (view.getSelectedIndex() + 1 != view.getComponentCount()) {
            tabSelected();
        }

        RequestPanel requestPanel = owner.getRequestTabbedPane().getRequestListController().getPanelByQuestId(q.getParent());
        if (view.getTabCount() == 0) {
            owner.getRequestTabbedPane().getRequestListController().removeRequestPanel(q.getParent());
            owner.getRequestTabbedPane().remove(requestPanel);
        }

        owner.enableControls(enableControls());
    }

    public void relayoutGraph(QueryPanel p) {
        LayoutType type = p.getLayoutType();
        Layout layout = AlgorithmsHolder.getInstance().doLayout(queriesList.get(p).getId(), type, p.getData().getGraph());
        p.getGraph().setGraphLayout(layout);
    }

    public void relayoutCurrentGraph() {
        relayoutGraph(getCurrentComponent());
        getCurrentGraph().unlock();
        getCurrentGraph().repaint();
    }


    public void tabSelected() {
        if (queriesList.size() == 0) return;
        Set<QueryPanel> keys = queriesList.keySet();

        // unset selected flag and hide all dialogs for all present query panels
        for (QueryPanel q : keys) {
            if (q.isSelected()) {
                q.setSelected(false);
                q.hideAllDialogs();
            }
        }
        //
        QueryPanel queryPanelComponent = getCurrentComponent();
        //
        if (queryPanelComponent != null) {
            queryPanelComponent.setSelected(true);
            queryPanelComponent.showAllDialogs();

            if (queryPanelComponent.isLoaded()) {
                GraphNetworkMapper queryMapper = queryPanelComponent.getData();

                queryPanelComponent.getGraphModel().selectVertices(emptySet);
                queryPanelComponent.getGraphModel().selectEdges(emptySet);
                owner.setVertexEnableControl(false);
                owner.setEdgeEnableControl(false);

                setStatusBar(queryMapper);

                doLayout(queryPanelComponent.getLayoutType(), queryPanelComponent);
                queryPanelComponent.getGraph().repaint();
            } else {
                clear();
            }
        } else {
            clear();
        }


    }

    public void setStatusBar(GraphNetworkMapper mapper) {
        if (getCurrentComponent() == null) return;
        if (getCurrentComponent().getGraphController() == null) return;
        if (getCurrentComponent().getGraphController().getOriginalLayout() == null) return;
        if (getCurrentComponent().getGraphController().getOriginalLayout().getGraph() == null) return;
        owner.getStatusBar().setObjectCount(getCurrentComponent().getGraphController().getOriginalLayout().getGraph().numVertices());
        owner.getStatusBar().setLinkCount(getCurrentComponent().getGraphController().getOriginalLayout().getGraph().numEdges());
        if (mapper == null) return;
        owner.getStatusBar().setGroups(mapper.getCountGroup());

    }

    private void clear() {
        owner.getStatusBar().setObjectCount(0);
        owner.getStatusBar().setLinkCount(0);
        owner.getStatusBar().setGroups(0);
    }

    public void doLayout(LayoutType type, QueryPanel panel) {
        getLayoutAction(type).actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, ""));
    }

    public void removeLayout(Query q) {
        layouts.remove(q);
    }

    public void setDataLevel(DataLevel showData) {
        super.setDataLevel(showData);
        for (QueryPanel p : queriesList.keySet()) {
            QueryController qctrl = p.getController();
            if (qctrl != null) {
                qctrl.setDataLevel(showData);
            }
        }
    }

    public void setShowNeighbours(boolean show) {
        super.setShowNeighbours(show);
        for (QueryPanel p : queriesList.keySet())
            p.getController().setShowNeighbours(show);
    }

    public Set<QueryPanel> getQueriesList() {
        return queriesList.keySet();
    }

    public HashMap<QueryPanel, Query> getQueryList() {
        return queriesList;
    }

    public QueryPanel getQueryPanel(String name) {
        for (QueryPanel p : queriesList.keySet())
            if (name.equals(p.getName()))
                return p;
        return null;
    }

    public QueryPanel getQueryPanel(Query q) {
        return invQueriesList.get(q);
    }

    public QueryPanel getQueryPanel(long id) {
        for (Query q : queriesList.values())
            if (q.getId() == id)
                return invQueriesList.get(q);
        return null;
    }

    public QueryPanel getCurrentComponent() {
        RequestPanel requestPanel = (RequestPanel) owner.getRequestTabbedPane().getSelectedComponent();
        if (requestPanel == null) return null;
        QueryListPanel view = (QueryListPanel) requestPanel.getQueryListPanel();
        if (view == null) return null;
        return (QueryPanel) view.getSelectedComponent();
    }

    public Query getCurrentQuery() {
        return queriesList.get(getCurrentComponent());
    }

    public GraphNetworkMapper getCurrentData() {
        if (getCurrentComponent() == null)
            return null;
        return getCurrentComponent().getData();
    }

    public BufferedViewer getCurrentGraph() {
        if (getCurrentComponent() == null)
            return null;
        return getCurrentComponent().getGraph();
    }

    public GraphDataModel getCurrentGraphModel() {
        if (getCurrentComponent() == null)
            return null;
        return getCurrentComponent().getGraphModel();
    }

    //todo
    public boolean setCurrentQuery(QueryPanel p) {
        if (queriesList.containsKey(p)) {
            //view.setSelectedComponent(p);
            return true;
        }
        return false;
    }

    public boolean enableControls() {
        boolean res = (queriesList.size() > 0);
        saveLayoutA.setEnabled(res);
        closeA.setEnabled(res);
        zoomIn.setEnabled(res);
        zoomOut.setEnabled(res);
        for (DoLayoutAction ac : layoutActions) {
            ac.setEnabled(res);
        }
        return res;
    }

    public void setForse(LayoutType layoutType, boolean flag) {
        getLayoutAction(layoutType).setForce(flag);
    }

    // ********************* ACTIONS ****************************
    public class SaveLayoutAction extends AbstractAction {
        public SaveLayoutAction() {
            super(rb.getString("QueryListController.saveLayout.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.saveLayout.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            if (getCurrentQuery() != null) {
                boolean res = saveCurrentLayout();
                if (res) {
                    doLayout(LayoutType.Custom, getCurrentComponent());
                    owner.getStatusBar().addMessage(rb.getString("QueryListController.saveLayout.successMsg"));
                } else {
                    MessageDialogs.generalError(owner, null, rb.getString("QueryListController.saveLayout.errorMsg"), rb
                            .getString("error.serverCommunicate"));
                }
            } else {
                // this should never happen
                MessageDialogs.warning(owner, rb.getString("error.noGraphMsg"), rb.getString("error.operationInpossibleTitle"));
            }
        }
    }

    public class CloseTabAction extends AbstractAction {
        public CloseTabAction() {
            super(rb.getString("QueryListController.closeGraph.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.closeGraph.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            Query query = queriesList.get(getCurrentComponent());
            boolean res = MessageDialogs.confirm(owner, rb.getString("QueryListController.saveLayout.confirm"),
                    rb.getString("confirm.title"));
            if (res) {
                saveLayoutA.actionPerformed(null);
            }
            getCurrentComponent().closeAllDialogs();
            removeQuery(getCurrentComponent());
        }
    }

    public class ZoomInAction extends AbstractAction {
        public ZoomInAction() {
            super(rb.getString("QueryListController.zoomIn.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.zoomIn.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            if (getCurrentGraph() != null)
                getCurrentGraph().zoomIn();
        }
    }

    public class ZoomOutAction extends AbstractAction {
        public ZoomOutAction() {
            super(rb.getString("QueryListController.zoomOut.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.zoomOut.toolTip"));
        }

        public void actionPerformed(ActionEvent e) {
            if (getCurrentGraph() != null)
                getCurrentGraph().zoomOut();
        }
    }

    public class ShowNeighboursAction extends AbstractSelectableAction {
        public ShowNeighboursAction(boolean state) {
            super(rb.getString("QueryListController.neighbours.label"));
            putValue(SHORT_DESCRIPTION, rb.getString("QueryListController.neighbours.toolTip"));
            setState(state);
            setShowNeighbours(state);
        }

        public void actionPerformed(ActionEvent e) {
            setState(!getState());
            setShowNeighbours(getState());
            super.actionPerformed(e);
            if (getCurrentGraph() != null)
                getCurrentGraph().repaint();
        }
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
            setDataLevel(DataLevel.GraphCard);
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
            setDataLevel(DataLevel.TableCard);
            super.actionPerformed(e);
        }
    }

    public class DoLayoutAction extends AbstractSelectableAction {
        private LayoutType layoutType;
        private boolean force = false;

        public DoLayoutAction(String name, LayoutType layoutType) {
            super(name);
            this.layoutType = layoutType;
            setState(true);
        }

        public LayoutType getLayout() {
            return layoutType;
        }

        public boolean isForce() {
            return force;
        }

        public void setForce(boolean force) {
            this.force = force;
        }

        public void actionPerformed(ActionEvent e) {

            if (isLockrec()) {
                return;
            }
            QueryPanel targetPanel = getTarget(e);
            BufferedViewer targetViewer = targetPanel.getGraph();

            if (targetViewer == null || layoutType == null) {
                return;
            }

            if (isForce() || targetPanel.getLayoutType() != layoutType) {
                setLayoutGraph(targetPanel, targetViewer, layoutType);
            }

            super.actionPerformed(e);
        }


        private QueryPanel getTarget(ActionEvent event) {
            Object candidate = event.getSource();
            QueryPanel panelComponent = getCurrentComponent();
            if (candidate != null && candidate instanceof QueryPanel) {
                panelComponent = (QueryPanel) candidate;
            }
            return panelComponent;
        }
    }

    private void setLayoutGraph(QueryPanel targetPanel, BufferedViewer targetViewer, LayoutType layoutType) {
        if (layoutType == LayoutType.Custom) {
            LayoutCoordinates layoutCoordinates = layouts.get(queriesList.get(targetPanel));
            if (layoutCoordinates == null) {
                owner.getStatusBar().addMessage(rb.getString("QueryListController.error.noCustomLayout"));
            } else {
                targetViewer.setGraphLayout(AlgorithmsHolder.loadCoordinates(layoutCoordinates, targetPanel.getData().getGraph()));
                targetViewer.unlock();
                targetViewer.repaint();
            }
        } else {
            Layout layout = AlgorithmsHolder.getInstance().doLayout(queriesList.get(targetPanel).getId(), layoutType, targetPanel.getData().getGraph());
            targetViewer.setGraphLayout(layout);
            targetViewer.unlock();
            targetViewer.repaint();
        }
        targetPanel.setLayoutType(layoutType);
    }

    /*
    * public void run(){ // TODO Auto-generated method stub
    *  } private void addDataToPanel(){ if(opener.getLayoutType() != null){
    * System.out.println("TOKA ZAWLI"); if(opener.getLayoutCoordinates() !=
    * null) layouts.put(q, opener.getLayoutCoordinates()); //newQuery =
    * opener.getResult(); newQuery.setLoaded(true); //
    * newQuery.getController().setPickState(getPickState());
    * newQuery.getController().setDataLevel(getDataLevel());
    * newQuery.getController().setShowNeighbours(getShowNeighbours());
    * queriesList.put(newQuery, q); invQueriesList.put(q, newQuery);
    * //view.addTab(newQuery.getName(), newQuery);
    * //view.setSelectedComponent(newQuery); System.out.println("OK - LOAD");
    * owner.getBrowserPanel().getController().setModel(newQuery.getGraphModel());
    * owner.enableControls(enableControls()); doLayout(opener.getLayoutType());
    * int objs =
    * GraphNetworkUtil.getObjectsLimit(newQuery.getData().getNetwork());
    * if(objs != 0){ MessageDialogs.info(owner,
    * rb.getString("QueryListController.info.dataLimited.msg") + " " + objs, rb
    * .getString("QueryListController.info.dataLimited.title")); } } }
    */
    public HashMap<Query, LayoutCoordinates> getLayouts() {
        return layouts;
    }

    public void createGroupVertexes(String nameGroup, ActionEvent e) {
        QueryPanel queryPanel = getCurrentComponent();
        if (queryPanel == null) return;
        try {
            Set picked = new HashSet(getCurrentGraph().getPickedState().getPickedVertices());
            Set vertexs = queryPanel.getGraphController().collapseVertex(getCurrentData(), picked,
                    nameGroup);
            getCurrentData().setFilterIntegrationLink(queryPanel.getController().isFilterIsSet());
            clearParallelIndex();
            getLayoutAction(queryPanel.getLayoutType()).setForce(true);
            getLayoutAction(queryPanel.getLayoutType()).actionPerformed(e);
            if (vertexs != null)
                getCurrentGraphModel().selectVertices(vertexs);
        }
        finally {
            getLayoutAction(queryPanel.getLayoutType()).setForce(false);
            queryPanel.getController().setVisibleStateForObject();
            queryPanel.getController().doFilter();
            setStatusBar(getCurrentData());
            owner.setVertexEnableControl((getCurrentGraph().getPickedState().getPickedVertices()).size() > 0);
            queryPanel.getController().repaintGraph();
        }
    }


    public void removeGroupVertexes(ActionEvent e) {
        QueryPanel queryPanel = getCurrentComponent();
        if (queryPanel == null) return;
        try {
            Set picked = new HashSet(getCurrentGraph().
                    getPickedState().getPickedVertices());
            Set vertexs = queryPanel.getGraphController().expandVertex(getCurrentData(), picked);
            getCurrentData().setFilterIntegrationLink(queryPanel.getController().isFilterIsSet());
            clearParallelIndex();

            getLayoutAction(queryPanel.getLayoutType()).setForce(true);
            getLayoutAction(queryPanel.getLayoutType()).actionPerformed(e);
            if (vertexs != null)
                getCurrentGraphModel().selectVertices(vertexs);
        }
        finally {
            getLayoutAction(queryPanel.getLayoutType()).setForce(false);
            setStatusBar(getCurrentData());
            owner.setVertexEnableControl(true);
            queryPanel.getController().setVisibleStateForObject();
            queryPanel.getController().doFilter();
            queryPanel.getController().repaintGraph();
        }
    }

    public void clearParallelIndex() {
        BufferedViewer bufferedViewer = getCurrentGraph();
        if (bufferedViewer == null) return;
        ((CustomParallelEdgeIndexFunction) ((CustomRenderer) bufferedViewer.getRenderer()).getParallelEdgeIndexFunction()).reset();
    }
}
