/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.panel.object;

/**
 * User: A.Borisenko
 * Date: 09.06.2007
 * Time: 21:08:10
 */
public class SimpleComboBoxObject {
    private Long id;
    private String name;

    public String toString(){
        return name;
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
}
