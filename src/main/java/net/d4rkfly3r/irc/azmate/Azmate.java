package net.d4rkfly3r.irc.azmate;

import javafx.application.Application;
import javafx.scene.text.Font;
import net.d4rkfly3r.irc.azmate.annotations.Listener;
import net.d4rkfly3r.irc.azmate.annotations.Plugin;
import net.d4rkfly3r.irc.azmate.events.WindowCloseEvent;
import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCNickNameException;
import net.d4rkfly3r.irc.azmate.lib.IRCPasswordException;
import net.d4rkfly3r.irc.azmate.plugins.events.*;
import net.d4rkfly3r.irc.azmate.ui.MainApplication;
import net.d4rkfly3r.irc.azmate.ui.SplashScreen;

import java.awt.*;
import java.io.IOException;

@Plugin
public class Azmate {

    IRCConnection ircConnection;
    SplashScreen splashScreen;

    public Azmate() {
        this.splashScreen = new SplashScreen();
        this.splashScreen.loading();
        this.splashScreen.text("Loading fonts!");
        Toolkit.getDefaultToolkit();
        this.splashScreen.text("Making connection!");
        ircConnection = new IRCConnection("irc.esper.net");
        ircConnection.setUsername("d4rkfly3r");
        ircConnection.setNick("d4rkfly3r");
    }

    @Listener
    public void onPluginPreInit(PluginPreInitEvent event) {

    }

    @Listener
    public void onPluginInit(PluginInitEvent event) {
        this.splashScreen.loaded();
        new Thread(() -> Application.launch(MainApplication.class)).start();
    }

    private void attemptConnection() {
        try {
            ircConnection.connect();
            IRCChannel testChannel = ircConnection.createChannel("joshtest");
            testChannel.join();
            System.out.println("IRC Client Connected & Channel Joined!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IRCNickNameException e) {
            e.printStackTrace();
        } catch (IRCPasswordException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onMessageEvent(MessageEvent event) {
        System.out.println(event.getSender() + ": " + event.getMessage());
    }

    @Listener
    public void onUserJoinEvent(UserJoinedEvent event) {
        System.out.println(event.getChannel() + " | " + event.getSender());
    }

    @Listener
    public void onUserPartEvent(UserPartedEvent event) {
        System.out.println(event.getChannel() + " | " + event.getSender() + " | " + event.getMessage());
    }

    @Listener
    public void onWindowClose(WindowCloseEvent event) {
        System.exit(0);
    }
}
