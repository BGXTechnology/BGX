package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.object.FieldObjectContainer;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.LinkType;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Date;

import ru.zsoft.jung.viewer.BufferedViewer;

/**
 * Class QueryController
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class QueryController extends QueryState {

    private GraphNetworkMapper data;
    private GraphDataModel model;
    private Query q;
    private QueryPanel view;
    private MainFrame owner;

    protected boolean verticesExportable = true;
    protected boolean edgesExportable = true;
    protected Set<Vertex> centerVertices = new HashSet<Vertex>();

    private boolean filterIsSet = false;

    public QueryController(Query q, GraphNetworkMapper data, MainFrame owner) {
        this.q = q;
        this.data = data;
        this.model = new GraphDataModel(data);
        this.owner = owner;
        setDataLevel(DataLevel.GraphCard);
     }


    public boolean isFilterIsSet() {
        return filterIsSet;
    }

    public Set<Vertex> getCenterVertices() {
        return centerVertices;
    }

    public void setCenterVertices(Set<Vertex> centerVertices) {
        this.centerVertices = centerVertices;
    }

    public MainFrame getOwner() {
        return owner;
    }

    public Query getQuery() {
        return q;
    }

    public GraphNetworkMapper getData() {
        return data;
    }

    public GraphDataModel getModel() {
        return model;
    }


    public void setModel(GraphDataModel model) {
        this.model = model;
    }

    public QueryPanel getView() {
        return view;
    }

    public void setView(QueryPanel view) {
        this.view = view;
    }

    public boolean isVerticesExportable() {
        return verticesExportable;
    }

    public void setVerticesExportable(boolean verticesExportable) {
        this.verticesExportable = verticesExportable;
    }

    public boolean isEdgesExportable() {
        return edgesExportable;
    }

    public void setEdgesExportable(boolean edgesExportable) {
        this.edgesExportable = edgesExportable;
    }

    public void setDataLevel(String mode) {
        if (mode.equals(QueryPanel.dataCard))
            super.setDataLevel(DataLevel.TableCard);
        else if (mode.equals(QueryPanel.graphCard)) {
            super.setDataLevel(DataLevel.GraphCard);
            owner.enableControls(true);
            owner.setEnabledOtherButton(true);
            BufferedViewer source = view.getGraph();
            if (source != null) {
                if (source.getPickedState().getPickedEdges().size() > 0) {
                    owner.setEdgeEnableControl((source.getPickedState().getPickedEdges()).size() > 0);
                    owner.setCurrentSelectedEdge((Edge) (source.getPickedState().getPickedEdges()).toArray()[0]);
                } else {
                    owner.setEdgeEnableControl(false);
                }

                if (source.getPickedState().getPickedVertices().size() > 0) {
                    if (source.getPickedState().getPickedVertices().size() > 1)
                        owner.setDisabledOpenCard();
                    owner.setCurrentSelectedVertex((Vertex) (source.getPickedState().getPickedVertices()).toArray()[0]);
                    owner.setVertexEnableControl((source.getPickedState().getPickedVertices()).size() > 0);
                } else {
                    owner.setVertexEnableControl(false);
                }
            }
            getView().managementAction.setEnabled(true);
            getView().filterA.setEnabled(true);
        } else if (mode.equals(QueryPanel.timeCard)) {
            super.setDataLevel(DataLevel.TimeCard);
            owner.disableControlsForTTView();
            owner.setEnabledOtherButton(false);
            getView().managementAction.setEnabled(false);
            getView().filterA.setEnabled(false);
        }

        view.showCard(mode);
    }

//    public String getDataLevel(){
//
//    }

    public void setShowNeighbours(boolean show) {
        view.getGraphController().setSelectNeighbours(show);
        super.setShowNeighbours(show);
    }

    public void doFilter() {
        FieldObjectContainer fieldObjectContainer = view.getFieldObjectContainer();
        if (fieldObjectContainer == null) return;
        BufferedViewer viewer = view.getGraph();
        viewer.lock();
        Graph g = viewer.getGraphLayout().getGraph();

        setGraphVisible(g, true);

        Set edges = g.getEdges();
        ArrayList<Edge> alledges = new ArrayList<Edge>();
        Edge e;

        filterIsSet = false;
        for (Object o : edges) {
            e = (Edge) o;
            alledges.add(e);
            LinkObject linkObject = GraphNetworkUtil.getLinkObject(e);
            LinkWorker linkWorker = new LinkWorker(linkObject);

            boolean visibleFlag = true;
            for (FieldObject fieldObject : fieldObjectContainer.getLinkProperties()) {
                String val = linkWorker.getValueByPropertyCode(fieldObject.getCode());
                switch (fieldObject.getDataTypeCode()) {
                    case 2:
                    case 12:
                        if (!equalsStringValues(val, (String) fieldObject.getValue()))
                            visibleFlag = false;
                        break;
                    case 91:
                        Date dateValue = FieldObject.convertValueToDate(val);
                        ArrayList<Object> datesObject = (ArrayList<Object>) fieldObject.getValue();
                        String startDateAsString = (String) datesObject.get(0);
                        Date startDate = FieldObject.convertValueToDate(startDateAsString);

                        String finishDateAsString = (String) datesObject.get(1);
                        Date finishDate = FieldObject.convertValueToDate(finishDateAsString);
                        ArrayList<Date> dates = new ArrayList<Date>();
                        dates.add(startDate);
                        dates.add(finishDate);

                        if (!equalsDateValues(dateValue, dates))
                            visibleFlag = false;
                        break;
                }
                if (!visibleFlag) break;
            }
            if (!visibleFlag)
                filterIsSet = true;
            GraphDataUtil.setVisible(e, visibleFlag);
        }

        Set vertices = g.getVertices();
        Vertex v;

        for (Object o : vertices) {
            v = (Vertex) o;
            ControlObject conrolObject = GraphNetworkUtil.getControlObject(v);
            if (conrolObject == null) continue;

            ObjectWorker objectWorker = new ObjectWorker(conrolObject);
            //Set invisible for all excepted objects
            edges = v.getIncidentEdges();

            if (!objectWorker.isVisible()) {
                for (Object o1 : edges) {
                    GraphDataUtil.setVisible((Edge) o1, false);
                }
                GraphDataUtil.setVisible(v, false);
                continue;
            }

            boolean visibleFlag = true;
            for (FieldObject fieldObject : fieldObjectContainer.getObjectProperties()) {
                String val = objectWorker.getValueByPropertyCode(fieldObject.getCode());
                switch (fieldObject.getDataTypeCode()) {
                    case 2:
                    case 12:
                        if (!equalsStringValues(val, (String) fieldObject.getValue()))
                            visibleFlag = false;
                        break;
                    case 91:
                        Date dateValue = FieldObject.convertValueToDate(val);
                        ArrayList<Object> datesObject = (ArrayList<Object>) fieldObject.getValue();
                        String startDateAsString = (String) datesObject.get(0);
                        Date startDate = FieldObject.convertValueToDate(startDateAsString);

                        String finishDateAsString = (String) datesObject.get(1);
                        Date finishDate = FieldObject.convertValueToDate(finishDateAsString);
                        ArrayList<Date> dates = new ArrayList<Date>();
                        dates.add(startDate);
                        dates.add(finishDate);

                        if (!equalsDateValues(dateValue, dates))
                            visibleFlag = false;
                        break;
                }
                if (!visibleFlag) break;
            }
            if (!visibleFlag)
                filterIsSet = true;

            if (!visibleFlag)
                for (Object o1 : edges) {
                    GraphDataUtil.setVisible((Edge) o1, false);
                }
            GraphDataUtil.setVisible(v, visibleFlag);

            //remove handled edges from list of all edge
            for (Object o1 : edges) {
                alledges.remove((Edge) o1);
            }
        }

        for (Object o1 : alledges) {
            Edge edge = (Edge) o1;
            if (GraphNetworkUtil.getType(edge) != null &&
                    GraphNetworkUtil.getType(edge).equals(LinkType.IntegrationLink)) continue;
            GraphDataUtil.setVisible(edge, false);
        }

        viewer.unlock();
        viewer.repaint();
        view.setFilterButtonSelected(filterIsSet);
    }

    public void setVisibleStateForObject() {
        BufferedViewer viewer = view.getGraph();
        viewer.lock();

        Graph g = viewer.getGraphLayout().getGraph();
        //set visible for current graph
        setVisibleFor(g);
        g = getView().getGraphController().getOriginalLayout().getGraph();
        //set visible for original graph
        setVisibleFor(g);

        if (filterIsSet) doFilter();

        viewer.unlock();
        viewer.repaint();
    }

    private void setVisibleFor(Graph g) {
        setGraphVisible(g, true);
        Set vertices = g.getVertices();
        Vertex v;
        for (Object o : vertices) {
            if (o instanceof GraphCollapser.CollapsedVertex) continue;
            v = (Vertex) o;
            ControlObject conrolObject = GraphNetworkUtil.getControlObject(v);
            ObjectWorker objectWorker = new ObjectWorker(conrolObject);
            Set edges = v.getIncidentEdges();
            boolean visibleFlag = objectWorker.isVisible();
            if (!visibleFlag)
                for (Object o1 : edges) {
                    GraphDataUtil.setVisible((Edge) o1, false);
                }
            GraphDataUtil.setVisible(v, visibleFlag);
        }
    }

    private boolean equalsStringValues(String field, String searchString) {
        if (searchString == null || searchString.length() == 0) return true;
        if (field == null) return false;
        return field.indexOf(searchString) >= 0;
    }

    private boolean equalsDateValues(Date fieldDate, ArrayList<Date> dateInterval) {
        Date startDate = (Date) dateInterval.get(0);
        Date finishDate = (Date) dateInterval.get(1);
        if (startDate == null && finishDate == null)
            return true;
        if (startDate == null)
            return finishDate.getTime() >= fieldDate.getTime();
        if (finishDate == null)
            return startDate.getTime() <= fieldDate.getTime();
        return (startDate.getTime() <= fieldDate.getTime() && finishDate.getTime() >= fieldDate.getTime());
    }

    private void setGraphVisible(Graph g, boolean flag) {
        Set edges = g.getEdges();
        Edge e;
        for (Object o : edges) {
            e = (Edge) o;
     /*       if (GraphNetworkUtil.getType(e) != null &&
                    GraphNetworkUtil.getType(e).equals(LinkType.IntegrationLink)) continue;
*/
            GraphDataUtil.setVisible(e, flag);
        }

        Set vertices = g.getVertices();
        Vertex v;
        for (Object o : vertices) {
            v = (Vertex) o;
            GraphDataUtil.setVisible(v, flag);
        }
    }

    public void repaintGraph() {
        BufferedViewer viewer = view.getGraph();
        viewer.lock();
        viewer.repaint();
        viewer.unlock();
    }
}
