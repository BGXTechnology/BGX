package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;

/**
 * User: O.Gerasimenko
 * Date: 05.07.2007
 * Time: 16:04:25
 */
public class NameVertexStringer implements VertexStringer {
    public String getLabel(ArchetypeVertex v) {
        String name = LVGraphNetworkUtil.getName((Vertex) v);
        return name;
    }
}
