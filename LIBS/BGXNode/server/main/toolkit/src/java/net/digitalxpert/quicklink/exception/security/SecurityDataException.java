package net.bgx.bgxnetwork.exception.security;

import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.DataException;

public class SecurityDataException extends D3RuntimeException implements DataException{
    public SecurityDataException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public SecurityDataException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
