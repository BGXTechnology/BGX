package net.bgx.bgxnetwork.audit.strategy;
import java.security.Principal;
import javax.interceptor.InvocationContext;

public class AccountLog extends AbstractLog{
    public void process(InvocationContext inv, int code, Principal principal){
        super.code = code;
        super.principal = principal;
        Object[] args = inv.getParameters();
        if(args[0] != null){
            generateData(principal.getName());
        }
    }

    //todo
    private void generateData(String login){
        super.note = login;
        createLog();
    }
}
