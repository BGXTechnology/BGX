package net.bgx.bgxnetwork.bgxop.gui.lv.panel.object;

import java.io.Serializable;
import java.util.List;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class FieldObjectContainer implements Serializable{
	private List<FieldObject> objectProperties ;
	private List<FieldObject> linkProperties ;
	public List<FieldObject> getLinkProperties() {
		return linkProperties;
	}
	public void setLinkProperties(List<FieldObject> linkProperties) {
		this.linkProperties = linkProperties;
	}
	public List<FieldObject> getObjectProperties() {
		return objectProperties;
	}
	public void setObjectProperties(List<FieldObject> objectProperties) {
		this.objectProperties = objectProperties;
	}
}
