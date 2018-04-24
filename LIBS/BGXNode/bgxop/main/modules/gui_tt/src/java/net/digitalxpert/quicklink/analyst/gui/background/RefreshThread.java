package net.bgx.bgxnetwork.bgxop.gui.background;

import net.bgx.bgxnetwork.bgxop.gui.ProfilePanelController;
import net.bgx.bgxnetwork.bgxop.gui.StatusBar;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.transfer.query.*;

import javax.swing.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class RefreshThread
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class RefreshThread extends Thread {
  private static Logger log = Logger.getLogger(RefreshThread.class.getName());
  private boolean force = false;
  private boolean stop = false;
  private long pollingInterval;
  private long refreshInterval;
  private ProfilePanelController queryListContainer;
  private StatusBar status;
  private QueryServiceDelegator service = new QueryServiceDelegator();
  private List<QueryType> queryTypes = null;
  private List<Query> queries;

  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

  public RefreshThread(ProfilePanelController queryListContainer, long pollingInterval,
                       long refreshInterval, StatusBar status) {
    this.queryListContainer = queryListContainer;
    this.pollingInterval = pollingInterval;
    this.refreshInterval = refreshInterval;
    this.status = status;
  }

  public void forceRefresh() {
    force = true;
  }

  public long getRefreshInterval() {
    return refreshInterval;
  }

  public void setRefreshInterval(long refreshInterval) {
    this.refreshInterval = refreshInterval;
  }

  public void setStop() {
    this.stop = true;
  }

  public void run() {
    long risk;
    while (true) {
      try {
        if (queryTypes==null) queryTypes = service.getQueryTypeList();
        queries = service.getQueryList();
        SwingUtilities.invokeLater(new RepaintThread());
      } catch (Exception e) {
        log.severe(e.toString());
        e.printStackTrace();
        SwingUtilities.invokeLater(new ErrorThread(rb.getString("StatusBar.refreshError")));
      }
      force = false;
      risk = System.currentTimeMillis();
      while (System.currentTimeMillis()-risk < getRefreshInterval()) {
        if (stop) return;
        if (force) break;
        try {
          Thread.sleep(pollingInterval);
        } catch (InterruptedException e) {}
      }
    }
  }

  private class RepaintThread implements Runnable {
    public void run() {
      queryListContainer.refresh(queryTypes, queries);
      status.addMessage(rb.getString("StatusBar.refreshSuccess"));
    }
  }

  private class ErrorThread implements Runnable {
    private String msg;

    public ErrorThread(String msg) {
      this.msg = msg;
    }

    public void run() {
      queryListContainer.refresh(queryTypes, new ArrayList<Query>());
      status.addMessage(msg);
    }
  }
}
