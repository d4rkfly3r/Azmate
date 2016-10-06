package net.d4rkfly3r.irc.azmate.lib;

/**
 * Represents a user on the IRC server.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class IRCUser {

    /**
     * Mode character for voice.
     */
    protected static final char MODE_VOICE = 'v';
    /**
     * Mode character for operator.
     */
    protected static final char MODE_OPERATOR = 'o';
    /**
     * Mode character for half-op. (Not supported by RFC!)
     */
    protected static final char MODE_HALF_OP = 'h';
    /**
     * Mode character for founder. (Not supported by RFC!)
     */
    protected static final char MODE_FOUNDER = 'q';
    /**
     * Mode character for admin. (Not supported by RFC!)
     */
    protected static final char MODE_ADMIN = 'a';
    /**
     * Prefix character for half-op. (Not supported by RFC!)
     */
    protected static final char PREFIX_HALF_OP = '%';
    /**
     * Prefix character for founder. (Not supported by RFC!)
     */
    protected static final char PREFIX_FOUNDER = '~';
    /**
     * Prefix character for admin. (Not supported by RFC!)
     */
    protected static final char PREFIX_ADMIN = '&';
    /**
     * Prefix character for voice.
     */
    protected static final char PREFIX_VOICE = '+';
    /**
     * Prefix character for operator.
     */
    protected static final char PREFIX_OPERATOR = '@';
    /**
     * Possible user prefixes.
     */
    protected static final String USER_PREFIX = "~@%+&";
    /**
     * Hostname of this user (or null if unknown).
     */
    private final String hostName;
    /**
     * IRCConnection used to contact this user.
     */
    private final IRCConnection ircConnection;
    /**
     * Username of this user (or null if unknown).
     */
    private final String userName;
    private final String realName;
    /**
     * Nickname of this user.
     */
    private String nick;
    /**
     * Lowercase nickname of this user.
     */
    private String nickLower;
    /**
     * The prefix.
     */
    private char prefix;
    /**
     * Custom address to send messages to.
     */
    private String address = null;

    /**
     * Creates a new {@code IRCUser}.
     *
     * @param nick          The nickname.
     * @param ircConnection The IRCConnection used to send messages to this
     *                      user.
     */
    public IRCUser(final String nick, final IRCConnection ircConnection) {
        this(nick, null, null, null, ircConnection);
    }

    /**
     * Creates a new {@code IRCUser}.
     *
     * @param nick          The nickname.
     * @param user          The username.
     * @param host          The hostname.
     * @param realName      The 'real name'.
     * @param ircConnection The IRCConnection used to send messages to this
     *                      user.
     */
    protected IRCUser(final String nick, final String user, final String host, final String realName, final IRCConnection ircConnection) {
        this.setNick(nick);
        this.realName = realName;
        this.userName = user;
        this.hostName = host;
        this.ircConnection = ircConnection;
        this.address = this.getNick();
    }

    @Override
    public boolean equals(final Object user) {
        try {
            return ((IRCUser) user).getNick().equalsIgnoreCase(this.nick);
        } catch (final Exception ex) {
            return false;
        }
    }

    /**
     * Returns the address sIRC uses to send messages to this user.
     *
     * @return The address used to send messages to this user.
     */
    private String getAddress() {
        return this.address;
    }

    /**
     * Returns the hostname for this user.
     *
     * @return The hostname, or null if unknown.
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Returns the nickname for this user.
     *
     * @return The nickname.
     */
    public String getNick() {
        return this.nick;
    }

    /**
     * Changes the nickname of this user.
     *
     * @param nick The new nickname.
     */
    protected void setNick(String nick) {
        if (nick == null)
            return;
        if (IRCUser.USER_PREFIX.indexOf(nick.charAt(0)) >= 0) {
            this.prefix = nick.charAt(0);
            nick = nick.substring(1);
        }
        this.nick = nick;
        this.nickLower = nick.toLowerCase();
        // TODO: Check whether addresses like nick!user@server are
        // allowed
        if ((this.address != null) && this.address.contains("@")) {
            this.address = this.nick + "@" + this.address.split("@", 2)[1];
        } else {
            this.address = this.nick;
        }
    }

    /**
     * Returns the lowercase nickname for this user.
     *
     * @return Lowercase nickname.
     */
    public String getNickLower() {
        return this.nickLower;
    }

    /**
     * Returns this user's prefix.
     *
     * @return The prefix.
     */
    public char getPrefix() {
        return this.prefix;
    }

    /**
     * Returns the username for this user.
     *
     * @return The username.
     */
    public String getUserName() {
        return this.userName;
    }

    public String getRealName() {
        return this.realName != null ? this.realName : this.nick;
    }

    /**
     * Checks whether this user has Admin privileges.
     *
     * @return True if this user is an admin.
     * @since 1.1.0
     */
    public boolean hasAdmin() {
        return this.getPrefix() == IRCUser.PREFIX_ADMIN;
    }

    /**
     * Checks whether this user has Founder privileges.
     *
     * @return True if this user is a founder.
     * @since 1.1.0
     */
    public boolean hasFounder() {
        return this.getPrefix() == IRCUser.PREFIX_FOUNDER;
    }

    /**
     * Checks whether this user has Halfop privileges.
     *
     * @return True if this user is a half operator.
     * @since 1.1.0
     */
    public boolean hasHalfOp() {
        return this.getPrefix() == IRCUser.PREFIX_HALF_OP;
    }

    /**
     * Checks whether this user has Operator privileges.
     *
     * @return True if this user is an operator.
     * @since 1.1.0
     */
    public boolean hasOperator() {
        return this.getPrefix() == IRCUser.PREFIX_OPERATOR;
    }

    /**
     * Checks whether this user has Voice privileges.
     *
     * @return True if this user has voice.
     * @since 1.1.0
     */
    public boolean hasVoice() {
        return this.getPrefix() == IRCUser.PREFIX_VOICE;
    }

    /**
     * Checks if this {@code IRCUser} represents us.
     *
     * @return True if this {@code IRCUser} represents us, false
     * otherwise.
     * @see IRCConnection#isUs(IRCUser)
     */
    public boolean isUs() {
        return this.ircConnection.isUs(this);
    }

    /**
     * Send message to user.
     *
     * @param message The message to send.
     * @see #sendMessage(String)
     */
    public void send(final String message) {
        this.sendMessage(message);
    }

    /**
     * Sends an action.
     *
     * @param action The action to send.
     */
    public void sendAction(final String action) {
        this.sendCtcpAction(action);
    }

    /**
     * Sends CTCP request. This is a very primitive way to send CTCP
     * commands, other methods are preferred.
     *
     * @param command Command to send.
     */
    public void sendCtcp(final String command) {
        this.ircConnection.getOutput().send("PRIVMSG " + this.getAddress() + " :" + IRCPacket.CTCP + command + IRCPacket.CTCP);
    }

    /**
     * Sends a CTCP ACTION command.
     *
     * @param action The action to send.
     * @see #sendCtcp(String)
     */
    protected void sendCtcpAction(final String action) {
        if ((action != null) && (action.length() != 0)) {
            this.sendCtcp("ACTION " + action);
        }
    }

    /**
     * Sends a CTCP CLIENTINFO command.
     */
    public void sendCtcpClientInfo() {
        this.sendCtcp("CLIENTINFO");
    }

    /**
     * Sends a CTCP PING command.
     *
     * @return The timestamp sent to this user.
     */
    public long sendCtcpPing() {
        final Long time = System.currentTimeMillis();
        this.sendCtcp("PING " + time.toString());
        return time;
    }

    /**
     * Sends CTCP reply using notices. Replies to CTCP requests should
     * be sent using a notice.
     *
     * @param command Command to send.
     */
    protected void sendCtcpReply(final String command) {
        this.sendCtcpReply(command, false);
    }

    /**
     * Sends CTCP reply using notices. Replies to CTCP requests should
     * be sent using a notice.
     *
     * @param command   Command to send.
     * @param skipQueue Whether to skip the outgoing message queue.
     */
    protected void sendCtcpReply(final String command, final boolean skipQueue) {
        if (skipQueue) {
            this.ircConnection.getOutput().sendNow("NOTICE " + this.getAddress() + " :" + IRCPacket.CTCP + command + IRCPacket.CTCP);
        } else {
            this.ircConnection.getOutput().send("NOTICE " + this.getAddress() + " :" + IRCPacket.CTCP + command + IRCPacket.CTCP);
        }
    }

    /**
     * Sends a CTCP VERSION command to this user.
     */
    public void sendCtcpVersion() {
        this.sendCtcp("VERSION");
    }

    /**
     * Send message to this user.
     *
     * @param message The message to send.
     */
    public void sendMessage(final String message) {
        this.ircConnection.getOutput().send("PRIVMSG " + this.getAddress() + " :" + message);
    }

    /**
     * Send notice to this user.
     *
     * @param message The notice to send.
     */
    public void sendNotice(final String message) {
        this.ircConnection.getOutput().send("NOTICE " + this.getAddress() + " :" + message);
    }

    /**
     * Sets a custom address for this user. This address will be used
     * to send messages to instead of simply the nickname. Use an
     * address like {@code nick@server}. Setting the address to
     * {@code @server} will prepend the nick automatically.
     *
     * @param address The address to use.
     */
    public void setCustomAddress(final String address) {
        if (address == null) {
            this.address = this.getNick();
        } else if (address.startsWith("@")) {
            this.address = this.getNick() + address;
        } else {
            this.address = address;
        }
    }

    /**
     * Changes a user mode for given user.
     *
     * @param mode   The mode character.
     * @param toggle True to enable the mode, false to disable.
     */
    public void setMode(final char mode, final boolean toggle) {
        if (toggle) {
            this.setMode("+" + mode);
        } else {
            this.setMode("-" + mode);
        }
    }

    /**
     * Changes a user mode. The address is automatically added.
     * <p>
     * <pre>
     * setMode(&quot;+m&quot;);
     * </pre>
     *
     * @param mode The mode to change.
     */
    public void setMode(final String mode) {
        this.ircConnection.getOutput().send("MODE " + this.getAddress() + " " + mode);
    }

    @Override
    public String toString() {
        return this.getNick();
    }

    /**
     * Updates this IRCUser object with data from given IRCUser.
     *
     * @param IRCUser The fresh IRCUser object.
     */
    protected void updateUser(final IRCUser IRCUser) {
        //TODO: Unfinished method?
    }
}
