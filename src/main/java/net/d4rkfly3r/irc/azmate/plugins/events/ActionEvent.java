package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCUser sender;
    private final IRCChannel channel;
    private final String message;

    public ActionEvent(@Nonnull IRCConnection ircConnection, @Nullable IRCUser sender, @Nullable String message) {
        this.ircConnection = ircConnection;
        this.sender = sender;
        this.channel = null;
        this.message = message;
    }

    public ActionEvent(@Nonnull IRCConnection ircConnection, @Nullable IRCUser sender, @Nullable IRCChannel channel, @Nullable String message) {
        this.ircConnection = ircConnection;
        this.sender = sender;
        this.channel = channel;
        this.message = message;
    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nullable
    public IRCUser getSender() {
        return sender;
    }

    @Nullable
    public IRCChannel getChannel() {
        return channel;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nonnull
    public Boolean isForChannel() {
        return channel != null;
    }
}
