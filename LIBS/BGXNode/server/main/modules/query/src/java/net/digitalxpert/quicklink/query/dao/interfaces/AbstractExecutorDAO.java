package net.bgx.bgxnetwork.query.dao.interfaces;

import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.exception.query.QueryDataException;

/**
 * User: A.Borisenko
 * Date: 08.11.2006
 * Time: 19:35:00
 */
public interface AbstractExecutorDAO {
    public QueryData readQueryData(Long qId, int limit) throws QueryDataException;
    public void removeQueryData(Long qId) throws QueryDataException;
}
