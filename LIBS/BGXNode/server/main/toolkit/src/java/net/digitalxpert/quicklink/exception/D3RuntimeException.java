/* $Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/exception/D3RuntimeException.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.bgxnetwork.exception;

import java.util.Locale;

/**
 * Декларирует обратимые ситуации, не связанные с выполнением бизнес функций.
 */
public class D3RuntimeException extends RuntimeException implements D3ExceptionInterface {
    private Object[] _messageArguments = null;
    private String _moduleCode;
    private String _errorCode;

    public D3RuntimeException(String aModuleCode, String aErrorCode) {
        _moduleCode = aModuleCode;
        _errorCode = aErrorCode;
    }

    public D3RuntimeException(String aModuleCode, String aErrorCode, Object[] aMessageArguments) {
        _moduleCode = aModuleCode;
        _errorCode = aErrorCode;
        _messageArguments = aMessageArguments;
    }

    public D3RuntimeException(String aModuleCode, String aErrorCode, Throwable cause) {
        super(cause);
        _moduleCode = aModuleCode;
        _errorCode = aErrorCode;
    }

    public D3RuntimeException(String aModuleCode, String aErrorCode, Object[] aMessageArguments,
                              Throwable cause) {
        super(cause);
        _moduleCode = aModuleCode;
        _errorCode = aErrorCode;
        _messageArguments = aMessageArguments;
    }

    public String getModuleCode() {
        return _moduleCode;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public Object[] getMessageArguments() {
        return _messageArguments;
    }

    public String getMessage() {
        return D3ExceptionHelper.getMessage(Locale.getDefault(), this, super.getMessage());
    }

    public String getLocalizedMessage(Locale aLocale) {
        return D3ExceptionHelper.getMessage(aLocale, this, super.getMessage());
    }

    public String getCompleteMessage() {
        return D3ExceptionHelper.buildMessageTag(this) + getMessage();
    }

    public String getCompleteLocalizedMessage(Locale aLocale) {
        return D3ExceptionHelper.buildMessageTag(this) + getLocalizedMessage(aLocale);
    }

}
