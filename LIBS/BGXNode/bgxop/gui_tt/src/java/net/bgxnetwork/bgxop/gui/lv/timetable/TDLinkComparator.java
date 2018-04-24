/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TDLinkComparator.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.util.Comparator;

import net.bgx.bgxnetwork.transfer.tt.*;

public class TDLinkComparator implements Comparator<TDLink> {
    public int compare(TDLink linkOne, TDLink linkTwo) {
        return linkOne.getTimestamp().compareTo(linkTwo.getTimestamp());
    }
}
