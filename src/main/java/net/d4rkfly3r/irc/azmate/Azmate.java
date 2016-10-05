package net.d4rkfly3r.irc.azmate;

import net.d4rkfly3r.irc.azmate.annotations.Listener;
import net.d4rkfly3r.irc.azmate.annotations.Plugin;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.plugins.events.PluginInitEvent;
import net.d4rkfly3r.irc.azmate.plugins.events.PluginPreInitEvent;

@Plugin
public class Azmate {

    @Listener
    public void onPluginPreInit(PluginPreInitEvent event) {

    }

    @Listener
    public void onPluginInit(PluginInitEvent event) {
        System.out.println(event.toString());
        System.out.println();

        IRCConnection ircConnection = new IRCConnection("na.irc.esper.net");

        System.out.println(ircConnection);

    }
}
