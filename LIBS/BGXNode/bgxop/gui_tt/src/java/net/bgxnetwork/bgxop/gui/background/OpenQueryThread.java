package net.bgx.bgxnetwork.bgxop.gui.background;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;
import net.bgx.bgxnetwork.bgxop.engine.AlgorithmsHolder;
import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.engine.LayoutType;
import net.bgx.bgxnetwork.bgxop.gui.*;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.exception.D3Exception;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.query.Query;
import oracle.spatial.network.Network;
import oracle.spatial.network.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * Class OpenQueryThread
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class OpenQueryThread extends ServerThread {
    private Object obj = new Object();
    private Query q;
    private MainFrame owner;
    private LayoutCoordinates lc;
    private LayoutType lt = null;
    private QueryPanel queryPanel = null;
    private QueryListController controller;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

    public OpenQueryThread(WaitDialog dialog, MainFrame owner, Query q, LayoutCoordinates lc, QueryPanel panel,
                           QueryListController controller) {
        super(dialog);
        if (q != null) {
            setName(q.getName());
        } else {
            setName("null query");
        }
        this.q = q;
        this.owner = owner;
        this.lc = lc;
        this.controller = controller;
        queryPanel = panel;
    }

    public void run() {

        GraphNetworkMapper data = null;
        Vertex center = null;
        try {
            NetWorker netWorker = new NetWorker();
            doNetworkOperation(netWorker);
            Network net = netWorker.getResult();
            if (net == null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.close();
                        MessageDialogs.generalError(owner, null, rb.getString("ProfilePanelController.error.query.NoData"),
                                rb.getString("error.commonTitle"));
                        controller.removeQuery(queryPanel);
                    }
                });
                return;
            } else if (net.getNoOfNodes() == 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.close();
                        MessageDialogs.warning(owner, rb.getString("info.emptyData"), rb.getString("confirm.title"));
                        controller.removeQuery(queryPanel);
                    }
                });
                return;
            } else {
                try {
                    // ok - network is valid

                    data = new GraphNetworkMapper(net);
                    data.restoreGraph(); // QueryBusinesException
                }
                catch (D3Exception e) {
                    ex = e;
                    finish();
                    return;
                }
            } // ok - graph restored
            if (lc == null) {
                LCWorker lcWorker = new LCWorker();
                doNetworkOperation(lcWorker);
                lc = lcWorker.getResult();
            }
            Layout l = null;
            if (lc == null) {
                lt = LayoutType.Circle;
                l = AlgorithmsHolder.getInstance().doLayout(q.getId(), lt, data.getGraph());
            } else {
                lt = LayoutType.Custom;
                l = AlgorithmsHolder.loadCoordinates(lc, data.getGraph());
            } // ok - layout is ready
            QueryController queryController = new QueryController(q, data, owner);
            queryController.setVerticesExportable(true);

            NodeListTableModel tableModel = null;
            Set<Vertex> centers = new HashSet<Vertex>();

            if (center == null) {
                List<Node> nodes = new ArrayList<Node>();
                for (Vertex v : queryController.getModel().getVertices()) {
                    nodes.add(GraphNetworkUtil.getNode(v));
                }
                LVObject objectProperty = Util.getVisibleFieldsForObject(q.getViewedAttributes());
                ArrayList<FieldObject> headers = new ArrayList<FieldObject>();
                for (FieldObject field : objectProperty.getFields()) {
                    if (field == null) continue;
                    if (field.isVisible())
                        headers.add(field);
                }
                tableModel = new NodeListTableModel(nodes, headers);
                queryController.setEdgesExportable(true);
            }

            queryController.setCenterVertices(centers);
            NodeListTable table = new NodeListTable(owner, tableModel);
            table.setRowHeader();
            // VertexListTable table = new VertexListTable(owner, model);
            NodeListSelectionListener listener = new NodeListSelectionListener(table, queryController.getModel());
            table.getSelectionModel().addListSelectionListener(listener);
            queryPanel.setQueryPanel(table, queryController, lt, l);
            addDataToPanel();
            queryController.setVisibleStateForObject();
            setStatusData(data.getGraph().numVertices(), data.getGraph().numEdges());
        } catch (InterruptedException e) {
            ex = null;
            finish();
        } catch (Exception e) {
            ex = e;
            finish();
            return;
        } finally {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    queryPanel.removeDialog(dialog);
                    Query qqq = queryPanel.getQuery();
                    QueryListPanel qlp = controller.getView(qqq.getId());
                    controller.setForegroundByComponent(qlp, queryPanel, Color.BLACK);
                    dialog.close();
                }
            });
        }
    } // run

    synchronized public void addDataToPanel() {
        if (getLayoutType() != null) {
            controller.clearParallelIndex();
            if (getLayoutCoordinates() != null) {
                controller.getLayouts().put(q, getLayoutCoordinates());
                Set<Vertex> groupVertex = queryPanel.madeGroupGraph(controller.getLayouts().get(q));
                if (groupVertex.size() > 0) {
                    controller.getCurrentData().setFilterIntegrationLink(queryPanel.getController().isFilterIsSet());
                    controller.setForse(getLayoutType(), true);
                    controller.getLayoutAction(getLayoutType()).actionPerformed(new ActionEvent(queryPanel, ActionEvent.ACTION_PERFORMED, null));
                    controller.getCurrentGraphModel().selectVertices(groupVertex);
                    controller.getCurrentGraph().unlock();
                    controller.getCurrentGraph().repaint();
                }

            }

            queryPanel.setLoaded(true);
            queryPanel.getController().setDataLevel(controller.getDataLevel());
            queryPanel.getController().setShowNeighbours(controller.getShowNeighbours());
            owner.enableControls(controller.enableControls());
            controller.doLayout(getLayoutType(), queryPanel);
            controller.setForse(getLayoutType(), false);

        }
    }

    synchronized public void setStatusData(int vertNumber, int lnkNumber) {
        GraphNetworkMapper mapper = queryPanel.getData();
        if (mapper != null) {
            owner.getQueryListController().setStatusBar(mapper);
        }
    }

    public LayoutCoordinates getLayoutCoordinates() {
        return lc;
    }

    public LayoutType getLayoutType() {
        return lt;
    }

    public QueryPanel getResult() {
        return queryPanel;
    }

    protected class NetWorker extends AbstractNetworkWorker<Network> {
        public void run() {
            try {
                result = QueryServiceDelegator.getInstance().getQueryData(q.getId());
            }
            catch (D3Exception e) {
                ex = e;
                finish();
                QueryPanel exists = owner.getQueryListController().getQueryPanel(q.getName());
                owner.getQueryListController().removeQuery(exists);
                return;
            }
            catch (Exception e) {
                ex = e;
                finish();
            }
        }
    }

    protected class LCWorker extends AbstractNetworkWorker<LayoutCoordinates> {
        public void run() {
            try {
                result = QueryServiceDelegator.getInstance().getLayout(q.getId());
            }
            catch (Exception e) {
                ex = e;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (ex instanceof QueryBusinesException
                                && ((D3Exception) ex).getErrorCode() == ErrorList.BUSINES_ROLE_PERMISSION_EXCEPTION) {
                            MessageDialogs.generalError(owner, ex, rb.getString("error.noPrivileges.msg"),
                                    rb.getString("error.noPrivileges.title"));
                        } else {
                            MessageDialogs.generalError(owner, ex, rb.getString("QueryListController.error.readLayout"),
                                    rb.getString("error.serverCommunicate"));
                        }
                    }
                });
            } // catch
        }
    }

} // OpenQueryThread
