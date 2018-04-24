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
public class LinkPK implements Serializable {
    private Long linkId;
    private Long queryId;


    public LinkPK() {
    }


    public LinkPK(Long linkId, Long queryId) {
        this.linkId = linkId;
        this.queryId = queryId;

    }

    @Column(name = "LINK_ID")
    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
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
        if (!(linkId.equals(pk.getNodeId()))) return false;
        if (!(queryId.equals(pk.getQueryId()))) return false;
        return true;
    }

    public int hashCode() {
        return linkId.hashCode() + queryId.hashCode();
    }

}
