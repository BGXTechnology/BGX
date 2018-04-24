package net.bgx.bgxnetwork.persistence.query;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Class ParameterTypeEntity
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

@Entity
@Table(name = "query_parametr")
public class ParameterTypeEntity implements Serializable {
  private Integer parameterId;
  private String name;
  private Boolean optional;
  private Boolean collection;
//  private Integer queryTypeId;
  private Collection<QueryTypeEntity> queryTypes;

  public ParameterTypeEntity() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "query_parametr_id")
  public Integer getParameterId() {
    return parameterId;
  }

  public void setParameterId(Integer parameterId) {
    this.parameterId = parameterId;
  }

  @Column(name = "name", length = 254)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "optional")
  public Boolean getOptional() {
    return optional;
  }

  public void setOptional(Boolean optional) {
    this.optional = optional;
  }

  @Column(name = "query_parametr_type")
  public Boolean getCollection() {
    return collection;
  }

  public void setCollection(Boolean collection) {
    this.collection = collection;
  }

//  @Column(name = "id")
//  public Integer getQueryTypeId() {
//    return queryTypeId;
//  }
//
//  public void setQueryTypeId(Integer queryTypeId) {
//    this.queryTypeId = queryTypeId;
//  }

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "query_type_parameters",
      joinColumns = {@JoinColumn(name = "parameter_type_id")},
      inverseJoinColumns = {@JoinColumn(name = "query_type_id")})
  public Collection<QueryTypeEntity> getQueryTypes() {
    return queryTypes;
  }

  public void setQueryTypes(Collection<QueryTypeEntity> queryTypes) {
    this.queryTypes = queryTypes;
  }
}
