package sample.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private static final String MAIN_FXML = "/mainWindow.fxml";

    //TODO Come up with a title
    private static final String WINDOW_TITLE = "Some title";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = new FXMLLoader(getClass().getResource(MAIN_FXML)).load();
        stage = primaryStage;
        stage.setTitle(WINDOW_TITLE);
        stage.setOnCloseRequest(e -> {
            e.consume();
            close();
        });
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setMaximized(false);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    private void close() {
        FTPConnection.getInstance().dispose();
        System.out.println("Bye!");
        stage.close();
        Platform.exit();
    }
}
