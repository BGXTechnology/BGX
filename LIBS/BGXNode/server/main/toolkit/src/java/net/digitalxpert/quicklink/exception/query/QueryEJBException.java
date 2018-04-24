package net.bgx.bgxnetwork.exception.query;
import net.bgx.bgxnetwork.exception.D3RuntimeException;
import net.bgx.bgxnetwork.exception.EJBException;

public class QueryEJBException extends D3RuntimeException implements EJBException{
    public QueryEJBException(String aErrorCode){
        super(ErrorList.MODULE_CODE, aErrorCode);
    }
    public QueryEJBException(String aErrorCode, Object[] aMessageArguments){
        super(ErrorList.MODULE_CODE, aErrorCode, aMessageArguments);
    }
}
