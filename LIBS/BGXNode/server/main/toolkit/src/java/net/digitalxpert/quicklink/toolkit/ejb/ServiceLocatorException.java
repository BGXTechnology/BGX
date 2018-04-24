package net.bgx.bgxnetwork.toolkit.ejb;

import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.toolkit.ErrorList;

/**
 * This class implements an exception which can wrimpled a lower-level exception.
 *
 */
public class ServiceLocatorException extends D3RuntimeException {
    public ServiceLocatorException(String aErrorCode) {
        super(ErrorList.MODULE_CODE, aErrorCode);
    }

    public ServiceLocatorException(String aErrorCode, Throwable cause) {
        super(ErrorList.MODULE_CODE, aErrorCode, cause);
    }

    public ServiceLocatorException(String aErrorCode, Object[] aMessageArguments,
                   Throwable cause) {
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments, cause);
    }
}



