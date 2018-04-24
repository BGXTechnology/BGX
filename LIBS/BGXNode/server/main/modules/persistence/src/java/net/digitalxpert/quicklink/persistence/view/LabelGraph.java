package net.bgx.bgxnetwork.persistence.view;

import net.bgx.bgxnetwork.persistence.metadata.ObjectType;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;

import org.hibernate.annotations.IndexColumn;

/**
 * User: O.Gerasimenko
 * Date: 14.02.2007
 * Time: 10:26:25
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "LABEL")
public class LabelGraph implements Serializable {
    private Integer labelId;
    private String nameLabel;
    private ObjectType objectType;
    private Boolean defaultLabel;
    private Integer objectTypeId;
    private String pattern;
    private List<LabelElement> labelElements;
    private Integer sortIndex;
    private GroupLabelType groupLabelType;


    public LabelGraph() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LABEL_ID")
    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    @Column(name = "NAME_LABEL")
    public String getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(String nameLabel) {
        this.nameLabel = nameLabel;
    }

    @ManyToOne
    @JoinColumn(name = "OBJECT_TYPE_ID")
    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    @Column(name = "DEFAULT_LABEL")
    public Boolean getDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(Boolean defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "LABEL_LABEL_ELEMENT",
            joinColumns = @JoinColumn(name = "LABEL_ID"),
            inverseJoinColumns = @JoinColumn(name = "ELEMENT_ID"))
    @IndexColumn(name = "INDEX_COL")
    @OrderBy("sortIndex ASC")
    public List<LabelElement> getLabelElements() {
        return labelElements;
    }

    public void setLabelElements(List<LabelElement> labelElements) {
        this.labelElements = labelElements;
    }

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Column(name = "OBJECT_TYPE_ID", insertable = false, updatable = false)
    public Integer getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(Integer objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    /**
     * возвращает шаблон по представлению надписи
     *
     * @return
     */
    @Column(name = "PATTERN")
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "GROUP_LABEL_TYPE", joinColumns = @JoinColumn(name = "ELEMENT_ID"), inverseJoinColumns = @JoinColumn(name = "ID_GROUP"))
    @IndexColumn(name = "INDEX_COL")
    public GroupLabelType getGroupLabelType() {
        return groupLabelType;
    }

    public void setGroupLabelType(GroupLabelType groupLabelType) {
        this.groupLabelType = groupLabelType;
    }
}
