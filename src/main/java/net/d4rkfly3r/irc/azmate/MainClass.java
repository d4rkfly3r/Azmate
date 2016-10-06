package net.d4rkfly3r.irc.azmate;

import net.d4rkfly3r.irc.azmate.plugins.PluginBus;
import net.d4rkfly3r.irc.azmate.plugins.events.PluginInitEvent;

public class MainClass {

    public MainClass() {
        PluginBus pluginBus = PluginBus.getInstance();
        pluginBus.init();

        pluginBus.fireEvent(new PluginInitEvent(pluginBus.plugins));
    }

    public static void main(String[] args) {
        new MainClass();
    }
}
