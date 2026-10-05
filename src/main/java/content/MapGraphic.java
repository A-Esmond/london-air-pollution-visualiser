package content;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import src.resources.LondonMapData;

import java.util.ArrayList;
import java.util.List;

public class MapGraphic {
    private final Image image;
    private final ImageView imageView;
    private final Canvas markerLayer;
    private final Label coordinatesLabel;
    private final List<ClickListener> clickListeners = new ArrayList<>();
    private final List<MoveListener> moveListeners = new ArrayList<>();

    @FunctionalInterface
    public interface ClickListener {
        void onClicked(int easting, int northing);
    }

    public void addClickListener(ClickListener listener) {
        clickListeners.add(listener);
    }


    @FunctionalInterface
    public interface MoveListener {
        void onMoved(int easting, int northing);
    }

    public void addMoveListener(MoveListener listener) {
        moveListeners.add(listener);
    }

    public MapGraphic(String mapPath) {
        image = new Image(mapPath);
        imageView = new ImageView(image);
        markerLayer = new Canvas(LondonMapData.IMAGE_WIDTH, LondonMapData.IMAGE_HEIGHT);
        coordinatesLabel = new Label("Coords: ");

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        markerLayer.setOnMouseMoved(event -> {
            int easting = (int) LondonMapData.mapToRealWidth(event.getX());
            int northing = (int) LondonMapData.mapToRealHeight(event.getY());
            coordinatesLabel.setText("Coords: " + easting + ", " + northing);
            moveListeners.forEach(l -> l.onMoved(easting, northing));
        });

        markerLayer.setOnMouseClicked(event -> {
            int easting = (int) LondonMapData.mapToRealWidth(event.getX());
            int northing = (int) LondonMapData.mapToRealHeight(event.getY());
            clickListeners.forEach(l -> l.onClicked(easting, northing));
        });
    }

    public void bindSizeTo(StackPane container) {
        imageView.fitWidthProperty().bind(container.widthProperty());
        imageView.fitHeightProperty().bind(container.heightProperty());
    }

    public AnchorPane buildOverlay() {
        AnchorPane overlay = new AnchorPane(coordinatesLabel);
        AnchorPane.setTopAnchor(coordinatesLabel, 10.0);
        AnchorPane.setRightAnchor(coordinatesLabel, 10.0);
        return overlay;
    }


    public Image getImage() {
        return image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Canvas getMarkerLayer() {
        return markerLayer;
    }
}