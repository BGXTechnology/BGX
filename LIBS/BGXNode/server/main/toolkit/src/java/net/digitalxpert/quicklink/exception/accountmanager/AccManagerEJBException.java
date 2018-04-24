package net.bgx.bgxnetwork.exception.accountmanager;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.EJBException;

public class AccManagerEJBException extends D3RuntimeException implements EJBException{
    public AccManagerEJBException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public AccManagerEJBException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
