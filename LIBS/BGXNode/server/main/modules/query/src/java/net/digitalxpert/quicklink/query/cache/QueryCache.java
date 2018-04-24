/**
 * User: A.Borisenko
 * Date: 11.06.2007
 * Time: 15:26:32
 */
package net.bgx.bgxnetwork.query.cache;

import net.bgx.bgxnetwork.transfer.query.QueryType;

import java.util.ArrayList;
import java.util.List;

public class QueryCache {
    private List<QueryType> queryTypeList = null;

    private static QueryCache ourInstance = new QueryCache();

    public static QueryCache getInstance() {
        return ourInstance;
    }

    private QueryCache() {
    }

    public List<QueryType> getQueryTypeList(){
        return queryTypeList;
    }

    public void setQueryTypeList(List<QueryType> qtl){
        queryTypeList = qtl; 
    }
}
