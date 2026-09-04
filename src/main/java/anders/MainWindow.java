package anders;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Displays the conversation and sends user commands to Anders. */
public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messages;
    @FXML private TextField input;
    private final Anders anders = new Anders();

    /** Creates the main GUI layout and its input handlers. */
    public MainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window layout", exception);
        }
    }

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(messages.heightProperty());
        messages.getChildren().add(DialogBox.getAndersDialog(
                "Hello! I'm Anders, your friendly study companion."));
    }

    /** Processes the text field contents and appends both messages to the conversation. */
    @FXML
    private void handleInput() {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        messages.getChildren().add(DialogBox.getUserDialog(command));
        messages.getChildren().add(DialogBox.getAndersDialog(anders.getResponse(command)));
        input.clear();
    }
}
