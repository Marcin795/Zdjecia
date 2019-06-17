package sample.java;

public class FTPSettings {


    String server;
    int port;
    String user;
    String password;
    boolean savePassword;

    String syncDirectory;


    FTPSettings(String server, int port, String user, String password, boolean savePassword, String syncDirectory) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.savePassword = savePassword;
        this.syncDirectory = syncDirectory;
    }

    public String getServer() {
        if(server != null) {
            return server;
        } else {
            return "";
        }
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        if(user != null) {
            return user;
        } else {
            return "";
        }
    }

    public String getPassword() {
        if(password != null) {
            return password;
        } else {
            return "";
        }
    }

    public boolean getSavePassword() {
        return savePassword;
    }

    public String getSyncDirectory() {
        if(syncDirectory != null) {
            return syncDirectory;
        } else {
            return "/";
        }
    }

    public void setSyncDirectory(String syncDirectory) {
        this.syncDirectory = syncDirectory;
    }
}
