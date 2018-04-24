/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.panel;

import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
//import net.bgx.bgxnetwork.system.SystemSetting;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * User: A.Borisenko
 * Date: 27.06.2007
 * Time: 20:08:14
 */
public class InstructionPanel extends JDialog implements HyperlinkListener {
    JEditorPane editorPane = new JEditorPane();
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

    public InstructionPanel() {
        ImageIcon image = (ImageIcon) ResourceLoader.getInstance().getIconByResource(rb, "icon");
        this.setIconImage(image.getImage());
        this.setTitle(rb.getString("Instruction.Panel.title"));

        int x = (int)getToolkit().getScreenSize().getWidth() * 8 / 10;
        int y = (int)getToolkit().getScreenSize().getHeight() * 8 / 10;

        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);
        JScrollPane scroll = new JScrollPane(editorPane);
        editorPane.setPreferredSize(new Dimension(x, y));
        try {
            editorPane.setPage(System.getProperty("help.url"));
        }
        catch (IOException e) {
            MessageDialogs.generalError(this, "Couldn't load resource from "+System.getProperty("help.url"));
            e.printStackTrace();
        }
        this.getContentPane().setPreferredSize(new Dimension(x,y));
        this.getContentPane().add(scroll);
        this.setResizable(false);
        this.pack();
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
              try {
                editorPane.setPage(e.getURL());
              }
              catch(IOException ioe) {
                MessageDialogs.generalError(this, "Couldn't load resource by URL "+e.getURL());
              }
            }

    }
}
