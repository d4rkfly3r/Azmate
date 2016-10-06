package net.d4rkfly3r.irc.azmate.lib;

/**
 * Thrown when the server password was wrong.
 */
public final class IRCPasswordException extends Exception {

    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = -7856391898471344111L;

    /**
     * Creates a new IRCPasswordException.
     *
     * @param string Error string.
     */
    public IRCPasswordException(final String string) {
        super(string);
    }
}
