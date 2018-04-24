package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.bgx.bgxnetwork.bgxop.engine.AlgorithmsHolder;
import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import ru.zsoft.jung.viewer.BufferedViewer;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Class ClustererDialog
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ClustererDialog extends JDialog implements ActionListener, ChangeListener {
  private JSlider level = new JSlider(JSlider.HORIZONTAL, 0, 50, 1);
  private JButton show, cancel;
  private JTextField value = new JTextField();
  private boolean shown = false;
  private BufferedViewer graph;
  private GraphNetworkMapper data;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");

  public ClustererDialog(MainFrame owner) throws HeadlessException {
    super(owner);
    setTitle(rb.getString("ClustererDialog.title"));
    graph = owner.getQueryListController().getCurrentGraph();
    data = owner.getQueryListController().getCurrentData();
    setAlwaysOnTop(true);

    show = new JButton(rb.getString("ClustererDialog.ok"));
    show.addActionListener(this);
    cancel = new JButton(rb.getString("ClustererDialog.cancel"));
    cancel.addActionListener(this);
    level.setMajorTickSpacing(10);
    level.setMinorTickSpacing(1);
    level.setPaintLabels(true);
    level.setPaintTicks(true);
    level.addChangeListener(this);
    level.setOpaque(false);
    value.setEditable(false);
    value.setOpaque(false);
    stateChanged(null);

    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(new JLabel(rb.getString("ClustererDialog.clusterLevelLabel")), new GridBagConstraints(0,0,1,1,0.0,0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10,10,5,5), 0,0));
    getContentPane().add(value, new GridBagConstraints(1,0,1,1,0.0,0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,5,10), 20,0));
    getContentPane().add(level, new GridBagConstraints(0,1,2,1,1.0,0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,10), 150,0));
    getContentPane().add(show, new GridBagConstraints(0,2,1,1,1.0,0.0,
        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,10,5), 0,0));
    getContentPane().add(cancel, new GridBagConstraints(1,2,1,1,1.0,0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,10,10), 0,0));

    setVisible(true);
    pack();
  }

  public void stateChanged(ChangeEvent e) {
    value.setText(""+level.getValue());
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==show) {
      shown = true;
      try {
        Iterator it = AlgorithmsHolder.getInstance().getAlgorithms().getClusters(data, level.getValue());
        ArrayList list = new ArrayList();
        while (it.hasNext()) list.add(it.next());
        float step = 0.7f/list.size();
        int i;
        Color c;
        for (i=0; i<list.size(); i++) {
          c = Color.getHSBColor(i*step, 1f, 1f);
          for (Object v : (Set)list.get(i))
            GraphDataUtil.setColor((Vertex)v, c);
        }
        graph.unlock();
        graph.repaint();
      } catch (QueryBusinesException ex) {
        MessageDialogs.generalError(this, ex, rb.getString("error.operationInpossibleMsg"),
            rb.getString("error.generalTitle"));
        return;
      }
    } else if (e.getSource()==cancel) {
      if (shown) {
        for (Iterator it = data.getGraph().getVertices().iterator(); it.hasNext();)
          GraphDataUtil.setColor((Vertex)it.next(), null);
        graph.unlock();
        graph.repaint();
      }
      this.dispose();
    }
  }
}
