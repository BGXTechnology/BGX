package net.bgx.bgxnetwork.persistence.metadata;

import net.bgx.bgxnetwork.persistence.view.LabelGraph;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.List;

import org.hibernate.annotations.IndexColumn;

/**
 * User: O.Gerasimenko
 * Date: 14.12.2006
 * Time: 10:12:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "TYPE_OBJECT")
public class ObjectType implements Serializable {
    private Integer idObjectType;
    private String nameObjectType;
    private Object picture;
    private Set<GroupObjectType> groupObjectTypes;
    private String nameClassTypeObject;
    private Integer codeTypeObject;
    private List<PropertyType> propertyTypes;
    private List<LabelGraph> labelGraphs;

    /**
     * определяет связь с внешними ключами типов объектов
     *
     * @return
     */

    @Column(name = "CODE_TYPE_OBJECT")
    public Integer getCodeTypeObject() {
        return codeTypeObject;
    }

    public void setCodeTypeObject(Integer codeTypeObject) {
        this.codeTypeObject = codeTypeObject;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @IndexColumn(name="INDEX_COL")
    @OrderBy("sortOrder ASC, namePropertyType ASC")
    public List<PropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<PropertyType> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "GROUP_OBJECT_TYPE",joinColumns = @JoinColumn(name = "ELEMENT_ID" ),inverseJoinColumns = @JoinColumn(name = "ID_GROUP") )
    @IndexColumn(name="INDEX_COL")
    public Set<GroupObjectType> getGroupObjectTypes() {
        return groupObjectTypes;
    }

    public void setGroupObjectTypes(Set<GroupObjectType> groupObjectTypes) {
        this.groupObjectTypes = groupObjectTypes;
    }

    @Column(name = "NAME_CLASS_OBJECT_TYPE")
    public String getNameClassTypeObject() {
        return nameClassTypeObject;
    }

    public void setNameClassTypeObject(String nameClassTypeObject) {
        this.nameClassTypeObject = nameClassTypeObject;
    }

    public ObjectType() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_OBJECT_TYPE")
    public Integer getIdObjectType() {
        return idObjectType;
    }

    public void setIdObjectType(Integer value) {
        this.idObjectType = value;
    }

    @Column(name = "NAME_OBJECT_TYPE")
    public String getNameObjectType() {
        return nameObjectType;
    }

    public void setNameObjectType(String name) {
        this.nameObjectType = name;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "PICTURE")
    public Object Picture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "objectType", fetch = FetchType.EAGER)
    @OrderBy("defaultLabel DESC ,sortIndex ASC, nameLabel ASC")
    public List<LabelGraph> getLabels() {
        return labelGraphs;
    }

    public void setLabels(List<LabelGraph> labelGraphs) {
        this.labelGraphs = labelGraphs;
    }
}


