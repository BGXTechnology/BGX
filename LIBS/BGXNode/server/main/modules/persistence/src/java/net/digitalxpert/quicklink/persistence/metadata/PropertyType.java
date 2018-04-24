package net.bgx.bgxnetwork.persistence.metadata;

import net.bgx.bgxnetwork.persistence.metadata.ValueType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

import org.hibernate.annotations.IndexColumn;

/**
 * тип свойств
 *
 * @version 1.0
 * @updated 08-Feb-2007 14:23:21
 */
@Entity
@Table(name = "PROPERTY_TYPE")
public class PropertyType implements Serializable {
    /**
     * предназначена для связывания типа свойства с возможными внеш. ключами из баз
     * (используется только у кодификационных типов свойств)
     */
    private String codePropertyType;
    /**
     * описание свойства полное (напр. Адрес) пользовательское название
     */
    private String nameDescFull;
    /**
     * описание свойства сокращенное (напр. Адр.) пользовательское название
     */
    private String nameDescShort;
    /**
     * название типа свойства
     */
    private String namePropertyType;


    /**
     * Группировка типов свойств.
     */

    private Set<GroupPropertyType> groupPropertyTypes;
    /**
     * уникальный ключ
     */
    private Integer propertyTypeId;
    /**
     * сортировочный индекс
     */
    private Integer sortOrder;

    private Integer valueTypeId;

    private ValueType valueType;

    @Column(name = "CODE_PROPERTY_TYPE")
    public String getCodePropertyType() {
        return codePropertyType;
    }

    public void setCodePropertyType(String codePropertyType) {
        this.codePropertyType = codePropertyType;
    }

    @Column(name = "NAME_DESC_FULL")
    public String getNameDescFull() {
        return nameDescFull;
    }

    public void setNameDescFull(String nameDescFull) {
        this.nameDescFull = nameDescFull;
    }

    @Column(name = "NAME_DESC_SHORT")
    public String getNameDescShort() {
        return nameDescShort;
    }

    public void setNameDescShort(String nameDescShort) {
        this.nameDescShort = nameDescShort;
    }

    @Column(name = "NAME_PROPERTY_TYPE")
    public String getNamePropertyType() {
        return namePropertyType;
    }

    public void setNamePropertyType(String namePropertyType) {
        this.namePropertyType = namePropertyType;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "GROUP_PROPERTY_TYPE", joinColumns = @JoinColumn(name = "ELEMENT_ID"), inverseJoinColumns = @JoinColumn(name = "ID_GROUP"))
    @IndexColumn(name = "INDEX_COL")
    public Set<GroupPropertyType> getGroupPropertyTypes() {
        return groupPropertyTypes;
    }

    public void setGroupPropertyTypes(Set<GroupPropertyType> groupPropertyTypes) {
        this.groupPropertyTypes = groupPropertyTypes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PROP_TYPE_ID")
    public Integer getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(Integer propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    @Column(name = "SORT_ORDER")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }


    @ManyToOne
    @JoinColumn(name = "VALUE_TYPE_ID")
    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }


    @Column(name = "VALUE_TYPE_ID", insertable = false, updatable = false)
    public Integer getValueTypeId() {
        return valueTypeId;
    }

    public void setValueTypeId(Integer valueTypeId) {
        this.valueTypeId = valueTypeId;
    }


}