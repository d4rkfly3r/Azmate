package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;

public class CTCPReplyEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCUser sender;
    private final String command;
    private final String args;

    public CTCPReplyEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCUser sender, @Nonnull String command, @Nonnull String args) {
        this.ircConnection = ircConnection;
        this.sender = sender;
        this.command = command;
        this.args = args;
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
    public String getCommand() {
        return command;
    }

    @Nonnull
    public String getArgs() {
        return args;
    }
}
