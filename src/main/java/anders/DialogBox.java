package anders;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Represents one message in the Anders conversation. */
public class DialogBox extends HBox {
    @FXML
    private Label speaker;
    @FXML
    private Label message;
    @FXML
    private ImageView avatar;

    private DialogBox(String speakerText, String messageText, Image image, boolean isUser) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog layout", exception);
        }
        speaker.setText(speakerText);
        message.setText(messageText);
        avatar.setImage(image);
        setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
    }

    /** Creates a right-aligned message written by the user. */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox("You", message, loadImage("/images/DaUser.png"), true);
    }

    /** Creates a left-aligned message written by Anders. */
    public static DialogBox getAndersDialog(String message) {
        return new DialogBox("Anders", message, loadImage("/images/DaDuke.png"), false);
    }

    private static Image loadImage(String path) {
        return new Image(DialogBox.class.getResourceAsStream(path));
    }
}
