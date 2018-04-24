/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.tt;

import net.bgx.bgxnetwork.persistence.metadata.ControlObject;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 * User: A.Borisenko
 * Date: 17.07.2007
 * Time: 11:51:53
 */
public class TransferControlObjectPair implements Serializable {
    private Boolean isSelected;
    Long objectId;
    Long linkedId;
    Long queryId;
//    private ControlObject controlObject;
//    private ControlObject linkedObject;

//    private HashSet<ControlObject> neighbors;
//
//    public HashSet<ControlObject> getNeighbors() {
//        return neighbors;
//    }
//    public void setNeighbors(Set<ControlObject> neighbors) {
//        this.neighbors = new HashSet<ControlObject>();
//        for(ControlObject co : neighbors){
//            this.neighbors.add(co);
//        }
//    }

//    public ControlObject getControlObject() {
//        return controlObject;
//    }
//    public void setControlObject(ControlObject controlObject) {
//        this.controlObject = controlObject;
//    }
//    public ControlObject getLinkedObject() {
//        return linkedObject;
//    }
//    public void setLinkedObject(ControlObject linkedObject) {
//        this.linkedObject = linkedObject;
//    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(Long linkedId) {
        this.linkedId = linkedId;
    }

    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public Boolean isSelected() {
        return isSelected;
    }
    public void setSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }
}