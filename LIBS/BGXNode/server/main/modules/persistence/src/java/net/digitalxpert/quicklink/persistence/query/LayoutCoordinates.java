package net.bgx.bgxnetwork.persistence.query;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.awt.*;

/**
 * Class LayoutCoordinates
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class LayoutCoordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    private String networkName;
    private Object key;
    private Dimension layoutSize;
    private HashMap<Integer, SerialPoint2D> coordinates;
    private Map<Long, String> names;

    public LayoutCoordinates() {
    }

    public LayoutCoordinates(String networkName, Object key, Dimension layoutSize, HashMap<Integer, SerialPoint2D> coordinates) {
        this.networkName = networkName;
        this.key = key;
        this.layoutSize = layoutSize;
        this.coordinates = coordinates;
    }

    public LayoutCoordinates(String networkName, Object key, Dimension layoutSize) {
        this.networkName = networkName;
        this.key = key;
        this.layoutSize = layoutSize;
        this.coordinates = new HashMap<Integer, SerialPoint2D>();
        this.names = new HashMap<Long, String>();
    }

    public String getNetworkName() {
        return networkName;
    }

    public Object getKey() {
        return key;
    }

    public HashMap<Integer, SerialPoint2D> getCoordinates() {
        return coordinates;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public void setCoordinates(HashMap<Integer, SerialPoint2D> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinate(int vertexId, SerialPoint2D coordinate) {
        coordinates.put(vertexId, coordinate);
    }

    public SerialPoint2D getCoordinate(int vertexId) {
        return coordinates.get(vertexId);
    }

    public Dimension getLayoutSize() {
        return layoutSize;
    }

    public void setLayoutSize(Dimension layoutSize) {
        this.layoutSize = layoutSize;
    }

    public void addName(Long id, String name) {
        names.put(id, name);
    }

    public Map<Long, String> getNames() {
        return names;
    }

    public void setNames(Map<Long, String> names) {
        this.names = names;
    }
}
