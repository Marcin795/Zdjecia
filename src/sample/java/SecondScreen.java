package sample.java;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class SecondScreen implements Initializable {

    @FXML
    TilePane tile;
    private String path = "zdjecia/2019-06-06/11";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        loadImages();
        System.out.println("2nd screen initialized!");
    }

    public void refresh(String path) {
//        tile.set
        loadImages(path);
    }

    private void loadImages() {
        loadImages(path);
    }

    private void loadImages(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(File::isFile);

        if (listOfFiles == null) return;
        tile.getChildren().clear();
        for(final File file: listOfFiles) {
            if(file.isDirectory()) {
                break;
            } else {
                ImageView imageView = createImageView(file);
                tile.getChildren().addAll(imageView);
            }
        }
    }

    private ImageView createImageView(final File imageFile) {
        ImageView imageView = null;

        try {
            final Image image = new Image(new FileInputStream(imageFile), 250, 0, true, true);
            imageView = new ImageView(image);
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(true);

            imageView.setOnMouseClicked(event -> {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 2) {
                        System.out.println("double clicked image " + event.getTarget());
                    }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
        return imageView;
    }

}
