package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ребра графа (таб. PLN_LINK)
 *
 * @version 1.0
 * @created 08-Feb-2007 12:59:45
 */

@Entity
@Table(name = "PLN_LINK$")
public class Link implements Serializable {

    private String active;
    private String bidirected;
    private Float cost;
    private Long endNodeId;
    private String linkTypeId;
    private String nameLink;
    private LinkPK pk;
    private String relationDescription;
    private Integer relationType;
    private Long startNodeId;
    private Long linkLevel;

    @Column(name = "ACTIVE")
    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Column(name = "BIDIRECTED")
    public String getBidirected() {
        return bidirected;
    }

    public void setBidirected(String bidirected) {
        this.bidirected = bidirected;
    }

    @Column(name = "COST")
    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    @Column(name = "END_NODE_ID")
    public Long getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Long endNodeId) {
        this.endNodeId = endNodeId;
    }

    @EmbeddedId
    public LinkPK getPk() {
        return pk;
    }

    public void setPk(LinkPK pk) {
        this.pk = pk;
    }


    @Column(name = "LINK_TYPE")
    public String getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(String linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    @Column(name = "LINK_NAME")
    public String getNameLink() {
        return nameLink;
    }

    public void setNameLink(String nameLink) {
        this.nameLink = nameLink;
    }


    @Column(name = "RELATION_DESC")
    public String getRelationDescription() {
        return relationDescription;
    }

    public void setRelationDescription(String relationDescription) {
        this.relationDescription = relationDescription;
    }

    @Column(name = "RELATION_TYPE")
    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }

    @Column(name = "START_NODE_ID")
    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    @Column(name = "LINK_LEVEL")
    public Long getLinkLevel() {
        return linkLevel;
    }

    public void setLinkLevel(Long linkLevel) {
        this.linkLevel = linkLevel;
    }


    public Link() {

    }

    public void finalize() throws Throwable {

    }

}