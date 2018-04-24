package net.bgx.bgxnetwork.exception.audit;

import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.DataException;

public class AuditDataException extends D3RuntimeException implements DataException{
    public AuditDataException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public AuditDataException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
