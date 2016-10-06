package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;

public class ModeChangedEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCChannel channel;
    private final IRCUser sender;
    private final String mode;

    public ModeChangedEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCChannel channel, @Nonnull IRCUser sender, @Nonnull String mode) {
        this.ircConnection = ircConnection;
        this.channel = channel;
        this.sender = sender;
        this.mode = mode;
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

    @Nonnull
    public String getMode() {
        return mode;
    }
}
