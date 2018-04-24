package net.bgx.bgxnetwork.exception.query;
import net.bgx.bgxnetwork.exception.BusinesException;
import net.bgx.bgxnetwork.exception.D3Exception;

public class QueryBusinesException extends D3Exception implements BusinesException{
    public QueryBusinesException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public QueryBusinesException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
