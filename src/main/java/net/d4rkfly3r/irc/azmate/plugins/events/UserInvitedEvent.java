package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

import javax.annotation.Nonnull;

public class UserInvitedEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCUser sender;
    private final IRCUser invitee;
    private final IRCChannel channel;

    public UserInvitedEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCUser sender, @Nonnull IRCUser invitee, @Nonnull IRCChannel channel) {
        this.ircConnection = ircConnection;
        this.sender = sender;
        this.invitee = invitee;
        this.channel = channel;
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
    public IRCUser getInvitee() {
        return invitee;
    }

    @Nonnull
    public IRCChannel getChannel() {
        return channel;
    }
}

