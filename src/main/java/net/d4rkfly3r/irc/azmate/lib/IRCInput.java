package net.d4rkfly3r.irc.azmate.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.SocketException;

/**
 * Input Thread.
 */
final class IRCInput extends Thread {

    /**
     * Stream used to read from the IRC server.
     */
    private final BufferedReader bufferedReader;
    /**
     * The IRCConnection.
     */
    private final IRCConnection ircConnection;

    private final IRCParser parser = new IRCParser();

    /**
     * Creates a new input thread.
     *
     * @param ircConnection  The IRCConnection using this output thread.
     * @param bufferedReader The stream to use for communication.
     */
    protected IRCInput(final IRCConnection ircConnection, final Reader bufferedReader) {
        this.setName("sIRC-IN:" + ircConnection.getServerAddress() + "-" + ircConnection.getClient().getUserName());
        this.setPriority(Thread.NORM_PRIORITY);
        this.setDaemon(false);
        this.bufferedReader = new BufferedReader(bufferedReader);
        this.ircConnection = ircConnection;
    }

    /**
     * Closes the input stream.
     *
     * @throws IOException
     * @see IRCConnection#disconnect()
     */
    protected void close() throws IOException {
        this.bufferedReader.close();
    }

    /**
     * Returns the reader used bufferedReader this input thread.
     *
     * @return the reader.
     */
    protected BufferedReader getReader() {
        return this.bufferedReader;
    }

    /**
     * Handles a line received by the IRC server.
     *
     * @param line The line to handle.
     */
    private void handleLine(final String line) {
        // transform the raw line into an easier format
        final IRCPacket parser = new IRCPacket(line, this.ircConnection);
        // Handle numeric server replies.
        if (parser.isNumeric()) {
            this.parser.parseNumeric(this.ircConnection, parser);
            return;
        }
        // Handle different commands
        this.parser.parseCommand(this.ircConnection, parser);
    }

    /**
     * Checks the input stream for new messages.
     */
    @Override
    public void run() {
        String line = null;
        try {
            // wait for lines to come bufferedReader
            while ((line = this.bufferedReader.readLine()) != null) {
                IRCDebug.log("<<< " + line);
                // always respond to PING
                if (line.startsWith("PING ")) {
                    this.ircConnection.out.pong(line.substring(5));
                } else {
                    this.handleLine(line);
                }
            }
        } catch (final IOException ex) {
            this.ircConnection.setConnected(false);
        } catch (final Exception ex) {
            IRCDebug.log("Exception " + ex + " on: " + line);
            ex.printStackTrace();
        }
        // when reaching this, we are disconnected
        this.ircConnection.setConnected(false);
        // close connections
        this.ircConnection.disconnect();
        // send disconnect event
//FIXME		for (final Iterator<ServerListener> it = this.ircConnection.getServerListeners(); it.hasNext();) {
//			it.next().onDisconnect(this.ircConnection);
//		}
    }
}
