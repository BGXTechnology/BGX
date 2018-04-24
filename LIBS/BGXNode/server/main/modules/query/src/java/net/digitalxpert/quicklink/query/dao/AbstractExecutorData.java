package net.bgx.bgxnetwork.query.dao;

import net.bgx.bgxnetwork.query.dao.interfaces.AbstractExecutorDAO;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.ErrorList;

import java.sql.*;
import java.util.logging.Logger;

/**
 * User: A.Borisenko
 * Date: 08.11.2006
 * Time: 19:33:57
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExecutorData extends ExecutorDAO implements AbstractExecutorDAO {
    private Logger log = Logger.getLogger(AbstractExecutorData.class.getName());
}
