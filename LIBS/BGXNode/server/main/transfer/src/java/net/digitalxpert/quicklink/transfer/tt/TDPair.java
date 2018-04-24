/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/TDPair.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.transfer.tt;

import java.util.List;

public  interface TDPair {
    public TDObject getObjectOne();
    public TDObject getObjectTwo();
    public List<TDLink> getLinks();
    public void setLinks(List<TDLink> links);
}
