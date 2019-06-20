package sample.java;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Screen secondaryScreen;
//    private SecondScreen secondScreenController;
    private ImagePreview secondScreenController;

    @FXML
    ImagePreview imagePreviewController;

    @FXML
    TilePane tile;
    private String path = "zdjecia/";

    @FXML
    TreeView<File> fileTree;

    FTPConnection ftpConnection;
    Settings settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ftpConnection = FTPConnection.getInstance();
        settings = Settings.getInstance();

        createFileTreeView();

        tile = imagePreviewController.getTile();

        try {
            openSecondScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("initialized!");
    }

    private TreeItem<File> createTree(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        File[] childs = file.listFiles(File::isDirectory);
        if (childs != null) {
            for (File child : childs) {
                item.getChildren().add(createTree(child));
            }
        }
        return item;
    }

    private void createFileTreeView() {
        fileTree.refresh();
        fileTree.setRoot(createTree(new File(path)));
        fileTree.setCellFactory((e) -> new TreeCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText((item.getName()));
                } else {
                    setText("");
                }
            }
        });

        fileTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.isLeaf()) {
                imagePreviewController.loadImages(newValue.getValue().getPath());
                secondScreenController.refresh(newValue.getValue().getPath());
            }
        });

        fileTree.setShowRoot(false);
    }

    public void refresh() {
        fileTree.refresh();
        createFileTreeView();
        openMostRecent();
    }

    public void openSettings() throws Exception {

        Stage settingsWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/settingsWindow.fxml"));
        Parent root = loader.load();

        SettingsWindow settingsController = loader.getController();
        settingsController.setMainController(this);

        settingsWindow.initModality(Modality.APPLICATION_MODAL);
        settingsWindow.setTitle("Settings");
        settingsWindow.setScene(new Scene(root, 600, 400));
        settingsWindow.showAndWait();

    }

    private void openSecondScreen() throws IOException {

        Screen primaryScreen = Screen.getPrimary();

        Screen.getScreens().stream()
                .filter(s->!s.equals(primaryScreen))
                .findFirst().ifPresent(s->secondaryScreen = s);

        if(secondaryScreen != null) {
            Stage secondScreenWindow = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/imagePreview.fxml"));
            Parent root = loader.load();

            secondScreenController = loader.getController();
            System.out.println(secondScreenController);

            secondScreenWindow.setScene(new Scene(root));
            Rectangle2D bounds = secondaryScreen.getBounds();

            secondScreenWindow.setX(bounds.getMinX());
            secondScreenWindow.setY(bounds.getMinY());
            secondScreenWindow.setWidth(bounds.getWidth());
            secondScreenWindow.setHeight(bounds.getHeight());

            secondScreenWindow.initStyle(StageStyle.UNDECORATED);
            secondScreenWindow.show();
        }

        imagePreviewController.scroll.vvalueProperty().addListener(scroll -> secondScreenController.setScroll(imagePreviewController.getScroll()));
    }

    public void openMostRecent() {
        if(fileTree.getRoot().getChildren().isEmpty()) {
            return;
        }
        ObservableList<TreeItem<File>> days = fileTree.getRoot().getChildren();
        days.get(days.size()-1).setExpanded(true);
        ObservableList<TreeItem<File>> hours = days.get(days.size()-1).getChildren();
        TreeItem<File> last = hours.get(hours.size()-1);
        System.out.println(last);
        fileTree.getSelectionModel().select(last);
    }

}
