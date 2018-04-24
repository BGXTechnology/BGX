/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.request;

import net.bgx.bgxnetwork.bgxop.gui.QueryListPanel;

import javax.swing.*;
import java.awt.*;

/**
 * User: A.Borisenko
 * Date: 19.06.2007
 * Time: 9:52:52
 */
public class RequestPanel extends JPanel {
    private Long requestId;
    private QueryListPanel queryListPanel;

    public RequestPanel(Long requestId) {
        this.requestId = requestId;
    }

    public QueryListPanel getQueryListPanel() {
        return queryListPanel;
    }

    public void setQueryListPanel(QueryListPanel queryListPanel) {
        this.queryListPanel = queryListPanel;
        this.setLayout(new BorderLayout());
        this.add(queryListPanel, BorderLayout.CENTER);
    }

    public Long getRequestId() {
        return requestId;
    }
}
