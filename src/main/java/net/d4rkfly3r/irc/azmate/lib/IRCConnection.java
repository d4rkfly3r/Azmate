package net.d4rkfly3r.irc.azmate.lib;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Main IRC Connection class in sIRC.
 * <p>
 * sIRC acts as a layer between an IRC server and java applications. It provides
 * an event-driven architecture to handle common IRC events.
 * </p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class IRCConnection {

    /**
     * sIRC Library version.
     */
    public static final String VERSION = "1.1.6-SNAPSHOT";
    /**
     * Debug: Show raw messages
     */
    protected static final boolean DEBUG_MSG = false;
    /**
     * End line character.
     */
    protected static final String ENDLINE = "\n";
    /**
     * The sIRC about string, used in CTCP
     */
    public static String ABOUT = "Sorcix Lib-IRC (sIRC) v" + IRCConnection.VERSION;
    /**
     * IRC Client state.
     */
    private final IRCClientState state;
    /**
     * Connection OutputStream thread.
     */
    protected IRCOutput out = null;
    /**
     * Connection InputStream thread.
     */
    private IRCInput in = null;
    /**
     * Outgoing message delay. (Flood control)
     */
    private int messageDelay = 100;
    /**
     * Connection socket.
     */
    private Socket socket = null;
    /**
     * Custom version string.
     */
    private String version = null;
    /**
     * The server this IRCConnection is connected to.
     */
    private IRCServer server;
    /**
     * Whether we're connected or not.
     */
    private boolean connected;
    /**
     * The Character set to use for encoding the connection
     */
    private Charset charset = Charset.defaultCharset();
    /**
     * Whether to allow server redirection (bounce) or not.
     */
    private boolean bounceAllowed = false;

    /**
     * Creates a new IRCConnection object.
     */
    public IRCConnection() {
        this(null, IRCServer.DEFAULT_PORT, null);
    }

    /**
     * Creates a new IRCConnection object.
     *
     * @param server Server address.
     */
    public IRCConnection(final String server) {
        this(server, IRCServer.DEFAULT_PORT, null);
    }

    /**
     * Creates a new IRCConnection object.
     *
     * @param server Server address.
     * @param port   Port number to connect to.
     */
    public IRCConnection(final String server, final int port) {
        this(server, port, null);
    }

    /**
     * Creates a new IRCConnection object.
     *
     * @param server   Server address.
     * @param port     Port number to connect to
     * @param password The password to use.
     */
    public IRCConnection(final String server, final int port,
                         final String password) {
        this.server = new IRCServer(server, port, password, false);
        this.state = new IRCClientState();
    }

    /**
     * Creates a new IRCConnection object.
     *
     * @param server   Server address.
     * @param password The password to use.
     */
    public IRCConnection(final String server, final String password) {
        this(server, IRCServer.DEFAULT_PORT, password);
    }


    /**
     * Sends the MOTD command to the server, which makes the server send us the
     * Message of the Day. (Through ServerListener)
     *
     * @since 1.0.2
     */
    public void askMotd() {
        this.out.send(IRCPacketFactory.createMOTD());
    }

    /**
     * Send a raw command to the IRC server.  Unrecognized responses
     * are passed to the AdvancedListener's onUnknown() callback.
     *
     * @param line The raw line to send.
     */
    public void sendRaw(final String line) {
        this.out.send(line);
    }

    /**
     * Asks the userlist for a certain IRCChannel.
     *
     * @param IRCChannel The IRCChannel to request the userlist for.
     */
    protected void askNames(final IRCChannel IRCChannel) {
        this.out.send(IRCPacketFactory.createNAMES(IRCChannel.getName()));
    }

    /**
     * Closes all streams.
     */
    private void close() {
        try {
            this.in.interrupt();
            this.out.interrupt();
            // close input stream
            this.in.close();
            // close output stream
            this.out.close();
            // close socket
            if (this.socket.isConnected()) {
                this.socket.close();
            }
        } catch (final Exception ex) {
            // ignore
        }
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException          When anything went wrong while connecting.
     * @throws IRCNickNameException If the given nickname is already in use or invalid.
     * @throws IRCPasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     */
    public void connect() throws IOException, IRCNickNameException, IRCPasswordException {
        this.connect((SSLContext) null);
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @param sslctx The SSLContext to use.
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException          When anything went wrong while connecting.
     * @throws IRCNickNameException If the given nickname is already in use or invalid.
     * @throws IRCPasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     */
    public void connect(SSLContext sslctx) throws IOException, IRCNickNameException, IRCPasswordException {
        if (this.server.isSecure()) {
            try {
                if (sslctx == null)
                    sslctx = SSLContext.getDefault();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            this.connect(sslctx.getSocketFactory());
        } else {
            this.connect(SocketFactory.getDefault());
        }
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @param sfact The SocketFactory to create a socket with.
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException          When anything went wrong while connecting.
     * @throws IRCNickNameException If the given nickname is already in use or invalid.
     * @throws IRCPasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     */
    public void connect(SocketFactory sfact) throws IOException, IRCNickNameException, IRCPasswordException {
        // check if a server is given
        if ((this.server.getAddress() == null)) {
            throw new IOException("Server address is not set!");
        }
        // connect socket
        if (this.socket == null || !this.socket.isConnected()) {
            Socket socket = sfact.createSocket(this.server.getAddress(), this.server.getPort());
            this.socket = null;
            this.connect(socket);
        } else if (this.socket != null) {
            this.connect(this.socket);
        } else {
            throw new IllegalStateException("invalid socket state");
        }
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @param sock The socket to connect to.
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException          When anything went wrong while connecting.
     * @throws IRCNickNameException If the given nickname is already in use or invalid.
     * @throws IRCPasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     * @since 1.0.0
     */
    public void connect(Socket sock) throws IOException, IRCNickNameException, IRCPasswordException {
        boolean reconnecting = true;
        // don't even try if nickname is empty
        if ((this.state.getClient() == null) || this.state.getClient().getNick().trim().equals("")) {
            throw new IRCNickNameException("Nickname is empty or null!");
        }
        // allows for handling SASL, etc. before doing IRC handshake
        // set to input socket
        if (sock != null && this.socket != sock) {
            this.socket = sock;
            reconnecting = false;
        }
        // open streams
        this.out = new IRCOutput(this, new OutputStreamWriter(this.socket.getOutputStream(), this.charset));
        this.in = new IRCInput(this, new InputStreamReader(this.socket.getInputStream(), this.charset));
        if (!reconnecting) {
            // send password if given
            if (this.server.getPassword() != null) {
                this.out.sendNowEx(IRCPacketFactory.createPASS(this.server
                        .getPassword()));
            }
            this.out.sendNowEx(IRCPacketFactory.createUSER(this.state.getClient()
                    .getUserName(), this.state.getClient().getNick()));
        }
        this.out.sendNowEx(IRCPacketFactory.createNICK(this.state.getClient()
                .getNick()));
        // wait for reply
        String line;
        loop:
        while ((line = this.in.getReader().readLine()) != null) {
            IRCDebug.log(line);
            final IRCPacket decoder = new IRCPacket(line, this);
            if (decoder.isNumeric()) {
                final int command = decoder.getNumericCommand();
                switch (command) {
                    case 1:
                    case 2:
                    case 3: {
                        final String nick = decoder.getArgumentsArray()[0];
                        if (!this.state.getClient().getNick().equals(nick))
                            this.setNick(nick);
                    }
                    break;
                    case 4: // login OK
                        break loop;
                    case 432:
                    case 433: {
                        // bad/in-use nickname nickname
                        throw new IRCNickNameException("Nickname " + this.state.getClient().getNick() + " already in use or not allowed!");
                    }
                    case 464: {
                        // wrong password
                        this.disconnect();
                        throw new IRCPasswordException("Invalid password");
                    }
                }
            }
            if (line.startsWith("PING ")) {
                this.out.pong(line.substring(5));
            }
        }
        // start listening
        this.in.start();
        this.out.start();
        // we are connected
        this.setConnected(true);
        // send events
//FIXME        for (final Iterator<ServerListener> it = this.getServerListeners(); it
//                .hasNext(); ) {
//            it.next().onConnect(this);
//        }
    }

    /**
     * Creates a {@link IRCChannel} object with given channel name. Note that this
     * method does not actually create a channel on the IRC server, it just
     * creates a {@link IRCChannel} object linked to this {@code IRCConnection}. If
     * the local user is in the channel this method will return a global channel
     * object containing a user list.
     *
     * @param name The channel name, starting with #.
     * @return A {@code IRCChannel} object representing given channel.
     * @see IRCChannel#isGlobal()
     */
    public IRCChannel createChannel(String name) {
        if (IRCChannel.CHANNEL_PREFIX.indexOf(name.charAt(0)) < 0) {
            name = "#" + name;
        }
        if (this.getState().hasChannel(name)) {
            return this.getState().getChannel(name);
        } else {
            return new IRCChannel(name, this, false);
        }
    }

    /**
     * Creates a {@link IRCUser} object with given nickname. This will create a
     * {@link IRCUser} object without any information about modes.
     *
     * @param nick The nickname.
     * @return A {@code IRCUser} object representing given user.
     * @see IRCUser#IRCUser(String, IRCConnection)
     */
    public IRCUser createUser(final String nick) {
        return new IRCUser(nick, this);
    }

    /**
     * Creates a {@link IRCUser} object with given nickname. This will attempt to
     * retrieve a global {@link IRCUser} object for given {@link IRCChannel}
     * containing information about user modes. If it isn't possible to return a
     * global {@link IRCUser} object, this method will return a new {@link IRCUser}.
     *
     * @param nick    The nickname.
     * @param channel The channel this user is in.
     * @return A {@code IRCUser} object representing given user.
     */
    public IRCUser createUser(final String nick, final String channel) {
        final IRCUser empty = this.createUser(nick);
        if (this.getState().hasChannel(channel)
                && this.getState().getChannel(channel).hasUser(nick)) {
            return this.getState().getChannel(channel).getUser(nick);
        } else {
            return empty;
        }
    }

    /**
     * Disconnects from the server. In the case a connection to the server is
     * alive, this method will send the QUIT command and wait for the server to
     * disconnect us.
     */
    public void disconnect() {
        this.disconnect(null);
    }

    /**
     * Disconnects from the server. In the case a connection to the server is
     * alive, this method will send the QUIT command and wait for the server to
     * disconnect us.
     *
     * @param message The QUIT message to use.
     */
    public void disconnect(final String message) {
        if (this.isConnected()) {
            this.out.sendNow(IRCPacketFactory.createQUIT(message));
        } else {
            this.close();
            this.getState().removeAll();
        }
    }

    /**
     * Gives all channels we're currently in.
     *
     * @return All channels we're currently in.
     */
    public Iterator<IRCChannel> getChannels() {
        return this.getState().getChannels();
    }

    /**
     * Returns the character set that is used for the connection's encoding. The
     * default is the system default returned by
     * {@link Charset#defaultCharset()}.
     *
     * @return The character set for the connection's encoding.
     */
    public Charset getCharset() {
        return this.charset;
    }

    /**
     * Sets the character set to use for the connections's encoding. If a
     * connection is already open, it will need to be closed then reopened
     * before any encoding changes will take effect.
     *
     * @param charset The character set to use for the connection's encoding.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Returns the client used by this {@code IRCConnection}.
     *
     * @return IRCUser representing this client.
     */
    public IRCUser getClient() {
        return this.state.getClient();
    }

    /**
     * Returns the outgoing message delay in milliseconds.
     *
     * @return Outgoing message delay in milliseconds.
     */
    public int getMessageDelay() {
        return this.messageDelay;
    }

    /**
     * Sets the outgoing message delay in milliseconds. Note that sending a lot
     * of messages in a short period of time might cause the server to
     * disconnect you. The default is 1 message each 100ms.
     *
     * @param messageDelay The outgoing message delay in milliseconds.
     */
    public void setMessageDelay(final int messageDelay) {
        if (messageDelay < 0) {
            throw new IllegalArgumentException(
                    "Message Delay can't be negative!");
        }
        this.messageDelay = messageDelay;
    }


    /**
     * Returns the output thread used for sending messages through this
     * {@code IRCConnection}.
     *
     * @return The {@code IRCOutput} used to send messages.
     */
    protected IRCOutput getOutput() {
        return this.out;
    }

    /**
     * Returns the server this {@code IRCConnection} connects to.
     *
     * @return The IRC server.
     */
    public IRCServer getServer() {
        return this.server;
    }

    /**
     * Sets the server details to use while connecting.
     *
     * @param server The server to connect to.
     */
    public void setServer(final IRCServer server) {
        if (!this.isConnected()) {
            this.server = server;
        }
    }

    /**
     * Gives the server address this {@code IRCConnection} is using to connect.
     *
     * @return Server address.
     * @since 1.0.0
     */
    public String getServerAddress() {
        return this.server.getAddress();
    }

    /**
     * Sets the server address to use while connecting.
     *
     * @param address The address of the server.
     * @since 1.0.0
     */
    public void setServerAddress(final String address) {
        if (!this.isConnected() && (address != null)) {
            this.server.setAddress(address);
        }
    }

    /**
     * Gives the port number this {@code IRCConnection} is using to connect.
     *
     * @return Port number
     * @since 1.0.0
     */
    public int getServerPort() {
        return this.server.getPort();
    }

    /**
     * Sets the server address to use while connecting.
     *
     * @param port The port number to use.
     */
    public void setServerPort(final int port) {
        if (!this.isConnected() && (port > 0)) {
            this.server.setPort(port);
        }
    }

    /**
     * Retrieves the {@link IRCClientState} for this {@code IRCConnection}.
     *
     * @return The {@link IRCClientState}.
     * @since 1.1.0
     */
    public IRCClientState getState() {
        return this.state;
    }

    /**
     * Gives the version string used.
     *
     * @return The version string.
     * @since 0.9.4
     */
    protected String getVersion() {
        if (this.version != null) {
            return this.version;
        }
        return IRCConnection.ABOUT;
    }

    /**
     * Set the string returned on CTCP VERSION and FINGER commands.
     *
     * @param version The string to return on CTCP VERSION and FINGER commands, or
     *                {@code null} to use the default sIRC version string.
     * @since 0.9.4
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Returns whether this connection is allowed to be redirected.
     *
     * @return {@code true} if redirection is allowed, {@code false} otherwise.
     */
    public boolean isBounceAllowed() {
        return this.bounceAllowed;
    }

    /**
     * Sets whether this connection is allowed to be redirected. If {@code true}
     * , sIRC will change server when it receives a bounce reply.
     *
     * @param bounceAllowed {@code true} if redirection is allowed, {@code false}
     *                      otherwise.
     */
    public void setBounceAllowed(final boolean bounceAllowed) {
        this.bounceAllowed = bounceAllowed;
    }

    /**
     * Checks whether the client is still connected.
     *
     * @return True if the client is connected, false otherwise.
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Changes the connection state of the client.
     *
     * @param connected Whether we are still connected.
     */
    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    /**
     * Checks if given {@link IRCUser} object represents us.
     *
     * @param IRCUser {@code IRCUser} to check
     * @return True if given {@code IRCUser} represents us, false otherwise.
     */
    public boolean isUs(final IRCUser IRCUser) {
        return IRCUser.equals(this.state.getClient());
    }

    /**
     * Checks whether this connection is using SSL.
     *
     * @return True if this connection is using SSL, false otherwise.
     */
    public boolean isUsingSSL() {
        return this.server.isSecure();
    }

    /**
     * Sets whether this connection should use SSL to connect. Note that the
     * connection will fail if the server has no valid certificate. This
     * property can only be changed while sIRC is not connected to an IRC
     * server.
     *
     * @param usingSSL True to use SSL, false otherwise.
     * @see #setServerPort(int)
     */
    public void setUsingSSL(final boolean usingSSL) {
        if (!this.isConnected()) {
            this.server.setSecure(usingSSL);
        }
    }


    /**
     * Marks you as away on the server. If any user sends a message to you while
     * marked as away, the the server will send them a message back.
     *
     * @param reason The reason for being away.
     * @see #setNotAway()
     * @since 1.0.2
     */
    public void setAway(final String reason) {
        this.out.send(IRCPacketFactory.createAWAY(reason));
    }

    /**
     * Changes the nickname of this client. While connected, this method will
     * attempt to change the nickname on the server.
     *
     * @param nick New nickname.
     */
    public void setNick(final String nick) {
        if (!this.isConnected()) {
            if (nick != null) {
                if (this.state.getClient() == null) {
                    this.state.setClient(new IRCUser(nick, "sIRC", null, null, this));
                    return;
                }
                this.state.getClient().setNick(nick);
            }
        } else {
            this.out.sendNow(IRCPacketFactory.createNICK(nick));
        }
    }

    public void setUsername(final String username) {
        setUsername(username, null);
    }

    public void setUsername(final String username, final String realname) {
        if (!this.isConnected()) {
            if (username != null) if (this.state.getClient() == null) {
                this.state.setClient(new IRCUser(null, username, null, realname, this));
            }
        }
    }

    /**
     * Removes the away mark.
     *
     * @see #setAway(String)
     * @since 1.0.2
     */
    public void setNotAway() {
        this.setAway(null);
    }

    /**
     * Sets the server details to use while connecting.
     *
     * @param address The address of the server.
     * @param port    The port number to use.
     * @since 1.0.0
     */
    public void setServer(final String address, final int port) {
        this.setServerAddress(address);
        this.setServerPort(port);
    }
}
