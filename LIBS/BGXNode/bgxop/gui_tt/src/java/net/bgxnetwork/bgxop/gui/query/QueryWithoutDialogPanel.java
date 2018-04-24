package net.bgx.bgxnetwork.bgxop.gui.query;

import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.bgxop.gui.query.panel.AbstractQueryPanel;

/**
 * User: A.Borisenko
 * Date: 14.09.2006
 * Time: 13:04:23
 * To change this template use File | Settings | File Templates.
 */
public class QueryWithoutDialogPanel extends AbstractQueryPanel {
    public void setQuery(Query q) throws QueryBusinesException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void cleanUp() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void checkForReady() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String produceQueryName(Query query) {
        return "query name placeholder";
    }
}
