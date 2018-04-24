/**
 * User: A.Borisenko
 * Date: 08.06.2007
 * Time: 14:14:58
 */
package net.bgx.lanv.meta.util;

import net.bgx.lanv.meta.dao.MetaDataReader;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

public class MetaTableHelper {
    private static MetaTableHelper ourInstance = new MetaTableHelper();
    private HashMap<String, HashMap<String, String>> _captions = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, HashMap<String, Long>> _codes = new HashMap<String, HashMap<String, Long>>();

    public static MetaTableHelper getInstance() {
        return ourInstance;
    }

    private MetaTableHelper() {
    }

    public String getColumnName(String tableName, String fieldName, String userName, String pwd){
        if (_captions.get(tableName) == null){
            MetaDataReader mdr = new MetaDataReader();
            _captions.put(tableName, mdr.getCaptionsByTable(tableName, userName, pwd));
        }
        HashMap<String, String> fieldCaptions = (HashMap<String, String>)_captions.get(tableName);
        String caption = (String)fieldCaptions.get(fieldName);
        if (caption == null)  return "";
        return caption;
    }

    public Long getFieldCode(String tableName, String fieldName, String userName, String pwd){
        if (_codes.get(tableName) == null){
            MetaDataReader mdr = new MetaDataReader();
            _codes.put(tableName, mdr.getCodeFieldsByTable(tableName, userName, pwd));
        }
        HashMap<String, Long> fieldCodes = (HashMap<String, Long>)_codes.get(tableName);
        Long code = (Long)fieldCodes.get(fieldName);
        if (code == null) return 0L;
        return code;
    }

    public static LVObject createLVObject(ResultSet res, String tableName, String userName, String pwd) throws SQLException {
        ResultSetMetaData resMeta = res.getMetaData();
        int columnCount = resMeta.getColumnCount();

        LVObject dataObject = new LVObject();
        for (int i = 1; i<=columnCount; i++){
            String columnName = resMeta.getColumnName(i).toUpperCase();
            String columnType = resMeta.getColumnTypeName(i);
            int columnTypeCode = resMeta.getColumnType(i);

            String caption = MetaTableHelper.getInstance().getColumnName(tableName, columnName, userName, pwd);
            Long fieldCode = MetaTableHelper.getInstance().getFieldCode(tableName, columnName, userName, pwd);

            Object val = res.getObject(i);
            if (val instanceof java.sql.Date) {
                val = new Date(res.getTimestamp(i).getTime());
            }

            FieldObject field = new FieldObject();
            if (caption != null){
                field.setCaption(caption);
                field.setValue(val);
                field.setDataType(columnType);
                field.setDataTypeCode(columnTypeCode);
                field.setDbFieldName(columnName);
                field.setCode(fieldCode);
                dataObject.addField(field);
            }
        }
        return dataObject;
    }
}
