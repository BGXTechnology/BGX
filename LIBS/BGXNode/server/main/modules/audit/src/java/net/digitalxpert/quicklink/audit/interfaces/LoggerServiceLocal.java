package net.bgx.bgxnetwork.audit.interfaces;

import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;

import javax.ejb.Local;

@Local
public interface LoggerServiceLocal{
    void setLog(EventLog log);
    String getQueryById(Long id);
    String getFullNameById(Long id);
}
