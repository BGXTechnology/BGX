/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TDTableModel.java#3 $
$DateTime: 2007/07/05 16:46:42 $
$Change: 19428 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationModel;

import net.bgx.bgxnetwork.transfer.tt.*;

class TDTableModel implements TableModel, ChangeListener {
    public static final int linksColumnIndex = 1;
    private static final int objectOneColumnIndex = 0;
    private static final int objectTwoColumnIndex = 2;
    private static final int columnsCount = 3;
    private TDModel diagramModel = null;
    private List<VisualizationModel> visualizationModels = null;
    private TDLinkComparator linksComparator = null;
    private UserDataContainer.CopyAction copyAction = null;
    private EventListenerList listenerList = null;
    private TableModelEvent tableModelEvent = null;

    public TDTableModel(TDModel diagramModel) {
        this.diagramModel = diagramModel;
        if(diagramModel != null) {
            this.diagramModel.addChangeListener(this);
        }
        linksComparator = new TDLinkComparator();
        copyAction = new UserDataContainer.CopyAction.Shared();
        visualizationModels = new ArrayList<VisualizationModel>();
        listenerList = new EventListenerList();
        //
        loadData();
        //
    }
    public Class<?> getColumnClass(int columnIndex) {
        Class columnClass = Object.class;
        switch(columnIndex) {
            case objectOneColumnIndex:
            case objectTwoColumnIndex:
                columnClass = TDObject.class;
                break;
            case linksColumnIndex:
                columnClass = VisualizationModel.class;
        }
        return columnClass;
    }
    public int getColumnCount() {
        return columnsCount;
    }
    public String getColumnName(int columnIndex) {
        return "column "+columnIndex;
    }
    public int getRowCount() {
        return diagramModel == null ? 0 : diagramModel.getPairsCount();
    }
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        TDPair pair = null;
        switch(columnIndex) {
            case objectOneColumnIndex:
                pair = diagramModel.getPairAt(rowIndex);
                value = pair.getObjectOne();
                break;
            case objectTwoColumnIndex:
                pair = diagramModel.getPairAt(rowIndex);
                value = pair.getObjectTwo();
                break;
            case linksColumnIndex:
                value = visualizationModels.get(rowIndex);
                break;
        }
        return value;
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
    public void stateChanged(ChangeEvent e) {
        loadData();
        fireTableModelEvent();
    }
    public void addTableModelListener(TableModelListener listener) {
        listenerList.add(TableModelListener.class, listener);
    }
    public void removeTableModelListener(TableModelListener listener) {
        listenerList.remove(TableModelListener.class, listener);
    }
    private void fireTableModelEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == TableModelListener.class) {
                if (tableModelEvent == null) {
                    tableModelEvent = new TableModelEvent(this);
                }
                ((TableModelListener) listeners[i+1]).tableChanged(tableModelEvent);
            }
        }
    }
    private void loadData() {
        visualizationModels.clear();
        if ( diagramModel != null ) {
            int pairsCount = diagramModel.getPairsCount();
            for (int i=0; i < pairsCount; i++) {
                visualizationModels.add(i, convert(diagramModel.getPairAt(i).getLinks()));
            }
        }
    }
    private VisualizationModel convert(List<TDLink> links) {
        Graph graph = new SparseGraph();
        VisualizationModel model = null;
        Layout layout = null;
        Edge edge = null;
        Vertex vertexLink = null;
        Vertex vertexKnot = null;
        model = new TDVisualizationModel();

        if(links.size() > 0) {
            Collections.sort(links, linksComparator);
        }

        for(TDLink link : links) {
            vertexKnot = new SparseVertex();
            vertexLink = new SparseVertex();
  
            vertexLink.setUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT, link, copyAction);
            ((TDVisualizationModel)model).addLinkToVertex(vertexLink, link);
            ((TDVisualizationModel)model).addLinkToVertex(vertexKnot, link);
            graph.addVertex(vertexLink);
            graph.addVertex(vertexKnot);


            edge = new UndirectedSparseEdge(vertexKnot, vertexLink);
            graph.addEdge(edge);

        }

        if(links.size() > 0) {
            graph.setUserDatum(TDConstants.KEY_GRAPH_LINKOBJECT_MAX, Collections.max(links, linksComparator), copyAction);
            graph.setUserDatum(TDConstants.KEY_GRAPH_LINKOBJECT_MIN, Collections.min(links, linksComparator), copyAction);
        }

        layout = new TDLayout(graph, diagramModel.getMinimum(), diagramModel.getMaximum(),
                TDConstants.LAYOUT_HEIGHT,
                TDConstants.LAYOUT_Y_TOP,
                TDConstants.LAYOUT_Y_CENTER,
                TDConstants.LAYOUT_Y_BOTTOM);

        model.setGraphLayout(layout);
        return model;
    }

    public VisualizationModel getVisualizationModelBy(int i){
        return visualizationModels.get(i);
    }

}
