package com.purej.vminspect.util;

/**
 * A simple text message with a type (ok, info, warn, error).
 *
 * @author Stefan Mueller
 */
public final class Message {
  /**
   * The meaning of this message.
   */
  public static enum MessageType {
    /**
     * An OK message.
     */
    OK("okMsg"),
    /**
     * An info message.
     */
    INFO("infoMsg"),
    /**
     * A warning message.
     */
    WARN("warnMsg"),
    /**
     * An error message.
     */
    ERROR("errorMsg");

    private final String _tag;

    private MessageType(String tag) {
      _tag = tag;
    }

    /**
     * A tag for this message type.
     */
    public String getTag() {
      return _tag;
    }
  }

  private final String _text;
  private final MessageType _type;

  /**
   * Creates a new instance of this class.
   *
   * @param text the message text
   * @param type the message type
   */
  public Message(String text, MessageType type) {
    super();
    _text = text;
    _type = type;
  }

  /**
   * Return the message text.
   */
  public String getText() {
    return _text;
  }

  /**
   * Returns the message type.
   */
  public MessageType getType() {
    return _type;
  }
}
