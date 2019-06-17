package sample.java;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static Settings instance = new Settings();

    FTPConnection ftp;

    String homePath;
    String fileName;
    String propertiesPath;
    String appName;
    Properties properties;

    public static Settings getInstance() {
        return instance;
    }

    private Settings() {
        ftp = FTPConnection.getInstance();
        homePath = System.getProperty("user.home");
        properties = new Properties();
        appName = "zdjecia";
        fileName = "settings";
        propertiesPath = homePath + File.separator + "." + appName + File.separator + fileName + ".properties";
        propertiesPath = "settings.properties";

        loadSettings();
    }

    void loadSettings() {
        try (InputStream inputStream = new FileInputStream(propertiesPath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("rip, no properties");
        }
    }

    FTPSettings getFTPSettings() {
        String server = properties.getProperty("ftp.server");
        String port = properties.getProperty("ftp.port");
        if(port == null) {
            port = "21";
        }
        String user = properties.getProperty("ftp.user");
        String password = properties.getProperty("ftp.password");
        String savePassword = properties.getProperty("ftp.savePassword");
        String syncDirectory = properties.getProperty("ftp.syncDirectory");
        if(savePassword == null) {
            savePassword = "false";
        }
        FTPSettings ftpSettings = new FTPSettings(
                server,
                Integer.parseInt(port),
                user,
                password,
                Boolean.parseBoolean(savePassword),
                syncDirectory);
        return ftpSettings;
    }

    void saveSettings() {
        try (OutputStream outputStream = new FileOutputStream(propertiesPath)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            System.out.println("Can't save properties.");
        }
    }

    void saveFTPSettings(FTPSettings ftpSettings) {
        try (OutputStream outputStream = new FileOutputStream(propertiesPath)) {
            properties.setProperty("ftp.server", ftpSettings.getServer());
            properties.setProperty("ftp.port", String.valueOf(ftpSettings.getPort()));
            properties.setProperty("ftp.user", ftpSettings.getUser());
            properties.setProperty("ftp.password", ftpSettings.savePassword ? ftpSettings.getPassword() : "");
            properties.setProperty("ftp.savePassword", String.valueOf(ftpSettings.getSavePassword()));
            properties.setProperty("ftp.syncDirectory", ftpSettings.getSyncDirectory());
            properties.store(outputStream, null);
        } catch (IOException e) {
            System.out.println("Can't save properties.");
        }
    }

}
