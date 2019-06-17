package sample.java;

//import com.google.gson.Gson;
//import com.google.gson.stream.JsonReader;
import javafx.collections.ObservableList;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class FTPConnection {

    private static FTPConnection instance = new FTPConnection();

//    private FTPClient client;
    public FTPClient client;
    boolean connected;
    private DateFormat dateFormat;

    private String server;
    private int port;

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private String user;
    private String password;
    private String prefix = "zdjecia/";

    public static FTPConnection getInstance() {
        return instance;
    }

    private FTPConnection() {
//        System.out.println("Constructing FTPConnection");
        client = new FTPClient();
        connected = false;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH");
        server = "localhost";
        port = 21;
        user = "test";
        password = "a";
    }

    void setSettings(FTPSettings ftpSettings) {
        this.server = ftpSettings.getServer();
        this.port = ftpSettings.getPort();
        this.user = ftpSettings.getUser();
        this.password = ftpSettings.getPassword();
//        Settings.getInstance().saveFTPSettings();
        System.out.println("Set stuff!");
    }

    synchronized void connect() {
//        System.out.println("Connecting...");
//        new Thread( () -> {
        try {
            dispose();
            client.connect(server, port);
            System.out.println("client.connect: " + client.getReplyCode());
            if(client.getReplyCode() == 220) {
//                System.out.println("Logging in...");
                client.login(user, password);
                System.out.println("client.login: " + Arrays.toString(client.getReplyStrings()));
            } else throw new ConnectException();
            if(client.getReplyCode() == 230) {
            } else throw new ConnectException();
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            connected = true;
//            System.out.println("Connected!");
        } catch(Exception e) {
            e.printStackTrace();
            dispose();
            System.out.println("Can't connect.");
        }
//        }).start();
        notifyAll();
    }

    public void syncImages() {
//        System.out.println("Syncing.. " + connected);
        if(!connected) {
            System.out.println("Not connected!");
            return;
        }
        try {
            FTPFile[] remoteFiles = client.listFiles();

            for(FTPFile file : remoteFiles) {
                if(file.isDirectory()) {
                    continue;
                }
                String savePath = prefix + "/" + dateFormat.format(file.getTimestamp().getTime()) + "/" + file.getName();
                File downloadFile = new File(savePath);
                //noinspection ResultOfMethodCallIgnored
                downloadFile.getParentFile().mkdirs();
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                boolean success = client.retrieveFile(file.getName(), outputStream);
                outputStream.close();
                if(!success) {
                    System.out.println("Transfer failed: " + file.getName());
                }
            }
//            System.out.println("Synced!");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getRemoteDirectories() {
        ArrayList<String> directories = new ArrayList<>();
        return getRemoteDirectories(directories, 0);
    }

    public ArrayList<String> getRemoteDirectories(ArrayList<String> directories, int depth) {

        FTPFile[] home;

        try {
            home = client.listDirectories();
            String path = client.printWorkingDirectory();
            System.out.println(path);
            for(FTPFile file : home) {
                if(file.isDirectory()) {
                    String prefix = "";
                    for(int i = 0; i < depth; i++) {
                        prefix += "-";
                    }
                    directories.add(prefix + path);
                    client.changeWorkingDirectory(path + file.getName() + "/");
                    System.out.println(client.printWorkingDirectory());
                    getRemoteDirectories(directories, depth + 1);
                }
            }
            client.changeWorkingDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directories;
    }

    void dispose() {
        try {
            if(client.isConnected()) {
//                client.logout();
                client.disconnect();
            }
            System.out.println("Disconnected!");
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
