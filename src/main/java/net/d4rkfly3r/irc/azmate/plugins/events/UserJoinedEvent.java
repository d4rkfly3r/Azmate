package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;

public class UserJoinedEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCChannel channel;
    private final IRCUser sender;

    public UserJoinedEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCChannel channel, @Nonnull IRCUser sender) {
        this.ircConnection = ircConnection;
        this.channel = channel;
        this.sender = sender;
    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nonnull
    public IRCChannel getChannel() {
        return channel;
    }

    @Nonnull
    public IRCUser getSender() {
        return sender;
    }
}
