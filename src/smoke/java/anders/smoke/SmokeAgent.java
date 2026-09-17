package anders.smoke;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Exercises the real java -jar application without putting test commands or hooks in the release JAR. */
public class SmokeAgent {
    private static final AtomicBoolean IS_FINISHED = new AtomicBoolean();

    /**
     * Starts a bounded test observer before the application's normal launcher runs.
     *
     * @param mode either create (first launch) or reload (second launch)
     */
    public static void premain(String mode) {
        Thread.setDefaultUncaughtExceptionHandler((thread, failure) -> fail(failure));
        Thread observer = new Thread(() -> waitForWindow(mode), "jar-smoke-observer");
        observer.setDaemon(true);
        observer.start();
    }

    /** Waits for JavaFX startup and fails instead of silently accepting a process that never opens a window. */
    private static void waitForWindow(String mode) {
        long deadline = System.nanoTime() + 30_000_000_000L;
        while (!IS_FINISHED.get() && System.nanoTime() < deadline) {
            try {
                Platform.runLater(() -> inspectWindow(mode));
            } catch (IllegalStateException exception) {
                // The regular application launcher has not started JavaFX yet.
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                fail(exception);
                return;
            }
        }
        if (!IS_FINISHED.get()) {
            fail(new AssertionError("Anders did not display a usable window within 30 seconds."));
        }
    }

    /** Runs commands through the visible input field once the real application's stage is showing. */
    private static void inspectWindow(String mode) {
        if (IS_FINISHED.get()) {
            return;
        }
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage && stage.isShowing() && stage.getTitle().startsWith("Anders")) {
                IS_FINISHED.set(true);
                try {
                    checkSession(stage.getScene().getRoot(), mode);
                    System.out.println("SMOKE PASSED: " + mode);
                    Platform.exit();
                } catch (Throwable failure) {
                    fail(failure);
                }
                return;
            }
        }
    }

    /** Checks creation, validation, and persistence across two independently launched JVMs. */
    private static void checkSession(Parent root, String mode) {
        if ("create".equals(mode)) {
            submit(root, "list", "A clear path! No tasks yet.");
            submit(root, "todo packaged smoke task /tags #release", "Your trail holds 1 task.");
            submit(root, "deadline impossible /by 31/2/2026", "A deadline needs a real date");
            submit(root, "mark 1", "Task marked as done:");
            submit(root, "find #release", "1.[T][X] packaged smoke task (tags: #release)");
        } else if ("reload".equals(mode)) {
            submit(root, "list", "1.[T][X] packaged smoke task (tags: #release)");
            submit(root, "unmark 1", "Task marked as not done:");
            submit(root, "delete 1", "Your trail holds 0 tasks.");
            submit(root, "list", "A clear path! No tasks yet.");
        } else {
            throw new IllegalArgumentException("Unknown smoke-test mode: " + mode);
        }
    }

    /** Submits through the GUI and verifies the resulting bot message. */
    private static void submit(Parent root, String command, String expected) {
        TextField input = (TextField) root.lookup("#input");
        if (input == null) {
            throw new AssertionError("The packaged FXML did not create the command input.");
        }
        input.setText(command);
        input.fireEvent(new ActionEvent());
        VBox messages = (VBox) root.lookup("#messages");
        Label reply = (Label) messages.getChildren().getLast().lookup("#message");
        String actual = reply.getText();
        System.out.println("> " + command + "\n" + actual);
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected: " + expected + "\nActual: " + actual);
        }
        if (!input.getText().isEmpty()) {
            throw new AssertionError("The command input did not clear after submission.");
        }
    }

    /** Makes application-thread failures visible to the calling smoke-test process. */
    private static void fail(Throwable failure) {
        failure.printStackTrace();
        System.exit(1);
    }
}
