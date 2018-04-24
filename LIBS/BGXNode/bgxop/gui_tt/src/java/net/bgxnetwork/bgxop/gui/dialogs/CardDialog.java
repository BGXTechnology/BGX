package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.services.CardServiceDelegator;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.query.card.Card;
import net.bgx.bgxnetwork.transfer.query.card.CardField;
import net.bgx.bgxnetwork.transfer.query.card.CardTab;

/**
 * Class CardDialog
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class CardDialog extends JDialog {

    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    protected ResourceBundle guirb = PropertyResourceBundle.getBundle("gui");
    protected ResourceBundle qrb = PropertyResourceBundle.getBundle("query");
    protected static String SEPARATOR = "******************************";
    protected static String TITLE_SEPARATOR = " :: ";
    protected Frame owner;
    protected QueryPanel currentPanel = null;
    protected Card card = null;
//    protected Node node = null;

    public CardDialog(Frame owner, String title, Card card) {
        super(owner, title, true);
        init(owner, card);
    }

    public CardDialog(Frame owner, String title, Card card, QueryPanel currentPanel) {
        super(owner, title, true);
        this.currentPanel = currentPanel;
        init(owner, card);
    }

    public void copyCardToClipboard() {
        StringBuilder builder = new StringBuilder();
        attachHeader(builder);
        for (CardTab tab : card.getTabs()) {
            attachTab(builder, tab, true);
        }
        attachFooter(builder);
        wrapWithHtml(builder);
        //
        JTextComponent hiddenText = new JEditorPane("text/html", builder.toString());
        hiddenText.selectAll();
        hiddenText.copy();
        MessageDialogs.info(this, rb.getString("Card.clipboard.message"), rb.getString("Card.clipboard.title"));
    }

    protected String getCardField(String key) {
        for (CardTab tab : card.getTabs()) {
            for (CardField field : tab.getFields()) {
                if (key.equals(field.getName())) {
                    return field.getValue();
                }
            }
        }
        return null;
    }

    protected void attachField(StringBuilder builder, String key) {
        String value = getCardField(key);
        if(value == null) {
            value = rb.getString("Card.clipboard.error");
        }
        else if (value.equals("")) {
            value = rb.getString("Card.noData");
        }
        builder.append("<tr><td>"+key+": "+value+"</td></tr>");
    }

    protected void init(Frame own, Card card) {
        this.card = card;
        this.owner = own;
        
        setModal(false);
        boolean visible = true;
        JTabbedPane content = new JTabbedPane();
        content.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        content.setFont(UIManager.getFont("Label.font"));

        String innTitle = getCardField(qrb.getString("Card.general.inn"));
        String dialogTitle = getTitle();
        if( innTitle != null && ! innTitle.equals("")) {
            if ( ! dialogTitle.equals("")) {
                innTitle += TITLE_SEPARATOR + dialogTitle;
            }
            setTitle(innTitle);
        }

        for (CardTab tab : card.getTabs()) {
            if ( tab == null ) {
                continue;
            }
            JPanel t = new JPanel(new GridBagLayout());
            StringBuilder builder = new StringBuilder();
            attachTab(builder, tab, false);
            wrapWithHtml(builder);
            JEditorPane info = new JEditorPane("text/html", builder.toString());
            if ( info != null && info.getText().length() > 0 ) {
                info.setCaretPosition(0);
            }
            info.setEditable(false);
            t.add(new JScrollPane(info), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            content.addTab(tab.getName(), t);
        }
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(content, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 10), 0, 0));
        JButton close = new JButton(rb.getString("CardDialog.button"));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardDialog.this.processWindowEvent(new WindowEvent(CardDialog.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        getContentPane().add(content, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().setPreferredSize(new Dimension(450, 350));
        pack();
        setLocationRelativeTo(owner);
        QueryPanel panel = null;
        try {
            panel = ((MainFrame) owner).getQueryListController().getCurrentComponent();
        }
        catch (Exception e) {
        }
        if ( currentPanel != null ) {
            currentPanel.addDialogs(this);
            if ( panel == null )
                visible = true;
            else if ( !currentPanel.equals(panel) ) {
                visible = false;
            }
        }
        else if ( panel != null ) {
            currentPanel = panel;
            panel.addDialogs(this);
        }
        GlobalPopupUtil.initListeners(this);
        setVisible(visible);
    }

    protected void wrapWithHtml(StringBuilder builder) {
        builder.insert(0, "<html><body>");
        builder.append("</body></html>");
    }

    protected void attachHeader(StringBuilder builder) {
        ObjectType objectType = card.getObjectType();
        builder.append("<table>");
        builder.append("<tr><td><b>"+SEPARATOR+"</b></td></tr>");
        builder.append("<tr><td><b>"+rb.getString("Card.clipboard.header")+"</b></td></tr>");

        switch (objectType) {
            default:
                attachField(builder, qrb.getString("Card.general.fullName"));
                attachField(builder, qrb.getString("Card.general.shortName"));
                attachField(builder, qrb.getString("Card.general.inn"));
                break;
        }
        builder.append("<tr><td><b>"+SEPARATOR+"</b></td></tr>");
        builder.append("<tr><td>");
    }
    
    protected void attachTab(StringBuilder builder, CardTab tab, boolean withTabName) {
        boolean isActualWrite = false;
        boolean isHistoryWrite = false;
        boolean isActualBlock = false;
        builder.append("<table>");
        if (withTabName) {
            builder.append("<tr><td colspan=2><b>"+rb.getString("Card.section")+" "+tab.getName()+"</b></td></tr>");
        }
        for (CardField f : tab.getFields()) {
            if ( (!isActualWrite || !isActualBlock) && f.isActual() ) {
                builder.append("<tr><td colspan=2><b>"+SEPARATOR+"</b></td></tr>");
                builder.append("<tr><td colspan=2><b>" + rb.getString("Card.general.actual") + "</b></td></tr>");
                builder.append("<tr><td colspan=2><b>"+SEPARATOR+"</b></td></tr>");
                isActualWrite = true;
                isActualBlock = true;
            }
            else if ( (!isHistoryWrite || isActualBlock) && !f.isActual() ) {
                builder.append("<tr><td colspan=2><b>"+SEPARATOR+"</b></td></tr>");
                builder.append("<tr><td colspan=2><b>" + rb.getString("Card.general.history") + "</b></td></tr>");
                builder.append("<tr><td colspan=2><b>"+SEPARATOR+"</b></td></tr>");
                isHistoryWrite = true;
                isActualBlock = false;
            }
            builder.append("<tr><td><b>" + f.getName() + "</b></td><td>" + f.getValue() + "</td></tr>");
        }
        if ( tab.getFields().size() == 0 ) {
            builder.append("<tr><td><b>" + rb.getString("Card.noData") + "</b></td></tr>");
        }
        builder.append("</table>");
    }

    protected void attachFooter(StringBuilder builder) {
        String dateTimeFormatted = null;
        try {
            long serverTime = CardServiceDelegator.getInstance().getServerTime();
            dateTimeFormatted = DateFormat.getDateTimeInstance().format(new Date(serverTime));
        }
        catch (Exception e) {
            dateTimeFormatted = rb.getString("Card.clipboard.error");
            e.printStackTrace();
        }
        builder.append("</td></tr>");
        builder.append("<tr><td><b>"+SEPARATOR+"</b></td></tr>");
        builder.append("<tr><td><b>");
        builder.append(", ["+dateTimeFormatted+"]");
        builder.append("</b></td></tr>");

        builder.append("<tr><td><b>"+SEPARATOR+"</b></td></tr>");
        builder.append("</table>");
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            if ( currentPanel != null ) {
                currentPanel.removeDialog(this);
            }
        }
    }
}