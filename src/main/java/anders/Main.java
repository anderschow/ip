package anders;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Provides the JavaFX window for Anders. */
public class Main extends Application {
    /** Creates and displays a compact, resizable window. */
    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow();
        stage.setTitle("Anders");
        stage.setMinWidth(360);
        stage.setMinHeight(400);
        stage.setScene(new Scene(mainWindow, 460, 640));
        stage.show();
    }
}
