package net.bgx.bgxnetwork.transfer.query;

import net.bgx.bgxnetwork.persistence.metadata.PropertyType;

import java.util.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Class Query
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class Query implements Serializable {
  private long id;
  private String name;
  private String description;
  private QueryType queryType = null;
  private Map<QueryParameterType, Object> parameters =
      new EnumMap<QueryParameterType, Object>(QueryParameterType.class);
  private QueryStatus queryStatus = QueryStatus.NotSaved;
  private Date createdDate = null;
  private Date completedDate = null;
  private Date startedDate = null;
  private String networkName = null;
  private int objectsLimit = 0;

    private HashSet<Query> _childs =
            new HashSet<Query>();
    private Long parentId = null;

    private float percent = 0;

  private boolean innIsRequired = true;
  private HashMap<Long, List<PropertyType>> viewedAttributes = new HashMap<Long, List<PropertyType>>();

  public Query() {
  }

  public Query(long id, String name, QueryType queryType, Long parent) {
    this.id = id;
    this.name = name;
    this.queryType = queryType;
    this.parentId = parent;
  }

  public void setQueryStatus(QueryStatus queryStatus) {
    this.queryStatus = queryStatus;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public QueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(QueryType queryType) {
    this.queryType = queryType;
  }

  public QueryStatus getQueryStatus() {
    return queryStatus;
  }

  public Map<QueryParameterType, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<QueryParameterType, Object> parameters) {
    this.parameters = parameters;
  }

  public Object addParameter(QueryParameterType type, Object o) {
    return parameters.put(type, o);
  }

  public Object removeParameter(QueryParameterType type) {
    return parameters.remove(type);
  }

  public Object getParameter(QueryParameterType type) {
    return parameters.get(type);
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public Date getCompletedDate() {
    return completedDate;
  }

  public Date getStartedDate() {
      return startedDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public void setCompletedDate(Date completedDate) {
    this.completedDate = completedDate;
  }

  public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNetworkName() {
    return networkName;
  }

  public void setNetworkName(String networkName) {
    this.networkName = networkName;
  }

  public int getObjectsLimit() {
    return objectsLimit;
  }

  public void setObjectsLimit(int objectsLimit) {
    this.objectsLimit = objectsLimit;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Query query = (Query) o;

    if (id != query.id) return false;

    return true;
  }

  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

    public List<Query> getChilds() {
        ArrayList<Query> outList = new ArrayList<Query>();
        for (Query q : _childs){
            outList.add(q);
        }
        Collections.sort(outList, new Comparator(){
            public int compare(Object o1, Object o2) {
                Query q1 = (Query)o1;
                Query q2 = (Query)o2;
                String name1 = q1.getName();
                String name2 = q2.getName();
                if (name1 == null) return -1;
                if (name2 == null) return 1;
                return name1.compareTo(name2);
            }
        });
        return outList;
    }

    public void addChild(Query q) {
        _childs.add(q);
    }

    public Long getParent() {
        return parentId;
    }

    public void setParent(Long _parentId) {
        this.parentId = _parentId;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float _percent) {
        this.percent = _percent;
    }

    public boolean isInnIsRequired() {
        return innIsRequired;
    }

    public void setInnIsRequired(boolean _innIsRequired) {
        this.innIsRequired = _innIsRequired;
    }

    public HashMap<Long, List<PropertyType>> getViewedAttributes() {
        return viewedAttributes;
    }

    public void setViewedAttributes(HashMap<Long, List<PropertyType>> viewedAttributes) {
        this.viewedAttributes = viewedAttributes;
    }

    public void addViewAttribute(Long objectTypeId, PropertyType pt) {
        List<PropertyType> ptByObjectType = viewedAttributes.get(objectTypeId);
        if (ptByObjectType == null)
            ptByObjectType = new ArrayList<PropertyType>();

        if (!ptByObjectType.contains(pt))
            ptByObjectType.add(pt);

        viewedAttributes.put(objectTypeId, ptByObjectType);
    }
}
