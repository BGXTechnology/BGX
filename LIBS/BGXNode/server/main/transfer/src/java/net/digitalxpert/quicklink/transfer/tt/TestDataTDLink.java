/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/test/TestDataTDLink.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.transfer.tt;

import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;

import java.util.Map;
import java.util.ArrayList;
import java.io.Serializable;

public class TestDataTDLink implements TDLink, Serializable {
    private LinkObject object;
    private TDObject initiator = null;
    private TDPair pair = null;
    private Long timestamp = null;
    public TestDataTDLink(LinkObject linkObject, TDObject initiator, TDPair pair, Long timestamp) {
        this.object = linkObject;
        this.initiator = initiator;
        this.pair = pair;
        this.timestamp = timestamp;
    }
    public TestDataTDLink(Map<String, String> attributesMap, TDObject initiator, TDPair pair, Long timestamp) {
        //
        FieldObject fieldObject = null;
        for(String key : attributesMap.keySet()) {
            fieldObject = new FieldObject();
            fieldObject.setCaption(key);
            fieldObject.setValue(attributesMap.get(key));
        }
        //
        this.initiator = initiator;
        this.pair = pair;
        this.timestamp = timestamp;
    }
    public LinkObject getObject() {
        return object;
    }
    public TDObject getInitiator() {
        return initiator;
    }
    public TDPair getPair() {
        return pair;
    }
    public Long getTimestamp() {
        return timestamp;
    }
}
