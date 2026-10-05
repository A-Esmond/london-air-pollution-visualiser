package src.resources;

/**
 * Holds constants and coordinate mapping utilities for the London map image.
 * Provides conversions between real-world easting/northing coordinates
 * and pixel positions on the image.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class LondonMapData {
    private static final String IMAGE_PATH = FilePathLoader.load("London.png");

    public static final int MAX_EASTING = 553297;
    public static final int MIN_EASTING = 510394;

    public static final int MAX_NORTHING = 193305;
    public static final int MIN_NORTHING = 168504;

    public static final int IMAGE_HEIGHT = 1100;
    public static final int IMAGE_WIDTH = 1781;

    /**
     * Ratio of real-world easting units to image pixels horizontally.
     */
    private static double widthRatio() {
        return (double) (MAX_EASTING - MIN_EASTING) / IMAGE_WIDTH;
    }

    /**
     * Ratio of real-world northing units to image pixels vertically.
     */
    private static double heightRatio() {
        return (double) (MAX_NORTHING - MIN_NORTHING) / IMAGE_HEIGHT;
    }

    /**
     * Converts a pixel x position on the image to a real-world easting coordinate.
     *
     * @param currentCursorWidth
     * @return
     */
    public static double mapToRealWidth(double currentCursorWidth) {
        return widthRatio() * currentCursorWidth + MIN_EASTING;
    }

    /**
     * Converts a pixel y position on the image to a real-world northing coordinate.
     * Y is inverted as image coordinates increase downward, northings increase upward.
     *
     * @param currentCursorHeight
     * @return
     */
    public static double mapToRealHeight(double currentCursorHeight) {
        return heightRatio() * (IMAGE_HEIGHT - currentCursorHeight) + MIN_NORTHING;
    }

    /**
     * Converts a real-world easting coordinate to a pixel x position on the image.
     *
     * @param realWidth
     * @return
     */
    public static double mapToImageWidth(double realWidth) {
        return (realWidth - MIN_EASTING) / widthRatio();
    }

    /**
     * Converts a real-world northing coordinate to a pixel y position on the image.
     *
     * @param realHeight
     * @return
     */
    public static double mapToImageHeight(double realHeight) {
        return IMAGE_HEIGHT + (MIN_NORTHING - realHeight) / heightRatio();
    }

    /**
     * Returns the file path of the London map image.
     *
     * @return
     */
    public static String getImagePath() {
        return IMAGE_PATH;
    }

    /**
     * Checks whether a given easting/northing coordinate falls within the bounds of the map.
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean isOnMap(int x, int y) {
        return x >= MIN_EASTING && x <= MAX_EASTING &&
                y >= MIN_NORTHING && y <= MAX_NORTHING;
    }
}