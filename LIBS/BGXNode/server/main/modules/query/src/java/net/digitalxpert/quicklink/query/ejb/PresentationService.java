package net.bgx.bgxnetwork.query.ejb;

import org.jboss.annotation.security.SecurityDomain;

import javax.ejb.Stateless;
import javax.annotation.security.RunAs;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.query.interfaces.PresentationServiceLocal;
import net.bgx.bgxnetwork.persistence.metadata.*;
import net.bgx.bgxnetwork.exception.metadata.MetaDataBusinessException;
import net.bgx.bgxnetwork.exception.query.ErrorList;

import java.util.*;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 17:35:33
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RunAs("LVSystem")
public class PresentationService implements PresentationServiceLocal {
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;

    public void createControlObject(NodePK nodePK){
        ControlObject conrolObject = null;
        try{
            conrolObject = getControlObject(nodePK);
            if (conrolObject != null) return;
        }
        catch(Exception e){
            ;
        }

        conrolObject = new ControlObject();
        conrolObject.setPk(nodePK);
        //todo only one ObjectType in system with id = 1
        ObjectType ot = manager.find(ObjectType.class, 1);
        conrolObject.setTypeObject(ot);
        manager.persist(conrolObject);
    }

    public ControlObject getControlObject(NodePK nodePK) {
        return manager.find(ControlObject.class, nodePK);
    }

    public HashMap<Integer, ControlObject> getControlObjects(List<Long> nodeIds, Long queryId) {
        Query query = manager.createQuery("select co from ControlObject co where co.pk.nodeId in (:nodes) and co.pk.queryId=:qId");
        HashMap<Integer, ControlObject> res = new HashMap<Integer, ControlObject>();
        query.setParameter("qId", queryId);
        int cnt = 0;
        ArrayList<Long> nnn = new ArrayList<Long>();
        for (Long nodeId : nodeIds) {
            nnn.add(nodeId);
            if (cnt % 500 == 0) {
                query.setParameter("nodes", nnn);
                List<ControlObject> objects = query.getResultList();

                for (ControlObject co : objects) {
                    res.put(co.getPk().getNodeId().intValue(), co);
                }
                nnn.clear();
            }
            cnt++;
        }
        if (nnn.size() > 0) {
            query.setParameter("nodes", nnn);
            List<ControlObject> objects = query.getResultList();

            for (ControlObject co : objects) {
                res.put(co.getPk().getNodeId().intValue(), co);
            }
        }

        return res;
    }

    public HashMap<Integer, LinkObject> getLinkObjects(List<Long> linkIds, Long queryId) {
        Query query = manager.createQuery("select lo from LinkObject lo where lo.pk.linkId in (:links) and lo.pk.queryId=:qId");
        HashMap<Integer, LinkObject> res = new HashMap<Integer, LinkObject>();
        query.setParameter("qId", queryId);
        int cnt = 0;
        ArrayList<Long> nnn = new ArrayList<Long>();
        for (Long linkId : linkIds) {
            nnn.add(linkId);
            if (cnt % 500 == 0) {
                query.setParameter("links", nnn);
                List<LinkObject> links = query.getResultList();

                for (LinkObject linkObject : links) {
                    res.put(linkObject.getPk().getLinkId().intValue(), linkObject);
                }
                nnn.clear();
            }
            cnt++;
        }
        if (nnn.size() > 0) {
            query.setParameter("links", nnn);
            List<LinkObject> objects = query.getResultList();

            for (LinkObject linkObject : objects) {
                res.put(linkObject.getPk().getLinkId().intValue(), linkObject);
            }
        }

        return res;
    }

    public ControlObject updateControlObject(ControlObject controlObject) throws MetaDataBusinessException {
        if (controlObject != null) {
            ControlObject obj = manager.find(ControlObject.class, controlObject.getPk());
            if (obj == null) {
                String message = "Cannot find object control '" + controlObject.getPk() + "'. ";
                throw new MetaDataBusinessException(ErrorList.EJB_ENTITY_NOT_FOUND, new Object[]{message});
            }
            return manager.merge(controlObject);
        } else
            return null;
    }

    public LinkObject updateLinkObject(LinkObject linkObject) throws MetaDataBusinessException {
        if (linkObject != null) {
            LinkObject lnk = manager.find(LinkObject.class, linkObject.getPk());
            if (lnk == null) {
                String message = "Cannot find link '" + linkObject.getPk() + "'. ";
                throw new MetaDataBusinessException(ErrorList.EJB_ENTITY_NOT_FOUND, new Object[]{message});
            }
            return manager.merge(linkObject);
        } else
            return null;
    }

    public List<PropertyVal> getPropertyValueByGroupPropertyType(NodePK pk, Integer idGroup) throws MetaDataBusinessException {
        if (pk != null && idGroup != null) {
            try {
                ControlObject controlObject = manager.find(ControlObject.class, pk);
                GroupPropertyType groupPropertyType = manager.find(GroupPropertyType.class, idGroup);
                if (controlObject != null && controlObject.getPropertyVals() != null) {
                    List<PropertyVal> result = new ArrayList();
                    for (PropertyVal propertyVal : controlObject.getPropertyVals()) {
                        if (propertyVal.getPropertyType().getGroupPropertyTypes() != null &&
                                propertyVal.getPropertyType().getGroupPropertyTypes().contains(groupPropertyType)) {
                            result.add(propertyVal);
                        }
                    }
                    return result;
                }
                return null;
            } catch (Exception ex) {
                String message = "Cannot find object control '" + pk.getNodeId().intValue() + "'. ";
                throw new MetaDataBusinessException(ErrorList.DATA_CANNOT_FIND_DATA, new Object[]{message});

            }
        }
        return null;
    }

    public void addValueToObject(NodePK nodePK, Long propertyTypeId, String value){
        ControlObject controlObject = manager.find(ControlObject.class, nodePK);
        Set<PropertyVal> vals = controlObject.getPropertyVals();
        if (vals == null)
            vals = new HashSet<PropertyVal>();
        PropertyValPK pvPK = new PropertyValPK(nodePK, propertyTypeId);
        PropertyVal pv = new PropertyVal(pvPK);
        pv.setValueObject(value);
        vals.add(pv);
        controlObject.setPropertyVals(vals);
        manager.merge(controlObject);
    }

    public void createLinkObject(LinkPK linkPK, Long startId, Long endId) {
        LinkObject linkObject = null;
        try{
            linkObject  = getLinkObject(linkPK);
            if (linkObject != null) return;
        }
        catch(Exception e){
            ;
        }

        linkObject = new LinkObject();
        linkObject.setPk(linkPK);
        //todo only one LinkType in system with id = 2
        ObjectType ot = manager.find(ObjectType.class, 2);
        linkObject.setLinkType(ot);
        linkObject.setStartNodeId(startId);
        linkObject.setEndNodeId(endId);
        manager.persist(linkObject);
    }

    public LinkObject getLinkObject(LinkPK linkPK) {
        return manager.find(LinkObject.class, linkPK);
    }

    public void addValueToLink(LinkPK linkPK, Long propertyTypeId, String value){
        LinkObject linkObject = manager.find(LinkObject.class, linkPK);
        Set<LinkValue> vals = linkObject.getPropertyVals();
        if (vals == null)
            vals = new HashSet<LinkValue>();
        LinkValPK lvPK = new LinkValPK(linkPK, propertyTypeId);
        LinkValue lv = new LinkValue(lvPK);
        lv.setValueObject(value);
        vals.add(lv);
        linkObject.setPropertyVals(vals);
        manager.merge(linkObject);
    }

}
