package net.d4rkfly3r.irc.azmate.lib;

/**
 * Contains information about an IRC server.
 */
public class IRCServer {

    /**
     * Default port (6667)
     */
    protected static final int DEFAULT_PORT = 6667;
    /**
     * The server address.
     */
    private String address;
    /**
     * The server port.
     */
    private int port;
    /**
     * The server password (or null if there is none).
     */
    private String password;
    /**
     * Whether the server uses SSL.
     */
    private boolean secure;

    /**
     * Creates a new (non-SSL) IRCServer on default port.
     *
     * @param address The server address.
     */
    public IRCServer(final String address) {
        this(address, IRCServer.DEFAULT_PORT, null, false);
    }

    /**
     * Creates a new IRCServer.
     *
     * @param address  The server address.
     * @param port     The server port.
     * @param password The password to use (or null).
     * @param secure   Whether to use SSL.
     */
    public IRCServer(final String address, final int port, final String password, final boolean secure) {
        this.address = address;
        this.port = port;
        this.password = password;
        this.secure = secure;
    }

    /**
     * Creates a new (non-SSL) IRCServer.
     *
     * @param address  The server address.
     * @param password The server port.
     */
    public IRCServer(final String address, final String password) {
        this(address, IRCServer.DEFAULT_PORT, password, false);
    }

    /**
     * Retrieves the server address.
     *
     * @return The server address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Changes the server address.
     *
     * @param address The new server address.
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * Retrieves the password.
     *
     * @return The server password, or {@code null}.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Changes the server password.
     *
     * @param password The new server password.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Retrieves the port number.
     *
     * @return The server port.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Changes the server port.
     *
     * @param port The new server port.
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Checks whether this server is using SSL.
     *
     * @return True if this server is using SSL, false otherwise.
     */
    public boolean isSecure() {
        return this.secure;
    }

    /**
     * Changes whether this server is using SSL.
     *
     * @param secure Whether this server is using SSL.
     */
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }
}