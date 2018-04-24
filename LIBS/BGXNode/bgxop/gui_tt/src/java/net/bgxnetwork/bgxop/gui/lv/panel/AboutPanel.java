/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.panel;

import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.border.TitledBorder;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: A.Borisenko
 * Date: 26.06.2007
 * Time: 15:10:58
 */
public class AboutPanel extends JDialog {
    protected ResourceBundle about_resource = PropertyResourceBundle.getBundle("about");
    private MainFrame owner;

    public AboutPanel(MainFrame owner) {
        this.owner = owner;
        this.setTitle(about_resource.getString("panel.title"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(500, 430));

        JLabel picture = new JLabel(ResourceLoader.getInstance().getIconByResource(about_resource, "about.img"));
        picture.setPreferredSize(new Dimension(100, 300));

        JTextComponent about1 = new JEditorPane("text/html", about_resource.getString("about1"));
        about1.setOpaque(false);
        about1.setEditable(false);
        JTextComponent about2 = new JEditorPane("text/html", about_resource.getString("copyright"));
        about2.setOpaque(false);
        about2.setEditable(false);

        JPanel licensePanel1 = new JPanel();
        licensePanel1.setOpaque(false);
        licensePanel1.setBorder(new TitledBorder(about_resource.getString("group1.title")));
        JLabel licenseText1 = new JLabel(about_resource.getString("group1.text"));
        licensePanel1.add(licenseText1);


        JPanel licensePanel2 = new JPanel(new BorderLayout());
        licensePanel2.setOpaque(false);
        licensePanel2.setBorder(new TitledBorder(about_resource.getString("user.license.title")));
        //JLabel licenseTitle2 = new JLabel(about_resource.getString("user.license.title"));
        JTextArea licenseText = new JTextArea();
        licenseText.setWrapStyleWord(true);
        licenseText.setEnabled(true);
        licenseText.setEditable(false);
        //licenseText.setPreferredSize(new Dimension(370,100));
        JScrollPane scroll = new JScrollPane(licenseText);
        licenseText.setText(about_resource.getString("user.license.text"));
        licenseText.setCaretPosition(0);
        scroll.setPreferredSize(new Dimension(370, 70));

        //licensePanel2.add(licenseTitle2);
        licensePanel2.add(scroll, BorderLayout.CENTER);

        JTextComponent attention = new JEditorPane("text/html", about_resource.getString("attention.text"));
        attention.setOpaque(false);
        attention.setEditable(false);
        
        JPanel mainCenterPAnel = new JPanel(new BorderLayout());
        mainCenterPAnel.setOpaque(false);
        mainCenterPAnel.add(picture, BorderLayout.WEST);

        JPanel secondCentralPAnel = new JPanel(new GridBagLayout());
        secondCentralPAnel.setOpaque(false);
        secondCentralPAnel.add(about1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        secondCentralPAnel.add(about2, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        secondCentralPAnel.add(licensePanel1, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        secondCentralPAnel.add(licensePanel2, new GridBagConstraints(0, 3, 1, 2, 1.0, 5.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
                new Insets(5, 5, 0, 5), 0, 0));
        mainCenterPAnel.add(secondCentralPAnel, BorderLayout.CENTER);
        mainPanel.add(mainCenterPAnel, BorderLayout.CENTER);

        JPanel attentionPAnel = new JPanel(new BorderLayout());
        attentionPAnel.setOpaque(false);
        attentionPAnel.add(attention, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton closeButton = new JButton("Ok");
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(closeButton);
        attentionPAnel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(attentionPAnel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        this.setResizable(false);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(owner);
    }

}
