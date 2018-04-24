package net.bgx.bgxnetwork.audit.strategy;

import java.security.Principal;
import javax.interceptor.InvocationContext;

public class AuditLog extends AbstractLog{
    public void process(InvocationContext inv, int code, Principal principal){
//      add deleteAllLogs message
//      add else with "unknow"
    }

}
