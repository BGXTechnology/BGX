package net.bgx.bgxnetwork.query.ejb;

import net.bgx.bgxnetwork.persistence.metadata.ObjectType;
import net.bgx.bgxnetwork.persistence.metadata.PropertyTypeView;
import net.bgx.bgxnetwork.persistence.view.LabelGraph;
import net.bgx.bgxnetwork.query.interfaces.MetaDataServiceLocal;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.data.Request;
import net.bgx.bgxnetwork.transfer.data.Quest;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.lanv.meta.dao.MetaDataReader;
import net.bgx.lanv.meta.util.MetaTableHelper;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.EJBContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 17:32:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RunAs("LVSystem")
public class MetaDataService implements MetaDataServiceLocal {
    @Resource EJBContext ctx;
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;

    @RolesAllowed({"LV_ROLE"})
    public List<ObjectType> getObjectTypeList() {
        Query query = manager.createQuery("select o from ObjectType o");
        return query.getResultList();
    }

    @RolesAllowed({"LV_ROLE","LVSystem"})
    public List<PropertyTypeView> getListPropertyTypeView() {
        Query query = manager.createQuery("select ptv from PropertyTypeView ptv");
        return query.getResultList();
    }

    public List<LabelGraph> getLabelList() {
        return null;
    }

    @RolesAllowed({"LV_ROLE"})
    public List<Request> getRequestListByUser() {

        MetaDataReader mdr = new MetaDataReader();
        return mdr.readRequestsBy(ctx.getCallerPrincipal().getName());
    }
    
    @RolesAllowed({"LV_ROLE"})
    public List<Quest> getQuestListByRequest(Long id) {
        MetaDataReader mdr = new MetaDataReader();
        return mdr.readQuestsBy(ctx.getCallerPrincipal().getName(), id);
    }

    @RolesAllowed({"LV_ROLE"})
    public LVObject getObjectAttributeOverview(String tn) {
        MetaDataReader mdr = new MetaDataReader();
        return mdr.getAttributesOverview(ctx.getCallerPrincipal().getName(), tn);
    }
}
