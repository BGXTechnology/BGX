package net.bgx.bgxnetwork.bgxop.gui.completer;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractListModel;
/**
 * Class to hold the remaining objects that still match the users input.
 * 
 * @author ncochran
 * 
 */
public class FilterListModel extends AbstractListModel {
	private Object[] fullList;
	private ArrayList<Object> filteredList;
	public FilterListModel(Object[] unfilteredList) {
		fullList = unfilteredList;
		filteredList = new ArrayList<Object>(Arrays.asList(unfilteredList));
	}
	public int getSize() {
		return filteredList.size();
	}
	public Object getElementAt(int index) {
		return filteredList.get(index);
	}
	public boolean setFilter(String filter) {
		filteredList.clear();
		for (Object obj : fullList) {
			if (obj.toString().length() < filter.length())
				continue;
			if (obj.toString().substring(0, filter.length()).compareToIgnoreCase(filter) == 0)
				filteredList.add(obj);
		}
		if (filteredList.size() != 0) {
			fireContentsChanged(this, 0, filteredList.size());
			return true;
		} else {
			return false;
		}
	}
	public void clearFilter() {
		filteredList = new ArrayList<Object>(Arrays.asList(fullList));
	}
	public void setCompleterMatches(Object[] objectsToMatch) {
		fullList = objectsToMatch;
		clearFilter();
	}
}
