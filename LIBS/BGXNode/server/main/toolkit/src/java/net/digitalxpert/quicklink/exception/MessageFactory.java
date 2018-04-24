/* $Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/exception/MessageFactory.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.bgxnetwork.exception;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;


/**
 * ������� ��� ������������ ���������.
 */
class MessageFactory {
    private static String getMessagePattern(D3ExceptionInterface anException, Locale aLocale) throws Exception {
        String out = null;
        if (anException != null) {
            String fileName = anException.getClass().getSimpleName().toLowerCase();
            if (aLocale != null) {
                fileName += "_" + aLocale.toString();
            }
            fileName += ".properties";
            InputStream is = anException.getClass().getResourceAsStream(fileName);
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                out = (String) prop.get(anException.getErrorCode());
            } else if (aLocale != null) {
                out = getMessagePattern(anException, null);
            }
        }
        return out;
    }
    static String buildMessage(D3ExceptionInterface anException, Locale aLocale) {
        try {
            String pattern = getMessagePattern(anException, aLocale);
            if (pattern == null)
                return null;
            if (anException.getMessageArguments() == null)
                return pattern;
            return MessageFormat.format(pattern, anException.getMessageArguments());
        } catch (Exception e) {
            return null;
        }
    }
}
