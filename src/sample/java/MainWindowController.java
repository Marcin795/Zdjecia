package sample.java;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private Screen secondaryScreen;
    private ImagePreview secondScreenController;
    private boolean isSecondScreenOpen;

    @FXML
    ImageView smallPreview;

    @FXML
    ImagePreview imagePreviewController;

    @FXML
    ProgressBar loadingProgress;

    private final String path = "zdjecia/";

    @FXML
    TreeView<File> fileTree;

    private String currentPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        isSecondScreenOpen = false;

        imagePreviewController.setSmallPreview(smallPreview);

        createFileTreeView();

        isSecondScreenOpen = openSecondScreen();

        fileTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.isLeaf()) {
                currentPath = newValue.getValue().getPath();
                imagePreviewController.loadImages(newValue.getValue().getPath(), loadingProgress);
                if(isSecondScreenOpen) {
                    secondScreenController.loadImages(newValue.getValue().getPath(), null);
                }
            }
        });

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
        settingsController.setStage(settingsWindow);

        settingsWindow.initModality(Modality.APPLICATION_MODAL);
        settingsWindow.setTitle("Settings");
        settingsWindow.setScene(new Scene(root, 600, 400));
        settingsWindow.showAndWait();

    }

    private boolean openSecondScreen() {

        Screen primaryScreen = Screen.getPrimary();

        Screen.getScreens().stream()
                .filter(s -> !s.equals(primaryScreen))
                .findFirst().ifPresent(s -> secondaryScreen = s);

        if(secondaryScreen != null) {
            try {
                Stage secondScreenWindow = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/imagePreview.fxml"));
                Parent root;
                root = loader.load();
                secondScreenController = loader.getController();

                secondScreenWindow.setScene(new Scene(root));
                Rectangle2D bounds = secondaryScreen.getBounds();

                secondScreenWindow.setX(bounds.getMinX());
                secondScreenWindow.setY(bounds.getMinY());
                secondScreenWindow.setWidth(bounds.getWidth());
                secondScreenWindow.setHeight(bounds.getHeight());

                secondScreenWindow.initStyle(StageStyle.UNDECORATED);
                secondScreenWindow.show();
            } catch (IOException e) {
                ExceptionLogger.log(e);
                return false;
            }
        } else {
            return false;
        }

        imagePreviewController.scroll.vvalueProperty().addListener(scroll -> secondScreenController.setScroll(imagePreviewController.getScroll()));
        imagePreviewController.setSecondScreenPopupPane(secondScreenController.getPopupPane());

        return true;
    }

    public void openMostRecent() {
        if(fileTree.getRoot().getChildren().isEmpty()) {
            return;
        }

        ObservableList<TreeItem<File>> days = fileTree.getRoot().getChildren();
        days.get(days.size()-1).setExpanded(true);
        ObservableList<TreeItem<File>> hours = days.get(days.size()-1).getChildren();
        TreeItem<File> last = hours.get(hours.size()-1);
        fileTree.getSelectionModel().select(last);
    }
}
