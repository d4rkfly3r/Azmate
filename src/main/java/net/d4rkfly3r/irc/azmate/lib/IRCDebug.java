package net.d4rkfly3r.irc.azmate.lib;

import java.io.PrintStream;

/**
 * Handles debug output on sIRC. The default output stream is {@code
 * System.out}, and debug is disabled until you enable it.
 */
public final class IRCDebug {

    /**
     * Whether debug output is enabled.
     */
    private static boolean enabled = false;
    /**
     * The {@link PrintStream} to use for debug output.
     */
    private static PrintStream out = System.out;

    /**
     * Checks whether debug output is enabled.
     *
     * @return True if debug output is enabled, false otherwise.
     */
    public static boolean isEnabled() {
        return IRCDebug.enabled;
    }

    /**
     * Enables or disables debug output.
     *
     * @param enable True to enable debug output, false to disable it.
     */
    public static void setEnabled(final boolean enable) {
        IRCDebug.enabled = enable;
    }

    /**
     * Sends a line to the log. All messages are prefixed with the
     * current timestamp.
     *
     * @param line The message to log.
     */
    protected static void log(final String line) {
        if (IRCDebug.enabled) {
            IRCDebug.out.println("[" + System.currentTimeMillis() + "] " + line);
            IRCDebug.out.flush();
        }
    }

    /**
     * Changes the stream used for debug output.
     *
     * @param out The new stream for debug output.
     */
    public static void setLogStream(final PrintStream out) {
        if (out != null) {
            IRCDebug.out = out;
        }
    }
}