package sample.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class SettingsFTP extends SettingsPage implements Initializable {

    public SettingsFTP() {
        super("FTP", "FTP");
    }

    @FXML
    TextField server;
    @FXML
    TextField port;
    @FXML
    TextField user;
    @FXML
    PasswordField password;
    @FXML
    CheckBox savePassword;

    @FXML
    ChoiceBox<String> remoteDirectories;

    Settings settings = Settings.getInstance();
    FTPConnection ftpConnection = FTPConnection.getInstance();


    @FXML
    void connect() {
        System.out.println(remoteDirectories.getValue());
        FTPSettings ftpSettings = new FTPSettings(server.getText(), Integer.parseInt(port.getText()), user.getText(), password.getText(), savePassword.isSelected(), remoteDirectories.getValue());
        ftpConnection.setSettings(ftpSettings);
        settings.saveFTPSettings(ftpSettings);
        ftpConnection.connect();
        getRemoteDirectories();
        ftpConnection.syncImages();
        settingsWindow.refreshMain();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings.loadSettings();
        final FTPSettings[] ftpSettings = {settings.getFTPSettings()};
        server.setText(ftpSettings[0].server);
        port.setText(String.valueOf(ftpSettings[0].port));
        user.setText(ftpSettings[0].user);
        password.setText(ftpSettings[0].password);
        savePassword.setSelected(ftpSettings[0].savePassword);

        if(ftpConnection.client.isConnected()) {
            getRemoteDirectories();
        }

        remoteDirectories.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                if((Integer) newValue == -1) {
                    ftpConnection.client.changeWorkingDirectory(remoteDirectories.getItems().get(remoteDirectories.getItems().indexOf(settings.getFTPSettings().syncDirectory)));
                } else {
                    ftpConnection.client.changeWorkingDirectory(remoteDirectories.getItems().get((Integer) newValue));
                }
                ftpSettings[0].setSyncDirectory(ftpConnection.client.printWorkingDirectory() + "/");
                settings.saveFTPSettings(ftpSettings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void getRemoteDirectories() {
        ObservableList<String> directories = getDirectories("/");
        if(directories != null) {
            remoteDirectories.setItems(directories);
            remoteDirectories.setValue(settings.getFTPSettings().syncDirectory);
        }


    }

    private ObservableList<String> getDirectories(String path) {
        if(!ftpConnection.client.isConnected()) {
            return null;
        }
        ObservableList<String> list = FXCollections.observableList(new ArrayList<>());

        list.add(path);

        try {
            FTPFile[] directories = ftpConnection.client.listDirectories(path);
            if(directories != null) {
                for(FTPFile dir : directories) {
                    list.addAll(getDirectories(path + dir.getName() + "/"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
