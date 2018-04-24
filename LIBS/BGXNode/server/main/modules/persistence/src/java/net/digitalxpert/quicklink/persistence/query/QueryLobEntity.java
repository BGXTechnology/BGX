package net.bgx.bgxnetwork.persistence.query;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Class QueryLobEntity
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

@Entity
@Table(name = "query_head_lob")
public class QueryLobEntity implements Serializable {
  private Long id;
  private LayoutCoordinates layout;
  private GraphAnnotation annotation;
  private LinkedList<Object> timetablePairs;
  private ArrayList<Object> timetableParameters;
//  private String params;

  public QueryLobEntity() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id_query_lob")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Lob @Basic(fetch = FetchType.EAGER)
  @Column(name = "save_layout")
  public LayoutCoordinates getLayout() {
    return layout;
  }

  public void setLayout(LayoutCoordinates layout) {
    this.layout = layout;
  }

  @Lob @Basic(fetch = FetchType.EAGER)
  @Column(name = "save_annotation")
  public GraphAnnotation getAnnotation() {
    return annotation;
  }

  public void setAnnotation(GraphAnnotation annotation) {
    this.annotation = annotation;
  }

    @Lob @Basic(fetch = FetchType.EAGER)
    @Column(name = "tt_pairs")
    public LinkedList<Object> getTimetablePairs() {
        return timetablePairs;
    }

    public void setTimetablePairs(LinkedList<Object> timetablePairs) {
        this.timetablePairs = timetablePairs;
    }

    @Lob @Basic(fetch = FetchType.EAGER)
    @Column(name = "tt_parameters")
    public ArrayList<Object> getTimetableParameters() {
        return timetableParameters;
    }

    public void setTimetableParameters(ArrayList<Object> ttParameters) {
        this.timetableParameters = ttParameters;
    }
//
//    @Column(name = "Params")
//    public String getParams() {
//        return params;
//    }
//
//    public void setParams(String params) {
//        this.params = params;
//    }
}
