package anders;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Displays the conversation and sends user commands to Anders. */
public class MainWindow extends VBox {
    private final Anders anders = new Anders();
    private final VBox messages = new VBox(8);
    private final TextField input = new TextField();

    /** Creates the main GUI layout and its input handlers. */
    public MainWindow() {
        setPadding(new Insets(12));
        setSpacing(8);
        ScrollPane scrollPane = new ScrollPane(messages);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(messages.heightProperty());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> handleInput());
        input.setOnAction(event -> handleInput());
        getChildren().addAll(scrollPane, input, sendButton);
        messages.getChildren().add(DialogBox.getAndersDialog(
                "Hello! I'm Anders, your friendly study companion."));
    }

    /** Processes the text field contents and appends both messages to the conversation. */
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
