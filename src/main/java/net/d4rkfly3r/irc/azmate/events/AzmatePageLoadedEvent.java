package net.d4rkfly3r.irc.azmate.events;

import net.d4rkfly3r.irc.azmate.plugins.events.Event;
import net.d4rkfly3r.irc.azmate.ui.MainApplication;

public class AzmatePageLoadedEvent extends Event {
    private final MainApplication mainApplication;

    public AzmatePageLoadedEvent(MainApplication mainApplication) {
        super();
        this.mainApplication = mainApplication;
    }

    public MainApplication getMainApplication() {
        return mainApplication;
    }
}
