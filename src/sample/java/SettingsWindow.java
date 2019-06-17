package sample.java;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsWindow implements Initializable {

    @FXML
    TreeView<SettingsPage> settingsTree;

    @FXML
    BorderPane settingsWindow;

    private ArrayList<SettingsPage> settings;
    private Controller mainWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        settings = new ArrayList<>();
        settings.add(new SettingsPage("FTP"));
        settings.add(new SettingsPage("Cos", "cos innego"));

        settingsTree.refresh();
        TreeItem<SettingsPage> root = new TreeItem<>(new SettingsPage("Settings"));

        for(SettingsPage setting : settings) {
            TreeItem<SettingsPage> next = new TreeItem<>(setting);

            root.getChildren().addAll(new TreeItem<>(setting));
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
    //                loader.setMainController(getClass().getResource("sample.java.Settings" + pageID));
    //                loader.getController();
    //                System.out.println(this);
                    //                System.out.println(settingsFTP);
                    //                settingsFTP.setSettingsController(this);

                    settingsWindow.setCenter(loader.load());
                    SettingsPage sp = loader.getController();
                    sp.setSettingsController(this);
                    System.out.println(sp);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        settingsTree.getSelectionModel().select(root.getChildren().get(0));

    }

    public void setMainController(Controller controller) {
        this.mainWindow = controller;
    }

    public void refreshMain() {
        mainWindow.refresh();
    }
}
