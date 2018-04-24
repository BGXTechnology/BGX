package net.bgx.bgxnetwork.audit.strategy;

import java.security.Principal;
import javax.interceptor.InvocationContext;

import net.bgx.bgxnetwork.audit.interfaces.LoggerServiceLocal;
import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocator;
import net.bgx.bgxnetwork.transfer.query.Query;

public class QueryLog extends AbstractLog {
    public void process(InvocationContext inv, int code, Principal principal) {
        super.code = code;
        super.principal = principal;
        Object[] args = inv.getParameters();
        if (args[0] != null) {
            if (args[0] instanceof Query) {
                generateData((Query) args[0]);
            } else if (args[0] instanceof Long) {
                generateData((Long) args[0]);
            }
        }
        // add else with "unknow"
    }

    private void generateData(Query query) {
        String note = getFullQueryNameById(query.getId());
        super.note = note;
        String params = "" + query.getId();
        super.params = params;
        createLog();
    }

    private void generateData(Long queryId) {
        String params = "" + queryId;
        super.params = params;
        super.note = getFullQueryNameById(queryId);
        createLog();
    }

    private String getQueryNameById(Long id) {
        String name = null;
        LoggerServiceLocal loggerService = (LoggerServiceLocal) ServiceLocator.findEjb3LocalByDefault("LoggerServiceBean");
        name = loggerService.getQueryById(id);
        return name;
    }

    private String getFullQueryNameById(Long id) {
        String name = null;
        LoggerServiceLocal loggerService = (LoggerServiceLocal) ServiceLocator.findEjb3LocalByDefault("LoggerServiceBean");
        name = loggerService.getFullNameById(id);
        return name;
    }
}
