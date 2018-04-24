/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: A.Borisenko
 * Date: 06.06.2007
 * Time: 13:34:57
 */
@Entity
@Table(name = "LINK_VALUE")
public class LinkValue implements Serializable {
    private LinkValPK pk;
    private String value;
    private PropertyType propertyType;

    @EmbeddedId
    public LinkValPK getPk() {
        return pk;
    }


    public void setPk(LinkValPK pk) {
        this.pk = pk;
    }


    @ManyToOne
    @JoinColumn(name = "PROP_TYPE_ID", insertable = false, updatable = false)
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }


    @Column(name = "VALUE_OBJECT")
    public String getValueObject() {
        return value;
    }

    public void setValueObject(String valueObject) {
        this.value = valueObject;
    }

    public LinkValue(LinkValPK pk) {
        this.pk = pk;
    }

    public LinkValue() {
    }
}
