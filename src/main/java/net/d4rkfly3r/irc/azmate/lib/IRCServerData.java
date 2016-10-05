package net.d4rkfly3r.irc.azmate.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRCServerData {
    @Nonnull
    String getServerAddress();

    @Nonnull
    Integer getServerPort();

    @Nullable
    String getServerPassword();

    @Nonnull
    Boolean isServerSecure();

}
