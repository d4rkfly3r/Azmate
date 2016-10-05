package net.d4rkfly3r.irc.azmate.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IRCConnection implements IRCServerData {

    private static final int DEFAULT_PORT = 6667;

    private String serverAddress;
    private Integer serverPort;
    private String serverPassword;
    private Boolean serverSecure;

    public IRCConnection(@Nonnull String serverAddress) {
        this(serverAddress, DEFAULT_PORT);
    }

    public IRCConnection(@Nonnull String serverAddress, @Nonnull Integer serverPort) {
        this(serverAddress, serverPort, null);
    }

    public IRCConnection(@Nonnull String serverAddress, @Nonnull Integer serverPort, @Nullable String serverPassword) {
        this(serverAddress, serverPort, serverPassword, false);
    }

    public IRCConnection(@Nonnull String serverAddress, @Nonnull Integer serverPort, @Nullable String serverPassword, @Nonnull Boolean serverSecure) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverPassword = serverPassword;
        this.serverSecure = serverSecure;
    }

    @Nonnull
    public String getServerAddress() {
        return this.serverAddress;
    }

    @Nonnull
    public Integer getServerPort() {
        return this.serverPort;
    }

    @Nullable
    public String getServerPassword() {
        return this.serverPassword;
    }

    @Nonnull
    public Boolean isServerSecure() {
        return this.serverSecure;
    }
}
