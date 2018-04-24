/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * User: A.Borisenko
 * Date: 06.06.2007
 * Time: 13:33:24
 */
@Entity
@Table(name = "PLN_LINK$")
public class LinkObject implements Serializable {
    private LinkPK pk;
    private Set<LinkValue> propertyVals;
    private ObjectType linkType;
    private Integer idLinkType;
    private Long startNodeId;
    private Long endNodeId;

    @EmbeddedId
    public LinkPK getPk() {
        return pk;
    }

    public void setPk(LinkPK pk) {
        this.pk = pk;
    }

    @Column(name = "RELATION_TYPE", insertable = false, updatable = false)
    public Integer getIdLinkType() {
        return idLinkType;
    }

    public void setIdLinkType(Integer id) {
        this.idLinkType = id;
    }

    @Column(name = "START_NODE_ID")
    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    @Column(name = "END_NODE_ID")
    public Long getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Long endNodeId) {
        this.endNodeId = endNodeId;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "RELATION_TYPE", referencedColumnName = "ID_OBJECT_TYPE")
    public ObjectType getLinkType() {
        return linkType;
    }

    public void setLinkType(ObjectType typeObject) {
        this.linkType = typeObject;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "LINK_ID", referencedColumnName = "LINK_ID"),
    @JoinColumn(name = "QUERY_ID", referencedColumnName = "QUERY_ID")})
    public Set<LinkValue> getPropertyVals() {
        return propertyVals;
    }

    public void setPropertyVals(Set<LinkValue> propertyVals) {
        this.propertyVals = propertyVals;
    }
}
