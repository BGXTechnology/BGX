package net.bgx.bgxnetwork.dbmanager.dao.oracle;

import net.bgx.bgxnetwork.dbmanager.dao.KillRequestDAO;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class KillRequestDAOImpl implements KillRequestDAO {
    private DataSource ds;

    public KillRequestDAOImpl(DataSource ds) {
        this.ds = ds;
    }

    public void killStatement(Long sessionId) throws SQLException {
        Connection connection = null;
        CallableStatement cstmt = null;
        try {
            connection = ds.getConnection();
            cstmt = connection.prepareCall("{call QLDS.SP_KILL_SESSION (?)}");
            cstmt.setLong(1, sessionId);
            cstmt.execute();
        } finally {
            try {
                if (cstmt != null) cstmt.close();
                if (connection != null) connection.close();
            } catch (SQLException ignore) {
            }
        }
    }
}
