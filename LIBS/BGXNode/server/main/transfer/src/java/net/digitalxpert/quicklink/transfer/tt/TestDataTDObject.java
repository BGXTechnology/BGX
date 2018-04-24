/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/test/TestDataTDObject.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.transfer.tt;

import net.bgx.bgxnetwork.persistence.metadata.ControlObject;

import java.util.Map;
import java.io.Serializable;

public class TestDataTDObject implements TDObject, Serializable {
    private ControlObject object;
    private String name = null;

    public TestDataTDObject(String name, ControlObject co) {
        this.name = name;
        this.object = co;
    }

    public ControlObject getObject() {
        return object;
    }

    public String getName() {
        return name;
    }
}
