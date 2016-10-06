package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;

public class UserQuitEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCUser quitter;
    private final String message;

    public UserQuitEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCUser quitter, @Nonnull String message) {
        this.ircConnection = ircConnection;
        this.quitter = quitter;
        this.message = message;
    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nonnull
    public IRCUser getQuitter() {
        return quitter;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }
}
