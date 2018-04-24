/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/test/TestDataTDPair.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.transfer.tt;

import java.util.List;
import java.io.Serializable;

public class TestDataTDPair implements TDPair, Serializable {
    private TDObject objectOne = null;
    private TDObject objectTwo = null;
    private List<TDLink> links = null;

    public TestDataTDPair(TDObject objectOne, TDObject objectTwo) {
        this.objectOne = objectOne;
        this.objectTwo = objectTwo;
    }
    public TDObject getObjectOne() {
        return objectOne;
    }
    public TDObject getObjectTwo() {
        return objectTwo;
    }
    public List<TDLink> getLinks() {
        return links;
    }
    public void setLinks(List<TDLink> links) {
        this.links = links;
    }
}