package net.bgx.bgxnetwork.transfer.query;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA. User: yerokhin Date: 04.04.2006 Time: 13:30:12 To
 * change this template use File | Settings | File Templates.
 */
public enum QueryParameterType implements Serializable {
    REQUEST_ID(1),
    QUEST_ID(2),
    REQUEST_NAME(3),
    QUEST_NAME(4);

    private int value;
    QueryParameterType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
    public static QueryParameterType getByValue(int val){
        for(QueryParameterType qs : values())
            if(qs.getValue() == val)
                return qs;
        return null;
    }
}
