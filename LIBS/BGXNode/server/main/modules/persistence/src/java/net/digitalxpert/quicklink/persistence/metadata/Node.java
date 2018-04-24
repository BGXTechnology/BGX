package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.util.Set;
import java.io.Serializable;

/**
 * NODE графа (таб. PLN_NODE)
 *
 * @version 1.0
 * @created 08-Feb-2007 12:59:05
 */
@Entity
@Table(name = "PLN_NODE$")
public class Node implements Serializable {

    private String active;
    private ControlObject controlObject;
    private NodePK pk;
    private Long objectId;
    private String nodeName;
    private String nodeType;
    private Long partitionId;
    private Set<Link> startLinks;
    private Set<Link> endLinks;


    @Column(name = "OBJECT_ID")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Column(name = "ACTIVE")
    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @OneToOne
    @PrimaryKeyJoinColumn
    //@JoinColumns({@JoinColumn(name = "OBJECT_ID"),@JoinColumn(name="QUERY_ID")})
    public ControlObject getControlObject() {
        return controlObject;
    }

    public void setControlObject(ControlObject controlObject) {
        this.controlObject = controlObject;
    }

    @EmbeddedId
    public NodePK getPk() {
        return pk;
    }

    public void setPk(NodePK pk) {
        this.pk = pk;
    }


    @Column(name = "NODE_NAME")
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Column(name = "NODE_TYPE")
    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    @Column(name = "PARTITION_ID")
    public Long getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Long partitionId) {
        this.partitionId = partitionId;
    }


    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)

    @JoinColumns({@JoinColumn(name = "START_NODE_ID", referencedColumnName = "NODE_ID"),
    @JoinColumn(name = "QUERY_ID", referencedColumnName = "QUERY_ID")})
    public Set<Link> getStartLinks() {
        return startLinks;
    }

    public void setStartLinks(Set<Link> startLinks) {
        this.startLinks = startLinks;
    }

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "END_NODE_ID", referencedColumnName = "NODE_ID"),
    @JoinColumn(name = "QUERY_ID", referencedColumnName = "QUERY_ID")})
    public Set<Link> getEndLinks() {
        return endLinks;
    }

    public void setEndLinks(Set<Link> endLinks) {
        this.endLinks = endLinks;
    }


    public Node() {

    }


}