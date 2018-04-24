package net.bgx.bgxnetwork.audit.strategy;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import javax.interceptor.InvocationContext;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;

public abstract class AbstractLog implements Serializable{
    protected String note;
    protected String params;
    protected int code;
    protected Principal principal;
    protected ArrayList<EventLog> logs = new ArrayList<EventLog>();
    public abstract void process(InvocationContext inv, int code, Principal principal);
    public int getCode(){
        return code;
    }
    public String getNote(){
        return note;
    }
    public void createLog(){
        EventLog log = new EventLog();
        log.setCode(code);
        if(note != null){
            log.setArticle(note.substring(0, note.length() > 127 ? 127 : note.length()));
        }
        log.setParams(params);
        log.setEventTime(System.currentTimeMillis());
        log.setLogin(principal != null ? principal.getName() : "unknown");
        logs.add(log);
    }
    public ArrayList<EventLog> getLogs(){
        ArrayList<EventLog> newLogs = logs;
        cleanAll();
        return newLogs;
    }
    private void cleanAll(){
        logs = new ArrayList<EventLog>();
        note = null;
        code = 0;
        principal = null;
        params = null;
    }
}
