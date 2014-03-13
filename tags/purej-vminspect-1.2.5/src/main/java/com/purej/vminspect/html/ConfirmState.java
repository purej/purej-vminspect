package com.purej.vminspect.html;

/**
 * The current confirmation state for write-operations.
 *
 * @author Stefan Mueller
 */
public enum ConfirmState {
  /**
   * If no confirm is required at all (default).
   */
  OFF,

  /**
   * If the next screen will be a confirm screen.
   */
  NEXT,

  /**
   * If this screen is a confirm screen.
   */
  NOW;

  /**
   * If state equals NOW.
   */
  public boolean isNow() {
    return this == NOW;
  }

  /**
   * If state equals NEXT.
   */
  public boolean isNext() {
    return this == NEXT;
  }
}
