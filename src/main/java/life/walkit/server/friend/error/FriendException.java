package life.walkit.server.friend.error;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class FriendException extends BaseException {

  public FriendException(ErrorCode errorCode) {
    super(errorCode);
  }
}
