/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/model/test/TestDataTDModel.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TDLinkComparator;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TDModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TDUtils;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.transfer.tt.TDLink;
import net.bgx.bgxnetwork.transfer.tt.TDObject;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TestDataTDLink;
import net.bgx.bgxnetwork.transfer.tt.TestDataTDObject;
import net.bgx.bgxnetwork.transfer.tt.TestDataTDPair;


public class TestDataTDModel implements TDModel {
    private List<TDPair> pairs = null;
    private EventListenerList listenerList = null;
    private ChangeEvent changeEvent = null;
    private TDLinkComparator comparator = null;
    private long maxTimestamp = 0L;
    private long minTimestamp = 0L;
    private boolean lock = false;
    // fields for test purposes only
    private long increment = 20L;

    // constructor for test purposes only
    public TestDataTDModel(int pairsCount, int linksPerPair, long increment) {
        pairs = new LinkedList<TDPair>();
        comparator = new TDLinkComparator();
        listenerList = new EventListenerList();
        this.increment = increment;
        fill(pairsCount, linksPerPair);
        computeMinMax();
    }

    public TestDataTDModel(LinkedList<TDPair> pairs){
        this.pairs = pairs;
        //TDUtils.printPairsList(pairs);
        comparator = new TDLinkComparator();
        listenerList = new EventListenerList();
        computeMinMax();
    }

    public long getMaximum() {
        return maxTimestamp;
    }
    public long getMinimum() {
        return minTimestamp;
    }
    public TDPair getPairAt(int index) {
        return pairs.get(index);
    }
    public int getIndexOf(TDPair pair) {
        return pairs.indexOf(pair);
    }
    public int getPairsCount() {
        return pairs.size();
    }
    public void movePairs(List<TDPair> pairsToMove, int index) {
        lock = true;
        removePairs(pairsToMove);
        lock = false;
        insertPairs(pairsToMove, index);
    }
    public void insertPairs(List<TDPair> pairs, int index) {
        if (index < 0) {
            index = 0;
        } else if (index > this.pairs.size()) {
            index = this.pairs.size();
        }
        this.pairs.addAll(index, pairs);
        computeMinMax();
        fireStateChanged();
    }
    public void removePairs(List<TDPair> pairs) {
        this.pairs.removeAll(pairs);
        computeMinMax();
        fireStateChanged();
    }
    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }
    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }
    private void fireStateChanged() {
        if( ! lock) {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length-2; i >= 0; i -= 2) {
                if (listeners[i] == ChangeListener.class) {
                    if (changeEvent == null) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((ChangeListener) listeners[ i+1 ]).stateChanged(changeEvent);
                }
            }
        }
    }
    private void computeMinMax() {
        List<TDLink> allLinks = new LinkedList<TDLink>();
        for (TDPair pair : pairs) {
            allLinks.addAll(pair.getLinks());
        }
        if(allLinks.size() > 0) {
            Collections.sort(allLinks, comparator);
            //TDUtils.printLinkList(allLinks);
            minTimestamp = allLinks.get(0).getTimestamp();
            maxTimestamp = allLinks.get(allLinks.size() - 1).getTimestamp();
        } else {
            minTimestamp = 0;
            maxTimestamp = 0;
        }
    }

    public void setPairs(List<TDPair> pairs) {
        this.pairs = pairs;
    }
    //
    // methods for test purposes only
    //
    private void fill(int pairsCount, int linksPerPair) {
        pairs = getTestData(pairsCount, linksPerPair, increment);
    }
    public static LinkedList<TDPair> getTestData(int pairsCount, int linksPerPair, long timeIncrement) {
        TDObject objectOne = null;
        TDObject objectTwo = null;
        TDPair pair = null;
        TDLink link = null;
        List<TDLink> links = null;
        LinkedList<TDPair> testPairsList = new LinkedList<TDPair>();
        long timestamp = System.currentTimeMillis();
        for (int pairIdx = 0; pairIdx < pairsCount; pairIdx++) {
            TDObject initiator = null;
            objectOne = new TestDataTDObject("object one "+pairIdx, new ControlObject());
            objectTwo = new TestDataTDObject("object two "+pairIdx, new ControlObject());
            pair = new TestDataTDPair(objectOne, objectTwo);
            links = new ArrayList<TDLink>();
            for (int linkIdx = 0; linkIdx < linksPerPair; linkIdx++) {
                timestamp += timeIncrement;
                initiator = initiator != null && initiator == objectOne ? objectTwo : objectOne;
                link = new TestDataTDLink(new LinkObject(), initiator, pair, timestamp);
                links.add(link);
            }
            pair.setLinks(links);
            testPairsList.add(pair);
        }
        return testPairsList;
    }
//    private Map<String, String> attributes(int count) {
//        Map<String, String> attributes = new HashMap<String, String>();
//        for (int attributeIdx = 0; attributeIdx < count; attributeIdx++) {
//            attributes.put(new String("attribute key "+attributeIdx), new String("attribute value "+attributeIdx));
//        }
//        return attributes;
//    }
}