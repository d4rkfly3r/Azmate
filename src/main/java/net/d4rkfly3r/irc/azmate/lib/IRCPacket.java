package net.d4rkfly3r.irc.azmate.lib;

/**
 * Parses a raw server response into a more readable format.
 * <p>
 * <pre>
 * :&lt;prefix&gt; &lt;command&gt; &lt;receiver&gt; [&lt;arguments&gt;] [:&lt;message&gt;]
 * </pre>
 */
public final class IRCPacket {

    /**
     * Unknown command sent to IRC server.
     */
    protected static final int ERR_UNKNOWNCOMMAND = 421;
    /**
     * Termination of an RPL_MOTD list.
     */
    protected static final int RPL_ENDOFMOTD = 376;
    /**
     * Reply to MOTD. (message of the day)
     */
    protected static final int RPL_MOTD = 372;
    /**
     * Response to TOPIC with the set topic.
     */
    protected static final int RPL_TOPIC = 332;
    /**
     * Termination of an RPL_NAMREPLY list.
     */
    protected static final int RPL_ENDOFNAMES = 366;
    /**
     * Reply to NAMES (See RFC).
     */
    protected static final int RPL_NAMREPLY = 353;
    /**
     * Sent to the client to redirect it to another server.
     */
    protected static final int RPL_BOUNCE = 10;
    /**
     * CTCP message mark.
     */
    protected static final String CTCP = "\u0001";
    /**
     * Arguments separated by a space
     */
    private String arguments = null;
    /**
     * The numeric server reply.
     */
    private int cmdNumeric = -1;
    /**
     * The IRC command.
     */
    private String command = null;
    /**
     * Whether this is a CTCP command or not.
     */
    private boolean ctcp = false;
    /**
     * The message (basically anything behind the colon).
     */
    private String message = null;
    /**
     * Whether this is a numeric server reply.
     */
    private boolean numeric = false;
    /**
     * The sender.
     */
    private String prefix = null;
    /**
     * The sender user object.
     */
    private IRCUser sender = null;

    /**
     * Creates a new IRCPacket using the data from given raw IRC data.
     *
     * @param line Raw data from the server.
     * @param irc  The IRCConnection used to send messages.
     */
    public IRCPacket(String line, final IRCConnection irc) {
        line = IRCColors.remove(line);
        final int locLineStart = line.indexOf(':') + 1;
        int locCommand;
        // some messages don't have a prefix
        if ((locLineStart > 1) || (locLineStart < 0)) {
            locCommand = 0;
            this.prefix = null;
        } else {
            // space between sender and command
            locCommand = line.indexOf(' ', locLineStart + 1);
            // retrieve sender
            this.prefix = line.substring(locLineStart, locCommand);
        }
        // space between command and receiver
        final int locArgs = line.indexOf(' ', locCommand + 1);
        // retrieve command
        this.command = line.substring(locCommand + 1, locArgs);
        // colon between arguments and message
        final int locMsg = line.indexOf(':', locArgs);
        // if there are arguments, save them
        if ((locMsg - locArgs) > 1) {
            this.arguments = line.substring(locArgs + 1, locMsg - 1);
        } else if (locMsg < 0) {
            // there is no message, so arguments go to the end
            this.arguments = line.substring(locArgs + 1);
        }
        // If there is a message, save it
        if (locMsg > 0) {
            this.message = line.substring(locMsg + 1);
            // check if this message is a CTCP request
            if (this.message.startsWith(IRCPacket.CTCP)
                    && this.message.endsWith(IRCPacket.CTCP)) {
                this.ctcp = true;
                this.message = this.message.substring(1,
                        this.message.length() - 1);
            }
        }
        // check if the command is a server reply
        this.cmdNumeric = this.getInteger(this.command);
        if (this.cmdNumeric != -1) {
            // numeric server response
            this.numeric = true;
        }
        // if possible, parse the sender into a user object
        // TODO: Get this out of here, this shouldn't be in IRCPacket..
        if ((this.prefix != null) && (this.prefix.indexOf('!') > 0)) {
            final String[] stuff = this.prefix.split("@|!");
            if (stuff.length == 3) {
                this.sender = new IRCUser(stuff[0], stuff[1], stuff[2], null, irc);
            } else if (stuff.length == 1)
                this.sender = new IRCUser(stuff[0], irc);
        } else if (prefix != null) {
            this.sender = new IRCUser(this.prefix, irc);
        }
    }

    /**
     * Creates a new IRCPacket using given data.
     *
     * @param prefix    The prefix, or {@code null}.
     * @param command   The command
     * @param arguments A space separated arguments list, or {@code null}.
     * @param message   The message, or {@code null}.
     */
    public IRCPacket(final String prefix, final String command,
                        final String arguments, final String message) {
        this.prefix = prefix;
        this.command = command;
        this.arguments = arguments;
        this.message = message;
    }

    /**
     * Gives the arguments parsed from this raw server line.
     *
     * @return Arguments string, or {@code null} if there were none.
     */
    public String getArguments() {
        return this.arguments;
    }

    /**
     * Gives the arguments as an array.
     *
     * @return Arguments array, or {@code null} if there were none.
     */
    public String[] getArgumentsArray() {
        return this.arguments != null ? this.arguments.split(" ") : null;
    }

    /**
     * Gives the command parsed from this raw server line.
     *
     * @return The command string.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Tries to parse given string to an integer.
     *
     * @param parse String to parse.
     * @return Integer value, or -1 if the string is not an integer.
     */
    private int getInteger(final String parse) {
        try {
            return Integer.parseInt(parse);
        } catch (final NumberFormatException ex) {
            return -1;
        }
    }

    /**
     * Gives the message parsed from this raw server line.
     *
     * @return Message string, or {@code null} if there was none.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gives the command parsed from this raw server line.
     *
     * @return The command integer.
     */
    public int getNumericCommand() {
        return this.cmdNumeric;
    }

    /**
     * Gives the prefix of this packet. This usually is the sender of a message.
     * You might be able to use {@link #getSender()} instead.
     *
     * @return The sender string.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Generates a raw IRC packet.
     *
     * @return IRC String containing the data in this object.
     */
    protected String getRaw() {
        final StringBuilder buffer = new StringBuilder();

        if ((this.prefix != null) && (this.prefix.length() > 0)) {
            buffer.append(":").append(this.prefix).append(" ");
        }
        buffer.append(this.command);
        if ((this.arguments != null) && (this.arguments.length() > 0)) {
            buffer.append(" ").append(this.arguments);
        }
        if ((this.message != null) && (this.message.length() > 0)) {
            buffer.append(" :").append(this.message);
        }
        return buffer.toString();
    }

    /**
     * Returns the {@link IRCUser} that caused the server to send this packet.
     *
     * @return Sender {@link IRCUser}, or {@code null} if the sender was not a
     * user.
     */
    public IRCUser getSender() {
        return this.sender;
    }

    /**
     * Checks whether this line had arguments.
     *
     * @return True if there were arguments.
     */
    public boolean hasArguments() {
        return (this.arguments != null) && (this.arguments.length() > 0);
    }

    /**
     * Checks whether this line had a message.
     *
     * @return True if there was a message.
     */
    public boolean hasMessage() {
        return (this.message != null) && (this.message.trim().length() > 0);
    }

    /**
     * Checks whether this command is a CTCP command.
     *
     * @return True if this message was sent using CTCP, false otherwise.
     */
    public boolean isCtcp() {
        return this.ctcp;
    }

    /**
     * Checks if this server line is a numeric reply.
     *
     * @return True if this line is a numeric reply.
     */
    public boolean isNumeric() {
        return this.numeric;
    }
}
