/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.data;

/**
 * User: A.Borisenko
 * Date: 11.06.2007
 * Time: 19:16:05
 */
public class Relation extends LVObject{
    private Long qID;
    private String sourceObject;
    private String targetObject;

    public Long getqID() {
        return qID;
    }

    public void setqID(Long qID) {
        this.qID = qID;
    }

    public String getSourceObject() {
        return sourceObject;
    }

    public void setSourceObject(String sourceObject) {
        this.sourceObject = sourceObject;
    }

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }
}
