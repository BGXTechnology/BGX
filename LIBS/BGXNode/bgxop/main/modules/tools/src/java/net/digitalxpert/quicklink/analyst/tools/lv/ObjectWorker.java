/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.tools.lv;

import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.util.PropertyWorker;

/**
 * User: A.Borisenko
 * Date: 18.06.2007
 * Time: 17:38:10
 */
public class ObjectWorker {
    private final String NAME = "objectName";
    private final String ISVISIBLE = "visible";
    private final String GRP_ID = "group";

    private ControlObject _object;

    public ObjectWorker(ControlObject _object) {
        this._object = _object;
    }

    public String getName() {
        return PropertyWorker.getValuePropertyAsString(_object, NAME);
    }

    public boolean isVisible() {
        String stringVal = PropertyWorker.getValuePropertyAsString(_object, ISVISIBLE);
        return stringVal != null && "1".equals(stringVal);
    }

    public void setVisible(boolean b) {
        if (b) PropertyWorker.setValuePropertyAsString(_object, ISVISIBLE, "1");
        else PropertyWorker.setValuePropertyAsString(_object, ISVISIBLE, "0");
    }

    public String getValueByProperty(PropertyType propertyType) {
        return PropertyWorker.getValuePropertyAsString(_object, propertyType.getPropertyTypeId());
    }

    public String getValueByPropertyCode(Long code) {
        return PropertyWorker.getValuePropertyAsString(_object, code);
    }

    public void setGroupId(Long id) {
        PropertyWorker.setValuePropertyAsString(_object, GRP_ID, "" + id);
    }

    public Long getGroupId() {
        String stringVal = PropertyWorker.getValuePropertyAsString(_object, GRP_ID);
        return stringVal != null ? new Long(stringVal) : null;
    }

}
