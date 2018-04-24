package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * User: yerokhin
 * Date: 04.04.2006
 * Time: 13:18:31
 */
public class QueryType implements Serializable {
    public static final Long REQUEST = 1L;
    public static final Long QUEST = 2L;
    public static final Long VISUALIZATION = 3L;

  private int id;
  private String name;
  private String description;
  private Map<QueryParameterType, QueryParameterDataType> requiredParameters =
      new HashMap<QueryParameterType, QueryParameterDataType>();
  private Map<QueryParameterType, QueryParameterDataType> optionalParameters =
      new HashMap<QueryParameterType, QueryParameterDataType>();
  private String dialogClassName;
  private String roleName;

  public QueryType() {
  }

  public QueryType(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<QueryParameterType, QueryParameterDataType> getRequiredParameters() {
    return requiredParameters;
  }

  public void setRequiredParameters(Map<QueryParameterType, QueryParameterDataType> requiredParameters) {
    this.requiredParameters = requiredParameters;
  }

  public Map<QueryParameterType, QueryParameterDataType> getOptionalParameters() {
    return optionalParameters;
  }

  public void setOptionalParameters(Map<QueryParameterType, QueryParameterDataType> optionalParameters) {
    this.optionalParameters = optionalParameters;
  }

  private void addRequiredParameter(QueryParameterType parameter, QueryParameterDataType type) {
    requiredParameters.put(parameter, type);
  }

  private void addOptionalParameter(QueryParameterType parameter, QueryParameterDataType type) {
    optionalParameters.put(parameter, type);
  }

  public QueryParameterDataType getParameterDataType (QueryParameterType parameter) {
    QueryParameterDataType res = requiredParameters.get(parameter);
    if (res == null) res = optionalParameters.get(parameter);
    if (res == null) System.out.println("Wrong parameter was found : "+parameter.name());
    return res;
  }

  public void addParameter (QueryParameterType parameter, QueryParameterDataType type, boolean optional) {
    if (optional) addOptionalParameter(parameter, type);
    else addRequiredParameter(parameter, type);
  }

  public void removeParameter (QueryParameterType parameter) {
    optionalParameters.remove(parameter);
    requiredParameters.remove(parameter);
  }

  public String getDialogClassName() {
    return dialogClassName;
  }

  public void setDialogClassName(String dialogClassName) {
    this.dialogClassName = dialogClassName;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final QueryType queryType = (QueryType) o;

    if (id != queryType.id) return false;

    return true;
  }

  public int hashCode() {
    return id;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }
}
