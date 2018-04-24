/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.data;

import java.io.Serializable;

/**
 * User: A.Borisenko
 * Date: 08.06.2007
 * Time: 16:05:13
 */
public class Quest extends LVObject implements Serializable {
    private Long requestId;
    private Long id;
    private String name;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
