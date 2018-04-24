/* $Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/exception/D3ExceptionHelper.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.bgxnetwork.exception;

import java.util.Locale;

/**
 * TODO Document type D3ExceptionHelper
 */
final class D3ExceptionHelper {
    static String getMessage(Locale aLocale, D3ExceptionInterface anException,
                                     String aMessageBySuper) {
        String message = MessageFactory.buildMessage(anException, aLocale);
        if (message == null) message = aMessageBySuper;
        if (message == null)
            message = buildMessageTag(anException) + "no description " +
                    buildArgumentsList(anException);
        return message;
    }

    static String buildMessageTag(D3ExceptionInterface anException) {
        return anException.getModuleCode() + '-' + anException.getErrorCode() + ": ";
    }

    static String buildArgumentsList(D3ExceptionInterface anException) {
        Object[] args = anException.getMessageArguments();
        if (args == null) return "";
        StringBuffer s = new StringBuffer("[");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (i > 0) s.append(", ");
            s.append(arg.toString());
        }
        return s.append("]").toString();
    }
}
