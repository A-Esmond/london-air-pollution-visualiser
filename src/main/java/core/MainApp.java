package core;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        DataHandler dataHandler = new DataHandler();
        Display display = new Display();
        display.showDisplay(stage, dataHandler);
    }
}
