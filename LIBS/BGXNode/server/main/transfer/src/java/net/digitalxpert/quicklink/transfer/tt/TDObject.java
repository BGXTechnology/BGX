/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/TDObject.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.transfer.tt;

import net.bgx.bgxnetwork.persistence.metadata.ControlObject;

import java.util.Map;

public interface TDObject {
    String getName();
    ControlObject getObject();
}