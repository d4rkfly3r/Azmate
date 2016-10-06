package net.d4rkfly3r.irc.azmate.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import net.d4rkfly3r.irc.azmate.Azmate;

public class FontAwesome {

    public static final String MINUS = "\uf068";
    public static final String RESIZE_FULL = "\uf065";
    public static final String RESIZE_SMALL = "\uf066";
    public static final String REMOVE = "\uF00D";
    public static final String PLUS = "\uf067";
    public static final String GLOBE = "\uf0ac";
    public static final String COG = "\uf013";
    public static final String COMMENTS = "\uf086";
    public static final String USER = "\uf007";
    public static final String SIGN_OUT = "\uf08b";
    public static final String REPLY = "\uf112";
    public static final Font font = Font.loadFont(Azmate.class.getResource("/fonts/font_awesome.ttf").toExternalForm(), 26);

    static {
        System.out.println(font);
    }


    public static Button createIconButton(String iconName, String text, boolean pad, String styleClass) {
        return createIconButton(iconName, text, pad, styleClass, null);
    }

    public static Button createIconButton(String iconName, String text, boolean pad, String styleClass, String style) {
        final Button tempButton = new Button();
        tempButton.setText(text);
        tempButton.setGraphic(createIcon(iconName, styleClass != null));
        tempButton.setContentDisplay(ContentDisplay.RIGHT);
        tempButton.setFocusTraversable(false);

        if (pad) {
            tempButton.setMinHeight(33);
            tempButton.setMaxHeight(33);
            if (text.equals("")) {
                tempButton.setMinWidth(33);
                tempButton.setMaxWidth(33);
            }
        }
        if (styleClass != null) {
            tempButton.getStyleClass().add(styleClass);
            if (pad) {
                tempButton.setMinHeight(32);
                tempButton.setMaxHeight(32);
                tempButton.setStyle(style == null ? "-fx-padding: 0px 5px 0px 5px;" : style);
            }
        }
        return tempButton;
    }

    public static Label createIcon(String iconName) {
        return createIcon(iconName, false);
    }

    public static Label createIcon(String iconName, boolean translate) {
        final Label tempLabel = new Label();
        tempLabel.setText(iconName);
        tempLabel.setFont(font);
        tempLabel.getStyleClass().add("icon");
        if (translate) {
            tempLabel.setTranslateY(5);
        }
        return tempLabel;
    }

}
