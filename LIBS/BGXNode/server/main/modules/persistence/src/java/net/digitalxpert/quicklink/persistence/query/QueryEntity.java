package net.bgx.bgxnetwork.persistence.query;

import net.bgx.bgxnetwork.persistence.metadata.PropertyTypeView;

import javax.persistence.*;
import java.util.Date;
import java.util.Collection;
import java.io.Serializable;

/**
 * Class QueryEntity
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

@Entity
@Table(name = "query_head")
public class QueryEntity implements Serializable {
  private Long id;
  private String name;
  private String description;
  private QueryTypeEntity queryType;
  private Long nodeId;
  private Date createdDate;
  private Date completedDate;
  private Date startedDate;
  private String networkName;
  private Integer queryStatus;
  private Long sessionId;
  private Long ownerId;
  private Integer limited;
  private Collection<ParameterValueEntity> parameterValues;
  private QueryLobEntity lob;
  private Float percent;
  private Collection<QueryEntity> childs;
  private QueryEntity parent;
  private Collection<PropertyTypeView> propertyViews;
  private String ttParameters;

  public QueryEntity() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id_query")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ManyToOne
  @JoinColumn(name = "id_query_type")
  public QueryTypeEntity getQueryType() {
    return queryType;
  }

  public void setQueryType(QueryTypeEntity queryType) {
    this.queryType = queryType;
  }

  @Column(name = "id_node")
  public Long getNodeId() {
    return nodeId;
  }

  public void setNodeId(Long nodeId) {
    this.nodeId = nodeId;
  }

  @Column(name = "create_date")
  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  @Column(name = "complete_date")
  public Date getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(Date completedDate) {
    this.completedDate = completedDate;
  }

    @Column(name = "START_DATE")
  public Date getStartedDate() {
    return startedDate;
  }

  public void setStartedDate(Date _startedDate) {
    this.startedDate = _startedDate;
  }

  @Column(name = "spatial_scheme")
  public String getNetworkName() {
    return networkName;
  }

  public void setNetworkName(String networkName) {
    this.networkName = networkName;
  }

  @Column(name = "query_status")
  public Integer getQueryStatus() {
    return queryStatus;
  }

  public void setQueryStatus(Integer queryStatus) {
    this.queryStatus = queryStatus;
  }

  @Column(name = "id_owner")
  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  @Column(name = "is_limited")
  public Integer getLimited() {
    return limited;
  }

  public void setLimited(Integer limited) {
    this.limited = limited;
  }

  @OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE }, fetch = FetchType.EAGER, mappedBy = "queryId")
    public Collection<ParameterValueEntity> getParameterValues() {
      try {
        if (id != null) {
          for (ParameterValueEntity pve : parameterValues) {
            pve.setQueryId(id);
          }
        }
      } catch (Exception ex) {
      }
      return parameterValues;
    }


  public void setParameterValues(Collection<ParameterValueEntity> parameterValues) {
    this.parameterValues = parameterValues;
  }

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "id_query_lob")
  public QueryLobEntity getLob() {
    return lob;
  }

  public void setLob(QueryLobEntity lob) {
    this.lob = lob;
  }

    @Column(name = "PROGRESS_VAl")
    public Float getPercent() {
        return percent;
    }

    public void setPercent(Float _percent) {
        this.percent = _percent;
    }

    @Column(name = "session_id")
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @PreRemove
    public void preRemove() {
        
    }

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public QueryEntity getParent() {
        return parent;
    }

    public void setParent(QueryEntity parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent")
    public Collection<QueryEntity> getChilds(){
        return childs;
    }

    public void setChilds(Collection<QueryEntity> childs) {
        this.childs = childs;
    }

    @ManyToMany
    public Collection<PropertyTypeView> getPropertyViews() {
        return propertyViews;
    }

    public void setPropertyViews(Collection<PropertyTypeView> propertyViews) {
        this.propertyViews = propertyViews;
    }

    @Column(name = "tt_param", length=4000)
    public String getTtParameters() {
        return ttParameters;
    }

    public void setTtParameters(String ttParameters) {
        this.ttParameters = ttParameters;
    }
}
