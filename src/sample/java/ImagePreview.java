package sample.java;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ImagePreview implements Initializable {

    @FXML
    TilePane tile;

    @FXML
    ScrollPane scroll;

    private ImageCache cache = ImageCache.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(location);
    }

    public TilePane getTile() {
        return tile;
    }

    public void refresh(String path) {
        loadImages(path);
    }

    public double getScroll() {
        return scroll.getVvalue();
    }

    public void setScroll(double y) {
        scroll.setVvalue(y);
    }

    void loadImages(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(File::isFile);

        if (listOfFiles == null) return;
        tile.getChildren().clear();

        for(final File file: listOfFiles) {
            ImageView imageView = createImageView(file);
            tile.getChildren().addAll(imageView);
        }
    }

    private ImageView createImageView(final File imageFile) {
        final int COLUMNS = 6;

        ImageView imageView = cache.getImageView(imageFile);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        imageView.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    System.out.println("double clicked image " + event.getTarget());
                }
            }
        });
        imageView.fitWidthProperty().bind(scroll.widthProperty().subtract(15*COLUMNS+15).divide(COLUMNS));
        return imageView;
    }

}
