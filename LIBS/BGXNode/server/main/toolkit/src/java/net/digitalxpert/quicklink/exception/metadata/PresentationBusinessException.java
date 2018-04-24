package net.bgx.bgxnetwork.exception.metadata;

import net.bgx.bgxnetwork.exception.D3Exception;
import net.bgx.bgxnetwork.exception.BusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 18:06:01
 * To change this template use File | Settings | File Templates.
 */
public class PresentationBusinessException extends
        D3Exception implements BusinesException

{
    public PresentationBusinessException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public PresentationBusinessException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }

}
