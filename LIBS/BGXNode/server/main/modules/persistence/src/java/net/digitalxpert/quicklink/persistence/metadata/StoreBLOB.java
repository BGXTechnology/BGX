package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.io.Serializable;

/**
 * предназначен для хранения сериализованных значений свойств объектов
 *
 * @version 1.0
 * @created 08-Feb-2007 12:38:43
 */
@Entity
@Table(name = "STORE_BLOB")
public class StoreBLOB implements Serializable {
    private Long storeBlobId;
    private Object objectValue;
    private String descValue;

    public StoreBLOB() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getStoreBlobId() {
        return storeBlobId;
    }

    public void setStoreBlobId(Long storeBlobId) {
        this.storeBlobId = storeBlobId;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "OBJECT_VALUE")
    public Object getObjectValue() {
        return objectValue;
    }


    public void setObjectValue(Object objectValue) {
        this.objectValue = objectValue;
    }

    @Column(name = "DESC_VALUE")
    public String getDescValue() {
        return descValue;
    }

    public void setDescValue(String descValue) {
        this.descValue = descValue;
    }


}