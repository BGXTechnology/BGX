package net.bgx.bgxnetwork.dbmanager.interfaces;

import net.bgx.bgxnetwork.transfer.query.DataAccessException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;

@Local
public interface DatabaseServiceLocal {
    @RolesAllowed({"LVSystem"})
    void killSession(Long sessionId) throws DataAccessException;
}
