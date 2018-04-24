package net.bgx.bgxnetwork.exception.personal;

import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.DataException;

public class PersonalDataException extends D3RuntimeException implements DataException{
    public PersonalDataException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public PersonalDataException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
