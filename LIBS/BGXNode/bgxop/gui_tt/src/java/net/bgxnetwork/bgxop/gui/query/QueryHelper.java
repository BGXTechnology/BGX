/**
 * Created by IntelliJ IDEA.
 * User: A.Borisenko
 * Date: 12.02.2007
 * Time: 16:43:50
 * To change this template use File | Settings | File Templates.
 */
package net.bgx.bgxnetwork.bgxop.gui.query;

import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;

import java.util.List;

public class QueryHelper {
    private static QueryHelper ourInstance = new QueryHelper();
    private List<QueryType> queryTypes = null;

    public static QueryHelper getInstance() {
        return ourInstance;
    }

    private QueryHelper() {
    }

    public QueryType getQueryType(int id){
        if (queryTypes == null)
            readQueryTypes();
        for(QueryType qt : queryTypes){
            if(qt.getId() == id)
                return qt;
        }
        return null;
    }

    private void readQueryTypes(){
        try{
            queryTypes = QueryServiceDelegator.getInstance().getQueryTypeList();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
