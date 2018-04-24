package net.bgx.bgxnetwork.transfer.query;

import oracle.spatial.network.Network;
import oracle.spatial.network.Node;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import net.bgx.bgxnetwork.transfer.query.card.Card;
import net.bgx.bgxnetwork.persistence.metadata.PropertyVal;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;

/**
 * Class QueryData
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class QueryData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Network network = null;
    private HashMap<Integer, ControlObject> objects = new HashMap<Integer, ControlObject>();
    private HashMap<Integer, LinkObject> linkObjects = new HashMap<Integer, LinkObject>();

    private int aggregateVertex = -1;
    private int objectsLimit = 0;

    public QueryData() {
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public int getObjectsLimit() {
        return objectsLimit;
    }

    public void setObjectsLimit(int objectsLimit) {
        this.objectsLimit = objectsLimit;
    }


    public int getAggregateVertex() {
        return aggregateVertex;
    }

    public void setAggregateVertex(int aggregateVertex) {
        this.aggregateVertex = aggregateVertex;
    }

    public HashMap<Integer, ControlObject> getObjects() {
        return objects;
    }

    public void setObjects(HashMap<Integer, ControlObject> _objects) {
        this.objects = _objects;
    }

    public HashMap<Integer, LinkObject> getLinkObjects() {
        return linkObjects;
    }

    public void setLinkObjects(HashMap<Integer, LinkObject> linkObjects) {
        this.linkObjects = linkObjects;
    }
}
