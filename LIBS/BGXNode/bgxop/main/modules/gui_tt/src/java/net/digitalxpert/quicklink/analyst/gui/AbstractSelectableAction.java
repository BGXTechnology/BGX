package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

/**
 * Class AbstractSelectableAction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public abstract class AbstractSelectableAction extends AbstractAction implements ISelectableAction {
  private ArrayList<AbstractButton> parents = new ArrayList<AbstractButton>();
  private boolean state = false;
  private boolean lockrec = false;

  public AbstractSelectableAction() {
  }

  public AbstractSelectableAction(String name) {
    super(name);
  }

  public AbstractSelectableAction(String name, Icon icon) {
    super(name, icon);
  }

  public void addParent(AbstractButton parent) {
    parents.add(parent);
  }

  public void removeParent(AbstractButton parent) {
    parents.remove(parent);
  }

  public void clearParents() {
    parents.clear();
  }

  public List<AbstractButton> getParents() {
    return parents;
  }

  public boolean getState() {
    return state;
  }

  public void setState(boolean state) {
    this.state = state;
  }

  public boolean isLockrec() {
    return lockrec;
  }

  public void actionPerformed(ActionEvent e) {
    if (lockrec) return;
    lockrec = true;
    for (AbstractButton b : parents)
      if (b.isSelected() != getState()) b.setSelected(getState());
    lockrec = false;
  }
}
