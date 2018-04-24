package net.bgx.bgxnetwork.persistence.query;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: yerokhin
 * Date: 04.04.2006
 * Time: 13:18:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "query_type")
public class QueryTypeEntity implements Serializable {
  private Integer id;
  private String name;
  private String description;
  private Collection<ParameterTypeEntity> parameters;
  private String dialogClassName;
  private String executorClassName;
  private String roleName;

  public QueryTypeEntity() {
  }

  public QueryTypeEntity(Integer id) {
    this.id = id;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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

  @Column(name = "dialog_class_name")
  public String getDialogClassName() {
    return dialogClassName;
  }

  public void setDialogClassName(String dialogClassName) {
    this.dialogClassName = dialogClassName;
  }

  @Column(name = "executor_class_name")
  public String getExecutorClassName() {
    return executorClassName;
  }

  public void setExecutorClassName(String executorClassName) {
    this.executorClassName = executorClassName;
  }

  @Column(name = "role_name")
  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy="queryTypes")  
  public Collection<ParameterTypeEntity> getParameters() {
    return parameters;
  }

  public void setParameters(Collection<ParameterTypeEntity> parameters) {
    this.parameters = parameters;
  }
}
