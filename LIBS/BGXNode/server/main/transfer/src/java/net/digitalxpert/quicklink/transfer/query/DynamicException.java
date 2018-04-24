package net.bgx.bgxnetwork.transfer.query;

/**
 * Class DynamicException
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class DynamicException extends ApplicationException {
  public DynamicException() {
  }

  public DynamicException(String message) {
    super(message);
  }

  public DynamicException(String message, Throwable cause) {
    super(message, cause);
  }

  public DynamicException(Throwable cause) {
    super(cause);
  }
}
