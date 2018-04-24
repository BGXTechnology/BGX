package net.bgx.bgxnetwork.bgxop.gui.query;
import java.util.HashMap;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.bgxop.gui.query.panel.AbstractQueryPanel;

/**
 * Class QueryDialogLoader
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class QueryDialogLoader{
    public static AbstractQueryPanel getQueryTypePanel(QueryType queryType) throws QueryBusinesException {
        try{
            AbstractQueryPanel instance = (AbstractQueryPanel) Class.forName(queryType.getDialogClassName()).newInstance();
            if(instance instanceof QueryWithoutDialogPanel)
                return null;
            return instance;
        }catch (Exception e){
            String message = "Cannot instantiate dialog class for query type '" + queryType.getName() + "' : " + queryType.getDialogClassName();
            throw new QueryBusinesException(ErrorList.BUSINES_INSTATIATE_DIALOG_EXCEPTION, new Object[]{message});
        }
    }
}
