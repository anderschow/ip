package anders;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Provides the JavaFX window for Anders. */
public class Main extends Application {
    /** Creates and displays the main window. */
    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow();
        stage.setTitle("Anders");
        stage.setScene(new Scene(mainWindow, 600, 700));
        stage.show();
    }
}
