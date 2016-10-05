package net.d4rkfly3r.irc.azmate.lib;

import java.util.ArrayDeque;

public class IRCQueue {

    private final ArrayDeque<String> queue;

    public IRCQueue() {
        this.queue = new ArrayDeque<>(8);
    }

    public void add(final String line) {
        synchronized (this.queue) {
            this.queue.addLast(line);
            this.queue.notify();
        }
    }

    public void addToFront(final String line) {
        synchronized (this.queue) {
            this.queue.addFirst(line);
            this.queue.notify();
        }
    }

    public String take() {
        String line;
        synchronized (this.queue) {
            if (this.queue.isEmpty()) {
                try {
                    this.queue.wait();
                } catch (final InterruptedException e) {
                    return null;
                }
            }
            line = this.queue.getFirst();
            this.queue.removeFirst();
            return line;
        }
    }

}
