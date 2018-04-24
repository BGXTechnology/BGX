package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: O.Gerasimenko
 * Date: 08.02.2007
 * Time: 15:55:49
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class PropertyValPK implements Serializable {
    @EmbeddedId
    private NodePK nodePK;
    private Long propertyTypeId;


    public PropertyValPK() {
    }

    public PropertyValPK(NodePK nodePK, Long propertyTypeId) {
        this.nodePK = nodePK;
        this.propertyTypeId = propertyTypeId;
    }

    @Column(name = "PROP_TYPE_ID")
    public Long getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(Long propertTypeId) {
        this.propertyTypeId = propertTypeId;
    }

    public NodePK getNodePK() {
        return nodePK;
    }

    public void setNodePK(NodePK nodePK) {
        this.nodePK = nodePK;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PropertyValPK)) return false;
        PropertyValPK pk = (PropertyValPK) obj;
        if (!(nodePK.equals(pk.getNodePK()))) return false;
        if (!(propertyTypeId.equals(pk.getPropertyTypeId()))) return false;
        return true;
    }


    public int hashCode() {
        return nodePK.hashCode() + propertyTypeId.hashCode();
    }

}
