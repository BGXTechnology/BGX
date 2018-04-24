package net.bgx.bgxnetwork.bgxop.gui;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Class VertexListTable
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class NodeListTable extends FormatTable implements IVertexContainer{
    public NodeListTable(MainFrame owner, NodeListTableModel model){
        super(model);
        addMouseListener(new VertexPopupListener(owner, this));
    }
    public Vertex getVertexByPoint(Point p){
        int row = rowAtPoint(p);
        if(row < 0)
            return null;
        Vertex vertex = GraphNetworkUtil.getVertex(getNodeListModel().getObjectAt(row));
        Object[] vertices = getSelectedVertices();
        boolean isSelected = false;
        for(int i = 0; i < vertices.length; i++){
            if(GraphNetworkUtil.getID(vertex) == GraphNetworkUtil.getID((Vertex) vertices[i])){
                isSelected = true;
                break;
            }
        }
        if(!isSelected){
            selectVertexByPoint(p);
        }
        return vertex;
    }
    public void selectVertexByPoint(Point p){
        int row = rowAtPoint(p);
        int col = columnAtPoint(p);
        if(getSelectedRowCount() <= 1 || !isRowSelected(row)){
            if(row < 0)
                getSelectionModel().clearSelection();
            else
                changeSelection(row, col, false, false);
        }
    }
    public NodeListTableModel getNodeListModel(){
        return (NodeListTableModel) super.getModel();
    }
    public Object[] getSelectedVertices(){
        int[] rows = this.getSelectedRows();
        Set<Vertex> vertices = new HashSet<Vertex>();
        if(rows.length == 0)
            return vertices.toArray();
        for(int i = 0; i < rows.length; i++){
            vertices.add(GraphNetworkUtil.getVertex(getNodeListModel().getObjectAt(rows[i])));
        }
        return vertices.toArray();
    }
}
