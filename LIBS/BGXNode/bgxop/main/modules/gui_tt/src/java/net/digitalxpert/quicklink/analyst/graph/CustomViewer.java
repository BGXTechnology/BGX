package net.bgx.bgxnetwork.bgxop.graph;

import ru.zsoft.jung.viewer.BufferedViewer;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.graph.Graph;
import net.bgx.bgxnetwork.bgxop.graph.CustomRenderer;
import net.bgx.bgxnetwork.bgxop.gui.QueryController;
import net.bgx.bgxnetwork.bgxop.engine.Collapser;
import net.bgx.bgxnetwork.bgxop.engine.GraphCollapserDelegator;

import java.util.Set;
import java.util.HashSet;

/**
 * Class CustomViewer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomViewer extends BufferedViewer {
    public CustomViewer(Layout layout, CustomRenderer renderer, QueryController controller) {
        super(layout, renderer);
        renderer.setViewer(this);
        //todo для прямоугольника
        //renderer.setVerticesHeight();
        renderer.setColorForRootVertex(controller);
        setGraphMouse(new CustomMouse(this));

    }

/*
    public Graph collapseVertex(Graph inGraph) {
        Set picked = new HashSet(getPickedState().getPickedVertices());

        try {
            if (picked.size() == 1) {

                Object root = picked.iterator().next();
                Collapser collapser = GraphCollapserDelegator.getInstance(inGraph);
                getModel().getGraphLayout();
                return collapser.collapse(inGraph, collapser.getClusterGraph(inGraph, picked));
            } else
                return inGraph;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            getPickedState().getPickedVertices().clear();
            return inGraph;
        }
    }

    public Graph expandVertex(Graph inGraph) {
        Set picked = new HashSet(getPickedState().getPickedVertices());
        try {
            for (Object v : picked) {
                if (v instanceof Graph) {
                    Collapser collapser = GraphCollapserDelegator.getInstance(inGraph);
                    inGraph = collapser.expand(inGraph, collapser.getClusterGraph(inGraph, picked));
                }
            }
            getPickedState().getPickedVertices().clear();
            return inGraph;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return inGraph;
        }
    }
*/


}
