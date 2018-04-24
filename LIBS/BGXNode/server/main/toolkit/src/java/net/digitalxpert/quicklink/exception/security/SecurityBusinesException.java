package net.bgx.bgxnetwork.exception.security;
import net.bgx.bgxnetwork.exception.BusinesException;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.personal.ErrorList;

public class SecurityBusinesException extends D3RuntimeException implements BusinesException{
    public SecurityBusinesException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public SecurityBusinesException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}