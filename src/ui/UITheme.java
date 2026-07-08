package ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UITheme {

    public static final String BG_PRIMARY = "#E8EEF4";
    
    public static final String BG_SURFACE = "#FFFFFF";
    
    public static final String TEAL_PRIMARY = "#2D7A6B";
    
    public static final String TEAL_DARK = "#235F53";
    
    public static final String TEAL_LIGHT = "#B8D7D0";
    
    public static final String TEXT_PRIMARY = "#2C3035";
    
    public static final String TEXT_MUTED = "#7A8A96";
    
    public static final String BORDER_COLOR = "#C8D4DC";
    
    public static final String COLOR_ERROR = "#A02020";
    
    public static final String COLOR_SUCCESS = "#2D7A6B";
    
    public static final String COLOR_WARNING = "#D97706";
   
    public static Font fontHeading1() {
        return Font.font("Segoe UI", FontWeight.BOLD, 46);
    }
    
    public static Font fontHeading2() {
        return Font.font("Segoe UI", FontWeight.BOLD, 28);
    }
    
    public static Font fontHeading3() {
        return Font.font("Segoe UI", FontWeight.BOLD, 24);
    }
    
    public static Font fontHeading4() {
        return Font.font("Segoe UI", FontWeight.BOLD, 18);
    }
    
    public static Font fontBody() {
        return Font.font("Segoe UI", 14);
    }
    
    public static Font fontSmall() {
        return Font.font("Segoe UI", 12);
    }
    
    public static Font fontLabel() {
        return Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13);
    }
   
    public static String getMainContainerStyle() {
        return "-fx-background-color: " + BG_PRIMARY + ";";
    }
    
    public static String getCardStyle() {
        return "-fx-background-color: " + BG_SURFACE + "; "
                + "-fx-background-radius: 12; "
                + "-fx-border-color: " + BORDER_COLOR + "; "
                + "-fx-border-width: 1; "
                + "-fx-border-radius: 12;";
    }
    
    public static String getPrimaryButtonStyle() {
        return "-fx-background-color: " + TEAL_PRIMARY + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 12 30; "
                + "-fx-cursor: hand; "
                + "-fx-font-size: 14;";
    }
    
    public static String getSecondaryButtonStyle() {
        return "-fx-background-color: white; "
                + "-fx-text-fill: " + TEAL_PRIMARY + "; "
                + "-fx-border-color: " + TEAL_PRIMARY + "; "
                + "-fx-border-width: 2; "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 10 25; "
                + "-fx-cursor: hand;";
    }
    
    public static String getTextFieldStyle() {
        return "-fx-background-color: white; "
                + "-fx-border-color: " + BORDER_COLOR + "; "
                + "-fx-border-radius: 5; "
                + "-fx-padding: 10; "
                + "-fx-text-fill: " + TEXT_PRIMARY + ";";
    }
    
    public static String getLabelStyle() {
        return "-fx-text-fill: " + TEXT_MUTED + "; "
                + "-fx-font-weight: bold;";
    }
    
    public static String getSuccessStyle() {
        return "-fx-text-fill: " + COLOR_SUCCESS + ";";
    }
    
    public static String getErrorStyle() {
        return "-fx-text-fill: " + COLOR_ERROR + ";";
    }
   
    public static final int PADDING_LARGE = 32;
    public static final int PADDING_MEDIUM = 20;
    public static final int PADDING_SMALL = 12;
    public static final int GAP_LARGE = 25;
    public static final int GAP_MEDIUM = 15;
    public static final int GAP_SMALL = 8;
    
    public static final int BUTTON_MIN_WIDTH = 120;
    public static final int BUTTON_MIN_HEIGHT = 45;
    public static final int FIELD_MIN_HEIGHT = 40;
 
    public static Color colorTealPrimary() {
        return Color.web(TEAL_PRIMARY);
    }
    
    public static Color colorTealDark() {
        return Color.web(TEAL_DARK);
    }
    
    public static Color colorTextPrimary() {
        return Color.web(TEXT_PRIMARY);
    }
    
    public static Color colorTextMuted() {
        return Color.web(TEXT_MUTED);
    }

    public static Color colorTealLight() {
        return Color.web(TEAL_LIGHT);
    }
    
    public static Color colorError() {
        return Color.web(COLOR_ERROR);
    }
    
    public static Color colorSuccess() {
        return Color.web(COLOR_SUCCESS);
    }
}
