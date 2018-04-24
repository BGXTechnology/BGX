package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import net.bgx.bgxnetwork.transfer.query.LinkType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.*;

import ru.zsoft.jung.viewer.BufferedViewer;

/**
 * Class LayerDialog
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class LayerDialog extends JDialog implements ActionListener {
  private HashMap<LinkType, JCheckBox> linkTypes = new HashMap<LinkType, JCheckBox>();
  private JCheckBox linkCheckAll, showDisconnected;
  private JButton apply, revert, cancel;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
  protected ResourceBundle trb = PropertyResourceBundle.getBundle("transfer");
  private LayerController controller;

  public LayerDialog(Frame owner, String title, BufferedViewer view) {
    super(owner, title, true);
    controller = new LayerController(view);
    controller.setView(this);

    //create list of link types
    JPanel linkTypesPanel = new JPanel(new GridBagLayout());
    linkTypesPanel.setOpaque(false);
    linkTypesPanel.setBorder(new TitledBorder(rb.getString("LayerDialog.linkTypesTitle")));
    JPanel linkListPanel = new JPanel(new GridBagLayout());
    JCheckBox cb;
    GridBagConstraints gb = new GridBagConstraints(0,GridBagConstraints.RELATIVE,1,1,1.0,0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0,0);
    linkCheckAll = new JCheckBox(rb.getString("LayerDialog.selectAll"));
    linkCheckAll.addActionListener(this);
    for (LinkType lt : LinkType.values()) {
          cb = new JCheckBox(trb.getString("LinkType."+lt.name()));
          cb.setSelected(true);
          linkTypes.put(lt, cb);
          linkListPanel.add(cb, gb);
    }
    linkListPanel.setBorder(new TitledBorder(""));
//    JScrollPane scroll = new JScrollPane(linkListPanel);
//    scroll.setPreferredSize(new Dimension(300,350));
    linkTypesPanel.add(linkCheckAll, gb);
    linkTypesPanel.add(linkListPanel, gb);

    showDisconnected = new JCheckBox(rb.getString("LayerDialog.showDisconnected"));
    apply = new JButton(rb.getString("LayerDialog.apply"));
    apply.addActionListener(this);
    revert = new JButton(rb.getString("LayerDialog.revert"));
    revert.addActionListener(this);
    cancel = new JButton(rb.getString("LayerDialog.cancel"));
    cancel.addActionListener(this);

    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(linkTypesPanel, new GridBagConstraints(0,0,3,1,1.0,0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10,10,5,10), 0,0));
    getContentPane().add(showDisconnected, new GridBagConstraints(0,1,3,1,1.0,0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,10,10), 0,0));
    getContentPane().add(apply, new GridBagConstraints(0,2,1,1,0.5,0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,10,10), 0,0));
    getContentPane().add(revert, new GridBagConstraints(1,2,1,1,0.0,0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,10,10,10), 0,0));
    getContentPane().add(cancel, new GridBagConstraints(2,2,1,1,0.5,0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,10,10), 0,0));

    controller.loadData();

    pack();
    setLocationRelativeTo(owner);
    setVisible(true);
  }

  protected void loadData(ArrayList<LinkType> layers, boolean showDisconnected) {
    if (layers!=null) {
      Set<LinkType> set = linkTypes.keySet();
      for (LinkType t : set) {
        linkTypes.get(t).setSelected(layers.contains(t));
      }
    }
    this.showDisconnected.setSelected(showDisconnected);
  }

  protected void close() {
    processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==linkCheckAll) {
      for (JCheckBox cb : linkTypes.values())
        cb.setSelected(linkCheckAll.isSelected());
    } else if (e.getSource()==apply) {
      ArrayList<LinkType> types = new ArrayList<LinkType>();
      for (LinkType type : linkTypes.keySet()) {
        if (linkTypes.get(type).isSelected())
          types.add(type);
      }
      controller.apply(types, showDisconnected.isSelected());
    } else if (e.getSource()==revert) {
      controller.revert();
    } else if (e.getSource()==cancel) {
      close();
    }
  }
}
