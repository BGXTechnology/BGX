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
public class ControlObjectPK implements Serializable {


    private Long objectId;
    private Long queryId;


    public ControlObjectPK() {
    }


    public ControlObjectPK(Long objectId, Long queryId) {
        this.objectId = objectId;
        this.queryId = queryId;

    }

    @Column(name = "OBJECT_ID")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
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
        if (!(objectId.equals(pk.getNodeId()))) return false;
        if (!(queryId.equals(pk.getQueryId()))) return false;
        return true;
    }

    public int hashCode() {
        return objectId.hashCode() + queryId.hashCode();
    }

}

