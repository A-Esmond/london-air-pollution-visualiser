package src.resources;

import java.net.URL;

public class FilePathLoader {
    public static String load(String name) {
        URL url = FilePathLoader.class.getResource(name);
        if (url == null) throw new RuntimeException(name + " not found! Check it's in the resources folder.");
        return url.toExternalForm();
    }
}
