package sample.java;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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

//        ftpConnection.connect();
//        ftpConnection.syncImages();

        loadImages();

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
        } catch(Exception e) {
            e.printStackTrace();
        }
        return imageView;
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
                loadImages(newValue.getValue().getPath());
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
//        Parent root = FXMLLoader.load(getClass().getResource("settingsWindow.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/settingsWindow.fxml"));
        Parent root = loader.load();

        SettingsWindow settingsController = loader.getController();
        settingsController.setMainController(this);

        settingsWindow.initModality(Modality.APPLICATION_MODAL);
        settingsWindow.setTitle("Settings");
        settingsWindow.setScene(new Scene(root, 600, 400));
        settingsWindow.showAndWait();

    }

    public synchronized void openFTPConnection() {
        Runnable r = () -> ftpConnection.connect();
        while (!ftpConnection.connected) {
            try {
                ftpConnection.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("should be connected?");
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
