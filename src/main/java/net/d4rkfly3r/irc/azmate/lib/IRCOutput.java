package net.d4rkfly3r.irc.azmate.lib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Output thread, and manages the outgoing message queue.
 */
class IRCOutput extends Thread {

    /**
     * Maximum line length.
     */
    protected static final int MAX_LINE_LENGTH = 512;
    /**
     * The IRCConnection.
     */
    private final IRCConnection irc;
    /**
     * Stream used to write to the IRC server.
     */
    private final BufferedWriter out;
    /**
     * The outgoing message queue.
     */
    private final IRCQueue queue;

    /**
     * Creates a new output thread.
     *
     * @param irc The IRCConnection using this output thread.
     * @param out The stream to use for communication.
     */
    protected IRCOutput(final IRCConnection irc, final Writer out) {
        this.setName("sIRC-OUT:" + irc.getServerAddress() + "-" + irc.getClient().getUserName());
        this.setPriority(Thread.MIN_PRIORITY);
        this.setDaemon(true);
        this.irc = irc;
        this.queue = new IRCQueue();
        this.out = new BufferedWriter(out);
    }

    /**
     * Closes the output stream.
     *
     * @throws IOException
     * @see IRCConnection#disconnect()
     */
    protected void close() throws IOException {
        this.out.flush();
        this.out.close();
    }

    /**
     * Sends messages from the output queue.
     */
    @Override
    public void run() {
        try {
            boolean running = true;
            String line;
            while (running) {
                Thread.sleep(this.irc.getMessageDelay());
                line = this.queue.take();
                if (line != null) {
                    this.sendNow(line);
                } else {
                    running = false;
                }
            }
        } catch (final InterruptedException e) {
            // end this thread
        }/* catch (final IllegalStateException e) {
            if (this.irc.isConnected()) {
				this.irc.setConnected(false);
				this.irc.disconnect();
			}
			e.printStackTrace();
		}*/
    }

    /**
     * Sends {@link IRCPacket} to the IRC server, using the message queue.
     *
     * @param packet The data to send.
     */
    protected synchronized void send(final IRCPacket packet) {
        if (this.irc.getMessageDelay() == 0) {
            this.sendNow(packet.getRaw());
            return;
        }
        this.queue.add(packet.getRaw());
    }

    /**
     * Sends raw line to the IRC server, using the message queue.
     *
     * @param line The raw line to send.
     * @deprecated Use {@link #send(IRCPacket)} instead.
     */
    @Deprecated
    protected synchronized void send(final String line) {
        //TODO: Remove in a future release.
        if (this.irc.getMessageDelay() == 0) {
            this.sendNow(line);
            return;
        }
        this.queue.add(line);
    }

    /**
     * Sends {@link IRCPacket} to the IRC server, without using the message
     * queue. This method will ignore any exceptions thrown while
     * sending the message.
     *
     * @param packet The IRCPacket to send.
     */
    protected synchronized void sendNow(final IRCPacket packet) {
        try {
            this.sendNowEx(packet.getRaw());
        } catch (final Exception ex) {
            // ignore
        }
    }

    /**
     * Sends raw line to the IRC server, without using the message
     * queue. This method will ignore any exceptions thrown while
     * sending the message.
     *
     * @param line The raw line to send.
     * @deprecated Use {@link #sendNow(IRCPacket)} instead.
     */
    @Deprecated
    protected synchronized void sendNow(final String line) {
        //TODO: Remove in a future release.
        try {
            this.sendNowEx(line);
        } catch (final Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Sends {@link IRCPacket} to the IRC server, without using the message
     * queue.
     *
     * @param packet The IRCPacket to send.
     * @throws IOException If anything goes wrong while sending this
     *                     message.
     */
    protected synchronized void sendNowEx(final IRCPacket packet) throws IOException {
        this.sendNowEx(packet.getRaw());
    }

    /**
     * Sends raw line to the IRC server, without using the message
     * queue.
     *
     * @param line The raw line to send.
     * @throws IOException If anything goes wrong while sending this
     *                     message.
     */
    private synchronized void sendNowEx(String line) throws IOException {
        if (line.length() > (IRCOutput.MAX_LINE_LENGTH - 2)) {
            line = line.substring(0, IRCOutput.MAX_LINE_LENGTH - 2);
        }
        IRCDebug.log(">>> " + line);
        this.out.write(line + IRCConnection.ENDLINE);
        this.out.flush();
    }

    /**
     * Shortcut to quickly send a PONG packet back.
     *
     * @param code The code to send with the PONG packet.
     */
    protected void pong(String code) {
        try {
            this.sendNowEx("PONG " + code);
        } catch (final Exception ex) {
            // ignore
        }
    }
}
