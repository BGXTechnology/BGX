package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.engine.DataExporter;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class ExportDialogController
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ExportDialogController {
  private QueryPanel dataPanel;
  private ExportDialog view = null;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");

  public ExportDialogController(QueryPanel dataPanel) {
    this.dataPanel = dataPanel;
  }

  public ExportDialog getView() {
    return view;
  }

  public void setView(ExportDialog view) {
    this.view = view;
  }

  public QueryPanel getDataPanel() {
    return dataPanel;
  }

  public boolean exportObjects(String fileName) {
    if (fileName==null || fileName.length()==0) return true;
    try {
      DataExporter.exportTable(dataPanel.getDataTable().getModel(), fileName);
      return true;
    } catch (IOException e) {
      MessageDialogs.generalError(view, e, rb.getString("export.dataStoreErrorMsg")+
          " '"+fileName+"'", rb.getString("export.dataStoreErrorTitle"));
      return false;
    }
  }

  public boolean exportLinks(String fileName) {
    if (fileName==null || fileName.length()==0) return true;
    try {
      DataExporter.exportLinks(dataPanel.getDataTable().getFormatModel(), fileName);
      return true;
    } catch (IOException e) {
      MessageDialogs.generalError(view, e, rb.getString("export.dataStoreErrorMsg")+
          " '"+fileName+"'", rb.getString("export.dataStoreErrorTitle"));
      return false;
    }
  }
}
