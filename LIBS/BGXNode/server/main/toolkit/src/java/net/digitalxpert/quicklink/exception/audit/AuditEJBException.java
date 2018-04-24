package net.bgx.bgxnetwork.exception.audit;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.EJBException;

public class AuditEJBException extends D3RuntimeException implements EJBException{
    public AuditEJBException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public AuditEJBException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
