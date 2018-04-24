package net.bgx.bgxnetwork.audit.strategy;
public enum AuditCode {
    ACTOR_LOGIN(1), 
    ACTOR_LOGOUT(2), 
    VIEW_ACCOUNTS(3), 
    VIEW_PERSONS(4), 
    VIEW_GROUPS(5), 
    VIEW_AUDIT(6), 
    AUTH_ERROR(7), 
    ERROR_ACCOUNTS(8), 
    ERROR_PERSONS(9), 
    ERROR_GROUPS(10), 
    ERROR_AUDIT(11), 
    UNKNOW_ERROR(12), 
    CREATE_ACCOUNT(13), 
    UPDATE_ACCOUNT(14), 
    DELETE_ACCOUNT(15), 
    UPDATE_GROUP(17), 
    DELETE_GROUP(18),
    CREATE_PERSON(19), 
    UPDATE_PERSON(20), 
    DELETE_PERSON(21), 
    EXECUTE_QUERY(22), 
    SAVE_QUERY(23), 
    DELETE_QUERY(24), 
    EXPORT_QUERY(25), 
    PRINT_QUERY(26), 
    SAVE_IMAGE_QUERY(27), 
    ACCESS_PERMIT(28),
    COPY_VERSION(29),
    OPEN_VERSION (30),
    VISIBLE_OBJECT(35),
    SAVE_LAYOUT(36),
    CREATE_VERSION(37);

    
    private int code;
    
    private AuditCode(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
