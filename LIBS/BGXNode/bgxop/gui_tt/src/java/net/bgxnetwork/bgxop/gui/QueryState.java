package net.bgx.bgxnetwork.bgxop.gui;

/**
 * Class QueryState
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public abstract class QueryState {

//  private boolean pickState = false;
  private boolean neighbours = false;
  private DataLevel dataLevel;

  protected QueryState() {
  }

  public void setDataLevel(DataLevel mode) {
    this.dataLevel = mode;
  }

//  public void setPickState(boolean pick) {
//    this.pickState = pick;
//  }

  public void setShowNeighbours(boolean show) {
    this.neighbours = show;
  }

  public DataLevel getDataLevel() {
    return dataLevel;
  }

//  public boolean getPickState() {
//    return pickState;
//  }

  public boolean getShowNeighbours() {
    return neighbours;
  }

}
