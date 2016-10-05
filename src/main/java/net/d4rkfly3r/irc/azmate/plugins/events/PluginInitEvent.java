package net.d4rkfly3r.irc.azmate.plugins.events;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class PluginInitEvent extends Event {

    private final HashMap<Class<?>, Object> plugins;

    public PluginInitEvent(@Nonnull final HashMap<Class<?>, Object> plugins) {
        this.plugins = plugins;
    }

    @Nonnull
    public final HashMap<Class<?>, Object> getPlugins() {
        return plugins;
    }

    @Override
    public String toString() {
        return "{\"PluginInitEvent\":{" +
                "\"plugins\":" + plugins +
                "}}";
    }
}
