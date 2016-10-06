package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCUser;

public class UserDeHalfoppedEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCChannel channel;
    private final IRCUser sender;
    private final IRCUser user;

    public UserDeHalfoppedEvent(IRCConnection ircConnection, IRCChannel channel, IRCUser sender, IRCUser user) {
        this.ircConnection = ircConnection;
        this.channel = channel;
        this.sender = sender;
        this.user = user;
    }

    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    public IRCChannel getChannel() {
        return channel;
    }

    public IRCUser getSender() {
        return sender;
    }

    public IRCUser getUser() {
        return user;
    }
}
