/**
 * User: A.Borisenko
 * Date: 23.06.2007
 * Time: 13:07:38
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.meta;

import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.bgxop.services.MetaDataServiceDelegator;
import net.bgx.bgxnetwork.transfer.query.ObjectType;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    private List<PropertyType> objectAllPropertyTypes = null;
    private List<PropertyType> linkAllPropertyTypes = null;

    private static Manager ourInstance = new Manager();

    public static Manager getInstance() {
        return ourInstance;
    }

    private Manager() {
    }

    public List<PropertyType> getEnabledVisibleObjectProperty(){
        if (objectAllPropertyTypes == null)
            try {
                objectAllPropertyTypes =
                        (List<PropertyType>) MetaDataServiceDelegator.getInstance().getVisiblePropertyByObjectType((long) ObjectType.SIMPLE_OBJECT.getValue());
            }
            catch (Exception e) {
                return new ArrayList<PropertyType>();
            }
        return objectAllPropertyTypes;
    }

    public List<PropertyType> getEnabledVisibleLinkProperty(){
        if (linkAllPropertyTypes == null)
            try {
                linkAllPropertyTypes =
                        (List<PropertyType>) MetaDataServiceDelegator.getInstance().getVisiblePropertyByObjectType((long) ObjectType.LINK_OBJECT.getValue());
            }
            catch (Exception e) {
                return new ArrayList<PropertyType>();
            }
        return linkAllPropertyTypes;
    }
}
