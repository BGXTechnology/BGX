/* $Id: //depot/MNS/bgxnetwork/server/build/src/java/net/bgx/build/EventLogger.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Output given message with border and time
 */
public class EventLogger extends Task {
    private final static String _DELIMITER = "-----------------------------------------------------------------";
    private SimpleDateFormat _format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
    private String _text;

    public String getText() {
        return _text;
    }

    public void setText(String aText) {
        _text = aText;
    }

    public void execute() throws BuildException {
        log(_DELIMITER);
        log(_format.format(new Date()));
        log(getText());
        log(_DELIMITER);
    }
}
