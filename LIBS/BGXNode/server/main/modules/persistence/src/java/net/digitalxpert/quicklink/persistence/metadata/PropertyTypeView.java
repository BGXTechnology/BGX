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
 * Date: 22.06.2007
 * Time: 11:09:05
 */
@Entity
@Table(name = "PROPERTY_TYPE_VIEW")
public class PropertyTypeView implements Serializable {
    private Long id;
    private ObjectType objectType;
    private PropertyType propertyType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PTV_ID")
    public Long getId() {
        return id;
    }

    public void setId(Long propertyTypeViewId) {
        this.id = propertyTypeViewId;
    }

    @ManyToOne
    @JoinColumn(name = "OBJECT_TYPE_ID")
    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    @ManyToOne
    @JoinColumn(name = "PROPERTY_TYPE_ID")
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
}
