package net.bgx.bgxnetwork.bgxop.gui.lv.panel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.ControlObjectPair;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.TimedDiagramTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
public class AddPairDialog extends JDialog implements ActionListener {
	private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
	private JButton addButton = new JButton(rb.getString("AddPairDialog.button.addButton"));
	private JButton cancelButton = new JButton(rb.getString("AddPairDialog.button.cancelButton"));
	private JComboBox pairBox = new JComboBox();
	private JComboBox linkedPairBox = new JComboBox();
	private ArrayList<ControlObjectPair> pairs;
	private TimedDiagramTableModel model;
	public AddPairDialog(MainFrame mainFrame, ArrayList<ControlObjectPair> pairs, TimedDiagramTableModel model) {
		super(mainFrame);
		this.pairs = pairs;
		this.model = model;
		init();
	}
	private void init() {
		setTitle(rb.getString("AddPairDialog.title"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JLabel topClearLabel = new JLabel();
		topClearLabel.setPreferredSize(new Dimension(0, 10));
		JLabel leftClearLabel = new JLabel();
		leftClearLabel.setPreferredSize(new Dimension(10, 0));
		JLabel rightClearLabel = new JLabel();
		rightClearLabel.setPreferredSize(new Dimension(10, 0));
		getContentPane().add(topClearLabel, BorderLayout.NORTH);
		getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
		getContentPane().add(leftClearLabel, BorderLayout.WEST);
		getContentPane().add(rightClearLabel, BorderLayout.EAST);
		getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);
		initData();
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	private void initData() {
        Collections.sort(pairs, new Comparator<ControlObjectPair>(){
            public int compare(ControlObjectPair o1, ControlObjectPair o2) {
                if (o1 == null) return 1;
                if (o2 == null) return -1;
                ControlObject co1 = o1.getControlObject();
                ControlObject co2 = o2.getControlObject();
                if (co1 == null) return 1;
                if (co2 == null) return -1;
                String name1 = LVGraphNetworkUtil.getName(co1);
                String name2 = LVGraphNetworkUtil.getName(co2);
                if (name1 == null) return 1;
                if (name2 == null) return -1;
                name1 = name1.toUpperCase();
                name2 = name2.toUpperCase();
                return name1.compareTo(name2);
            }
        });

        for (ControlObjectPair pair : pairs) {
			pairBox.addItem(pair);
		}
	}
	private Component getBottomPanel() {
		JPanel under=new JPanel();
		under.setLayout(new BorderLayout());
		JPanel out = new JPanel();
		out.add(addButton);
		out.add(cancelButton);
		addButton.addActionListener(this);
		cancelButton.addActionListener(this);
		under.add(out, BorderLayout.EAST);
		return under;
	}
	private Component getCenterPanel() {
		JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BorderLayout());
		JPanel out = new JPanel();
        out.setOpaque(false);
        JPanel labelPanel = new JPanel();
        labelPanel.setOpaque(false);
        labelPanel.add(new JLabel(rb.getString("AddPairDialog.selectLabel")));
		out.setLayout(new GridLayout(2, 2));
		JPanel u = new JPanel();
        u.setOpaque(false);
        u.add(new JLabel(rb.getString("AddPairDialog.firstObject") + " "));
		u.add(pairBox);
		pairBox.setPreferredSize(new Dimension(180, 23));
		out.add(u);
		u = new JPanel();
        u.setOpaque(false);
        u.add(new JLabel(rb.getString("AddPairDialog.secondObject") + " "));
		u.add(linkedPairBox);
		linkedPairBox.setPreferredSize(new Dimension(180, 23));
		out.add(u);
		pairBox.addActionListener(this);
		container.add(labelPanel, BorderLayout.CENTER);
		container.add(out, BorderLayout.SOUTH);
		return container;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == pairBox) {
			linkedPairBox.removeAllItems();
			ControlObjectPair pair = (ControlObjectPair) pairBox.getSelectedItem();
            HashSet<ControlObject> neighbours  =  pair.getNeighbors();

            ArrayList<ControlObject>  sortedNeighbours = new ArrayList<ControlObject>();
            for(ControlObject co : neighbours){
                sortedNeighbours.add(co);
            }

            Collections.sort(sortedNeighbours, new Comparator<ControlObject>(){
                public int compare(ControlObject o1, ControlObject o2) {
                    String name1 = LVGraphNetworkUtil.getSimpleName(o1);
                    String name2 = LVGraphNetworkUtil.getSimpleName(o2);
                    if (name1 == null) return 1;
                    if (name2 == null) return -1;
                    name1 = name1.toUpperCase();
                    name2 = name2.toUpperCase();
                    return name1.compareTo(name2);
                }
            });

            for (ControlObject p : sortedNeighbours) {
				ControlObjectPair cp = new ControlObjectPair();
				cp.setControlObject(p);
				linkedPairBox.addItem(cp);
			}
		}
		if (e.getSource() == addButton) {
			model.addRow((ControlObjectPair) pairBox.getSelectedItem(), (ControlObjectPair) linkedPairBox.getSelectedItem());
			dispose();
		}
		if (e.getSource() == cancelButton) {
			dispose();
		}
	}
}
