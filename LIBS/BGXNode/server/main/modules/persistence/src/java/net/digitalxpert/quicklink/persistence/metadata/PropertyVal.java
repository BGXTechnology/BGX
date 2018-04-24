package net.bgx.bgxnetwork.persistence.metadata;


import javax.persistence.*;
import java.io.Serializable;

/**
 * @version 1.0
 * @created 08-Feb-2007 12:28:15
 */
@Entity
@Table(name = "PROP_VALUE")
public class PropertyVal implements Serializable {


    /**
     * ключевой объект
     */

    private PropertyValPK pk;

    /**
     * значение
     */
    private String valueObject;


    /**
     * тип свойства
     */

    private PropertyType propertyType;

    /**
     * BLOB объект для сложных значений
     */
    private StoreBLOB storeBLOB;

    @EmbeddedId
    public PropertyValPK getPk() {
        return pk;
    }


    public void setPk(PropertyValPK pk) {
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
        return valueObject;
    }

    public void setValueObject(String valueObject) {
        this.valueObject = valueObject;
    }

    @OneToOne
    @JoinTable(name = "PROP_VAL_STORE")
    public StoreBLOB getStoreBLOB() {
        return storeBLOB;
    }

    public void setStoreBLOB(StoreBLOB storeBLOB) {
        this.storeBLOB = storeBLOB;
    }


    public PropertyVal(PropertyValPK pk) {
        this.pk = pk;
    }

    public PropertyVal() {
    }
}