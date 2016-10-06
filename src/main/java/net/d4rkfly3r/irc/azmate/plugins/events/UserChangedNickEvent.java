package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;

public class UserChangedNickEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCUser sender;
    private final IRCUser newUser;

    public UserChangedNickEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCUser sender, @Nonnull IRCUser newUser) {
        this.ircConnection = ircConnection;
        this.sender = sender;
        this.newUser = newUser;
    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nonnull
    public IRCUser getSender() {
        return sender;
    }

    @Nonnull
    public IRCUser getNewUser() {
        return newUser;
    }
}
