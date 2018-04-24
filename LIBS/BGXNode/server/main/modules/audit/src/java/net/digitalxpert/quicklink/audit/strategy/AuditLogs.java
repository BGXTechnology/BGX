package net.bgx.bgxnetwork.audit.strategy;


public enum AuditLogs {
    STANDART_LOG(new StandartLog()), 
    AUDIT_LOG(new AuditLog()),
    QUERY_LOG(new QueryLog()); 
    private AbstractLog strategy;
    private AuditLogs(AbstractLog s){
        this.strategy = s;
    }
    public AbstractLog getStrategy(){
        return strategy;
    }
}
