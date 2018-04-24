/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.data;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

/**
 * User: A.Borisenko
 * Date: 08.06.2007
 * Time: 12:38:49
 */
public class Request extends  LVObject implements Serializable {
    private Long id;
    private Long userId;
//    private String atr1;
//    private Date atr2;
//    private Long atr3;
//    private Long atr4;
//    private String atr5;
//    private String atr6;
//    private Date atr7;
//    private String atr8;
//    private Date atr9;
    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
