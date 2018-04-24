package net.bgx.bgxnetwork.transfer.query;

/**
 * Class InsufficientPrivilegesException
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class InsufficientPrivilegesException extends ApplicationException {
  public InsufficientPrivilegesException() {
  }

  public InsufficientPrivilegesException(String message) {
    super(message);
  }

  public InsufficientPrivilegesException(String message, Throwable cause) {
    super(message, cause);
  }

  public InsufficientPrivilegesException(Throwable cause) {
    super(cause);
  }
}
