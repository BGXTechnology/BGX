/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.EmbeddedId;
import javax.persistence.Embeddable;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * User: A.Borisenko
 * Date: 06.06.2007
 * Time: 13:35:48
 */
@Embeddable
public class LinkValPK implements Serializable {
    @EmbeddedId
    private LinkPK linkPK;
    private Long propertyTypeId;

    public LinkValPK() {
    }

    public LinkValPK(LinkPK linkPK, Long propertyTypeId) {
        this.linkPK = linkPK;
        this.propertyTypeId = propertyTypeId;
    }

    @Column(name = "PROP_TYPE_ID")
    public Long getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(Long propertTypeId) {
        this.propertyTypeId = propertTypeId;
    }

    public LinkPK getLinkPK() {
        return linkPK;
    }

    public void setLinkPK(LinkPK linkPK) {
        this.linkPK = linkPK;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof LinkValPK)) return false;
        LinkValPK pk = (LinkValPK) obj;
        if (!(linkPK.equals(pk.getLinkPK()))) return false;
        if (!(propertyTypeId.equals(pk.getPropertyTypeId()))) return false;
        return true;
    }


    public int hashCode() {
        return linkPK.hashCode() + propertyTypeId.hashCode();
    }

}
