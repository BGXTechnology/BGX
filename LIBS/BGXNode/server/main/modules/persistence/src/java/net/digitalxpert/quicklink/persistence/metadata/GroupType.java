package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

/**
 * User: O.Gerasimenko
 * Date: 02.03.2007
 * Time: 11:56:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "GROUP_TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE_GROUP", discriminatorType = DiscriminatorType.INTEGER)
public abstract class GroupType implements Serializable {
    private Integer idGroup;
    private String nameGroup;
    private Integer typeGroup;
    private Integer sortIndex;
    private Date dateChange;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_GROUP")
    public Integer getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Integer idGroup) {
        this.idGroup = idGroup;
    }

        @Column(name = "NAME_GROUP")
    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }
    @Column(name="TYPE_GROUP", insertable = false, updatable = false)
    public Integer getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(Integer typeGroup) {
        this.typeGroup = typeGroup;
    }
    @Column(name="SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }
    @Column(name="DATE_CHANGE")
    @Temporal(TemporalType.DATE)
    public Date getDateChange() {
        return dateChange;
    }

    public void setDateChange(Date dateChange) {
        this.dateChange = dateChange;
    }
}
