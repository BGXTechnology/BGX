package net.bgx.bgxnetwork.query.dao.util;

import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.query.LinkType;

/**
 * Created by IntelliJ IDEA.
 * User: A.Borisenko
 * Date: 08.11.2006
 * Time: 16:28:19
 * To change this template use File | Settings | File Templates.
 */
public class Helper {
    public static String dbString(String s){
        if(s == null)
            return "null";
        else
            return '\'' + s.replaceAll("'", "''") + '\'';
    }
    public static String dbString(Long val){
        if(val == null)
            return "null";
        else
            return val.toString();
    }
    public static String dbString(Integer val){
        if(val == null)
            return "null";
        else
            return val.toString();
    }
    public static String dbString(ObjectType val){
        if(val == null)
            return "null";
        else
            return String.valueOf(val.getValue());
    }
    public static String dbString(LinkType val){
        if(val == null)
            return "null";
        else
            return String.valueOf(val.getValue());
    }

}
