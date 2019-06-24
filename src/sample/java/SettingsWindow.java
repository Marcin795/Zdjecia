package sample.java;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsWindow implements Initializable {

    @FXML
    TreeView<SettingsPage> settingsTree;

    @FXML
    BorderPane settingsWindow;

    Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private ArrayList<SettingsPage> settings;
    private MainWindowController mainWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        settings = new ArrayList<>();
        settings.add(new SettingsFTP());
        settings.add(new SettingsPrinter());

        settingsTree.refresh();
        TreeItem<SettingsPage> root = new TreeItem<>(new SettingsPage("Settings"));

        for(SettingsPage setting : settings) {
            root.getChildren().add(new TreeItem<>(setting));
        }
        settingsTree.setRoot(root);

        settingsTree.setCellFactory((e) -> new TreeCell<SettingsPage>() {
            @Override
            protected void updateItem(SettingsPage item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText(item.getText());
                } else {
                    setText("");
                }
            }
        });

        settingsTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue != null && newValue.isLeaf()) {
                    String pageID = newValue.getValue().getName();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings" + pageID + ".fxml"));
                    settingsWindow.setCenter(loader.load());
                    SettingsPage sp = loader.getController();
                    if(sp.getClass() == SettingsPrinter.class) {
                        ((SettingsPrinter) sp).setStage(stage);
                    }
                    sp.setSettingsController(this);
                }
            } catch (IOException e) {
                ExceptionLogger.log(e);
            }
        });

        settingsTree.getSelectionModel().select(root.getChildren().get(0));

    }

    public void setMainController(MainWindowController controller) {
        this.mainWindow = controller;
    }

    public void refreshMain() {
        mainWindow.refresh();
    }
}
