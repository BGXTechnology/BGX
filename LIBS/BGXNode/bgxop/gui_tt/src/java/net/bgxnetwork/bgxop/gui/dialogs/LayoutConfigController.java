package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import net.bgx.bgxnetwork.bgxop.engine.GraphCustoms;

/**
 * Class LayoutConfigController
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class LayoutConfigController {
  private LayoutConfigDialog view;
  private GraphCustoms data;
  private GraphCustoms defaults;
  private boolean dataModified = false;

  protected LayoutConfigController(GraphCustoms data, GraphCustoms defaults) {
    this.data = data;
    if (this.data == null) this.data = new GraphCustoms();
    this.defaults = defaults;
    if (this.defaults == null) this.defaults = new GraphCustoms();
  }

  public void setView(LayoutConfigDialog view) {
    this.view = view;
  }

  public LayoutConfigDialog getView() {
    return view;
  }

  public GraphCustoms getData() {
    return data;
  }

  public GraphCustoms getDefaults() {
    return defaults;
  }

  public boolean dataModified() {
    return dataModified;
  }

  protected void apply() {
    view.storeData(data);
    dataModified = true;
    view.close();
  }
}
