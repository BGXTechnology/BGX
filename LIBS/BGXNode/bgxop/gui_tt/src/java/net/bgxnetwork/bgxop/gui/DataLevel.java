package net.bgx.bgxnetwork.bgxop.gui;

import java.io.Serializable;

/**
 * User: A.Borisenko
 * Date: 02.07.2007
 * Time: 13:48:19
 */
public enum DataLevel implements Serializable {
    GraphCard(1),
    TableCard(2),
    TimeCard(3);

    private int value;

    DataLevel(int i){
        this.value = i;
    }
    public int getValue() {
        return value;
    }

    public static DataLevel getByValue(int val){
        for(DataLevel dl : values())
            if(dl.getValue() == val)
                return dl;
        return null;
    }

}
