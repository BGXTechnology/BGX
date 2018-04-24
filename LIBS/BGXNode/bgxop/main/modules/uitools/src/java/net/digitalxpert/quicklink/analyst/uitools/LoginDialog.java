package net.bgx.bgxnetwork.bgxop.uitools;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicPanelUI;
import net.bgx.bgxnetwork.transfer.types.LoginInfo;
import com.bgx.client.net.ClientLogin;
import com.bgx.client.net.LoginManager;

/**
 * Class LoginDialog
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class LoginDialog extends JFrame implements ActionListener{
    private JTextField user = new JTextField();
    private JPasswordField pwd = new JPasswordField();
    private JButton loginButton;
    private ILoginListener login;
    private ResourceBundle rb = PropertyResourceBundle.getBundle("uitools");
    public LoginDialog(ILoginListener login){
        this(login, null);
    }
    public LoginDialog(ILoginListener login, Image icon){
        super();
        if(icon != null)
            setIconImage(icon);
        this.login = login;
        setTitle(rb.getString("LoginDialog.title"));
        loginButton = new JButton(rb.getString("LoginDialog.loginButton"));
        loginButton.addActionListener(this);
        JButton cancel = new JButton(rb.getString("LoginDialog.cancelButton"));
        cancel.addActionListener(this);
        JLabel logo = null;
        Icon logoImg = UITools.loadIcon(rb.getString("LoginDialog.logo.img"), null);
        if(logoImg != null)
            logo = new JLabel(logoImg);
        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setOpaque(false);
        buttons.add(loginButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(
                5, 10, 10, 10), 0, 0));
        buttons.add(cancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,
                10, 10, 10), 0, 0));
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.white);
        loginPanel.add(new JLabel(rb.getString("LoginDialog.usernameLabel")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 0, 0));
        loginPanel.add(user, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 5, 10), 100, 0));
        loginPanel.add(new JLabel(rb.getString("LoginDialog.passwordLabel")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 5, 5), 0, 0));
        loginPanel.add(pwd, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 10), 100, 0));
        loginPanel.add(buttons, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        JPanel contentPane = new JPanel(new GridBagLayout());
        contentPane.setUI(new BasicPanelUI());
        contentPane.setBackground(Color.white);
        setContentPane(contentPane);
        if(logo != null)
            contentPane.add(logo, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(2, 2, 2, 2), 0, 0));
        contentPane.add(loginPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        getRootPane().setDefaultButton(loginButton);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        Rectangle r = getGraphicsConfiguration().getBounds();
        Dimension d = getPreferredSize();
        setLocation(r.x + r.width / 2 - d.width / 2 - 10, r.y + r.height / 2 - d.height / 2 - 10);
        setVisible(true);
        setResizable(false);
    }
    public void close(){
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == loginButton){
            WaitDialog dialog = new WaitDialog(this);
            LoginThread th = new LoginThread(dialog, user.getText(), new String(pwd.getPassword()));
            th.start();
            dialog.showDialog();
            if(th.loginInfo == null){
                MessageDialogs.generalError(this, th.exception, rb.getString("LoginDialog.loginErrorMsg"), rb
                        .getString("LoginDialog.loginErrorTitle"));
            }else{
                login.doLogin(th.loginInfo);
            }
        }else{
            close();
        }
    }
    class LoginThread extends Thread{
        LoginInfo loginInfo = null;
        Exception exception = null;
        private WaitDialog dialog;
        private String user;
        private String pwd;
        public LoginThread(WaitDialog dialog, String user, String pwd){
            this.dialog = dialog;
            this.user = user;
            this.pwd = pwd;
        }
        public void run(){
            try{
                ClientLogin cl = ClientLogin.getLoginInstance(user, pwd);
                cl.login();
                LoginManager lm = new LoginManager();
                loginInfo = lm.auth();
            }catch (Exception e1){
                exception = e1;
            }
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    dialog.close();
                }
            });
        }
    }
}
