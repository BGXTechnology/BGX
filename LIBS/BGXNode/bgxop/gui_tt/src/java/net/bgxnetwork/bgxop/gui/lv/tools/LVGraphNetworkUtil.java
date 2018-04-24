/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.tools;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

import java.util.List;

/**
 * User: A.Borisenko
 * Date: 23.06.2007
 * Time: 16:16:16
 */
public class LVGraphNetworkUtil {
    private static final int MAX_LENGHT = 40;
    public static final String TAG_HTML_START = "<html>";
    public static final String TAG_HTML_END = "</html>";
    private static final String TAG_HTML_BR = "<br>";
    private static final String TAG_END_STRING = "\n";

    public static String getName(Vertex vertex) {
        return getName(vertex, true);
    }

    public static String getName(Vertex vertex, boolean htmlType) {
        String name = LVGraphNetworkUtil.getNameFromControlObject(vertex, htmlType);
        if (name == null || name.equals("null") || name.length() == 0) {
            if (htmlType)
                name = LVGraphNetworkUtil.formatLabel(GraphNetworkUtil.getName(GraphNetworkUtil.getNode(vertex)));
            else
                name = GraphNetworkUtil.getName(GraphNetworkUtil.getNode(vertex));

            if (name == null || name.equals("null"))
                name = "";
            else {
                if (htmlType)
                    name = TAG_HTML_START + name + TAG_HTML_END;
            }
        }
        if (name == null || name.equals("null")) name = "";
        return name;
    }

    private static String getNameFromControlObject(Vertex v, boolean htmlType) {
        Query query = AttributeManager.getInstance().getCurrentVisibleQuery();
        String result;
        boolean hasValue = false;
        if (query == null) return "";
        String val = "";
        try {
            ControlObject conrolObject = GraphNetworkUtil.getControlObject(v);
            ObjectWorker worker = new ObjectWorker(conrolObject);

            LVObject objectProperty = Util.getVisibleFieldsForObject(query.getViewedAttributes());
            StringBuffer sb = new StringBuffer();
            if (htmlType)
                sb.append(TAG_HTML_START);
            for (FieldObject fieldObject : objectProperty.getFields()) {
                if (fieldObject == null) continue;
                if (!fieldObject.isVisible()) continue;
                result = formatLabel(worker.getValueByPropertyCode(fieldObject.getCode()));
                if (result.length() > 0) {
                    sb.append(result);
                    if (htmlType)
                        sb.append(TAG_HTML_BR);
                    else
                        sb.append(TAG_END_STRING);
                    hasValue = result.length() > 0;
                }
            }
            if (htmlType)
                sb.append(TAG_HTML_END);

            return hasValue ? sb.toString() : "";
        }
        catch (Exception e) {
            //todo
            e.printStackTrace();
            return "";
        }
    }

    public static String getName(Edge e) {
        return getName(e, true);
    }

    public static String getName(Edge e, boolean htmlType) {
        Query query = AttributeManager.getInstance().getCurrentVisibleQuery();
        String result;
        boolean hasValue = false;
        if (query == null) return "";
        try {
            LinkObject linkObject = GraphNetworkUtil.getLinkObject(e);
            LinkWorker worker = new LinkWorker(linkObject);
            if (query.getViewedAttributes() == null || query.getViewedAttributes().size()==0){
                return "";
            }
            LVObject linkProperty = Util.getVisibleFieldsForLink(query.getViewedAttributes());

            StringBuffer sb = new StringBuffer();
            if (htmlType)
                sb.append(TAG_HTML_START);
            for (FieldObject fieldObject : linkProperty.getFields()) {
                if (fieldObject == null) continue;
                if (!fieldObject.isVisible()) continue;
                result = formatLabel(worker.getValueByPropertyCode(fieldObject.getCode()));
                if (result.length() > 0) {
                    sb.append(result);
                    if (htmlType)
                        sb.append(TAG_HTML_BR);
                    else
                        sb.append(TAG_END_STRING);

                    hasValue = result.length() > 0;
                }
            }
            if (htmlType)
                sb.append(TAG_HTML_END);

            return hasValue ? sb.toString() : "";
        }
        catch (Exception ex) {
            //todo
            ex.printStackTrace();
            return "";
        }
    }

    public static String formatLabel(String textLabel) {
        return formatLabel(textLabel, MAX_LENGHT);
    }

    public static String formatLabel(String textLabel, int maxLenght) {
        String result;
        if (textLabel != null) {
            if (textLabel.length() > maxLenght) {
                StringBuffer sb = new StringBuffer();
                int posBlank = textLabel.substring(0, maxLenght).trim().lastIndexOf(" ");
                if (posBlank > -1) {
                    result = textLabel.substring(0, posBlank);
                    if (result.length() > 0) {
                        sb.append(result);
                        sb.append(TAG_HTML_BR);
                    }
                    if (textLabel.substring(posBlank + 1, textLabel.length()).length() > maxLenght) {
                        sb.append(formatLabel(textLabel.substring(posBlank + 1, textLabel.length()), maxLenght));
                    } else {
                        sb.append(textLabel.substring(posBlank + 1, textLabel.length()));
                        return sb.toString();
                    }
                } else {
                    result = textLabel.substring(0, maxLenght).trim();
                    if (result.length() > 0) {
                        sb.append(result);
                        sb.append(TAG_HTML_BR);
                    }
                    sb.append(formatLabel(textLabel.substring(maxLenght, textLabel.length()), maxLenght));
                    return sb.toString();
                }
            }
            return textLabel;
        }
        return "";

    }

    public static String getName(ControlObject conrolObject) {
        Query query = AttributeManager.getInstance().getCurrentVisibleQuery();
        if (query == null) return "";
        String val = "";
        try {
            ObjectWorker worker = new ObjectWorker(conrolObject);

            LVObject objectProperty = Util.getVisibleFieldsForObject(query.getViewedAttributes());

            for (FieldObject fieldObject : objectProperty.getFields()) {
                if (fieldObject == null) continue;
                if (!fieldObject.isVisible()) continue;
                val += worker.getValueByPropertyCode(fieldObject.getCode());
            }
            return val;
        }
        catch (Exception e) {
            //todo
            e.printStackTrace();
            return "";
        }
    }

    public static String getToolTipText(Vertex v){
        Query query = AttributeManager.getInstance().getCurrentVisibleQuery();
        String result;
        if (query == null) return "";
        boolean hasValue = false;
        try {
            ControlObject conrolObject = GraphNetworkUtil.getControlObject(v);
            ObjectWorker worker = new ObjectWorker(conrolObject);

            LVObject objectProperty = Util.getVisibleFieldsForObject(null);
            StringBuffer sb = new StringBuffer();
            sb.append(TAG_HTML_START);
            for (FieldObject fieldObject : objectProperty.getFields()) {
                if (fieldObject == null) continue;
                if (!fieldObject.isVisible()) continue;
                if (fieldObject.getCode() == null) continue;
                result = formatLabel(worker.getValueByPropertyCode(fieldObject.getCode()));
                if (result.length() > 0) {
                    sb.append(result);
                    sb.append(TAG_HTML_BR);
                    hasValue = result.length() > 0;
                }
            }
            sb.append(TAG_HTML_END);

            return hasValue ? sb.toString() : "";
        }
        catch (Exception e) {
            //todo
            e.printStackTrace();
            return "";
        }
    }
    /**
     * Метод использующий в качестве имени первый не пустой аттрибут объекта, сейчас у объекта один аттрибут
     * */
    public static String getSimpleName(ControlObject conrolObject){
        String val = "";
        try {
            ObjectWorker worker = new ObjectWorker(conrolObject);

            LVObject objectProperty = Util.getVisibleFieldsForObject(null);

            for (FieldObject fieldObject : objectProperty.getFields()) {
                if (fieldObject == null) continue;
                if (!fieldObject.isVisible()) continue;
                if (fieldObject.getCode() != null)
                val = worker.getValueByPropertyCode(fieldObject.getCode());
                break;
            }
            return val;
        }
        catch (Exception e) {
            //todo
            e.printStackTrace();
            return "";
        }
    }
}
