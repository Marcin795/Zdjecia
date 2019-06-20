package sample.java;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {

    private static ImageCache instance = new ImageCache();

    private Map<String, Map<String, Image>> cache;

    private ImageCache() {
        cache = new HashMap<>();
    }

    public static ImageCache getInstance() {
        return instance;
    }

    public Map<String, Image> findCache(String path) {
        return cache.computeIfAbsent(path, k -> new HashMap<>());
    }

    public ImageView getImageView(File imageFile) {
        Image image = findCache(imageFile.getParent()).get(imageFile.getPath());

        if(image != null) {
            return new ImageView(image);
        }

        try {
            image = new Image(new FileInputStream(imageFile), 300, 0, true, true);
            findCache(imageFile.getParent()).put(imageFile.getPath(), image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new ImageView(image);
    }

}
