/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;
import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.engine.LayoutType;
import net.bgx.bgxnetwork.bgxop.gui.background.RemoveEdgesThread;
import net.bgx.bgxnetwork.bgxop.gui.background.RemoveVerticesThread;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.MultiPageScreenshotPrinter;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.NodeGroupDialog;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.ScreenshotWriter;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.*;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.object.FieldObjectContainer;
import net.bgx.bgxnetwork.bgxop.gui.lv.request.RequestPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TDModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test.TestDataTDModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TimeDiagram;
import net.bgx.bgxnetwork.bgxop.services.PresentationServiceDelegator;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.bgxop.services.audit.AuditManager;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.persistence.metadata.*;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import net.bgx.bgxnetwork.transfer.tt.TransferControlObjectPair;
import oracle.spatial.network.Node;
import ru.zsoft.jung.viewer.BufferedViewer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import org.jboss.util.Base64;

/**
 * User: A.Borisenko
 * Date: 13.06.2007
 * Time: 9:54:06
 */
public class AppAction {
    public static final int CREATE_ACTION = 1;
    public static final int OPEN_ACTION = 2;
    public static final int EDIT_ACTION = 3;
    public static final int COPY_ACTION = 4;
    public static final int REMOVE_ACTION = 5;
    public static final int CLOSE_ACTION = 6;
    public static final int SAVE_ACTION = 7;
    public static final int SAVE_IMG_ACTION = 8;
    public static final int PAGE_PROPERTY_ACTION = 9;
    public static final int PRINT_ACTION = 10;
    public static final int EXIT_ACTION = 11;
    public static final int CREATE_GROUP_ACTION = 12;
    public static final int REMOVE_GROUP_ACTION = 13;
    public static final int REMOVE_OBJECT_ACTION = 14;
    public static final int FILTER_ACTION = 15;
    public static final int CARD_OBJECT_ACTION = 16;
    public static final int CARD_LINK_ACTION = 17;
    public static final int MANEGEMENT_ATR_ACTION = 18;
    public static final int CARD_INTEGRATEDLINK_ACTION = 20;
    public static final int TABLE_VIEW_ACTION = 21;
    public static final int GRAPH_VIEW_ACTION = 22;
    public static final int TIMETABLE_VIEW_ACTION = 23;
    public static final int SHOW_NEIGHBOUR_ACTION = 24;
    public static final int SAVE_LAYOUT_ACTION = 25;
    public static final int CIRCLE_LAYOUT_ACTION = 26;
    public static final int KK_LAYOUT_ACTION = 27;
    public static final int FR_LAYOUT_ACTION = 28;
    public static final int AUTO_LAYOUT_ACTION = 29;
    public static final int STATIC_LAYOUT_ACTION = 30;
    public static final int CUSTOM_LAYOUT_ACTION = 31;
    public static final int ABOUT_ACTION = 32;
    public static final int INSTRUCTION_ACTION = 33;
    public static final int SHOW_INVISIBLE_OBJECT_ACTION = 34;
    public static final int REMOVE_LINK_ACTION = 41;
    public static final int SET_INVISIBLE_OBJECT_ACTION = 42;
    public static final int CLOSE_REQUEST_TAB = 43;
    public static final int OPEN_NODE_GRP_ACTION = 44;
    public static final int SAVE_PAIRS_ACTION = 45;
    public static final int LOAD_PAIRS_ACTION = 46;
    public static final int SAVE_TT_PARAMETERS_ACTION = 47;
    public static final int LOAD_TT_PARAMETERS_ACTION = 48;


    protected ResourceBundle gui_resource = PropertyResourceBundle.getBundle("gui");
    protected ResourceBundle gui_dialog_resource = PropertyResourceBundle.getBundle("gui_dialogs");
    private MainFrame mainFrame;
    private ProfilePanelController profilePanelController;
    private QueryListController queryListController;

    private static PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();

    private static AppAction ourInstance = new AppAction();

    private AppAction() {

    }

    public static AppAction getInstance() {
        return ourInstance;
    }

    /*Instance of Actions*/
    //file
    private Action createAction, openAction, editAction, copyAction, removeAction, closeAction, saveAction, saveImageAction, pagePropertyAction, printAction, exitAction = null;
    //edit
    private Action createGrpAction, removeGrpAction, removeObjectAction, showInvObjectAction, filterAction = null;
    //parameters
    private Action cardObjectAction, cardLinkAction, manegementAtrAction, cardIntegrateLinkAction, removeLinkAction = null;
    //view
    private Action tableViewAction, neighbourAction, graphViewAction = null;
    //layout
    private Action saveLayoutAction, circleLayoutAction, kkLayoutAction, frLayoutAction, autoLayoutAction, staticLayoutAction, customLayoutAction = null;
    //about
    private Action aboutAction, instructionAction = null;
    //??? TimeTableView
    private Action ttViewAction = null;

    private Action invisibleAction = null;

    private Action closeRequestAction = null;
    private Action openNodeGrpAction = null;

    public void setOwner(MainFrame owner) {
        this.mainFrame = owner;
    }

    public void setProfilePanelController(ProfilePanelController ppc) {
        this.profilePanelController = ppc;
    }

    public void setQueryListController(QueryListController queryListController) {
        this.queryListController = queryListController;
    }

    public Action getAction(int actionCode) {
        switch (actionCode) {
            case CREATE_ACTION:
                if (createAction == null)
                    createAction = new CreateAction();
                return createAction;
            case OPEN_ACTION:
                if (openAction == null)
                    openAction = new OpenAction();
                return openAction;
            case EDIT_ACTION:
                if (editAction == null)
                    editAction = new EditAction();
                return editAction;
            case COPY_ACTION:
                if (copyAction == null)
                    copyAction = new CopyAction();
                return copyAction;
            case REMOVE_ACTION:
                if (removeAction == null)
                    removeAction = new RemoveAction();
                return removeAction;
            case CLOSE_ACTION:
                if (closeAction == null)
                    closeAction = new CloseAction();
                return closeAction;
            case SAVE_ACTION:
                if (saveAction == null)
                    saveAction = new SaveAction();
                return saveAction;
            case SAVE_IMG_ACTION:
                if (saveImageAction == null)
                    saveImageAction = new SaveImageAction();
                return saveImageAction;
            case PAGE_PROPERTY_ACTION:
                if (pagePropertyAction == null)
                    pagePropertyAction = new PagePropertyAction();
                return pagePropertyAction;
            case PRINT_ACTION:
                if (printAction == null)
                    printAction = new PrintAction();
                return printAction;
            case EXIT_ACTION:
                if (exitAction == null)
                    exitAction = new ExitAction();
                return exitAction;
            case CREATE_GROUP_ACTION:
                if (createGrpAction == null)
                    createGrpAction = new CreateGroupAction();
                return createGrpAction;
            case REMOVE_GROUP_ACTION:
                if (removeGrpAction == null)
                    removeGrpAction = new RemoveGroupAction();
                return removeGrpAction;
            case REMOVE_OBJECT_ACTION:
                if (removeObjectAction == null)
                    removeObjectAction = new RemoveObjectAction();
                return removeObjectAction;
            case REMOVE_LINK_ACTION:
                if (removeLinkAction == null)
                    removeLinkAction = new RemoveLinkAction();
                return removeObjectAction;
            case SHOW_INVISIBLE_OBJECT_ACTION:
                if (showInvObjectAction == null)
                    showInvObjectAction = new ShowObjectsAction();
                return showInvObjectAction;
            case FILTER_ACTION:
                if (filterAction == null)
                    filterAction = new FilterAction();
                return filterAction;
            case CARD_OBJECT_ACTION:
                if (cardObjectAction == null)
                    cardObjectAction = new OpenObjectCardAction();
                return cardObjectAction;
            case CARD_LINK_ACTION:
                if (cardLinkAction == null)
                    cardLinkAction = new OpenLinkCardAction();
                return cardLinkAction;
            case MANEGEMENT_ATR_ACTION:
                if (manegementAtrAction == null)
                    manegementAtrAction = new ManagementObjectAction();
                return manegementAtrAction;
            case CARD_INTEGRATEDLINK_ACTION:
                if (cardIntegrateLinkAction == null)
                    cardIntegrateLinkAction = new OpenIntegratedLinkAction();
                return cardIntegrateLinkAction;
            case TABLE_VIEW_ACTION:
                if (tableViewAction == null)
                    tableViewAction = new TableViewAction();
                return tableViewAction;
            case GRAPH_VIEW_ACTION:
                if (graphViewAction == null)
                    graphViewAction = new GraphViewAction();
                return graphViewAction;
            case TIMETABLE_VIEW_ACTION:
                if (ttViewAction == null)
                    ttViewAction = new TTViewAction();
                return ttViewAction;
            case SHOW_NEIGHBOUR_ACTION:
                if (neighbourAction == null)
                    neighbourAction = new ShowNeighbourAction();
                return neighbourAction;
            case SAVE_LAYOUT_ACTION:
                if (saveLayoutAction == null)
                    saveLayoutAction = new SaveLayoutAction();
                return saveLayoutAction;
            case CIRCLE_LAYOUT_ACTION:
                if (circleLayoutAction == null)
                    circleLayoutAction = new CircleLayoutAction();
                return circleLayoutAction;
            case KK_LAYOUT_ACTION:
                if (kkLayoutAction == null)
                    kkLayoutAction = new KKLayoutAction();
                return kkLayoutAction;
            case FR_LAYOUT_ACTION:
                if (frLayoutAction == null)
                    frLayoutAction = new FRLayoutAction();
                return frLayoutAction;
            case AUTO_LAYOUT_ACTION:
                if (autoLayoutAction == null)
                    autoLayoutAction = new AutoLayoutAction();
                return autoLayoutAction;
            case STATIC_LAYOUT_ACTION:
                if (staticLayoutAction == null)
                    staticLayoutAction = new StaticLayoutAction();
                return staticLayoutAction;
            case CUSTOM_LAYOUT_ACTION:
                if (customLayoutAction == null)
                    customLayoutAction = new CustomLayoutAction();
                return customLayoutAction;
            case ABOUT_ACTION:
                if (aboutAction == null)
                    aboutAction = new AboutAction();
                return aboutAction;
            case INSTRUCTION_ACTION:
                if (instructionAction == null)
                    instructionAction = new InstructionAction();
                return instructionAction;
            case SET_INVISIBLE_OBJECT_ACTION:
                if (invisibleAction == null)
                    invisibleAction = new InvisibleAction();
                return invisibleAction;
            case CLOSE_REQUEST_TAB:
                if (closeRequestAction == null)
                    closeRequestAction = new CloseRequestAction();
                return closeRequestAction;
            case OPEN_NODE_GRP_ACTION:
                if (openNodeGrpAction == null)
                    openNodeGrpAction = new OpenNodeGroupAction();
                return openNodeGrpAction;
            case SAVE_PAIRS_ACTION:
                return new TestAction7();
            case LOAD_PAIRS_ACTION:
                return new TestAction8();
            case SAVE_TT_PARAMETERS_ACTION:
                return new TestAction9();
            case LOAD_TT_PARAMETERS_ACTION:
                return new TestAction10();
        }
        return null;
    }

    private class CreateAction extends AbstractAction {
        public CreateAction() {
            super(gui_resource.getString("Action.name.create"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.create"));
        }

        public void actionPerformed(ActionEvent e) {
            profilePanelController.createQueryPanel();
        }
    }

    private class OpenAction extends AbstractAction {
        public OpenAction() {
            super(gui_resource.getString("Action.name.open"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.open"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = profilePanelController.getView().findSelectedQuery();
            if (q != null) profilePanelController.openQuery(q);
        }
    }

    private class EditAction extends AbstractAction {
        public EditAction() {
            super(gui_resource.getString("Action.name.edit"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.edit"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = profilePanelController.getView().findSelectedQuery();
            if (q != null) profilePanelController.updateQuery(q);
        }
    }

    private class CopyAction extends AbstractAction {
        public CopyAction() {
            super(gui_resource.getString("Action.name.copy"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.copy"));
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getProfilePanel().findSelectedQuery();
            DefaultMutableTreeNode dmtn = mainFrame.getProfilePanel().getController().findNodeById(query.getId());
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) dmtn.getParent();
            ResourceBundle rb = PropertyResourceBundle.getBundle("gui_query");
            String newName = mainFrame.getProfilePanel().getController().getChildNodeName(parentNode, rb.getString("Visualization.default.name"));
            try {
                Query newQuery = new Query();
                newQuery.setName(newName);
                newQuery.setId(query.getId());
                newQuery.setParameters(query.getParameters());
                newQuery.setParent(query.getParent());
                newQuery.setQueryType(query.getQueryType());
                Long newqID = QueryServiceDelegator.getInstance().copyQueryFromExist(newQuery);
                newQuery.setId(newqID);
                mainFrame.getProfilePanel().addNodeAsChild(parentNode, newQuery);
                mainFrame.getProfilePanel().getController().refresh();
            }
            catch (Exception exc) {
                MessageDialogs.generalError((Frame) null, exc, gui_resource.getString("Action.exception.name"), gui_resource.getString("Action.exception.title"));
            }
        }
    }

    private class RemoveAction extends AbstractAction {
        public RemoveAction() {
            super(gui_resource.getString("Action.name.remove"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.remove"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = profilePanelController.getView().findSelectedQuery();
            if (q != null) {
                if (profilePanelController.removeQuery(q, false)) {
                    profilePanelController.getView().clearQueryInfo();
                }
            }
        }
    }

    private class CloseAction extends AbstractAction {
        public CloseAction() {
            super(gui_resource.getString("Action.name.close"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.close"));
        }

        public void actionPerformed(ActionEvent e) {
            ActionEvent ae = new ActionEvent(mainFrame, 0, gui_resource.getString("QueryListController.dataView.label"));
            mainFrame.getQueryListController().closeA.actionPerformed(ae);
        }
    }

    private class SaveAction extends AbstractAction {
        public SaveAction() {
            super(gui_resource.getString("Action.name.save"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.save"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = mainFrame.getQueryListController().getCurrentQuery();
            if (q == null) return;
            QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(q);
            if (queryPanel == null) return;
            //save node state
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
            catch (Exception ex) {
                //todo
                ex.printStackTrace();
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

            try {
                TestDataTDModel tdModel = (TestDataTDModel) queryPanel.getTimeDiagram().getModel();
                LinkedList<TDPair> pairs = new LinkedList<TDPair>();
                for (int i = 0; i < tdModel.getPairsCount(); i++) {
                    pairs.add(tdModel.getPairAt(i));
                }
                QueryServiceDelegator.getInstance().saveTTPairs(queryPanel.getController().getQuery().getId(), pairs);
            }
            catch (Exception e3) {
                e3.printStackTrace();
            }

            try {
                TimedDiagrammDataSnapshot data = queryPanel.getDataSnapshot();
/*
                //if (data.getDateFrom() != null && data.getDateTo() != null && data.getPairs()!=null && data.getPairs().size() != 0){
                String strData = serializeObject(data);
                QueryServiceDelegator.getInstance().updateTTParams(queryPanel.getController().getQuery().getId(), strData);
                //}
*/
                QueryServiceDelegator.getInstance().saveTTParameters(queryPanel.getController().getQuery().getId(), data);
            }
            catch (Exception e4) {
                e4.printStackTrace();
            }

            mainFrame.getQueryListController().saveLayoutByPanel(queryPanel);
            MessageDialogs.info(mainFrame, gui_resource.getString("QueryListController.saveLayout.successMsg"), gui_resource.getString("QueryListController.saveLayout.title"));
            mainFrame.getStatusBar().addMessage(gui_resource.getString("QueryListController.saveLayout.successMsg"));
        }
    }

    private String serializeObject(Object obj) {
        String serializeObj = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(obj);
            out.flush();
            out.close();
            serializeObj = Base64.encodeBytes(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serializeObj;
    }

    private class SaveImageAction extends AbstractAction {
        public SaveImageAction() {
            super(gui_resource.getString("Action.name.saveImg"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.saveImg"));
        }

        public void actionPerformed(ActionEvent e) {
            if (mainFrame.getQueryListController().getCurrentComponent() == null) return;
            try {
                try {
                    String queryName = mainFrame.getQueryListController().getCurrentComponent().getName();
                    AuditManager auditManager = new AuditManager();
                    auditManager.addSaveImageEvent(mainFrame.getQueryListController().getCurrentComponent().getQuery().getId());
                }
                catch (Exception ex) {
                    MessageDialogs.generalError(mainFrame, ex);
                }
                ScreenshotWriter writer = new ScreenshotWriter(mainFrame, mainFrame.currentDir);
                QueryPanel qp = mainFrame.getQueryListController().getCurrentComponent();
                DataLevel dataLevel = qp.getController().getDataLevel();

                if (dataLevel.getValue() == DataLevel.GraphCard.getValue()) {
                    try {
                        BufferedImage image = mainFrame.getQueryListController().getCurrentGraph().getScreenshot();
                        if (image == null)
                            MessageDialogs.warning(mainFrame, gui_resource.getString("MainFrame.saveImage.noImage"), null);
                        else {
                            writer.saveScreenshot(image, mainFrame.getQueryListController().getCurrentComponent().getName());
                            mainFrame.currentDir = writer.getCurrentDir();
                        }
                    } catch (IOException exeption) {
                        MessageDialogs.generalError(mainFrame, exeption);
                    }
                } else if (dataLevel.getValue() == DataLevel.TimeCard.getValue()) {
                    TimeDiagram timediagram = qp.getTimeDiagram();
                    writer.saveScreenshot(timediagram.getImage(), mainFrame.getQueryListController().getCurrentComponent().getName());
                }
            }
            catch (Exception ex) {
                MessageDialogs.generalError(mainFrame, ex);
            }


        }
    }

    private class PagePropertyAction extends AbstractAction {
        public PagePropertyAction() {
            super(gui_resource.getString("Action.name.pageProperty"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.pageProperty"));
        }

        public void actionPerformed(ActionEvent e) {
            if (pageFormat == null)
                pageFormat = PrinterJob.getPrinterJob().defaultPage();
            pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
        }
    }

    private class PrintAction extends AbstractAction {
        public PrintAction() {
            super(gui_resource.getString("Action.name.print"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.print"));
        }

        public void actionPerformed(ActionEvent e) {
            if (mainFrame.getQueryListController().getCurrentComponent() == null) return;
            ArrayList<String> roles = new ArrayList<String>();
            roles.add("QueryResultPrinting");
//            try {
//                if (!ActorInfo.getInstance().permitRulles(roles)) {
//                    throw new QueryBusinesException("Permit no denide");
//                }
            try {
                String queryName = mainFrame.getQueryListController().getCurrentComponent().getName();
                AuditManager auditManager = new AuditManager();
                auditManager.addPrintEvent(mainFrame.getQueryListController().getCurrentComponent().getQuery().getId());
            } catch (Exception exeption) {
                MessageDialogs.generalError(mainFrame, exeption);
            }
            MultiPageScreenshotPrinter p = new MultiPageScreenshotPrinter(mainFrame, gui_resource.getString("MainFrame.print.label"));

            Query q = mainFrame.getQueryListController().getCurrentQuery();
            QueryPanel qp = mainFrame.getQueryListController().getCurrentComponent();

            //check view graph or timetable
            DataLevel dataLevel = qp.getController().getDataLevel();
            if (dataLevel.getValue() == DataLevel.GraphCard.getValue()) {
                BufferedViewer view = mainFrame.getQueryListController().getQueryPanel(q).getGraph();
                BufferedImage image = view.getScreenshot();
                if (image == null)
                    MessageDialogs.warning(mainFrame, gui_resource.getString("MainFrame.print.noImage"), null);
                else {
                    p.showPrintDialog(image, pageFormat);
                    pageFormat = p.getPageFormat();
                }
            } else if (dataLevel.getValue() == DataLevel.TimeCard.getValue()) {
                TimeDiagram tdm = qp.getTimeDiagram();
                p.showPrintDialog(tdm.getImage(), pageFormat);
            }
//            }
//            catch (QueryBusinesException ex) {
//                String note = gui_resource.getString("MainFrame.print.permissionLogNote");
//                permissionDeniedInLog(note);
//                MessageDialogs.warning(mainFrame, gui_resource.getString("MainFrame.print.noPermission"), null);
//            }
        }
    }

    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super(gui_resource.getString("Action.name.exit"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.exit"));
        }

        public void actionPerformed(ActionEvent e) {
            //implementation in MainFrame
        }
    }

    private class CreateGroupAction extends AbstractAction {
        public CreateGroupAction() {
            super(gui_resource.getString("Action.name.createGroup"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.createGroup"));
        }

        public void actionPerformed(ActionEvent e) {
            mainFrame.getQueryListController().createGroupVertexes(((JTextField) e.getSource()).getText(), e);
        }

    }

    private class RemoveGroupAction extends AbstractAction {
        public RemoveGroupAction() {
            super(gui_resource.getString("Action.name.removeGroup"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.removeGroup"));
        }

        public void actionPerformed(ActionEvent e) {
            mainFrame.getQueryListController().removeGroupVertexes(e);
        }
    }

    private class RemoveObjectAction extends AbstractAction {
        public RemoveObjectAction() {
            super(gui_resource.getString("Action.name.removeObject"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.removeObject"));
        }

        public void actionPerformed(ActionEvent e) {
            String messageText = gui_dialog_resource.getString("RemoveObject.confirm");
            boolean res = MessageDialogs.confirm(mainFrame, messageText, gui_resource.getString("confirm.title"));
            if (!res) return;
            Query q = mainFrame.getQueryListController().getCurrentQuery();
            Graph g = mainFrame.getQueryListController().getCurrentData().getGraph();
            if (q == null) return;
            if (g == null) return;
            Vertex currentVertex = mainFrame.getCurrentSelectedVertex();
            if (currentVertex == null) return;
            Set<Edge> edges = currentVertex.getIncidentEdges();
            WaitDialog wait = new WaitDialog(mainFrame);
            RemoveVerticesThread th = new RemoveVerticesThread(wait, q, currentVertex, edges);
            th.start();
            wait.showDialog();
            if (th.getException() == null) {
                BufferedViewer view = mainFrame.getQueryListController().getCurrentGraph();
                mainFrame.getQueryListController().getCurrentComponent().getGraphController().removeVertex(currentVertex);
                view.unlock();
                view.repaint();
                mainFrame.getQueryListController().setStatusBar(mainFrame.getQueryListController().getCurrentData());
            }

        }
    }

    private class RemoveLinkAction extends AbstractAction {
        public RemoveLinkAction() {
            super(gui_resource.getString("Action.name.removeLink"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.removeLink"));

        }

        public void actionPerformed(ActionEvent e) {
            Query q = mainFrame.getQueryListController().getCurrentQuery();
            Graph g = mainFrame.getQueryListController().getCurrentData().getGraph();
            if (q == null) return;
            if (g == null) return;
            if (mainFrame.getCurrentSelectedEdge() == null) return;
            Object[] arrEdges = mainFrame.getQueryListController().getCurrentComponent().getGraphController().getSelectedEdges();
            Set<Edge> edges = new HashSet<Edge>();
            if (arrEdges != null) {
                edges.addAll(Arrays.asList((Edge[]) arrEdges));
            } else
                edges.add(mainFrame.getCurrentSelectedEdge());
            WaitDialog wait = new WaitDialog(mainFrame);
            RemoveEdgesThread th = new RemoveEdgesThread(wait, q, edges);
            th.start();
            wait.showDialog();
            if (th.getException() == null) {
                BufferedViewer view = mainFrame.getQueryListController().getCurrentGraph();
                for (Edge edge : edges) {
                    mainFrame.getQueryListController().getCurrentComponent().getGraphController().removeEdge
                            (edge);

                }
                view.unlock();
                view.repaint();
                mainFrame.getQueryListController().setStatusBar(mainFrame.getQueryListController().getCurrentData());
            }
        }
    }

    private class ShowObjectsAction extends AbstractAction {
        public ShowObjectsAction() {
            super(gui_resource.getString("Action.name.showObjects"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.showObjects"));
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<ControlObject> controlObjects = new ArrayList<ControlObject>();
            Query query = mainFrame.getQueryListController().getCurrentQuery();
            if (query == null) return;
            Long qId = query.getId();
            QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(query);

            GraphNetworkMapper graphNetworkMapper = mainFrame.getQueryListController().getCurrentData();
            for (Node node : graphNetworkMapper.getNetwork().getNodeArray()) {
                controlObjects.add(GraphNetworkUtil.getControlObject(node));
            }
            HiddenObjectDialogPanel hiddenObjectDialogPanel =
                    new HiddenObjectDialogPanel(mainFrame, controlObjects, qId);
            queryPanel.addDialogs(hiddenObjectDialogPanel);
            hiddenObjectDialogPanel.setVisible(true);
        }
    }

    private class FilterAction extends AbstractAction {
        public FilterAction() {
            super(gui_resource.getString("Action.name.filter"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.filter"));
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getQueryListController().getCurrentQuery();
            if (query == null) return;
            QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(query);

            HashMap<Long, java.util.List<PropertyType>> visibleAtt = query.getViewedAttributes();

            LVObject objectProperty = Util.getVisibleFieldsForObject(visibleAtt);
            LVObject linkProperty = Util.getVisibleFieldsForLink(visibleAtt);

            FilterPanel filterPanel = new FilterPanel(mainFrame, query, objectProperty, linkProperty);
            FieldObjectContainer fieldObjectContainer = mainFrame.getQueryListController().getQueryPanel(query).getFieldObjectContainer();
            if (fieldObjectContainer != null)
                filterPanel.setFilterParameters(fieldObjectContainer);

            queryPanel.addDialogs(filterPanel);
            filterPanel.setVisible(true);
        }
    }

    private class OpenObjectCardAction extends AbstractAction {
        public OpenObjectCardAction() {
            super(gui_resource.getString("Action.name.card.object"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.card.object"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = mainFrame.getQueryListController().getCurrentQuery();
            if (q == null) return;
            if (mainFrame.getCurrentSelectedVertex() == null) return;
            QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(q);
            try {
                Vertex v = (Vertex) mainFrame.getCurrentSelectedVertex();
                if (v == null) return;
                if (!GraphDataUtil.getVisible(v)) return;
                if (GraphNetworkUtil.getCard(v) == null) {
                    ControlObject controlObject = GraphNetworkUtil.getControlObject(v);
                    ObjectCardDialogPanel objectCardDialogPanel = new ObjectCardDialogPanel(mainFrame, controlObject);
                    GraphNetworkUtil.setCard(v, objectCardDialogPanel);
                    queryPanel.addDialogs(objectCardDialogPanel);
                    objectCardDialogPanel.setVisible(true);
                } else {
                    JDialog dlg = ((JDialog) GraphNetworkUtil.getCard(v));
                    queryPanel.addDialogs(dlg);
                    dlg.setVisible(true);
                }
                AuditManager.getInstance().addOpenCardObjectEvent(mainFrame.getQueryListController().getCurrentQuery().getId(),
                        GraphNetworkUtil.getName(mainFrame.getCurrentSelectedVertex()));
            } catch (Throwable ex) {
                MessageDialogs.generalError(mainFrame, ex, gui_resource.getString("error.cardFetch"),
                        gui_resource.getString("error.commonTitle"));
            }
        }
    }

    private class OpenLinkCardAction extends AbstractAction {
        public OpenLinkCardAction() {
            super(gui_resource.getString("Action.name.card.link"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.card.link"));
        }

        public void actionPerformed(ActionEvent e) {
            Date date = new Date(System.currentTimeMillis());
            System.out.println("Start open card for link at: "+date);

            Query q = mainFrame.getQueryListController().getCurrentQuery();
            if (q == null) return;
            if (mainFrame.getCurrentSelectedEdge() == null) return;
            QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(q);
            try {
                date = new Date(System.currentTimeMillis());
                System.out.println("evaluate edges at: "+date);
                Edge edge = mainFrame.getCurrentSelectedEdge();
                if (edge == null) return;
                if (!GraphDataUtil.getVisible(edge)) return;
                if (GraphNetworkUtil.getCard(edge) == null) {
                    LinkObject linkObject = GraphNetworkUtil.getLinkObject(edge);
                    CardLinkDialogPanel cardLinkDialogPanel = new CardLinkDialogPanel(mainFrame, linkObject);
                    GraphNetworkUtil.setCard(edge, cardLinkDialogPanel);
                    queryPanel.addDialogs(cardLinkDialogPanel);
                    cardLinkDialogPanel.setVisible(true);
                    date = new Date(System.currentTimeMillis());
                    System.out.println("show card for link at: "+date);
                }
                else {
                    JDialog dlg = ((JDialog) GraphNetworkUtil.getCard(edge));
                    queryPanel.addDialogs(dlg);
                    ((JDialog) GraphNetworkUtil.getCard(edge)).setVisible(true);
                    date = new Date(System.currentTimeMillis());
                    System.out.println("show card for link at: "+date);
                }
                date = new Date(System.currentTimeMillis());
                System.out.println("card created, sending audit message at: "+date);

                LinkWorker worker = new LinkWorker(GraphNetworkUtil.getLinkObject(mainFrame.getCurrentSelectedEdge()));
                AuditManager.getInstance().addOpenCardLinkEvent(mainFrame.getQueryListController().getCurrentQuery().getId(),
                        GraphNetworkUtil.getLinkObject(mainFrame.getCurrentSelectedEdge()).getLinkType().getNameObjectType());
            }
            catch (Throwable ex) {
                MessageDialogs.generalError(mainFrame, ex,
                        gui_resource.getString("error.cardFetch"),
                        gui_resource.getString("error.commonTitle"));
            }
        }

    }

    private class ManagementObjectAction extends AbstractAction {
        public ManagementObjectAction() {
            super(gui_resource.getString("Action.name.object.management"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.object.management"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Query query = mainFrame.getQueryListController().getCurrentQuery();
                if (query == null) return;
                QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(query);

                HashMap<Long, java.util.List<PropertyType>> visibleAtt = query.getViewedAttributes();

                LVObject objectProperty = Util.getVisibleFieldsForObject(visibleAtt);
                LVObject linkProperty = Util.getVisibleFieldsForLink(visibleAtt);

                VisibleAttributesDialogPanel dialog = new VisibleAttributesDialogPanel(mainFrame, objectProperty, linkProperty, query);
                queryPanel.addDialogs(dialog);
                dialog.setVisible(true);
            }
            catch (Exception ex) {
                MessageDialogs.generalError(null, ex);
            }
        }
    }

    private class OpenIntegratedLinkAction extends AbstractAction {
        public OpenIntegratedLinkAction() {
            super(gui_resource.getString("Action.name.card.integratedlink"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.card.integratedlink"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Query query = mainFrame.getQueryListController().getCurrentQuery();
                if (query == null) return;
                QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(query);

                Edge edge = mainFrame.getCurrentSelectedEdge();
                if (edge != null) {
                    Pair pair = mainFrame.getCurrentSelectedEdge().getEndpoints();
                    Vertex first = (Vertex) pair.getFirst();
                    Vertex second = (Vertex) pair.getSecond();
                    IntegrateLinkDialogPanel dialog = new IntegrateLinkDialogPanel(mainFrame,
                            mainFrame.getQueryListController().getCurrentData().getParallelEdge(mainFrame.getCurrentSelectedEdge()),
                            " " + LVGraphNetworkUtil.getName(first, false),
                            " " + LVGraphNetworkUtil.getName(second, false),
                            query.getId());
                    queryPanel.addDialogs(dialog);
                    dialog.setVisible(true);
                    AuditManager.getInstance().addOpenCardIntegrationLinkEvent(mainFrame.getQueryListController().getCurrentQuery().getId(),
                            "AC-" + GraphNetworkUtil.getCountVisibleIntegrationLink(
                                    mainFrame.getCurrentSelectedEdge()));

                }
            } catch (Throwable ex) {
                MessageDialogs.generalError(mainFrame, ex,
                        gui_resource.getString("error.cardFetch"),
                        gui_resource.getString("error.commonTitle"));
            }
        }
    }

    private class TableViewAction extends AbstractAction {
        public TableViewAction() {
            super(gui_resource.getString("Action.name.view.table"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.view.table"));
        }

        public void actionPerformed(ActionEvent e) {
            ActionEvent ae = new ActionEvent(mainFrame, 0, gui_resource.getString("QueryListController.dataView.label"));
            mainFrame.getQueryListController().getCurrentComponent().showDataA.actionPerformed(ae);
        }
    }

    private class GraphViewAction extends AbstractAction {
        public GraphViewAction() {
            super(gui_resource.getString("Action.name.view.graph"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.view.graph"));
        }

        public void actionPerformed(ActionEvent e) {
            ActionEvent ae = new ActionEvent(mainFrame, 0, gui_resource.getString("QueryListController.graphView.label"));
            mainFrame.getQueryListController().getCurrentComponent().showGraphA.actionPerformed(ae);
        }
    }

    private class TTViewAction extends AbstractAction {
        public TTViewAction() {
            super(gui_resource.getString("Action.name.view.timetable"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.view.timetable"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                ActionEvent ae = new ActionEvent(mainFrame, 0, gui_resource.getString("QueryListController.ttView.label"));
                mainFrame.getQueryListController().getCurrentComponent().showTTA.actionPerformed(ae);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ShowNeighbourAction extends AbstractSelectableAction {
        public ShowNeighbourAction() {
            super(gui_resource.getString("Action.name.show.neighbour"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.show.neighbour"));
            setState(false);
            mainFrame.getQueryListController().setShowNeighbours(false);
        }

        public void actionPerformed(ActionEvent e) {
            setState(!getState());
            mainFrame.getQueryListController().setShowNeighbours(getState());
            super.actionPerformed(e);
            if (mainFrame.getQueryListController().getCurrentGraph() != null)
                mainFrame.getQueryListController().getCurrentGraph().repaint();
        }
    }

    private class SaveLayoutAction extends AbstractAction {
        public SaveLayoutAction() {
            super(gui_resource.getString("Action.name.save.layout"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.save.layout"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(mainFrame, 0, gui_resource.getString("QueryListController.saveLayout.label"));
            mainFrame.getQueryListController().saveLayoutA.actionPerformed(ae);
        }
    }

    private class CircleLayoutAction extends AbstractAction {
        public CircleLayoutAction() {
            super(gui_resource.getString("Action.name.layout.circle"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.layout.circle"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(queryPanel, 0, gui_resource.getString("QueryListController.layout.Circle"));
            mainFrame.getQueryListController().getLayoutAction(LayoutType.Circle).actionPerformed(ae);
        }
    }

    private class KKLayoutAction extends AbstractAction {
        public KKLayoutAction() {
            super(gui_resource.getString("Action.name.layout.kamada-kavai"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.layout.kamada-kavai"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(queryPanel, 0, gui_resource.getString("QueryListController.layout.KK"));
            mainFrame.getQueryListController().getLayoutAction(LayoutType.KK).actionPerformed(ae);

        }
    }

    private class FRLayoutAction extends AbstractAction {
        public FRLayoutAction() {
            super(gui_resource.getString("Action.name.layout.fr"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.layout.fr"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(queryPanel, 0, gui_resource.getString("QueryListController.layout.FR"));
            mainFrame.getQueryListController().getLayoutAction(LayoutType.FR).actionPerformed(ae);
        }
    }

    private class AutoLayoutAction extends AbstractAction {
        public AutoLayoutAction() {
            super(gui_resource.getString("Action.name.layout.auto"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.layout.auto"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(queryPanel, 0, gui_resource.getString("QueryListController.layout.SOM"));
            mainFrame.getQueryListController().getLayoutAction(LayoutType.ISOM).actionPerformed(ae);
        }
    }

    private class StaticLayoutAction extends AbstractAction {
        public StaticLayoutAction() {
            super(gui_resource.getString("Action.name.layout.static"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.layout.static"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(queryPanel, 0, gui_resource.getString("QueryListController.layout.Static"));
            mainFrame.getQueryListController().getLayoutAction(LayoutType.Static).actionPerformed(ae);
        }
    }

    private class CustomLayoutAction extends AbstractAction {
        public CustomLayoutAction() {
            super(gui_resource.getString("Action.name.layout.custom"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.layout.custom"));
        }

        public void actionPerformed(ActionEvent e) {
            QueryPanel queryPanel = mainFrame.getQueryListController().getCurrentComponent();
            if (queryPanel == null) return;
            ActionEvent ae = new ActionEvent(queryPanel, 0, gui_resource.getString("QueryListController.layout.Custom"));
            mainFrame.getQueryListController().getLayoutAction(LayoutType.Custom).actionPerformed(ae);
        }
    }

    private class AboutAction extends AbstractAction {
        public AboutAction() {
            super(gui_resource.getString("Action.name.help"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.help"));
        }

        public void actionPerformed(ActionEvent e) {
            AboutPanel aboutPanel = new AboutPanel(mainFrame);
            aboutPanel.setVisible(true);
        }
    }

    private class InstructionAction extends AbstractAction {
        public InstructionAction() {
            super(gui_resource.getString("Action.name.instruction"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.instruction"));
        }

        public void actionPerformed(ActionEvent e) {
            InstructionPanel instructionPanel = new InstructionPanel();
            instructionPanel.setVisible(true);
        }
    }

    private class InvisibleAction extends AbstractAction {
        public InvisibleAction() {
            super(gui_resource.getString("Action.name.invisible"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.invisible"));
        }

        public void actionPerformed(ActionEvent e) {
            Query q = mainFrame.getQueryListController().getCurrentQuery();
            if (q == null) return;
            Vertex v = mainFrame.getCurrentSelectedVertex();
            if (v == null) return;
            try {
                QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(q);
                BufferedViewer viewer = queryPanel.getGraph();
                viewer.lock();
                ControlObject controlObject = GraphNetworkUtil.getControlObject(v);
                ObjectWorker worker = new ObjectWorker(controlObject);
                worker.setVisible(false);
                Set edges = v.getIncidentEdges();
                for (Object o1 : edges) {
                    GraphDataUtil.setVisible((Edge) o1, false);
                }
                GraphDataUtil.setVisible(v, false);
                queryPanel.getGraphModel().selectVertices(new HashSet<Vertex>());
                mainFrame.setVertexEnableControl(false);
                viewer.repaint();
                viewer.unlock();

            } catch (Throwable ex) {
                MessageDialogs.generalError(mainFrame, ex, gui_resource.getString("error.cardFetch"),
                        gui_resource.getString("error.commonTitle"));
            }
        }
    }

    private class CloseRequestAction extends AbstractAction {
        public CloseRequestAction() {
            super(gui_resource.getString("Action.name.request.close"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.request.close"));
        }

        public void actionPerformed(ActionEvent e) {
            RequestPanel requestPanel = (RequestPanel) mainFrame.getRequestTabbedPane().getSelectedComponent();
            if (requestPanel != null) {

                for (Component component : requestPanel.getQueryListPanel().getComponents()) {
                    if (component instanceof QueryPanel) {
                        QueryPanel queryPanel = (QueryPanel) component;
                        //if (queryPanel.isLayoutChanged()) {
                        boolean res = MessageDialogs.confirm(mainFrame, gui_resource.getString("QueryListController.saveLayout.confirm"),
                                gui_resource.getString("confirm.title"));
                        if (res) {
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
                            catch (Exception ex) {
                                //todo
                                ex.printStackTrace();
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

                            try {
                                TestDataTDModel tdModel = (TestDataTDModel) queryPanel.getTimeDiagram().getModel();
                                LinkedList<TDPair> pairs = new LinkedList<TDPair>();
                                for (int i = 0; i < tdModel.getPairsCount(); i++) {
                                    pairs.add(tdModel.getPairAt(i));
                                }
                                QueryServiceDelegator.getInstance().saveTTPairs(queryPanel.getController().getQuery().getId(), pairs);
                            }
                            catch (Exception e3) {
                                e3.printStackTrace();
                            }

                            try {
                                TimedDiagrammDataSnapshot data = queryPanel.getDataSnapshot();
                                //if (data.getDateFrom() != null && data.getDateTo() != null && data.getPairs()!=null && data.getPairs().size() != 0){
                                //}
//
                                QueryServiceDelegator.getInstance().saveTTParameters(queryPanel.getController().getQuery().getId(), queryPanel.getDataSnapshot());
                            }
                            catch (Exception e4) {
                                e4.printStackTrace();
                            }
                            mainFrame.getQueryListController().saveLayoutByPanel(queryPanel);
                        }
                        //}
                        requestPanel.getQueryListPanel().getController().removeQuery(queryPanel);
                    }
                }
            }
            mainFrame.getRequestTabbedPane().remove(requestPanel);
            mainFrame.getRequestTabbedPane().getRequestListController().removeRequestPanel(requestPanel.getRequestId());
        }
    }

    public class OpenNodeGroupAction extends AbstractAction {
        public OpenNodeGroupAction()

        {
            super(gui_resource.getString("Action.name.createGroup"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.createGroup"));
        }


        public void actionPerformed(ActionEvent e) {
            Vertex v = (Vertex) mainFrame.getCurrentSelectedVertex();
            if (v instanceof GraphCollapser.CollapsedVertex) {
                NodeGroupDialog dialog = new NodeGroupDialog(mainFrame,
                        gui_dialog_resource.getString("NodeGroupDialog.Edit.title"),
                        GraphNetworkUtil.getName(v));
            } else {
                NodeGroupDialog dialog = new NodeGroupDialog(mainFrame,
                        gui_dialog_resource.getString("NodeGroupDialog.Add.title"),
                        null);
            }
        }
    }

    private class TestAction1 extends AbstractAction {
        public TestAction1() {
            super(gui_resource.getString("Action.name.test1"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test1"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Query query = mainFrame.getQueryListController().getCurrentQuery();

                HashMap<Long, java.util.List<PropertyType>> visibleAtt = query.getViewedAttributes();

                LVObject objectProperty = Util.getVisibleFieldsForObject(visibleAtt);
                LVObject linkProperty = Util.getVisibleFieldsForLink(visibleAtt);

                VisibleAttributesDialogPanel dialog = new VisibleAttributesDialogPanel(mainFrame, objectProperty, linkProperty, query);
                dialog.setVisible(true);
            }
            catch (Exception ex) {
                MessageDialogs.generalError(null, ex);
            }
        }
    }

    private class TestAction2 extends AbstractAction {
        public TestAction2() {
            super(gui_resource.getString("Action.name.test2"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test2"));
        }

        public void actionPerformed(ActionEvent e) {
            //
        }
    }

    private class TestAction3 extends AbstractAction {
        public TestAction3() {
            super(gui_resource.getString("Action.name.test3"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test3"));
        }

        public void actionPerformed(ActionEvent e) {
            LinkPK linkPK = new LinkPK(0L, 1027L);
            try {
                CardLinkDialogPanel cardLinkDialogPanel = new CardLinkDialogPanel(mainFrame, PresentationServiceDelegator.getInstance().getLinkObject(linkPK));
                cardLinkDialogPanel.setVisible(true);
            }
            catch (Exception e1) {
                MessageDialogs.generalError(mainFrame, e1, "", "");
            }

        }
    }

    private class TestAction4 extends AbstractAction {
        public TestAction4() {
            super(gui_resource.getString("Action.name.test4"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test4"));
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<LinkObject> linkObjects = new ArrayList<LinkObject>();
            for (int i = 0; i < 3; i++) {
                LinkPK linkPK = new LinkPK((long) i, 1027L);
                try {
                    linkObjects.add(PresentationServiceDelegator.getInstance().getLinkObject(linkPK));
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            IntegrateLinkDialogPanel integrateLinkDialogPanel =
                    new IntegrateLinkDialogPanel(mainFrame, linkObjects, "first", "second", mainFrame.getQueryListController().getCurrentQuery().getId());
            integrateLinkDialogPanel.setVisible(true);
        }
    }

    private class TestAction5 extends AbstractAction {
        public TestAction5() {
            super(gui_resource.getString("Action.name.test5"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test5"));
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<ControlObject> controlObjects = new ArrayList<ControlObject>();
            Long qId = mainFrame.getQueryListController().getCurrentQuery().getId();
            for (int i = 0; i < 3; i++) {
                NodePK nodePK = new NodePK((long) i, qId);
                try {
                    controlObjects.add(PresentationServiceDelegator.getInstance().getControlObject(nodePK));
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            HiddenObjectDialogPanel hiddenObjectDialogPanel =
                    new HiddenObjectDialogPanel(mainFrame, controlObjects, qId);
            hiddenObjectDialogPanel.setVisible(true);
        }
    }

    private class TestAction6 extends AbstractAction {
        public TestAction6() {
            super(gui_resource.getString("Action.name.test6"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test6"));
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getQueryListController().getCurrentQuery();

            HashMap<Long, java.util.List<PropertyType>> visibleAtt = query.getViewedAttributes();

            LVObject objectProperty = Util.getVisibleFieldsForObject(visibleAtt);
            LVObject linkProperty = Util.getVisibleFieldsForLink(visibleAtt);

            FilterPanel filterPanel = new FilterPanel(mainFrame, query, objectProperty, linkProperty);
            FieldObjectContainer fieldObjectContainer = mainFrame.getQueryListController().getQueryPanel(query).getFieldObjectContainer();
            if (fieldObjectContainer != null)
                filterPanel.setFilterParameters(fieldObjectContainer);
            filterPanel.setVisible(true);
        }
    }


    private void permissionDeniedInLog(String note) {
        try {
            AuditManager auditManager = new AuditManager();
            auditManager.addAccesDeniedEvent(note);
        } catch (Exception ex) {
            MessageDialogs.generalError(mainFrame, ex);
        }
    }

    private class TestAction7 extends AbstractAction {
        public TestAction7() {
            super(gui_resource.getString("Action.name.test7"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test7"));
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getQueryListController().getCurrentQuery();
            if (query == null) return;
            TDModel tdModel = new TestDataTDModel(5, 5, 5);
            LinkedList<TDPair> pairs = new LinkedList<TDPair>();
            for (int i = 0; i < tdModel.getPairsCount(); i++) {
                pairs.add(tdModel.getPairAt(i));
            }
            try {
                QueryServiceDelegator.getInstance().saveTTPairs(query.getId(), pairs);
            }
            catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private class TestAction8 extends AbstractAction {
        public TestAction8() {
            super(gui_resource.getString("Action.name.test8"));
            putValue(SHORT_DESCRIPTION, gui_resource.getString("Action.name.test8"));
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getQueryListController().getCurrentQuery();
            if (query == null) return;
            try {
                LinkedList<TDPair> objs = QueryServiceDelegator.getInstance().getTTPairs(query.getId());
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private class TestAction9 extends AbstractAction {
        public TestAction9() {
            super("Save tt parameters");
            putValue(SHORT_DESCRIPTION, "Save tt parameters");
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getQueryListController().getCurrentQuery();
            if (query == null) return;
            try {
                QueryPanel queryPanel = mainFrame.getQueryListController().getQueryPanel(query);
                QueryServiceDelegator.getInstance().saveTTParameters(query.getId(), queryPanel.getDataSnapshot());
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private class TestAction10 extends AbstractAction {
        public TestAction10() {
            super("Get tt parameters");
            putValue(SHORT_DESCRIPTION, "Get tt parameters");
        }

        public void actionPerformed(ActionEvent e) {
            Query query = mainFrame.getQueryListController().getCurrentQuery();
            if (query == null) return;
            try {
                TimedDiagrammDataSnapshot dataParametrs = QueryServiceDelegator.getInstance().getTTParameters(query.getId());
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
