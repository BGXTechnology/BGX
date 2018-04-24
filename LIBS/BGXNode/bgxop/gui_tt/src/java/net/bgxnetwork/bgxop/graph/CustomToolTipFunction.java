package net.bgx.bgxnetwork.bgxop.graph;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import edu.uci.ics.jung.graph.decorators.ToolTipFunctionAdapter;
import edu.uci.ics.jung.graph.Vertex;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;

/**
 * Class CustomToolTipFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomToolTipFunction extends ToolTipFunctionAdapter {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("engine");
    private String strKpp;

    public CustomToolTipFunction() {
        super();
        try {
            strKpp = rb.getString("DataExporter.ULList.kpp");
        } catch (Exception ex) {
            strKpp = "";
            ex.printStackTrace();
        }
    }

    public String getToolTipText(Vertex vertex) {
        String out = null;
        if (vertex != null && GraphDataUtil.getVisible(vertex)) {
            out = LVGraphNetworkUtil.getToolTipText(vertex);
        }
        return out;
    }
}
