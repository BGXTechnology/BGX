package net.bgx.bgxnetwork.exception.security;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.EJBException;

public class SecurityEJBException extends D3RuntimeException implements EJBException{
    public SecurityEJBException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public SecurityEJBException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
