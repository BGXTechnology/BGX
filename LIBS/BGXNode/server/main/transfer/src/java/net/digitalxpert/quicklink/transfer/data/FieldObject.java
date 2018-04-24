/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * User: A.Borisenko
 * Date: 08.06.2007
 * Time: 13:06:07
 */
public class FieldObject implements Serializable {
    private Object value;
    private String caption;
    private String dataType;
    private int dataTypeCode;
    private String dbFieldName;
    private Long code;
    private boolean visible;

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(int dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public static Object convertToString(Object val, String sqlType){
        if (val == null) return "";
        if (sqlType.equals("VARCHAR2")){
            return object2String(val);
        }
        else if (sqlType.equals("DATE")){
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            return dateFormat.format(val);
        }
        else if (sqlType.equals("NUMBER")){
            return val.toString();
        }
        else{
            return object2String(val);
        }
    }

    public static Object convertToString(Object val, int sqlTypeCode){
        if (val == null) return "";
        switch(sqlTypeCode){
            case 2 :
                return ""+val.toString();
            case 12 :
                return object2String(val);
            case 91 :
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                return dateFormat.format(object2Date(val));
            default :
                return object2String(val);
        }
    }

    public static Date convertValueToDate(String dateAsString){
        if (dateAsString == null || dateAsString.length() == 0) return null;
        Date dt = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat shortFormatter = new SimpleDateFormat("dd.MM.yyyy");

        try {
            dt = formatter.parse(dateAsString);
        }
        catch (ParseException e) {
            try {
                dt = shortFormatter.parse(dateAsString);
            }
            catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return dt;
    }

    private static String object2String(Object val){
        return val.toString();
    }

    private static Long object2Long(Object val){
        return (Long)val;
    }

    private static Double object2Double(Object val){
        return (Double)val;
    }

    private static Date object2Date(Object val){
        return (Date)val;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
