package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import net.bgx.bgxnetwork.bgxop.gui.AppAction;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import oracle.spatial.network.Node;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * User: O.Gerasimenko
 * Date: 03.07.2007
 * Time: 10:42:02
 */
public class NodeGroupDialog extends JDialog implements ActionListener {
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private MainFrame owner;
    private String nameGroup;
    private AppAction _actions = AppAction.getInstance();
    private Action createGroup = _actions.getAction(AppAction.CREATE_GROUP_ACTION);
    private JButton cancelButton;
    private JButton okButton;
    private JTextField textNameGroup;
    private int mode;
    static final int ADD_GROUP = 0;
    static final int EDIT_GROUP = 1;
    static final int MAX_LENGTH = 30;


    public NodeGroupDialog(MainFrame owner, String title, String nameGroup) {
        super(owner, title);
        this.owner = owner;
        this.setTitle(title);
        this.nameGroup = nameGroup;
        if (nameGroup == null || nameGroup.length() == 0)
            mode = ADD_GROUP;
        else
            mode = EDIT_GROUP;
        init();

    }

    private void init() {
        this.setLayout(new BorderLayout());
        JPanel centre = new JPanel();
        centre.setBorder(new TitledBorder(rb.getString("NodeGroupDialog.DescriptionTitle")));
        JLabel label = new JLabel(rb.getString("NodeGroupDialog.NameGroup"));
        textNameGroup = new JTextField(nameGroup);
        textNameGroup.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                if (dot > MAX_LENGTH) {
                    textNameGroup.setCaretPosition(MAX_LENGTH + 1);
                    textNameGroup.setEditable(false);
                } else
                    textNameGroup.setEditable(true);
            }


        });
        textNameGroup.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        createGroup.actionPerformed(new ActionEvent(textNameGroup, ADD_GROUP, ""));
                        dispose();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        break;
                }
            }


            public void keyReleased(KeyEvent e) {

            }
        }

        );
        centre.setLayout(new

                GridLayout(1, 2)

        );
        centre.add(label);
        centre.add(textNameGroup);
        JPanel bottom = new JPanel(new BorderLayout());
        okButton = new JButton(rb.getString("NodeGroupDialog.saveNameGroup"));
        okButton.addActionListener(this);
        cancelButton = new JButton(rb.getString("NodeGroupDialog.Cancel"));
        cancelButton.addActionListener(this);
        bottom.add(getUnderPanel(BorderLayout.EAST, okButton, cancelButton), BorderLayout.CENTER);
        this.add(centre, BorderLayout.NORTH);
        this.add(bottom, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(300, 120));
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            if (e.getSource().equals(okButton)) {
                switch (mode) {
                    case ADD_GROUP:
                        createGroup.actionPerformed(new ActionEvent(textNameGroup, ADD_GROUP, ""));
                        break;
                    case EDIT_GROUP:
                        Node node = GraphNetworkUtil.getNode(owner.getCurrentSelectedVertex());
                        GraphNetworkUtil.setNode(owner.getCurrentSelectedVertex(), node);
//todo ?????????
                        break;
                }
            }
            dispose();
        }
    }

    protected void close() {
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private JComponent getUnderPanel(String layout, JComponent... components) {
        JPanel out = new JPanel();
        JPanel under = new JPanel();
        out.setLayout(new BorderLayout());
        out.add(under, layout);
        for (JComponent component : components) {
            under.add(component);
        }
        return out;
    }


}
