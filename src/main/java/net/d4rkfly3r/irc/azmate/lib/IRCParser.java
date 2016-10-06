package net.d4rkfly3r.irc.azmate.lib;

import net.d4rkfly3r.irc.azmate.plugins.PluginBus;
import net.d4rkfly3r.irc.azmate.plugins.events.*;

import java.util.Date;
import java.util.Iterator;

/**
 * Parses incoming messages and calls event handlers.
 */
final class IRCParser {

    /**
     * Buffer for motd.
     */
    private StringBuffer buffer = null;

    /**
     * Parses normal IRC commands.
     *
     * @param ircConnection IRCConnection receiving this packet.
     * @param packet        The input packet.
     */
    protected void parseCommand(final IRCConnection ircConnection, final IRCPacket packet) {
        if (packet.getCommand().equals("PRIVMSG") && (packet.getArguments() != null)) {
            if (packet.isCtcp()) {
                // reply to CTCP commands
                if (packet.getMessage().startsWith("ACTION ")) {
                    if (IRCChannel.CHANNEL_PREFIX.indexOf(packet.getArguments().charAt(0)) >= 0) {
                        // to channel
                        final IRCChannel chan = ircConnection.getState().getChannel(packet.getArguments());
                        PluginBus.getInstance().fireEvent(new ActionEvent(ircConnection, chan.updateUser(packet.getSender(), true), chan, packet.getMessage().substring(7)));
//                        for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                            it.next().onAction(ircConnection, chan.updateUser(packet.getSender(), true), chan, packet.getMessage().substring(7));
//                        }
                    } else {
                        // to user
                        PluginBus.getInstance().fireEvent(new ActionEvent(ircConnection, packet.getSender(), packet.getMessage().substring(7)));
//                        for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                            it.next().onAction(ircConnection, packet.getSender(), packet.getMessage().substring(7));
//                        }
                    }
                } else if (packet.getMessage().equals("VERSION") || packet.getMessage().equals("FINGER")) {
                    // send custom version string
                    packet.getSender().sendCtcpReply("VERSION " + ircConnection.getVersion());
                } else if (packet.getMessage().equals("SIRCVERS")) {
                    // send sIRC version information
                    packet.getSender().sendCtcpReply("SIRCVERS " + IRCConnection.ABOUT);
                } else if (packet.getMessage().equals("TIME")) {
                    // send current date&time
                    packet.getSender().sendCtcpReply(new Date().toString());
                } else if (packet.getMessage().startsWith("PING ")) {
                    // send ping reply
                    packet.getSender().sendCtcpReply("PING " + packet.getMessage().substring(5), true);
                } else if (packet.getMessage().startsWith("SOURCE")) {
                    // send sIRC source
                    packet.getSender().sendCtcpReply("SOURCE https://github.com/d4rkfly3r/Azmate");
                } else if (packet.getMessage().equals("CLIENTINFO")) {
                    // send client info
                    packet.getSender().sendCtcpReply("CLIENTINFO VERSION TIME PING SOURCE FINGER SIRCVERS");
                } else {
                    // send error message
                    packet.getSender().sendCtcpReply("ERRMSG CTCP Command not supported. Use CLIENTINFO to list supported commands.");
                }
            } else if (packet.getArguments().startsWith("#") || packet.getArguments().startsWith("&")) {
                // to channel
                final IRCChannel chan = ircConnection.getState().getChannel(packet.getArguments());
                PluginBus.getInstance().fireEvent(new MessageEvent(ircConnection, chan.updateUser(packet.getSender(), true), chan, packet.getMessage()));
//                for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                    it.next().onMessage(ircConnection, chan.updateUser(packet.getSender(), true), chan, packet.getMessage());
//                }
            } else {
                // to user
                PluginBus.getInstance().fireEvent(new MessageEvent(ircConnection, packet.getSender(), packet.getMessage()));
//                for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                    it.next().onPrivateMessage(ircConnection, packet.getSender(), packet.getMessage());
//                }
            }
        } else if (packet.getCommand().equals("NOTICE") && (packet.getArguments() != null)) {
            if (packet.isCtcp()) {
                // receive CTCP replies.
                final int cmdPos = packet.getMessage().indexOf(' ');
                final String command = packet.getMessage().substring(0, cmdPos);
                final String args = packet.getMessage().substring(cmdPos + 1);
                if (command.equals("VERSION") || command.equals("PING") || command.equals("CLIENTINFO")) {
                    PluginBus.getInstance().fireEvent(new CTCPReplyEvent(ircConnection, packet.getSender(), command, args));
//                    for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                        it.next().onCtcpReply(ircConnection, packet.getSender(), command, args);
//                    }
                }
            } else if (IRCChannel.CHANNEL_PREFIX.indexOf(packet.getArguments().charAt(0)) >= 0) {
                // to channel
                final IRCChannel chan = ircConnection.getState().getChannel(packet.getArguments());
                PluginBus.getInstance().fireEvent(new NoticeEvent(ircConnection, chan.updateUser(packet.getSender(), true), chan, packet.getMessage()));
//                for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                    it.next().onNotice(ircConnection, chan.updateUser(packet.getSender(), true), chan, packet.getMessage());
//                }
            } else {
                // to user
                PluginBus.getInstance().fireEvent(new NoticeEvent(ircConnection, packet.getSender(), packet.getMessage()));
//                for (final Iterator<MessageListener> it = ircConnection.getMessageListeners(); it.hasNext(); ) {
//                    it.next().onNotice(ircConnection, packet.getSender(), packet.getMessage());
//                }
            }
        } else if (packet.getCommand().equals("JOIN")) {
            // some server seem to send the joined channel as message,
            // while others have it as an argument. (quakenet related)
            String channel;
            if (packet.hasMessage()) {
                channel = packet.getMessage();
            } else {
                channel = packet.getArguments();
            }
            // someone joined a channel
            if (packet.getSender().isUs()) {
                // if the user joining the channel is the client
                // we need to add it to the channel list.
                ircConnection.getState().addChannel(new IRCChannel(channel, ircConnection, true));
            } else {
                // add user to channel list.
                ircConnection.getState().getChannel(channel).addUser(packet.getSender());
            }
            PluginBus.getInstance().fireEvent(new UserJoinedEvent(ircConnection, ircConnection.getState().getChannel(channel), packet.getSender()));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onJoin(ircConnection, ircConnection.getState().getChannel(channel), packet.getSender());
//            }
        } else if (packet.getCommand().equals("PART")) {
            // someone left a channel
            if (packet.getSender().isUs()) {
                // if the user leaving the channel is the client
                // we need to remove it from the channel list
                ircConnection.getState().removeChannel(packet.getArguments());
            } else {
                // remove user from channel list.
                ircConnection.getState().getChannel(packet.getArguments()).removeUser(packet.getSender());
            }
            PluginBus.getInstance().fireEvent(new UserPartedEvent(ircConnection, ircConnection.getState().getChannel(packet.getArguments()), packet.getSender(), packet.getMessage()));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onPart(ircConnection, ircConnection.getState().getChannel(packet.getArguments()), packet.getSender(), packet.getMessage());
//            }
        } else if (packet.getCommand().equals("QUIT")) {
            // someone quit the IRC server
            final IRCUser quitter = packet.getSender();
            PluginBus.getInstance().fireEvent(new UserQuitEvent(ircConnection, quitter, packet.getMessage()));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onQuit(ircConnection, quitter, packet.getMessage());
//            }
            for (final Iterator<IRCChannel> it = ircConnection.getState().getChannels(); it.hasNext(); ) {
                final IRCChannel IRCChannel = it.next();
                if (IRCChannel.hasUser(quitter)) {
                    IRCChannel.removeUser(quitter);
                }
            }
        } else if (packet.getCommand().equals("KICK")) {
            // someone was kicked from a IRCChannel
            final String[] data = packet.getArgumentsArray();
            if (data == null || data.length < 2) return;
            final IRCUser kicked = new IRCUser(data[1], ircConnection);
            final IRCChannel ircChannel = ircConnection.getState().getChannel(data[0]);
            if (kicked.isUs()) {
                // if the user leaving the IRCChannel is the client
                // we need to remove it from the IRCChannel list
                ircConnection.getState().removeChannel(data[0]);
            } else {
                // remove user from IRCChannel list.
                ircChannel.removeUser(kicked);
            }
            PluginBus.getInstance().fireEvent(new UserKickedEvent(ircConnection, ircChannel, packet.getSender(), kicked, packet.getMessage()));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onKick(ircConnection, IRCChannel, packet.getSender(), kicked, packet.getMessage());
//            }
        } else if (packet.getCommand().equals("MODE")) {
            this.parseMode(ircConnection, packet);
        } else if (packet.getCommand().equals("TOPIC")) {
            // someone changed the topic.
            final IRCChannel chan = ircConnection.getState().getChannel(packet.getArguments());
            PluginBus.getInstance().fireEvent(new TopicChangedEvent(ircConnection, chan, chan.updateUser(packet.getSender(), false), packet.getMessage()));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onTopic(ircConnection, chan, chan.updateUser(packet.getSender(), false), packet.getMessage());
//            }
        } else if (packet.getCommand().equals("NICK")) {
            IRCUser newIRCUser;
            if (packet.hasMessage()) {
                newIRCUser = new IRCUser(packet.getMessage(), ircConnection);
            } else {
                newIRCUser = new IRCUser(packet.getArguments(), ircConnection);
            }
            // someone changed his nick
            for (final Iterator<IRCChannel> it = ircConnection.getState().getChannels(); it.hasNext(); ) {
                it.next().renameUser(packet.getSender().getNickLower(), newIRCUser.getNick());
            }
            // change local user
            if (packet.getSender().isUs()) {
                ircConnection.getState().getClient().setNick(newIRCUser.getNick());
            }
            PluginBus.getInstance().fireEvent(new UserChangedNickEvent(ircConnection, packet.getSender(), newIRCUser));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onNick(ircConnection, packet.getSender(), newIRCUser);
//            }
        } else if (packet.getCommand().equals("INVITE")) {
            // someone was invited
            final String[] args = packet.getArgumentsArray();
            if (args == null) return;
            if ((args.length >= 2) && (packet.getMessage() == null)) {
                final IRCChannel IRCChannel = ircConnection.createChannel(args[1]);
                PluginBus.getInstance().fireEvent(new UserInvitedEvent(ircConnection, packet.getSender(), new IRCUser(args[0], ircConnection), IRCChannel));
//                for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                    it.next().onInvite(ircConnection, packet.getSender(), new IRCUser(args[0], ircConnection), IRCChannel);
//                }
            }
        } else {
            PluginBus.getInstance().fireEvent(new UnknownEvent(ircConnection, packet));
//            if (ircConnection.getAdvancedListener() != null) {
//                ircConnection.getAdvancedListener().onUnknown(ircConnection, packet);
//            }
        }
    }

    /**
     * Parses mode changes.
     *
     * @param ircConnection  IRCConnection receiving this packet.
     * @param packet The mode change packet.
     */
    private void parseMode(final IRCConnection ircConnection, final IRCPacket packet) {
        final String[] args = packet.getArgumentsArray();
        if ((args.length >= 2) && (IRCChannel.CHANNEL_PREFIX.indexOf(args[0].charAt(0)) >= 0)) {
            // general mode event listener
            PluginBus.getInstance().fireEvent(new ModeChangedEvent(ircConnection, ircConnection.getState().getChannel(args[0]), packet.getSender(), packet.getArguments().substring(args[0].length() + 1)));
//            for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                it.next().onMode(ircConnection, ircConnection.getState().getChannel(args[0]), packet.getSender(), packet.getArguments().substring(args[0].length() + 1));
//            }
            if ((args.length >= 3)) {
                final IRCChannel ircChannel = ircConnection.getState().getChannel(args[0]);
                final String mode = args[1];
                final boolean enable = mode.charAt(0) == '+';
                char current;
                // tries all known modes.
                // this is an ugly part of sIRC, but the only way to
                // do this.
                for (int x = 2; x < args.length; x++) {
                    current = mode.charAt(x - 1);
                    if (current == IRCUser.MODE_VOICE) {
                        // voice or devoice
                        ircConnection.askNames(ircChannel);
                        if (enable) {
                            PluginBus.getInstance().fireEvent(new UserVoicedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onVoice(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        } else {
                            PluginBus.getInstance().fireEvent(new UserDeVoicedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onDeVoice(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        }
                    } else if (current == IRCUser.MODE_ADMIN) {
                        // admin or deadmin
                        ircConnection.askNames(ircChannel);
                        if (enable) {
                            PluginBus.getInstance().fireEvent(new UserAdminedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onAdmin(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        } else {
                            PluginBus.getInstance().fireEvent(new UserDeAdminedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onDeAdmin(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        }
                    } else if (current == IRCUser.MODE_OPERATOR) {
                        // op or deop
                        ircConnection.askNames(ircChannel);
                        if (enable) {
                            PluginBus.getInstance().fireEvent(new UserOppedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onOp(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        } else {
                            PluginBus.getInstance().fireEvent(new UserDeOppedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onDeOp(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        }
                    } else if (current == IRCUser.MODE_HALF_OP) {
                        // halfop or dehalfop
                        ircConnection.askNames(ircChannel);
                        if (enable) {
                            PluginBus.getInstance().fireEvent(new UserHalfoppedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onHalfop(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        } else {
                            PluginBus.getInstance().fireEvent(new UserDeHalfoppedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onDeHalfop(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        }
                    } else if (current == IRCUser.MODE_FOUNDER) {
                        // founder or defounder
                        ircConnection.askNames(ircChannel);
                        if (enable) {
                            PluginBus.getInstance().fireEvent(new UserFounderedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onFounder(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        } else {
                            PluginBus.getInstance().fireEvent(new UserDeFounderedEvent(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x])));
//                            for (final Iterator<ModeListener> it = ircConnection.getModeListeners(); it.hasNext(); ) {
//                                it.next().onDeFounder(ircConnection, ircChannel, packet.getSender(), ircConnection.createUser(args[x]));
//                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Parses numeric IRC replies.
     *
     * @param ircConnection  IRCConnection receiving this packet.
     * @param packet The input packet.
     */
    protected void parseNumeric(final IRCConnection ircConnection, final IRCPacket packet) {
        switch (packet.getNumericCommand()) {
            case IRCPacket.RPL_TOPIC:
                PluginBus.getInstance().fireEvent(new TopicChangedEvent(ircConnection, ircConnection.getState().getChannel(packet.getArgumentsArray()[1]), null, packet.getMessage()));
//                for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                    it.next().onTopic(ircConnection, ircConnection.getState().getChannel(packet.getArgumentsArray()[1]), null, packet.getMessage());
//                }
                break;
            case IRCPacket.RPL_NAMREPLY:
                final String[] arguments = packet.getArgumentsArray();
                final IRCChannel IRCChannel = ircConnection.getState().getChannel(arguments[arguments.length - 1]);
                if (IRCChannel != null) {
                    final String[] users = packet.getMessage().split(" ");
                    IRCUser buffer;
                    for (final String user : users) {
                        buffer = new IRCUser(user, ircConnection);
                        /*
                         * if (IRCChannel.hasUser(buffer)) {
						 * IRCChannel.addUser(buffer); }
						 * IRCChannel.addUser(buffer);
						 */
                        IRCChannel.updateUser(buffer, true);
                    }
                }
                break;
            case IRCPacket.RPL_MOTD:
                if (this.buffer == null) {
                    this.buffer = new StringBuffer();
                }
                this.buffer.append(packet.getMessage());
                this.buffer.append(IRCConnection.ENDLINE);
                break;
            case IRCPacket.RPL_ENDOFMOTD:
                if (this.buffer != null) {
                    final String motd = this.buffer.toString();
                    this.buffer = null;
                    PluginBus.getInstance().fireEvent(new MessageOfTheDayEvent(ircConnection, motd));
//                    for (final Iterator<ServerListener> it = ircConnection.getServerListeners(); it.hasNext(); ) {
//                        it.next().onMotd(ircConnection, motd);
//                    }
                }
                break;
            case IRCPacket.RPL_BOUNCE:
                // redirect to another server.
                if (ircConnection.isBounceAllowed()) {
                    ircConnection.disconnect();
                    ircConnection.setServer(new IRCServer(packet.getArgumentsArray()[0], packet.getArgumentsArray()[1]));
                    try {
                        ircConnection.connect();
                    } catch (final Exception ex) {
                        // TODO: exception while connecting to new
                        // server?
                    }
                }
                break;
            default:
                PluginBus.getInstance().fireEvent(new UnknownEvent(ircConnection, packet));
        }
    }
}
