package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UserPartedEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCChannel channel;
    private final IRCUser sender;
    private final String message;

    public UserPartedEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCChannel channel, @Nonnull IRCUser sender, @Nullable String message) {
        this.ircConnection = ircConnection;
        this.channel = channel;
        this.sender = sender;
        this.message = message;
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

    @Nullable
    public String getMessage() {
        return message;
    }
}
