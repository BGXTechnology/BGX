/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TDVisualizationModel.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.utils.ChangeEventSupport;
import edu.uci.ics.jung.utils.DefaultChangeEventSupport;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.StatusCallback;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.graph.Vertex;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.transfer.tt.TDLink;

class TDVisualizationModel implements VisualizationModel {
    protected ChangeEventSupport changeSupport = new DefaultChangeEventSupport(this);
    protected Layout layout;
    private HashMap<Vertex, TDLink> storage = new HashMap<Vertex, TDLink>();
    //
    // change support implementation
    //
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }
    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    //
    // visualization model implementation
    //

    // layout
    public Layout getGraphLayout() {
        return layout;
    }
    public void setGraphLayout(Layout layout) {
        setGraphLayout(layout, null);
    }
    public void setGraphLayout(Layout layout, Dimension d) {
        this.layout = layout;
        layout.initialize(null);
    }

    //
    // lifecycle
    //
    public void init() {
        //
    }
    public void start() {
        //
    }
    public void stop() {
        //
    }
    public void restart() {
        //
    }
    public void prerelax() {
        //
    }
    public void suspend() {
        // nothing to suspend 
    }
    public void unsuspend() {
        // nothing to unsuspend
    }

    //
    // relaxer thread isn't used
    //
    public boolean isVisRunnerRunning() {
        return false;
    }
    public long getRelaxerThreadSleepTime() {
        return 0;
    }
    public void setRelaxerThreadSleepTime(long relaxerThreadSleepTime) {
    }
    public void restartThreadOnly() {
    }
    public void setTextCallback(StatusCallback scb) {
    }

    public void addLinkToVertex(Vertex v, TDLink l){
        storage.put(v, l);
    }

    public TDLink getLinkByVertex(Vertex v){
        return storage.get(v);
    }
}