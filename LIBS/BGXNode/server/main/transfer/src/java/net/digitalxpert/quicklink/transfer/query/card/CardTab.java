package net.bgx.bgxnetwork.transfer.query.card;

import java.util.List;
import java.io.Serializable;

/**
 * Class CardTab
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CardTab implements Serializable {
  private String name;
  private List<CardField> fields;

  public CardTab(String name, List<CardField> fields) {
    this.name = name;
    this.fields = fields;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<CardField> getFields() {
    return fields;
  }

  public void setFields(List<CardField> fields) {
    this.fields = fields;
  }
}
