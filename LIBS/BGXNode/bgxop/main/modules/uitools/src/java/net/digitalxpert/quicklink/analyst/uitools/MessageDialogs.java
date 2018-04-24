package net.bgx.bgxnetwork.bgxop.uitools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import net.bgx.bgxnetwork.exception.BusinesException;
import net.bgx.bgxnetwork.exception.D3ExceptionInterface;

/**
 * Class MessageDialogs
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class MessageDialogs extends JDialog implements ActionListener {
    private static Logger log = Logger.getLogger(MessageDialogs.class.getName());
    private static Icon error = null;
    private static Icon confirm = null;
    private static Icon warning = null;
    private static Icon info = null;
    private static ResourceBundle rb = null;
    private JButton b1, b2, b3;
    private JToggleButton details;
    private JScrollPane detailsPanel;
    private JPanel expandedPanel;
    private Status dialogStatus = Status.CANCELED;
    private Runnable callback = null;
    //private boolean status = false;

    public static void generalError(Component parent, Throwable ex) {
        initResourceBundle();
        if ( error == null ) {
            error = UITools.loadIcon(rb.getString("MessageDialogs.error.img"), null);
        }
        String msg = null;
        Throwable source = ex;
        if ( ex instanceof EJBTransactionRolledbackException ) {
            source = ex.getCause();
        }
        if ( source instanceof D3ExceptionInterface ) {
            msg = source.getMessage();
            msg += "\n" + rb.getString("MessageDialogs.error.inModule")
                    + ((D3ExceptionInterface) source).getModuleCode();
            msg += "\n" + getInfoAboutError(source);
            MessageDialogs dialog = new MessageDialogs(parent, msg,
                    rb.getString("MessageDialogs.serverErrorTitle"), rb.getString("MessageDialogs.error.button"), null, error, ex);
            dialog.setResizable(false);
            dialog.showModal();
        }
        else if ( source instanceof EJBAccessException ) {
            msg = rb.getString("MessageDialogs.error.noPermissionsMessage");
            msg += "\n" + rb.getString("MessageDialogs.error.errorAccess");
            warning(parent, msg, rb.getString("MessageDialogs.warning.title"));
        }
        else {
            msg = rb.getString("MessageDialogs.error.unknowMessage");
            msg += "\n" + rb.getString("MessageDialogs.error.errorRamek");
            new MessageDialogs(parent, msg, rb.getString("MessageDialogs.serverErrorTitle"),
                    rb.getString("MessageDialogs.error.button"), null, error, ex);
        }
    }
    public static void generalServerError(Component parent, Throwable e) {
        initResourceBundle();
        generalError(parent, e, rb.getString("MessageDialogs.commonServerErrorMsg")
                + rb.getString("MessageDialogs.serverErrorMsg"),
                rb.getString("MessageDialogs.serverErrorTitle"));
    }
    public static void generalError(Component parent, String msg) {
        initResourceBundle();
        if ( msg == null ) {
            msg = reformatString(rb.getString("MessageDialogs.error.unknowMessage"));
        }
        generalError(parent, null, msg + "\n" + rb.getString("MessageDialogs.serverErrorMsg"),
                rb.getString("MessageDialogs.serverErrorTitle"));
    }
    public static void serverError(Component parent, Throwable e, String msg) {
        initResourceBundle();
        generalError(parent, e, msg + "\n" + rb.getString("MessageDialogs.serverErrorMsg"),
                rb.getString("MessageDialogs.serverErrorTitle"));
    }
    public static void generalError(Component parent, Throwable e, String msg, String title) {
        initResourceBundle();
        if ( e != null )
            log.severe(UITools.getStackTrace(e));
        if ( error == null )
            error = UITools.loadIcon(rb.getString("MessageDialogs.error.img"), null);
        MessageDialogs dialog = new MessageDialogs(parent, msg, title,
                rb.getString("MessageDialogs.error.button"), null, error, e);
        dialog.setResizable(false);
        dialog.showModal();
    }
    public static boolean confirm(Component parent, String msg, String title) {
        initResourceBundle();
        if ( confirm == null )
            confirm = UITools.loadIcon(rb.getString("MessageDialogs.confirm.img"), null);
        MessageDialogs dialog = new MessageDialogs(parent, msg, title,
                rb.getString("MessageDialogs.confirm.ok"),
                rb.getString("MessageDialogs.confirm.cancel"), confirm, null);
        dialog.setResizable(false);
        dialog.showModal();
        return dialog.dialogStatus.equals(Status.ACCEPTED);
    }
    public static void info(Component parent, String msg, String title) {
        initResourceBundle();
        if ( info == null ) {
            info = UITools.loadIcon(rb.getString("MessageDialogs.info.img"), null);
        }
        if ( title == null ) {
            title = rb.getString("MessageDialogs.info.title");
        }
        MessageDialogs dialog = new MessageDialogs(parent, msg, title,
                rb.getString("MessageDialogs.info.button"), null, info, null);
        dialog.setResizable(false);
        dialog.showModal();
    }
    public static void warning(Component parent, String msg, String title) {
        initResourceBundle();
        if ( warning == null ) {
            warning = UITools.loadIcon(rb.getString("MessageDialogs.warning.img"), null);
        }
        if ( title == null ) {
            title = rb.getString("MessageDialogs.warning.title");
        }
        MessageDialogs dialog = new MessageDialogs(parent, msg, title,
                rb.getString("MessageDialogs.warning.button"), null, warning, null);
        dialog.setResizable(false);
        dialog.showModal();
    }
    public static void info(Component parent, String msg, String title, Icon icon) {
        initResourceBundle();
        MessageDialogs dialog = new MessageDialogs(parent, msg, title,
                rb.getString("MessageDialogs.info.button"), null, icon, null);
        dialog.setResizable(false);
        dialog.showModal();
    }
    public static boolean choose(Component parent, String msg, String title, String first, String second) {
        initResourceBundle();
        if ( confirm == null )
            confirm = UITools.loadIcon(rb.getString("MessageDialogs.confirm.img"), null);
        MessageDialogs dialog = new MessageDialogs(parent, msg, title, first, second, confirm, null);
        dialog.setResizable(false);
        dialog.showModal();
        return dialog.dialogStatus.equals(Status.ACCEPTED);
    }
    public static MessageDialogs get3WayDialog(Component parent, String msg, String title, String first, String second, String third) {
        initResourceBundle();
        if ( confirm == null ) {
            confirm = UITools.loadIcon(rb.getString("MessageDialogs.confirm.img"), null);
        }
        MessageDialogs dialog = new MessageDialogs(parent, msg, title, first, second, third, confirm, null);
        dialog.setResizable(false);
        return dialog;
    }
    private static void initResourceBundle() {
        if ( rb == null )
            rb = PropertyResourceBundle.getBundle("uitools");
    }
    private static String reformatString(String s) {
        return "<html>" + s.replaceAll("\n", "<br>") + "</html>";
    }

    public MessageDialogs(Component parent, String msg, String title, String button1, String button2, Icon icon, Throwable ex) {
        this(parent, msg, title, button1, button2, null, icon, ex);
    }
    
    public MessageDialogs(Component parent, String msg, String title, String button1, String button2, String button3, Icon icon, Throwable ex) {
        super(JOptionPane.getFrameForComponent(parent), title, false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        JLabel msgL = new JLabel(reformatString(msg));
        if ( icon != null ) {
            msgL.setIcon(icon);
        }
        getContentPane().add(msgL, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 20, 20, 20), 0, 0));
        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setOpaque(false);
        GridBagConstraints bgbc = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0);
        if ( button1 != null ) {
            b1 = new JButton(button1);
            b1.addActionListener(this);
            getRootPane().setDefaultButton(b1);
            buttons.add(b1, bgbc);
            if ( button2 != null ) {
                b2 = new JButton(button2);
                b2.addActionListener(this);
                buttons.add(b2, bgbc);
            }
            if ( button3 != null ) {
                b3 = new JButton(button3);
                b3.addActionListener(this);
                buttons.add(b3, bgbc);
            }
        }
        if ( ex != null ) {
            JTextArea area = new JTextArea(UITools.getStackTrace(ex));
            detailsPanel = new JScrollPane(area);
            detailsPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
            detailsPanel.setPreferredSize(new Dimension(150, 150));
            detailsPanel.setEnabled(false);
            details = new JToggleButton(rb.getString("MessageDialogs.error.details"));
            details.addActionListener(this);
            buttons.add(details, bgbc);
        }
        getContentPane().add(buttons, new GridBagConstraints(0, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 10, 20), 0, 0));
        expandedPanel = new JPanel(new GridBagLayout());
        expandedPanel.setOpaque(false);
        getContentPane().add(expandedPanel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0));
        pack();
        Dimension dim = getPreferredSize();
        if ( dim.width < 200 ) {
            setPreferredSize(new Dimension(200, dim.height));
        }
        pack();
        setLocationRelativeTo(getOwner());
    }
    private static String getMessage(Throwable ex) {
        String msg = null;
        Throwable source = ex;
        if ( ex instanceof EJBTransactionRolledbackException ) {
            source = ex.getCause();
        }
        if ( source instanceof D3ExceptionInterface ) {
            msg = source.getMessage();
            msg += "\n" + rb.getString("MessageDialogs.error.inModule")
                    + ((D3ExceptionInterface) source).getModuleCode();
            msg += "\n" + getInfoAboutError(source);
        }
        else {
            msg = rb.getString("MessageDialogs.error.unknowMessage");
            msg += "\n" + rb.getString("MessageDialogs.error.errorRamek");
        }
        return reformatString(msg);
    }
    private static String getInfoAboutError(Throwable ex) {
        String out = null;
        if ( ex instanceof BusinesException ) {
            out = rb.getString("MessageDialogs.error.errorRamek");
        }
        else {
            out = rb.getString("MessageDialogs.error.errorCOD");
        }
        return out;
    }
    public MessageDialogs.Status getStatus() {
        return dialogStatus;
    }
    public void setCallBack(Runnable callback) {
        this.callback = callback;
    }
    private void callBack() {
        if (callback != null) {
            SwingUtilities.invokeLater(callback);
        }
    }
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == b1 ) {
            dialogStatus = Status.ACCEPTED;
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        else if ( e.getSource() == b2 ) {
            dialogStatus = Status.REJECTED;
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        else if ( e.getSource() == b3 ) {
            dialogStatus = Status.CANCELED;
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        else if ( e.getSource() == details ) {
            if ( details.isSelected() ) {
                expandedPanel.add(detailsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            else {
                expandedPanel.remove(detailsPanel);
            }
            pack();
        }
    }
    private void showModal() {
        setModal(true);
        setVisible(true);
    }
    //
    public enum Status {
      ACCEPTED, REJECTED, CANCELED;  
    }
    protected void processWindowEvent(WindowEvent e) {
        if(e.getID() == WindowEvent.WINDOW_CLOSING) {
            callBack();
        }
        super.processWindowEvent(e);
    }
}