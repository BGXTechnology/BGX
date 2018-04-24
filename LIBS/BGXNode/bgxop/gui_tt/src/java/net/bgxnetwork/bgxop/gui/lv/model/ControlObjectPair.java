package net.bgx.bgxnetwork.bgxop.gui.lv.model;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.transfer.tt.TransferControlObjectPair;

public class ControlObjectPair implements Serializable{
    private Boolean isSelected;
    private ControlObject controlObject;
    private ControlObject linkedObject;
    private HashSet<ControlObject> neighbors;

    public HashSet<ControlObject> getNeighbors() {
        return neighbors;
    }
    public void setNeighbors(Set<ControlObject> neighbors) {
        this.neighbors = new HashSet<ControlObject>();
        for(ControlObject co :neighbors){
            this.neighbors.add(co);
        }
    }
    public String toString(){
        return LVGraphNetworkUtil.getSimpleName(controlObject);
    }
    public ControlObject getControlObject() {
        return controlObject;
    }
    public void setControlObject(ControlObject controlObject) {
        this.controlObject = controlObject;
    }
    public ControlObject getLinkedObject() {
        return linkedObject;
    }
    public void setLinkedObject(ControlObject linkedObject) {
        this.linkedObject = linkedObject;
    }
    public Boolean isSelected() {
        return isSelected;
    }
    public void setSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }
}
