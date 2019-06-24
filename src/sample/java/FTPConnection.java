package sample.java;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.ConnectException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class FTPConnection {

    private static FTPConnection instance = new FTPConnection();

    public FTPClient client;
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
        client = new FTPClient();
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
        System.out.println("Set stuff!");
    }

    synchronized void connect() {
//        new Thread( () -> {
        try {
            dispose();
            client.connect(server, port);
            System.out.println("client.connect: " + client.getReplyCode());
            if (client.getReplyCode() == 220) {
                client.login(user, password);
                System.out.println("client.login: " + Arrays.toString(client.getReplyStrings()));
                if (client.getReplyCode() != 230) throw new ConnectException("Can't login.");
            } else throw new ConnectException("Can't connect.");
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            System.err.println("Couldn't connect.");
            ExceptionLogger.log(e);
            dispose();
        }
//        }).start();
        notifyAll();
    }

    public void syncImages() {
        if(!client.isConnected()) {
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

//                System.out.println(savePath);
                File downloadFile = new File(savePath);
//                noinspection ResultOfMethodCallIgnored
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
            System.out.println("henlo");
            ExceptionLogger.log(e);
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
            ExceptionLogger.log(e);
        }

        return directories;
    }

    void dispose() {
        try {
            if(client.isConnected()) {
                client.disconnect();
                System.out.println("Disconnected!");
            }
        } catch(IOException e) {
            ExceptionLogger.log(e);
        }
    }
}
