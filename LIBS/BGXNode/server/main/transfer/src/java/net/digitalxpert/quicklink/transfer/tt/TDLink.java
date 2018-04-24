/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/TDLink.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.transfer.tt;

import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;

import java.util.ArrayList;

public interface TDLink {
    Long getTimestamp();
    LinkObject getObject();
    TDPair getPair();
    TDObject getInitiator();
}