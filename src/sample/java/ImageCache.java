package sample.java;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {

    private static ImageCache instance = new ImageCache();

    private Map<String, Map<String, Image>> cache;

    private ImageCache() {
        cache = new HashMap<>();
    }

    static ImageCache getInstance() {
        return instance;
    }

    private Map<String, Image> findCache(String path) {
        return cache.computeIfAbsent(path, k -> new HashMap<>());
    }

    synchronized ImageView getImageView(File imageFile) {

        Image image = findCache(imageFile.getParent()).get(imageFile.getPath());

        if(image != null) {
            return new ImageView(image);
        }

        try(FileInputStream fileStream = new FileInputStream(imageFile)) {
            ImageInputStream imageStream = ImageIO.createImageInputStream(imageFile);
            ImageReader reader = ImageIO.getImageReaders(imageStream).next();
            reader.setInput(imageStream);
            if(reader.getWidth(0) > reader.getHeight(0)) {
                image = new Image(fileStream, 450, 0, true, true);
            } else {
                image = new Image(fileStream, 0, 450, true, true);
            }

            findCache(imageFile.getParent()).put(imageFile.getPath(), image);
        } catch (IOException e) {
            ExceptionLogger.log(e);
        }

        return new ImageView(image);
    }

    String getPath(String path, ImageView imageView) {
        return cache.get(path)
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals(imageView.getImage()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
