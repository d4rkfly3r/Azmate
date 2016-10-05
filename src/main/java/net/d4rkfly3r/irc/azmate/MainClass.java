package net.d4rkfly3r.irc.azmate;

import net.d4rkfly3r.irc.azmate.plugins.PluginBus;
import net.d4rkfly3r.irc.azmate.plugins.events.PluginInitEvent;

public class MainClass {
    private final PluginBus pluginBus;

    public MainClass() {
        this.pluginBus = PluginBus.getInstance();
        this.pluginBus.init();
        this.pluginBus.fireEvent(new PluginInitEvent(this.pluginBus.plugins));
    }

    public static void main(String[] args) {
        new MainClass();
    }
}
