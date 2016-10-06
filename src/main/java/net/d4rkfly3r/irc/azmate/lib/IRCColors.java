package net.d4rkfly3r.irc.azmate.lib;

/**
 * This class provides mIRC color codes to be used when sending
 * messages. However, all known markup is stripped upon receiving, and
 * thus sIRC does not support viewing any incoming markup. It is not
 * planned to change this behavior.
 * <p>
 * Note that not all IRC clients are able to receive messages with
 * color codes, and that some IRC servers allow channel operators to
 * block color codes.
 * </p>
 * <p>
 * <pre>
 * String ex1 = &quot;This message uses &quot; + IRCColors.BOLD + &quot;bold text&quot; + IRCColors.RESET + &quot; and normal text.&quot;;
 * String ex2 = IRCColors.COLOR + IRCColors.BLUE + &quot;Colored messages start with the COLOR sign, followed by the color number.&quot;;
 * String ex3 = IRCColors.COLOR + IRCColors.WHITE + &quot;,&quot; + IRCColors.BLACK + &quot;Separate the background
 * color by a comma.&quot;;
 * </pre>
 * <p>
 * The mIRC website provides documentation on the color code syntax.
 *
 * @see <a href="http://www.mirc.com/help/color.txt">mIRC Colours</a>
 * </p>
 */
@SuppressWarnings("unused")
public final class IRCColors {

    /**
     * Color number: Black
     */
    public static final int BLACK = 1;
    /**
     * Color number: Blue
     */
    public static final int BLUE = 12;
    /**
     * Indicator: Bold text
     */
    public static final String BOLD = "\u0002";
    /**
     * Color number: Brown
     */
    public static final int BROWN = 5;
    /**
     * Indicator: Color code
     */
    public static final String COLOR = "\u0003";
    /**
     * Color number: Cyan
     */
    public static final int CYAN = 11;
    /**
     * Color number: Dark Blue
     */
    public static final int DARK_BLUE = 2;
    /**
     * Color number: Dark Green
     */
    public static final int DARK_GREEN = 3;
    /**
     * Color number: Dark Grey
     */
    public static final int DARK_GREY = 14;
    /**
     * Color number: Green
     */
    public static final int GREEN = 9;
    /**
     * Color number: Light Grey
     */
    public static final int LIGHT_GREY = 15;
    /**
     * Color number: Magenta
     */
    public static final int MAGENTA = 13;
    /**
     * Color number: Olive
     */
    public static final int ORANGE = 7;
    /**
     * Color number: Purple
     */
    public static final int PURPLE = 6;
    /**
     * Color number: Red
     */
    public static final int RED = 4;
    /**
     * Indicator: Remove all markup
     */
    public static final String RESET = "\u000f";
    /**
     * Indicator: Reversed text
     */
    public static final String REVERSE = "\u0016";
    /**
     * Color number: Teal
     */
    public static final int TEAL = 10;
    /**
     * Indicator: Underlined text
     */
    public static final String UNDERLINE = "\u001f";
    /**
     * Color number: White
     */
    public static final int WHITE = 0;
    /**
     * Color number: Yellow
     */
    public static final int YELLOW = 8;

    /**
     * Helper for adding color to a string. This method simply
     * prefixes given string with the color code, and adds the reset
     * character at the end. Note that this will also remove any other
     * formatting.
     *
     * @param text  The text to colorize.
     * @param color The color number to use.
     * @return Colorized text.
     */
    public static String color(final String text, final int color) {
        return IRCColors.COLOR + color + text + IRCColors.RESET;
    }

    /**
     * Helper for adding color and background to a string. This method
     * simply prefixes given string with the color code, and adds the
     * reset character at the end. Note that this will also remove any
     * other formatting.
     *
     * @param text       The text to colorize.
     * @param color      The color number to use.
     * @param background The color number to use as background color.
     * @return Colorized text.
     */
    public static String color(final String text, final int color, final int background) {
        return IRCColors.COLOR + color + "," + background + text + IRCColors.RESET;
    }

    /**
     * Removes all color codes and markup from given text.
     *
     * @param input Text to clear.
     * @return Given text without markup.
     */
    protected static String remove(final String input) {
        return IRCColors.remove(new StringBuffer(input)).toString();
    }

    /**
     * Removes all color codes and markup from given text.
     *
     * @param buf input Text to clear.
     * @return Given text without markup.
     */
    private static StringBuffer remove(StringBuffer buf) {
        int len = buf.length();
        for (int i = 0, j = 0, c; i < len; i++, j = i) {
            c = buf.charAt(i);
            try {
                // COLORS Beginning
                // (format:
                // <colorIndicator><int>[<int>][[,<int>[<int>]]
                if (c == 3) {
                    c = buf.charAt(++j);
                    if (('0' <= c) && (c <= '9')) { // first int
                        c = buf.charAt(++j);
                        if (('0' <= c) && (c <= '9')) {
                            c = buf.charAt(++j); // second int
                        }
                    }
                    if (c == ',') {
                        c = buf.charAt(++j); // comma
                    }
                    if (('0' <= c) && (c <= '9')) { // first int
                        c = buf.charAt(++j);
                        if (('0' <= c) && (c <= '9')) {
                            c = buf.charAt(++j); // second int
                        }
                    }
                    // CTCP / BOLD / UNDERLINE / COLOR END
                    // (format: <ctcpDelimiter> / <boldIndicator>
                    // etc.)
                } else if ((c == 31) || (c == 2) || (c == 15) || (c == 22)) {
                    j++;
                }
            } catch (final StringIndexOutOfBoundsException exc) {
                // we got the end of the string with a call to
                // charAt(++iIndexEnd)
                // nothing
            }
            if (j > i) {
                buf = buf.delete(i, j); // remove the cars
                len -= (j - i);
                i--;
            }
        }
        return buf;
    }
}
