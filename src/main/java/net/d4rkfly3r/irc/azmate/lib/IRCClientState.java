package net.d4rkfly3r.irc.azmate.lib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Contains information about an {@link IRCConnection}.
 */
public final class IRCClientState {

    /**
     * The list of channels.
     */
    private final Map<String, IRCChannel> channels;
    /**
     * Contains a singleton for all known users.
     */
    private final Map<String, IRCUser> users;
    /**
     * The local user.
     */
    private IRCUser client;

    /**
     * Creates a new IRCClientState.
     */
    protected IRCClientState() {
        this.channels = new HashMap<>();
        this.users = new HashMap<>();
    }

    /**
     * Adds a IRCChannel to the IRCChannel map.
     *
     * @param IRCChannel The IRCChannel to add.
     */
    protected void addChannel(final IRCChannel IRCChannel) {
        if (!this.channels.containsKey(IRCChannel.getName().toLowerCase())) {
            this.channels.put(IRCChannel.getName().toLowerCase(), IRCChannel);
        }
    }

    /**
     * Adds a IRCUser to the IRCUser map.
     *
     * @param IRCUser The IRCUser to add.
     */
    protected void addUser(final IRCUser IRCUser) {
        if (!this.users.containsKey(IRCUser.getNickLower())) {
            this.users.put(IRCUser.getNickLower(), IRCUser);
        }
    }

    /**
     * Retrieves a shared IRCChannel object from the IRCChannel map.
     *
     * @param IRCChannel A IRCChannel object representing this IRCChannel.
     * @return The IRCChannel, or null if this IRCChannel doesn't exist. (The local
     * user is not in that IRCChannel)
     * @see #getChannel(String)
     */
    protected IRCChannel getChannel(final IRCChannel IRCChannel) {
        return this.getChannel(IRCChannel.getName());
    }

    /**
     * Retrieves a shared channel object from the channel map.
     *
     * @param channel The channel name.
     * @return The channel, or null if this channel doesn't exist. (The local
     * user is not in that channel)
     */
    protected IRCChannel getChannel(final String channel) {
        if (channel != null && this.channels.containsKey(channel.toLowerCase())) {
            return this.channels.get(channel.toLowerCase());
        }
        return null;
    }

    /**
     * Creates an iterator through all Channels.
     *
     * @return an iterator through all Channels.
     */
    public Iterator<IRCChannel> getChannels() {
        return this.channels.values().iterator();
    }

    /**
     * Retrieves the local {@link IRCUser}.
     *
     * @return The local {@code IRCUser}.
     */
    public IRCUser getClient() {
        return this.client;
    }

    /**
     * Set the local {@link IRCUser}.
     *
     * @param IRCUser The local {@code IRCUser}.
     */
    protected void setClient(final IRCUser IRCUser) {
        this.client = IRCUser;
    }

    /**
     * Retrieves a shared user object from the users map.
     *
     * @param nick The nickname of this user.
     * @return The shared user object, or null if there is no singleton IRCUser
     * object for this user.
     */
    protected IRCUser getUser(final String nick) {
        //TODO: implement singleton users in IRCUser, IRCChannel and IRCConnection
        if (this.users.containsKey(nick)) {
            return this.users.get(nick);
        }
        return null;
    }

    /**
     * Checks if given channel is in the channel map.
     *
     * @param name The name of this channel.
     * @return True if the channel is in the list, false otherwise.
     */
    protected boolean hasChannel(final String name) {
        return name != null && this.channels.containsKey(name.toLowerCase());
    }

    /**
     * Remove all channels from the channel map.
     */
    protected void removeAll() {
        this.channels.clear();
    }

    /**
     * Removes a channel from the channel map.
     *
     * @param channel The channel name.
     */
    protected void removeChannel(final String channel) {
        if (channel != null && this.channels.containsKey(channel.toLowerCase())) {
            this.channels.remove(channel.toLowerCase());
        }
    }
}
