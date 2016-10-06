package net.d4rkfly3r.irc.azmate.lib;

/**
 * Thrown when the nickname is already in use.
 */
public final class IRCNickNameException extends Exception {

    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = -7856391898471344111L;

    /**
     * Creates a new IRCNickNameException.
     *
     * @param string Error string.
     */
    public IRCNickNameException(final String string) {
        super(string);
    }
}
