/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.tt;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User: A.Borisenko
 * Date: 17.07.2007
 * Time: 11:49:22
 */
public class TimedDiagrammDataSnapshot implements Serializable {
    private ArrayList<TransferControlObjectPair> pairs;
    private int scale;

    public ArrayList<TransferControlObjectPair> getPairs() {
        return pairs;
    }
    public void setPairs(ArrayList<TransferControlObjectPair> pairs) {
        this.pairs = pairs;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}