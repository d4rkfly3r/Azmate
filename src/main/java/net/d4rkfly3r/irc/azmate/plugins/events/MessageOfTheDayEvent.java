package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCConnection;

import javax.annotation.Nonnull;

public class MessageOfTheDayEvent extends Event {
    private final IRCConnection ircConnection;
    private final String motd;

    public MessageOfTheDayEvent(@Nonnull IRCConnection ircConnection, @Nonnull String motd) {
        this.ircConnection = ircConnection;
        this.motd = motd;
    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nonnull
    public String getMotd() {
        return motd;
    }
}
