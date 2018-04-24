package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;

/**
 * тип данных значени€
 *
 * @version 1.0
 * @created 08-Feb-2007 12:28:18
 */
@Entity
@Table(name = "VALUE_TYPE")
public class ValueType implements Serializable {

    /**
     * «начение по умолчанию дл€ типа данных
     */
    private String defaultValue;
    /**
     * название типа данных(String, Integer, Array и т.п.)
     */
    private String nameTypeValue;

    /**
     * уникальный ключ
     */
    private Integer valueTypeId;


    @Column(name = "DEFAULT_VALUE")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Column(name = "NAME_VALUE_TYPE")
    public String getNameTypeValue() {
        return nameTypeValue;
    }

    public void setNameTypeValue(String nameTypeValue) {
        this.nameTypeValue = nameTypeValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "VALUE_TYPE_ID")
    public Integer getValueTypeId() {
        return valueTypeId;
    }

    public void setValueTypeId(Integer valueTypeId) {
        this.valueTypeId = valueTypeId;
    }

    public ValueType() {

    }


}