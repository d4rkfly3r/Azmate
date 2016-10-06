package net.d4rkfly3r.irc.azmate.lib;

public final class IRCPacketFactory {

    protected static IRCPacket createAWAY(final String reason) {
        return new IRCPacket(null, "AWAY", null, reason);
    }

    protected static IRCPacket createMOTD() {
        return new IRCPacket(null, "MOTD", null, null);
    }

    protected static IRCPacket createNAMES(final String channel) {
        return new IRCPacket(null, "NAMES", channel, null);
    }

    protected static IRCPacket createNICK(final String nick) {
        return new IRCPacket(null, "NICK", nick, null);
    }

    protected static IRCPacket createPASS(final String password) {
        return new IRCPacket(null, "PASS", password, null);
    }

    protected static IRCPacket createQUIT(final String message) {
        return new IRCPacket(null, "QUIT", null, message);
    }

    protected static IRCPacket createUSER(final String username,
                                          final String realname) {
        return new IRCPacket(null, "USER", username + " Sorcix.com *", realname);
    }

}
