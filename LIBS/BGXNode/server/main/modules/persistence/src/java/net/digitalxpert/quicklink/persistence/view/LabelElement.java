package net.bgx.bgxnetwork.persistence.view;

import net.bgx.bgxnetwork.persistence.metadata.PropertyType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: O.Gerasimenko
 * Date: 14.02.2007
 * Time: 10:46:18
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "LABEL_ELEMENT")
public class LabelElement implements Serializable {
    private Integer labelElementId;
    private String keyName;
    private PropertyType propertyType;
    private Integer sortIndex;
    private Integer propertyTypeId;
    private String pattern;


    public LabelElement() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ELEMENT_ID")
    public Integer getLabelElementId() {
        return labelElementId;
    }

    public void setLabelElementId(Integer labelElementId) {
        this.labelElementId = labelElementId;
    }

    @Column(name = "KEY_NAME")
    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @ManyToOne
    @JoinColumn(name = "PROPERTY_TYPE_ID")
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }


    @Column(name = "PROPERTY_TYPE_ID",insertable = false, updatable = false)
    public Integer getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(Integer propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    @Column(name="PATTERN")
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
