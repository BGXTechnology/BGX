package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * User: O.Gerasimenko
 * Date: 08.02.2007
 * Time: 15:55:49
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class NodePK implements Serializable {
    private Long nodeId;
    private Long queryId;

    public NodePK() {
    }


    public NodePK(Long nodeId, Long queryId) {
        this.nodeId = nodeId;
        this.queryId = queryId;
    }

    @Column(name = "NODE_ID")
    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    @Column(name = "QUERY_ID")
    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodePK)) return false;
        NodePK pk = (NodePK) obj;
        if (!(nodeId.equals(pk.getNodeId()))) return false;
        if (!(queryId.equals(pk.getQueryId()))) return false;
        return true;
    }

    public int hashCode() {
        return nodeId.hashCode() + queryId.hashCode();
    }

}

