package net.bgx.bgxnetwork.bgxop.gui.background;

import net.bgx.bgxnetwork.bgxop.gui.QueryListController;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Class BackgroundLayouter
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class BackgroundLayouter extends Thread {
  private QueryListController queryListController;
  private ArrayList<String> queries;

  public BackgroundLayouter(QueryListController queryListController, ArrayList<String> queries) {
    this.queryListController = queryListController;
    this.queries = queries;
  }

  public void run() {
    QueryPanel p;
    for (String q : queries) {
      p = queryListController.getQueryPanel(q);
      if (p==null) continue;
      queryListController.relayoutGraph(p);
      SwingUtilities.invokeLater(new Repainter(p));
    }
  }

  protected static class Repainter extends Thread {
    private QueryPanel panel;

    public Repainter(QueryPanel panel) {
      this.panel = panel;
    }

    public void run() {
      panel.getGraph().unlock();
    }
  }
}
