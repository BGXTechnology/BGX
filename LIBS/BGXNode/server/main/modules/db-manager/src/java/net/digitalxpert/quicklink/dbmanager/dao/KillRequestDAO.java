package net.bgx.bgxnetwork.dbmanager.dao;

import java.sql.SQLException;

public interface KillRequestDAO {
    void killStatement(Long sessionId) throws SQLException;
}
