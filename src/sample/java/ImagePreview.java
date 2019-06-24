package sample.java;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class ImagePreview {

    @FXML
    TilePane tile;

    @FXML
    ScrollPane scroll;

    @FXML
    BorderPane popupPane;

    BorderPane secondScreenPopupPane;

    ImageView smallPreview;

    private ImageCache cache = ImageCache.getInstance();

    private String path;
    public BorderPane getPopupPane() {
        return popupPane;
    }

    public void setSmallPreview(ImageView smallPreview) {
        this.smallPreview = smallPreview;
    }

    public double getScroll() {
        return scroll.getVvalue();
    }

    public void setSecondScreenPopupPane(BorderPane secondScreenPopupPane) {
        this.secondScreenPopupPane = secondScreenPopupPane;
    }

    public void setScroll(double y) {
        scroll.setVvalue(y);
    }

    void loadImages(String path, ProgressBar loadingProgress) {

        this.path = path;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(File::isFile);

        if (listOfFiles == null) {
            return;
        }

        tile.getChildren().clear();

        new Thread( () -> {
            int loaded = 0;

            if(loadingProgress != null) {
                loadingProgress.setProgress(0f);
            }

            for(final File file: listOfFiles) {
                ImageView imageView = createImageView(file);
                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().addAll(imageView, new Label(file.getName().substring(0, file.getName().lastIndexOf('.'))));
                Platform.runLater(() -> tile.getChildren().addAll(vbox));

                if(loadingProgress != null) {
                    loadingProgress.setProgress((float) ++loaded / listOfFiles.length);
                }
            }
        }).start();
    }

    private ImageView createImageView(final File imageFile) {
        final int COLUMNS = 6;

        ImageView imageView = cache.getImageView(imageFile);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        // Set click handler
        // Single click - show smallPreview
        // TODO Make rightclick open context menu
        imageView.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    System.out.println("path: " + cache.getPath(path, (ImageView)event.getTarget()));
                    smallPreview.setImage(imageView.getImage());
//                    popupPane.getChildren().clear();
//                    System.gc();
                }
                if(event.getClickCount() == 1) {
                    preview(imageFile, popupPane);
                    if(secondScreenPopupPane != null) {
                        preview(imageFile, secondScreenPopupPane);
                    }
                }
            }
        });

        // Hide big smallPreview when mouse leaves the node.
        imageView.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                popupPane.getChildren().clear();
                if(secondScreenPopupPane != null) {
                    secondScreenPopupPane.getChildren().clear();
                }
                System.gc();
            }
        });

        // Set size to fit 6 columns.
        if(imageView.getImage().getWidth() / imageView.getImage().getHeight() > 1) {
            imageView.fitWidthProperty().bind(scroll.widthProperty().subtract(15 * COLUMNS + 15).divide(COLUMNS));
        } else {
            imageView.fitWidthProperty().bind(scroll.widthProperty()
                    .subtract(15 * COLUMNS + 15).divide(COLUMNS)
                    .multiply(Math.pow(imageView.getImage().getWidth(), 2))
                    .divide(Math.pow(imageView.getImage().getHeight(), 2)));
        }

        return imageView;
    }

    private void preview(File imageFile, BorderPane popupPane) {
        try {
            ImageView imageView = new ImageView(new Image(new FileInputStream(imageFile), 0, popupPane.getHeight(), true, true));
            imageView.setPreserveRatio(true);
            if(imageView.getImage().getWidth() > popupPane.getWidth()) {
                imageView.setFitWidth(popupPane.getWidth());
            } else {
                imageView.setFitHeight(popupPane.getHeight());
            }
            popupPane.getChildren().clear();
            popupPane.setCenter(imageView);
        } catch (FileNotFoundException e) {
            ExceptionLogger.log(e);
        }
    }

}
