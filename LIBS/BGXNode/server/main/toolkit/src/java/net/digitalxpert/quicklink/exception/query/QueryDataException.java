package net.bgx.bgxnetwork.exception.query;
import net.bgx.bgxnetwork.exception.D3Exception;
import net.bgx.bgxnetwork.exception.DataException;

public class QueryDataException extends D3Exception implements DataException{
    public QueryDataException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public QueryDataException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
    public QueryDataException(String aErrorCode, Throwable cause){
        super(ErrorList.MODULE_CODE, aErrorCode, cause);
    }
    public QueryDataException(String aErrorCode, Object[] aMessageArguments, Throwable cause){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments, cause);
    }
}
