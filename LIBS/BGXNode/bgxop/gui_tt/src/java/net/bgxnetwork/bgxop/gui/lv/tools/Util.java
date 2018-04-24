/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.tools;

import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.bgxop.services.MetaDataServiceDelegator;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.graph.NotationModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.meta.Manager;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;

import java.util.*;

/**
 * User: A.Borisenko
 * Date: 09.06.2007
 * Time: 22:53:07
 */
public class Util {
    private static HashMap<Long, LVObject> metaData = new HashMap<Long, LVObject>();

    public static String getParametersAsHtml(LVObject lvObject) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<HTML>");
        buffer.append("<table>");
        for (FieldObject field : lvObject.getFields()) {
            if (field.getCaption() == null || field.getCaption().length() == 0) continue;
            buffer.append("<tr>");
            buffer.append("<td><b>");
            buffer.append(field.getCaption());
            buffer.append(":</b></td>");
            buffer.append("<td>");
            buffer.append(FieldObject.convertToString(field.getValue(), field.getDataType()));
            buffer.append("</td>");
            buffer.append("</tr>");
        }
        buffer.append("</table>");
        buffer.append("</HTML>");

        return buffer.toString();
    }

    private static FieldObject getObjectAttributesByCode(Long code) {
        Long type = (long) ObjectType.SIMPLE_OBJECT.getValue();
        return getAttributesByCode(type, code);
    }

    private static FieldObject getLinkAttributesByCode(Long code) {
        Long type = (long) ObjectType.LINK_OBJECT.getValue();
        return getAttributesByCode(type, code);
    }

    public static FieldObject getAttributesByCode(Long type, Long code) {
        LVObject lvObject = metaData.get(type);
        if (lvObject == null) {
            try {
                if (type == (long) ObjectType.SIMPLE_OBJECT.getValue())
                    lvObject = (LVObject) MetaDataServiceDelegator.getInstance().getObjectAttributeOverview();
                else if (type == (long) ObjectType.LINK_OBJECT.getValue())
                    lvObject = (LVObject) MetaDataServiceDelegator.getInstance().getLinkAttributeOverview();
                metaData.put(type, lvObject);
            }
            catch (Exception e) {
                MessageDialogs.generalError(null, e);
            }
        }
        if (lvObject != null) {
            for (FieldObject fieldObject : lvObject.getFields()) {
                if (fieldObject == null) continue;
                if (fieldObject.getCode().longValue() == code)
                    return fieldObject;
            }
        }
        return null;
    }

    public static boolean isContains(List<PropertyType> propertyTypes, PropertyType propertyType) {
        for (PropertyType pt : propertyTypes) {
            if (pt != null &&
                    pt.getPropertyTypeId().equals(propertyType.getPropertyTypeId()))
                return true;
        }
        return false;
    }

    public static LVObject getVisibleFieldsForObject(HashMap<Long, java.util.List<PropertyType>> userSettings) {
        LVObject objectProperty = new LVObject();

        List<PropertyType> objectAllPropertyTypes = Manager.getInstance().getEnabledVisibleObjectProperty();

        net.bgx.bgxnetwork.persistence.metadata.ObjectType objectType =
                NotationModel.getInstance().getObjectTypes().get(ObjectType.SIMPLE_OBJECT.getValue());

        for (PropertyType propertyType : objectType.getPropertyTypes()) {
            if (propertyType == null) continue;
            if (propertyType.getCodePropertyType() == null) continue;
            if (Util.isContains(objectAllPropertyTypes, propertyType)) {
                FieldObject fieldObject = Util.getObjectAttributesByCode(Long.parseLong(propertyType.getCodePropertyType()));
                if (fieldObject == null) continue;
                if (userSettings != null && userSettings.size() != 0) {
                    ArrayList<PropertyType> userVisibleList =
                            (ArrayList<PropertyType>) userSettings.get((long) ObjectType.SIMPLE_OBJECT.getValue());
                    if (userVisibleList != null && Util.isContains(userVisibleList, propertyType))
                        fieldObject.setVisible(true);
                    else
                        fieldObject.setVisible(false);
                } else {
                    fieldObject.setVisible(true);
                }
                objectProperty.addField(fieldObject);
            }
        }
        return objectProperty;
    }

    public static LVObject getVisibleFieldsForLink(HashMap<Long, java.util.List<PropertyType>> userSettings) {
        LVObject linkProperty = new LVObject();

        List<PropertyType> linkAllPropertyTypes = Manager.getInstance().getEnabledVisibleLinkProperty();

        net.bgx.bgxnetwork.persistence.metadata.ObjectType linkType =
                NotationModel.getInstance().getObjectTypes().get(ObjectType.LINK_OBJECT.getValue());

        for (PropertyType propertyType : linkType.getPropertyTypes()){
            if (propertyType == null) continue;
            if (propertyType.getCodePropertyType() == null) continue;
            if (Util.isContains(linkAllPropertyTypes, propertyType)) {
                FieldObject fieldObject = Util.getLinkAttributesByCode(Long.parseLong(propertyType.getCodePropertyType()));
                if (fieldObject == null) continue;
                if (userSettings != null && userSettings.size() != 0) {
                    ArrayList<PropertyType> userVisibleList =
                            (ArrayList<PropertyType>) userSettings.get((long) ObjectType.LINK_OBJECT.getValue());
                    if (userVisibleList != null && Util.isContains(userVisibleList, propertyType))
                        fieldObject.setVisible(true);
                    else
                        fieldObject.setVisible(false);
                } else {
                    fieldObject.setVisible(true);
                }
                linkProperty.addField(fieldObject);
            }
        }
        return linkProperty;
    }

    public static long getTimeStampByDate(LinkObject lo) {
        LinkWorker worker = new LinkWorker(lo);
        String val = worker.getValueByPropertyCode(14L);
        if (val == null || val.length() == 0)
            return 0L;
        Date dt = FieldObject.convertValueToDate(val);
        if (dt == null)
            return 0L;
        return dt.getTime();
    }
}
