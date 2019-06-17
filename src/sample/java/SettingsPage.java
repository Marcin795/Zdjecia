package sample.java;

public class SettingsPage {

    String name;
    String text;
    SettingsWindow settingsWindow;

    public SettingsPage(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public SettingsPage(String name) {
        this.name = name;
        this.text = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSettingsController(SettingsWindow settingsWindow) {
        System.out.println("ssc");
        this.settingsWindow = settingsWindow;
    }
}
