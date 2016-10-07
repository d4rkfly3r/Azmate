package net.d4rkfly3r.irc.azmate;

import com.google.gson.Gson;
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
import netscape.javascript.JSObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Plugin
public class Azmate {

    private IRCChannel testChannel;
    private IRCConnection ircConnection;
    private String defChannel;
    private MainApplication mainApplication = null;

    private ArrayList<String> messages = new ArrayList<>();

    public Azmate() {
        Toolkit.getDefaultToolkit();
        ircConnection = new IRCConnection("irc.esper.net");
        ircConnection.setUsername(JOptionPane.showInputDialog(null, "Please enter your username!"));
        ircConnection.setNick(JOptionPane.showInputDialog(null, "Please enter your nickname!"));
        this.defChannel = JOptionPane.showInputDialog(null, "Please enter the channel you would like to join (no # symbol)!");
    }

    private static String padLeft(int length, String mod, String initial) {
        while (initial.length() < length) {
            initial = mod + initial;
        }
        return initial;
    }

    @Listener
    public void onPluginPreInit(PluginPreInitEvent event) {

    }

    @Listener
    public void onPluginInit(PluginInitEvent event) {
        new Thread(() -> Application.launch(MainApplication.class)).start();
        attemptConnection();
    }

    private void attemptConnection() {
        try {
            ircConnection.connect();
            testChannel = ircConnection.createChannel(defChannel);
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
        final String e = new Message("chat", event.getSender().getPreferredName(), event.getMessage()).toJSON();
        System.out.println(e);
        messages.add(e);
    }

    @Listener
    public void onUserJoinEvent(UserJoinedEvent event) {
        final String e = new Message("join", event.getSender().getPreferredName(), "[" + event.getSender().getUserName() + "@" + event.getSender().getHostName() + "] has joined " + event.getChannel()).toJSON();
        System.out.println(e);
        messages.add(e);
    }

    @Listener
    public void onUserQuitEvent(UserQuitEvent event) {
        final String e = new Message("quit", event.getQuitter().getPreferredName(), event.getMessage()).toJSON();
        System.out.println(e);
        messages.add(e);
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

    public static class Message {
        private static final Gson builder = new Gson();
        private final String type;
        private final String timestamp;
        private final String username;
        private final String message;

        public Message(String type, String username, String message) {
            this(type, padLeft(2, "0", String.valueOf(new Date().getHours())) + ":" + padLeft(2, "0", String.valueOf(new Date().getMinutes())), username, message);
        }

        public Message(String type, String timestamp, String username, String message) {
            this.type = type;
            this.timestamp = timestamp;
            this.username = username;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }

        public String toJSON() {
            return builder.toJson(this);
        }
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

        public void sendMessage(String message) {
            Azmate.this.testChannel.send(message);
            final String e = new Message("chat", ircConnection.getClient().getPreferredName(), message).toJSON();
            System.err.println(e);
            messages.add(e);
        }
    }
}
