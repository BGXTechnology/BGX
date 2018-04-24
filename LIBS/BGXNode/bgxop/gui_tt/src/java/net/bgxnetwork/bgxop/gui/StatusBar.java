package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Class StatusBar
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class StatusBar extends JPanel {
    private JLabel message;
    private JLabel groups;
    private JLabel objects;
    private JLabel links;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

    public StatusBar() {
        super(new GridBagLayout());
        message = new JLabel();
        message.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        message.setPreferredSize(new Dimension(120, 22));
        groups = new JLabel();
        groups.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        groups.setPreferredSize(new Dimension(120, 22));
        objects = new JLabel();
        objects.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        objects.setPreferredSize(new Dimension(120, 22));
        links = new JLabel();
        links.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        links.setPreferredSize(new Dimension(120, 22));

        add(message, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        add(groups, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
        add(objects, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
        add(links, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        addMessage(rb.getString("StatusBar.startup"));
    }

    public void addMessage(String txt) {
        message.setText("  " + df.format(new Date()) + ": " + txt);
    }

    public void setObjectCount(int count) {
        if (count < 0) objects.setText("");
        else objects.setText("  " + count + " " + rb.getString("StatusBar.objects"));
    }

    public void setLinkCount(int count) {
        if (count < 0) links.setText("");
        else links.setText("  " + count + " " + rb.getString("StatusBar.links"));
    }

    public void setGroups(int count) {
        if (count < 0) groups.setText("");
        else groups.setText("  " + count + " " + rb.getString("StatusBar.groups"));
    }
}
