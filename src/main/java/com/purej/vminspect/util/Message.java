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

    private final String tag;

    private MessageType(String tag) {
      this.tag = tag;
    }

    /**
     * A tag for this message type.
     */
    public String getTag() {
      return tag;
    }
  }

  private final String text;
  private final MessageType type;

  /**
   * Creates a new instance of this class.
   *
   * @param text the message text
   * @param type the message type
   */
  public Message(String text, MessageType type) {
    super();
    this.text = text;
    this.type = type;
  }

  /**
   * Return the message text. Note: Is already encoded!
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the message type.
   */
  public MessageType getType() {
    return type;
  }
}
