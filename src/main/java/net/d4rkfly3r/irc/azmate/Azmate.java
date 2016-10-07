package net.d4rkfly3r.irc.azmate;

import javafx.application.Application;
import net.d4rkfly3r.irc.azmate.annotations.Listener;
import net.d4rkfly3r.irc.azmate.annotations.Plugin;
import net.d4rkfly3r.irc.azmate.events.AzmatePageLoadedEvent;
import net.d4rkfly3r.irc.azmate.events.WindowCloseEvent;
import net.d4rkfly3r.irc.azmate.lib.IRCChannel;
import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCNickNameException;
import net.d4rkfly3r.irc.azmate.lib.IRCPasswordException;
import net.d4rkfly3r.irc.azmate.plugins.events.*;
import net.d4rkfly3r.irc.azmate.ui.MainApplication;
import net.d4rkfly3r.irc.azmate.ui.SplashScreen;
import netscape.javascript.JSObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Plugin
public class Azmate {

    IRCConnection ircConnection;
    SplashScreen splashScreen;
    private MainApplication mainApplication = null;

    private ArrayList<String> messages = new ArrayList<>();

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
        attemptConnection();
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
        messages.add("{ \"type\":\"chat\",\"timestamp\":\"" + padLeft(2, "0", String.valueOf(new Date().getHours())) + ":" + padLeft(2, "0", String.valueOf(new Date().getMinutes())) + "\",\"username\":\"" + event.getSender().getPreferredName() + "\",\"message\":\"" + event.getMessage() + "\"}");
    }

    private String padLeft(int length, String mod, String initial) {
        while (initial.length() < length) {
            initial = mod + initial;
        }
        return initial;
    }

    @Listener
    public void onUserJoinEvent(UserJoinedEvent event) {
        System.out.println(event.getChannel() + " | " + event.getSender());
        final String e = "{ \"type\":\"join\",\"timestamp\":\"" + padLeft(2, "0", String.valueOf(new Date().getHours())) + ":" + padLeft(2, "0", String.valueOf(new Date().getMinutes())) + "\",\"username\":\"" + event.getSender().getPreferredName() + "\",\"message\":\"[" + event.getSender().getUserName() + "@" + event.getSender().getHostName() + "]\"}";
        System.out.println(e);
        messages.add(e);

    }

    @Listener
    public void onUserPartEvent(UserPartedEvent event) {
        System.out.println(event.getChannel() + " | " + event.getSender() + " | " + event.getMessage());
    }

    @Listener
    public void onAzmatePageLoaded(AzmatePageLoadedEvent event) {
        System.out.println("Page loaded!");
        System.out.println(event.getMainApplication());
        this.mainApplication = event.getMainApplication();
        JSObject window = (JSObject) this.mainApplication.webView.getEngine().executeScript("window");
        window.setMember("link", new Link());
    }

    @Listener
    public void onWindowClose(WindowCloseEvent event) {
        System.exit(0);
    }

    public class Link {
        public Object[] getMessages() {
            final Object[] messages = Azmate.this.messages.toArray();
            Azmate.this.messages.clear();
            return messages;
        }

        public void log(Object data) {
            if (data instanceof Object[]) {
                System.out.println(Arrays.toString((Object[]) data));
            } else {
                System.out.println(data);
            }
        }
    }
}
