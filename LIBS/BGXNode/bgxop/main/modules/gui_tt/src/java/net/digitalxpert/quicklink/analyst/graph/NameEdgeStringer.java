package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.utils.ParallelEdgeIndexSingleton;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.transfer.query.LinkType;

/**
 * Class CustomEdgeStringer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class NameEdgeStringer implements EdgeStringer {

    public NameEdgeStringer() {
    }

    public String getLabel(ArchetypeEdge edge) {
        if (edge != null && GraphNetworkUtil.getType((Edge) edge) != null &&
                GraphNetworkUtil.getType((Edge) edge).equals(LinkType.IntegrationLink)) {
            return "AC-" + GraphNetworkUtil.getCountVisibleIntegrationLink((Edge) edge);
        } else {
            String name = LVGraphNetworkUtil.getName((Edge) edge);
            if (name == null) return null;
            if (name.length() == 0) return null;
            return name;
        }
    }
}
