package net.d4rkfly3r.irc.azmate.lib;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a channel on the IRC server.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class IRCChannel {

    /**
     * Possible channel prefixes.
     */
    protected static final String CHANNEL_PREFIX = "#&+!";
    /**
     * IRCConnection used to send messages to this channel.
     */
    private final IRCConnection irc;
    /**
     * IRCChannel name
     */
    private final String name;
    /**
     * The topic of this channel.
     */
    private String topic;
    /**
     * The user list.
     */
    private ConcurrentHashMap<String, IRCUser> users;

    /**
     * Creates a new {@code IRCChannel} object with given name.
     *
     * @param name   The channel name.
     * @param irc    The IRCConnection used to send messages to this
     *               channel.
     * @param global Whether this object is going to be shared.
     */
    protected IRCChannel(final String name, final IRCConnection irc, final boolean global) {
        this.name = name;
        this.irc = irc;
        if (global) {
            this.users = new ConcurrentHashMap<>(100, .75f, 2);
        } else {
            this.users = null;
        }
    }

    /**
     * Adds a IRCUser to the IRCUser list in this channel.
     *
     * @param ircUser The IRCUser to add.
     */
    protected void addUser(final IRCUser ircUser) {
        if (this.users != null) {
            this.users.putIfAbsent(ircUser.getNickLower(), ircUser);
        }
    }

    /**
     * Bans a IRCUser from this channel.
     *
     * @param ircUser The IRCUser to ban from this channel.
     * @param kick    Whether to kick this IRCUser after banning.
     */
    public void ban(final IRCUser ircUser, final boolean kick) {
        ban(ircUser, false, null);
    }

    /**
     * Bans a IRCUser from this channel with an optional kick message.
     *
     * @param ircUser The IRCUser to ban from this channel.
     * @param kick    Whether to kick this IRCUser after banning
     * @param reason  The message to append to the kick sent to the IRCUser.
     */
    public void ban(final IRCUser ircUser, final boolean kick, final String reason) {
        if (ircUser.getHostName() != null) {
            this.setMode("+b *!*@*" + ircUser.getHostName());
        } else {
            this.setMode("+b " + ircUser.getNick() + "!*@*");
        }

        if (kick) {
            if (reason == null) {
                this.kick(ircUser, "Banned");
            } else {
                this.kick(ircUser, reason);
            }
        }
    }

    /**
     * Changes the topic of this channel. Note that you need
     * privileges to do this.
     *
     * @param topic The new topic.
     */
    public void changeTopic(final String topic) {
        this.irc.getOutput().send("TOPIC " + this.getName() + " :" + topic);
    }

    @Override
    public boolean equals(final Object channel) {
        try {
            return ((IRCChannel) channel).getName().equalsIgnoreCase(this.name) && (this.irc != null && this.irc.equals(((IRCChannel) channel).irc));
        } catch (final Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Returns the channel name.
     *
     * @return The channel name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gives the topic of this channel, or null if unknown.
     *
     * @return The topic.
     */
    public String getTopic() {
        return this.topic;
    }

    /**
     * Changes the topic of this channel. This does not send a request
     * to the IRC server, to change the topic on the server, use
     * {@link #changeTopic(String)}.
     *
     * @param topic The new topic.
     */
    protected void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
     * Retrieves a global IRCUser object for a user in this channel. This
     * method is not public because end-users should use
     * {@link IRCConnection#createUser(String, String)} which always
     * returns a {@link IRCUser}, even if the user is not in this
     * channel.
     *
     * @param nickLower The nickname of this user.
     * @return A user object, or null if the user isn't in this
     * channel.
     */
    protected IRCUser getUser(final String nickLower) {
        return this.users.get(nickLower);
    }

    public IRCUser getUs() {
        return this.users.get(this.irc.getClient().getNickLower());
    }

    /**
     * Get an Iterator containing all users in this channel.
     * <p>
     * <pre>
     * Iterator&lt;IRCUser&gt; users = channel.getUsers();
     * IRCUser current;
     * while (users.hasNext()) {
     * 	current = users.next();
     * 	System.out.println(current.getNick() + &quot; is in this channel!&quot;);
     * }
     * </pre>
     *
     * @return All users in this channel.
     * @see #isGlobal()
     */
    public Iterator<IRCUser> getUsers() {
        return this.users.values().iterator();
    }

    /**
     * Give a ircUser admin privileges in this channel. (Not supported by
     * RFC!)
     *
     * @param ircUser The ircUser to give admin privileges.
     * @since 1.0.0
     */
    public void giveAdmin(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_ADMIN, ircUser, true);
    }

    /**
     * Give a ircUser founder privileges in this channel. (Not supported
     * by RFC!)
     *
     * @param ircUser The ircUser to give founder privileges.
     * @since 1.0.0
     */
    public void giveFounder(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_FOUNDER, ircUser, true);
    }

    /**
     * Give a ircUser halfop privileges in this channel. (Not supported
     * by RFC!)
     *
     * @param ircUser The ircUser to give halfop privileges.
     * @since 1.0.0
     */
    public void giveHalfop(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_HALF_OP, ircUser, true);
    }

    /**
     * Give a ircUser operator privileges in this channel.
     *
     * @param ircUser The ircUser to give operator privileges.
     */
    public void giveOperator(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_OPERATOR, ircUser, true);
    }

    /**
     * Give a ircUser voice privileges in this channel.
     *
     * @param ircUser The ircUser to give voice privileges.
     */
    public void giveVoice(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_VOICE, ircUser, true);
    }

    /**
     * Checks whether given user is in this channel.
     *
     * @param nick The nickname to check.
     * @return True if given user is in this channel, false otherwise.
     */
    public boolean hasUser(final String nick) {
        return (this.users != null) && this.users.containsKey(nick.toLowerCase());
    }

    /**
     * Checks whether given ircUser is in this channel.
     *
     * @param ircUser The ircUser to check.
     * @return True if given ircUser is in this channel, false otherwise.
     */
    public boolean hasUser(final IRCUser ircUser) {
        return this.hasUser(ircUser.getNickLower());
    }

    /**
     * Checks whether this IRCChannel object is shared. Shared channel
     * objects contain a list of users.
     *
     * @return True if this channel object is shared.
     */
    public boolean isGlobal() {
        return this.users != null;
    }

    /**
     * Attempts to join this channel.
     */
    public void join() {
        this.irc.getOutput().send("JOIN " + this.getName());
    }

    /**
     * Attempts to join this channel using given password.
     *
     * @param password The password needed to join this channel.
     */
    public void join(final String password) {
        this.irc.getOutput().send("JOIN " + this.getName() + " " + password);
    }

    /**
     * Kicks given ircUser from this channel.
     *
     * @param ircUser The ircUser to kick from this channel.
     */
    public void kick(final IRCUser ircUser) {
        this.irc.getOutput().send("KICK " + this.getName() + " " + ircUser.getNick());
    }

    /**
     * Kicks given ircUser from this channel, with reason.
     *
     * @param ircUser The ircUser to kick from this channel.
     * @param reason  The reason why this ircUser was kicked.
     */
    public void kick(final IRCUser ircUser, final String reason) {
        this.irc.getOutput().send("KICK " + this.getName() + " " + ircUser.getNick() + " :" + reason);
    }

    /**
     * Attempts to leave/part this channel.
     */
    public void part() {
        this.irc.getOutput().send("PART " + this.getName());
    }

    /**
     * Remove admin privileges from a ircUser in this channel.
     *
     * @param ircUser The ircUser to remove admin privileges from.
     * @since 1.0.0
     */
    public void removeAdmin(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_ADMIN, ircUser, false);
    }

    /**
     * Remove founder privileges from a ircUser in this channel.
     *
     * @param ircUser The ircUser to remove founder privileges from.
     * @since 1.0.0
     */
    public void removeFounder(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_FOUNDER, ircUser, false);
    }

    /**
     * Remove halfop privileges from a ircUser in this channel.
     *
     * @param ircUser The ircUser to remove halfop privileges from.
     * @since 1.0.0
     */
    public void removeHalfop(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_HALF_OP, ircUser, false);
    }

    /**
     * Remove operator privileges from a ircUser in this channel.
     *
     * @param ircUser The ircUser to remove operator privileges from.
     */
    public void removeOperator(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_OPERATOR, ircUser, false);
    }

    /**
     * Removes a ircUser from the ircUser list in this channel.
     *
     * @param ircUser The ircUser to remove.
     */
    protected void removeUser(final IRCUser ircUser) {
        if (this.users != null) {
            this.users.remove(ircUser.getNickLower());
        }
    }

    /**
     * Remove voice privileges from a ircUser in this channel.
     *
     * @param ircUser The ircUser to remove voice privileges from.
     */
    public void removeVoice(final IRCUser ircUser) {
        this.setMode(IRCUser.MODE_VOICE, ircUser, false);
    }

    /**
     * Changes the nickname of a user in this channel.
     *
     * @param old  The old nickname.
     * @param neww The new nickname.
     */
    protected void renameUser(final String old, final String neww) {
        if (this.users != null) {
            final IRCUser ircUser = this.users.remove(old);
            if (ircUser != null) {
                ircUser.setNick(neww);
                this.users.put(ircUser.getNickLower(), ircUser);
            }
        }
    }

    /**
     * Send message to channel.
     *
     * @param message The message to send.
     * @see #sendMessage(String)
     */
    public void send(final String message) {
        this.sendMessage(message);
    }

    /**
     * Sends a CTCP ACTION command.
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
        this.irc.getOutput().send("PRIVMSG " + this.getName() + " :" + IRCPacket.CTCP + command + IRCPacket.CTCP);
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
     * Send message to channel.
     *
     * @param message The message to send.
     */
    public void sendMessage(final String message) {
        this.irc.getOutput().send("PRIVMSG " + this.getName() + " :" + message);
    }

    /**
     * Send notice to channel.
     *
     * @param message The notice to send.
     */
    public void sendNotice(final String message) {
        this.irc.getOutput().send("NOTICE " + this.getName() + " :" + message);
    }

    /**
     * Changes a channel mode for given IRCUser.
     *
     * @param mode    The mode character.
     * @param ircUser The target IRCUser.
     * @param toggle  True to enable the mode, false to disable.
     */
    public void setMode(final char mode, final IRCUser ircUser, final boolean toggle) {
        if (toggle) {
            this.setMode("+" + mode + " " + ircUser.getNick());
        } else {
            this.setMode("-" + mode + " " + ircUser.getNick());
        }
    }

    /**
     * Changes a channel mode. The channel name is automatically
     * added.
     * <p>
     * <pre>
     * setMode(&quot;+m&quot;);
     * </pre>
     *
     * @param mode The mode to change.
     */
    public void setMode(final String mode) {
        this.irc.getOutput().send("MODE " + this.getName() + " " + mode);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Updates the current shared IRCUser object with changes in a fresh
     * one and returns the updated shared object.
     *
     * @param ircUser   The fresh IRCUser object.
     * @param createNew Whether to add this IRCUser to the channel if it
     *                  didn't exist.
     * @return The updated shared IRCUser object.
     */
    protected IRCUser updateUser(final IRCUser ircUser, final boolean createNew) {
        if (this.hasUser(ircUser.getNickLower())) {
            // update IRCUser if it exists
            final IRCUser shared = this.getUser(ircUser.getNickLower());
            shared.updateUser(ircUser);
            return shared;
        } else if (createNew) {
            // create a new one
            this.addUser(ircUser);
            return ircUser;
        }
        return null;
    }
}
