package net.bgx.bgxnetwork.exception.personal;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.EJBException;

public class PersonalEJBException extends D3RuntimeException implements EJBException{
    public PersonalEJBException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public PersonalEJBException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
