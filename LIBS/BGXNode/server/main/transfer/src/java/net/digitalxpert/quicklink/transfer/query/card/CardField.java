package net.bgx.bgxnetwork.transfer.query.card;

import java.io.Serializable;

/**
 * Class CardField
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CardField implements Serializable {
  private String name;
  private String value;
  private boolean isActual = false;

  public CardField(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public CardField(String name, String value, boolean actual) {
      this.name = name;
      this.value = value;
      setActual(actual);
  }
    
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

    public boolean isActual() {
        return isActual;
    }

    public void setActual(boolean actual) {
        isActual = actual;
    }

    public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final CardField cardField = (CardField) o;

    if (name != null ? !name.equals(cardField.name) : cardField.name != null) return false;
    if (value != null ? !value.equals(cardField.value) : cardField.value != null) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (name != null ? name.hashCode() : 0);
    result = 29 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  public static CardField getSeparator(boolean flag){
    return new CardField("-------", "-------------", flag);
  }
}
