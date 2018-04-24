package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import java.util.List;

/**
 * Interface ISelectableAction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface ISelectableAction extends Action {

  public void addParent(AbstractButton parent);

  public void removeParent(AbstractButton parent);

  public void clearParents();

  public List<AbstractButton> getParents();

  public boolean getState();

  public void setState(boolean state);

  public boolean isLockrec();

}
