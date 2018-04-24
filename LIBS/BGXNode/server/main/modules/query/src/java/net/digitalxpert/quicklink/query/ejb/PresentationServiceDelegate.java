package net.bgx.bgxnetwork.query.ejb;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.annotation.ejb.RemoteBinding;

import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.annotation.security.PermitAll;

import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.query.interfaces.PresentationServiceRemote;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceLocal;
import net.bgx.bgxnetwork.query.interfaces.PresentationServiceLocal;
import net.bgx.bgxnetwork.persistence.metadata.NodePK;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkPK;
import net.bgx.bgxnetwork.exception.metadata.MetaDataBusinessException;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 17:47:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@PermitAll
@RemoteBinding(clientBindUrl = SystemSetting.QL_CLIENT_BIND_URL)
public class PresentationServiceDelegate implements PresentationServiceRemote {
    @EJB
    private PresentationServiceLocal presentationService;

    public Object getControlObject(NodePK nodePK) {
        try {
            return presentationService.getControlObject(nodePK);
        } catch (Exception ex) {
            return ex;
        }
    }

    public Object getLinkObject(LinkPK linkPK) {
        try {
            return presentationService.getLinkObject(linkPK);
        } catch (Exception ex) {
            return ex;
        }
    }

    public Object updateControlObject(ControlObject controlObject) throws MetaDataBusinessException {
        try {
            return presentationService.updateControlObject(controlObject);
        }
        catch (Exception ex) {
            return ex;
        }

    }

    public Object getPropertyValueByGroupPropertyType(NodePK pk, Integer idGroup) {
        try {
            return presentationService.getPropertyValueByGroupPropertyType(pk, idGroup);
        }
        catch (Exception ex) {
            return ex;
        }
    }
}
