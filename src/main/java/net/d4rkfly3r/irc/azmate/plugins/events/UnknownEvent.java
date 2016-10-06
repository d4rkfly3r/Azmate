package net.d4rkfly3r.irc.azmate.plugins.events;

import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCPacket;

import javax.annotation.Nonnull;

public class UnknownEvent extends Event {
    private final IRCConnection ircConnection;
    private final IRCPacket packet;

    public UnknownEvent(@Nonnull IRCConnection ircConnection, @Nonnull IRCPacket packet) {
        this.ircConnection = ircConnection;
        this.packet = packet;

    }

    @Nonnull
    public IRCConnection getIrcConnection() {
        return ircConnection;
    }

    @Nonnull
    public IRCPacket getPacket() {
        return packet;
    }
}
