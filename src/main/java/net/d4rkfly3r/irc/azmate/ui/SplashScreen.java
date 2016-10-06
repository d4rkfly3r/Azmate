package net.d4rkfly3r.irc.azmate.ui;

import javax.swing.*;

public class SplashScreen extends JFrame {

    private final JLabel label;

    public SplashScreen() {
        this.label = new JLabel();
        this.setSize(200, 50);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.add(label);
    }


    public SplashScreen loading() {
        this.setVisible(true);
        return this;
    }

    public SplashScreen text(String text) {
        this.label.setText(text);
        return this;
    }

    public SplashScreen loaded() {
        this.setVisible(false);
        return this;
    }
}
