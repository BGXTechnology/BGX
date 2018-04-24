package net.bgx.bgxnetwork.exception.accountmanager;

import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.DataException;

public class AccManagerDataException extends D3RuntimeException implements DataException{
    public AccManagerDataException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public AccManagerDataException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
