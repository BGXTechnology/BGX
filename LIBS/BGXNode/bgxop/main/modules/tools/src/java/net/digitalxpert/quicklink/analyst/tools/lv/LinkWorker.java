/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.tools.lv;

import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.util.PropertyWorker;

/**
 * User: A.Borisenko
 * Date: 22.06.2007
 * Time: 11:39:03
 */
public class LinkWorker {
    private final String ISVISIBLE = "visible";

    private LinkObject _linkObject;
    public LinkWorker(LinkObject linkObject) {
        this._linkObject = linkObject;
    }

    public boolean isVisibleInIntegratedLink(){
        String stringVal = PropertyWorker.getValuePropertyAsString(_linkObject, ISVISIBLE);
        return stringVal != null && "1".equals(stringVal);
    }


    public boolean existPropertyVisibleInIntegratedLink(){
        String stringVal = PropertyWorker.getValuePropertyAsString(_linkObject, ISVISIBLE);
        return stringVal != null;
    }

    public void setVisibleInIntegratedLink(boolean flag){
        if (flag) PropertyWorker.setValuePropertyAsString(_linkObject, ISVISIBLE, "1");
        else PropertyWorker.setValuePropertyAsString(_linkObject, ISVISIBLE, "0");
    }

    public String getValueByProperty(PropertyType propertyType){
        return PropertyWorker.getValuePropertyAsString(_linkObject, propertyType.getPropertyTypeId());
    }

    public String getValueByPropertyCode(Long code){
        return PropertyWorker.getValuePropertyAsString(_linkObject, code);
    }
}
