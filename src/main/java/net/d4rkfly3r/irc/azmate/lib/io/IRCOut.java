package net.d4rkfly3r.irc.azmate.lib.io;

import net.d4rkfly3r.irc.azmate.lib.IRCConnection;
import net.d4rkfly3r.irc.azmate.lib.IRCQueue;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class IRCOut extends Thread {
    private static final int MAX_LINE_LENGTH = 512;
    private final IRCConnection ircConnection;
    private final BufferedWriter bufferedWriter;
    private final IRCQueue ircQueue;


    public IRCOut(@Nonnull final IRCConnection ircConnection, @Nonnull final Writer writer) {
        this.setName("Azmate-iOUT:" + ircConnection.getServerAddress() + "-" + ircConnection.getUser().getUserName());
        this.setPriority(Thread.MIN_PRIORITY);
        this.setDaemon(true);

        this.ircConnection = ircConnection;
        this.bufferedWriter = new BufferedWriter(writer);
        this.ircQueue = new IRCQueue();
    }

    public void close() throws IOException {
        this.bufferedWriter.flush();
        this.bufferedWriter.close();
    }

    @Override
    public void run() {
        try {
            boolean running = true;
            String line;
            while (running) {
                Thread.sleep(this.ircConnection.getWriteDelay());
                line = this.ircQueue.take();
                if (line != null) {
                    this.sendNowSafe(line);
                } else {
                    running = false;
                }
            }
        } catch (final InterruptedException ignored) {
        }
    }

    private synchronized void sendNowSafe(@Nonnull String line) {
        try {
            this.sendNow(line);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private synchronized void sendNow(@Nonnull String line) throws IOException {
        if (line.length() > (IRCOut.MAX_LINE_LENGTH - 2)) {
            line = line.substring(0, IRCOut.MAX_LINE_LENGTH - 2);
        }
        this.bufferedWriter.write(line + IRCConnection.ENDLINE);
        this.bufferedWriter.flush();
    }
}
