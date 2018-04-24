package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;

/**
 * Class ApplicationException
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ApplicationException extends Exception implements Serializable {
  public ApplicationException() {
  }

  public ApplicationException(String message) {
    super(message);
  }

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }
}
