package panels;

import core.DataHandler;
import core.Display;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.PollutantRegistry;
import src.resources.FilePathLoader;

/**
 * Displays the welcome page at the start of the application
 * Users are presented with a button to move to the map panel
 * and also have a help button to explain the functionality of
 * the app.
 *
 * @author T. A.
 */

public class WelcomePanel extends Panel {

    //Components of the Welcome Panel GUI
    private Label title;
    private Label pollutants;
    private Label years;
    private final Display display;
    private Button startButton;
    private Button helpButton;
    private final StackPane content;
    private AnchorPane anchorPane;
    private final DataHandler dataHandler;


    /**
     * Constructs the WelcomePanel, initialising all UI components
     * and laying them out within the panel.
     *
     * @param name        The name of this panel
     * @param display     The shared display context
     * @param dataHandler Provides to all loaded datasets
     */
    public WelcomePanel(String name, Display display, DataHandler dataHandler) {
        super(name, display, dataHandler);

        this.display = display;
        this.dataHandler = dataHandler;

        // Creates a Vbox to hold the main text labels and start button
        VBox mainText = new VBox(20);
        mainText.setAlignment(Pos.CENTER);

        content = new StackPane();

        loadBackground();
        loadText();

        mainText.getChildren().addAll(title, pollutants, years, startButton);

        // Layers the background, main text, and help button into the StackPane
        content.getChildren().addAll(anchorPane, mainText, helpButton);
        setCenter(content);
    }

    /**
     * Loads and displays the background image, binding its dimensions to the
     * panel so it scales correctly on resize. Shows an error alert if the
     * image cannot be loaded.
     */
    private void loadBackground() {
        anchorPane = new AnchorPane();
        anchorPane.prefWidthProperty().bind(content.widthProperty());
        anchorPane.prefHeightProperty().bind(content.heightProperty());
        anchorPane.setMinSize(0, 0);

        try {
            // Resolve the image file path and load it as an Image
            String image_path = FilePathLoader.load("Welcome.png");
            Image backgroundImage = new Image(image_path);

            // Configure the ImageView to stretch and fill the AnchorPane
            ImageView iView = new ImageView(backgroundImage);
            iView.setPreserveRatio(false);
            iView.setSmooth(true);
            iView.fitWidthProperty().bind(anchorPane.widthProperty());
            iView.fitHeightProperty().bind(anchorPane.heightProperty());

            anchorPane.getChildren().addAll(iView);
        } catch (Exception e) {
            // Notify the user if the background image fails to load
            showImageError();
        }

    }

    /**
     * Initialises and styles the text labels, start button, and help button.
     * Retrieves pollutant and year data from the DataHandler to populate labels.
     */
    private void loadText() {

        PollutantRegistry registry = dataHandler.getPollutantRegistry();

        title = new Label("London Air Pollution Viewer");
        title.getStyleClass().add("title");

        pollutants = new Label("Pollutants covered: " + registry.getPollutantNames());
        pollutants.getStyleClass().add("body");

        years = new Label("Years covered: " + registry.getTotalYears());
        years.getStyleClass().add("body");

        //Creates button to move to map panel
        startButton = new Button("View Pollution Map");
        startButton.setOnAction(e ->
                display.changeCurrentPanel("Map")
        );

        //Creates help button
        helpButton = new Button("Help");
        helpButton.setOnAction(e -> showHelp());
        StackPane.setAlignment(helpButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(helpButton, new Insets(0, 10, 10, 0));

    }

    /**
     * Displays an informational alert explaining how to use the application,
     * including navigation, the map panel, and the stats panel.
     */
    private void showHelp() {
        Alert help = new Alert(Alert.AlertType.INFORMATION);
        help.setTitle("Program Information:");
        help.setHeaderText("Help:");
        String helpMessage = """
                • This program is to view the air pollution in the London area.
                • Within the program, users can see pollution at specific data
                  points on the map by clicking on the map or by searching by
                  grid code.
                • Use the navigate menu to move between the different panels
                  in the app.
                • The map panel is used to see pollution by grid code.
                • The stats panel is used to see summary statistics across a
                  specified area and allows you to filter for what statistics
                  you want to see (across specific years and pollutants).
                """;
        help.setContentText(helpMessage);
        help.showAndWait();
    }

    /**
     * Displays an error alert informing the user that the background
     * image could not be loaded.
     */
    private void showImageError() {
        Alert imageError = new Alert(Alert.AlertType.ERROR);
        imageError.setTitle("Program Error");
        imageError.setHeaderText("Error");
        String errorMessage = "The background image failed to load";
        imageError.setContentText(errorMessage);
        imageError.showAndWait();
    }
}