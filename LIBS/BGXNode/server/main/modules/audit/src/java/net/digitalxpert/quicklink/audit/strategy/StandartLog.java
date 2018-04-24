package net.bgx.bgxnetwork.audit.strategy;
import java.security.Principal;
import javax.interceptor.InvocationContext;

public class StandartLog extends AbstractLog{
    public void process(InvocationContext inv, int code, Principal principal){
        super.code = code;
        super.principal = principal;
        createLog();
    }
}
