package net.bgx.bgxnetwork.transfer.query;

/**
 * Class DataAccessException
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class DataAccessException extends ApplicationException {
  public DataAccessException() {
  }

  public DataAccessException(String message) {
    super(message);
  }

  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataAccessException(Throwable cause) {
    super(cause);
  }
}
