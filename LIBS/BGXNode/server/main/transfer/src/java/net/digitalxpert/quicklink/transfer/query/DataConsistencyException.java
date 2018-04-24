package net.bgx.bgxnetwork.transfer.query;

/**
 * Class DataConsistencyException
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class DataConsistencyException extends ApplicationException {
  public DataConsistencyException() {
  }

  public DataConsistencyException(String message) {
    super(message);
  }

  public DataConsistencyException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataConsistencyException(Throwable cause) {
    super(cause);
  }
}
