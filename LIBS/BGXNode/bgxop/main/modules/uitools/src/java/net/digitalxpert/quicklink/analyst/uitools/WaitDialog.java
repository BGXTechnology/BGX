package net.bgx.bgxnetwork.bgxop.uitools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class WaitDialog
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class WaitDialog extends JDialog {
  private boolean ready = false;
  private ResourceBundle rb = PropertyResourceBundle.getBundle("uitools");
  private String infoTitle;
  private Thread callBackThread = null;
  private boolean waitClosing = false;

  public WaitDialog(Frame owner) {
    super(owner, true);
    init();
  }

  public WaitDialog(Dialog owner) {
    super(owner, true);
    init();
  }

  private void init() {
    setTitle(rb.getString("WaitDialog.title"));

    Icon icon = UITools.loadIcon(rb.getString("WaitDialog.img"), null);
    JLabel label= new JLabel(rb.getString("WaitDialog.message"),
        icon, JLabel.TRAILING);

    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(label,
        new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(20,15,20,15), 0,0));
  }

  public String getInfoText(){
      return infoTitle;
  }

  public void setInfoText(String inf){
      infoTitle = inf;
      setTitle(getTitle()+" "+infoTitle);
  }

  public void showDialog() {
    if (ready) return;
    pack();
    setLocationRelativeTo(getOwner());
    setVisible(true);
  }

  public void close() {
    ready = true;
    processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  protected void processWindowEvent(WindowEvent e) {
    if (e.getID()==WindowEvent.WINDOW_CLOSING && !ready) {
        if (callBackThread != null){
            callBackThread.interrupt();
            waitClosing = true;
        }
    }
    if (callBackThread == null)
        if (e.getID()==WindowEvent.WINDOW_CLOSING && !ready) return;
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    super.processWindowEvent(e);
  }

  public void setThread(Thread th){
      callBackThread = th;
  }

  public boolean isCurrentClosed(){
    return waitClosing;
  }
}
