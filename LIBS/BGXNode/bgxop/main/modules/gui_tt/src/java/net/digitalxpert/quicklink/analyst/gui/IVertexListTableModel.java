package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.graph.Vertex;

/**
 * Interface IVertexListTableModel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IVertexListTableModel extends IFormatTableModel {

  public Vertex getObjectAt(int rowIndex);

  public int indexOf(Vertex v);

}
