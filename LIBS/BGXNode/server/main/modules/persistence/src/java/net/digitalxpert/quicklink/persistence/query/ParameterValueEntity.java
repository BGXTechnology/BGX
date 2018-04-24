package net.bgx.bgxnetwork.persistence.query;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class ParameterValueEntity
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

@Entity
@Table(name = "query_argument")
public class ParameterValueEntity implements Serializable {
  private Long id;
  private Long queryId;
//  private ParameterTypeEntity parameter;
  private Integer parameterTypeId;
  private String parameterValue;

  public ParameterValueEntity() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "arg_id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "id_query")
  public Long getQueryId() {
    return queryId;
  }

  public void setQueryId(Long queryId) {
    this.queryId = queryId;
  }

//  @ManyToOne
//  @JoinColumn(name = "query_parametr_id")
//  public ParameterTypeEntity getParameter() {
//    return parameter;
//  }
//
//  public void setParameter(ParameterTypeEntity parameter) {
//    this.parameter = parameter;
//  }

  @Column(name = "QUERY_PARAMETR_ID")
  public Integer getParameterTypeId() {
    return parameterTypeId;
  }

  public void setParameterTypeId(Integer parameterTypeId) {
    this.parameterTypeId = parameterTypeId;
  }

  @Column(name = "value", length = 254)
  public String getParameterValue() {
    return parameterValue;
  }

  public void setParameterValue(String parameterValue) {
    this.parameterValue = parameterValue;
  }
}
