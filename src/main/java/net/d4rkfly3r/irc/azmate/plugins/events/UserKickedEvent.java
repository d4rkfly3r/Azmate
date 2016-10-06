package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UserKickedEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCChannel ircChannel;
    private final IRCUser sender;
    private final IRCUser kicked;
    private final String message;

    public UserKickedEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCChannel ircChannel, @Nonnull IRCUser sender, @Nonnull IRCUser kicked, @Nullable String message) {
        this.ircConnection = ircConnection;
        this.ircChannel = ircChannel;
        this.sender = sender;
        this.kicked = kicked;
        this.message = message;
    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nonnull
    public IRCChannel getIrcChannel() {
        return ircChannel;
    }

    @Nonnull
    public IRCUser getSender() {
        return sender;
    }

    @Nonnull
    public IRCUser getKicked() {
        return kicked;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}
