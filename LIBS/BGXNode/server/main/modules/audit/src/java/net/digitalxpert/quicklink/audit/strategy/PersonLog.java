package net.bgx.bgxnetwork.audit.strategy;
import java.security.Principal;
import javax.interceptor.InvocationContext;

public class PersonLog extends AbstractLog{
    public void process(InvocationContext inv, int code, Principal principal){
        super.code = code;
        super.principal = principal;
        Object[] args = inv.getParameters();
        if(args[0] != null){
            generateData(principal.getName());
        }
    }

    private void generateData(String person){
        super.note = person;
        createLog();
    }
}
