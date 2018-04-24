package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;

/**
 * User: yerokhin
 * Date: 04.04.2006
 * Time: 13:34:24
 * To change this template use File | Settings | File Templates.
 */
public enum LinkType implements Serializable {
    Esteblish(1),
    IntegrationLink(2),
    Other(16);

    private int value;

    LinkType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LinkType getByValue(int value) {
        for (LinkType lt : LinkType.values())
            if (lt.getValue() == value) return lt;
        return null;
    }
}
