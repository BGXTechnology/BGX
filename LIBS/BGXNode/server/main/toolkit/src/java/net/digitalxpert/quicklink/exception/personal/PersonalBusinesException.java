package net.bgx.bgxnetwork.exception.personal;
import net.bgx.bgxnetwork.exception.BusinesException;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.EJBException;

public class PersonalBusinesException extends D3RuntimeException implements BusinesException{
    public PersonalBusinesException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public PersonalBusinesException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
