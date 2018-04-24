package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * объекты контроля источника
 *
 * @version 1.0
 * @created 08-Feb-2007 12:59:11
 */
@Entity
@Table(name = "PLN_NODE$")
public class ControlObject implements Serializable {

    private NodePK pk;
    private Integer idObjectType;
    private Set<PropertyVal> propertyVals;
    /**
     * тип объекта
     */
    private ObjectType typeObject;

    @EmbeddedId
    public NodePK getPk() {
        return pk;
    }

    public void setPk(NodePK pk) {
        this.pk = pk;
    }


    @Column(name = "OBJECT_TYPE", insertable = false, updatable = false)
    public Integer getIdObjectType() {
        return idObjectType;
    }

    public void setIdObjectType(Integer idObjectType) {
        this.idObjectType = idObjectType;
    }


    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "OBJECT_TYPE", referencedColumnName = "ID_OBJECT_TYPE")
    public ObjectType getTypeObject() {
        return typeObject;
    }

    public void setTypeObject(ObjectType typeObject) {
        this.typeObject = typeObject;
    }


    public ControlObject() {

    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID"),
    @JoinColumn(name = "QUERY_ID", referencedColumnName = "QUERY_ID")})
    public Set<PropertyVal> getPropertyVals() {
        return propertyVals;
    }

    public void setPropertyVals(Set<PropertyVal> propertyVals) {
        this.propertyVals = propertyVals;
    }
}