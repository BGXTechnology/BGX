package net.bgx.bgxnetwork.query.corpscollector;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

/**
 * User: Ramek
 * Date: 22.08.2006
 * Time: 15:32:44
 */
public class CollectorService {
    String _query = null;

    public CollectorService(String query) {
        _query = query;
    }

    /**
    * Соединение закрывается после вызова этого метода.
    */
    void execute(Connection aConnection) {
      try {
        closeDeadQuery(aConnection);
      }
      catch (SQLException e) {
        throw new Error("can't close some queries", e);
      }
      finally {
        try {
          aConnection.close();
        }
        catch (SQLException ignore) {
        }
      }
   }

   private void closeDeadQuery(Connection aConnection) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = aConnection.prepareStatement(getQuery());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
            closeJDBCResources(statement);
        }
    }

    private void closeJDBCResources(Statement aStatement) {
        if (aStatement != null) {
            try {
                aStatement.close();
            }
            catch (SQLException ignore) {
            }
        }
    }

    private String getQuery(){
        return _query;
    }

}
