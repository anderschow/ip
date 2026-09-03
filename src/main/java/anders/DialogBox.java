package anders;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one message in the Anders conversation. */
public class DialogBox extends HBox {
    private static final String USER_COLOR = "#dbeafe";
    private static final String ANDERS_COLOR = "#f3f4f6";

    /** Creates a message box with the supplied speaker and message. */
    private DialogBox(String speaker, String message, String color, Pos alignment) {
        Label text = new Label(speaker + ":\n" + message);
        text.setWrapText(true);
        text.setMaxWidth(500);
        text.setPadding(new Insets(8));
        text.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8;");
        setAlignment(alignment);
        setPadding(new Insets(4));
        getChildren().add(text);
    }

    /** Creates a right-aligned message written by the user. */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox("You", message, USER_COLOR, Pos.CENTER_RIGHT);
    }

    /** Creates a left-aligned message written by Anders. */
    public static DialogBox getAndersDialog(String message) {
        return new DialogBox("Anders", message, ANDERS_COLOR, Pos.CENTER_LEFT);
    }
}
