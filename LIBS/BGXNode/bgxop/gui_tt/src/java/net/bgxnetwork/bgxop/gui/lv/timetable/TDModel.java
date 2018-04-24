/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/TDModel.java#2 $
$DateTime: 2007/07/05 16:46:42 $
$Change: 19428 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.util.List;

import javax.swing.event.ChangeListener;

import net.bgx.bgxnetwork.transfer.tt.*;

public interface TDModel {
    public int getPairsCount();
    public TDPair getPairAt(int index);
    public int getIndexOf(TDPair pair);
    public long getMinimum();
    public long getMaximum();
    public void insertPairs(List<TDPair> pairs, int index);
    public void removePairs(List<TDPair> pairs);
    public void movePairs(List<TDPair> pairs, int index);
    public void addChangeListener(ChangeListener listener);
    public void removeChangeListener(ChangeListener listener);
}
