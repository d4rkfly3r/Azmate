package net.d4rkfly3r.irc.azmate.lib;

import java.util.ArrayDeque;

/**
 * Outgoing message queue.
 */
final class IRCQueue {

    /**
     * Message Queue.
     */
    private final ArrayDeque<String> queue;

    /**
     * Creates a new outgoing message queue.
     */
    protected IRCQueue() {
        this.queue = new ArrayDeque<>(8);
    }

    /**
     * Adds raw message to queue.
     *
     * @param line The raw IRC line to add to the queue.
     */
    protected void add(final String line) {
        synchronized (this.queue) {
            this.queue.addLast(line);
            this.queue.notify();
        }
    }

    /**
     * Adds raw message to the front of the queue. This should only be
     * used for urgent messages, as other will be delayed even more if
     * this is used frequently.
     *
     * @param line The raw IRC line to add to the queue.
     */
    protected void addToFront(final String line) {
        synchronized (this.queue) {
            this.queue.addFirst(line);
            this.queue.notify();
        }
    }

    /**
     * Takes a raw line from the queue.
     *
     * @return A raw IRC line to be sent.
     */
    protected String take() {
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