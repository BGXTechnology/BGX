package net.bgx.bgxnetwork.bgxop.graph;


import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.services.MetaDataServiceDelegator;
import net.bgx.bgxnetwork.persistence.metadata.ObjectType;
import net.bgx.bgxnetwork.transfer.query.LinkType;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class NotationModel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class NotationModel {

    private static NotationModel instance = null;

    public static NotationModel getInstance() {
        if (instance == null) instance = new NotationModel();
        return instance;
    }

    private static Color BLACK_COLOR = Color.BLACK;
    private static Color DEFAULT_COLOR = Color.GRAY;

    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
    protected Map<Integer,ObjectType> objectTypes ;

    public NotationModel() {
        init();
    }

    private void init() {
        try {
            MetaDataServiceDelegator metaDataServiceDelegator = new MetaDataServiceDelegator();
            List<ObjectType> objs = metaDataServiceDelegator.getObjectTypeList();
            objectTypes = new HashMap();
            for (ObjectType objectType : objs) {
                objectTypes.put(objectType.getIdObjectType(), objectType);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Icon getIcon4Object(String nameResource) {
        ResourceLoader loader = ResourceLoader.getInstance();
        return loader.getIconByResource(rb, nameResource);
    }

    public Icon getIcon4Object(net.bgx.bgxnetwork.transfer.query.ObjectType objType) {
        ResourceLoader loader = ResourceLoader.getInstance();
        return null;
    }

    public Color getColor4Link(LinkType linkType) {
        if (linkType == null) return DEFAULT_COLOR;
        switch (linkType) {
            case Esteblish:
                return BLACK_COLOR;
        }
        return DEFAULT_COLOR;
    }

    public Map<Integer, ObjectType> getObjectTypes() {
        return objectTypes;
    }
}
