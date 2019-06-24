package sample.java;

import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsPrinter extends SettingsPage implements Initializable {

    @FXML
    AnchorPane printerSettingsAnchor;

    @FXML
    Label printerStatus;

    @FXML
    Label selectedPrinter;

    @FXML
    ListView<String> printerAttributes;

    @FXML
    ListView<Printer> printersList;

    @FXML
    ChoiceBox paperSizes;





    Printer printer = null;
    PrinterJob job = null;
    JobSettings jobSettings = null;
    PageLayout pageLayout = null;
    Paper paper = null;
    Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableSet<Printer> printers = Printer.getAllPrinters();
        printersList.getItems().addAll(printers);

        paperSizes.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                return ((Paper) object).getName();
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    public void selectPrinter() {
        printer = printersList.getSelectionModel().getSelectedItem();
        job = PrinterJob.createPrinterJob(printer);
        jobSettings = job.getJobSettings();
        pageLayout = jobSettings.getPageLayout();
        printerStatus.textProperty().bind(job.jobStatusProperty().asString());
        selectedPrinter.textProperty().set(printer.getName());

        loadAttributes();
        loadPaperSizes();

    }

    private void loadPaperSizes() {
        paperSizes.getItems().addAll(printer.getPrinterAttributes().getSupportedPapers());
        paper = printer.getPrinterAttributes().getDefaultPaper();
        paperSizes.setValue(paper);

        paperSizes.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            paper = (Paper) newValue;
            System.out.println(paper + "\n\t" + paper.getHeight() + "x" + paper.getWidth());
            pageLayout = printer.createPageLayout(paper, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            loadAttributes();
        }));

        System.out.println(paper + "\n\t" + paper.getHeight() + "x" + paper.getWidth());

    }

    public void loadAttributes() {
        printerAttributes.getItems().clear();
        for (Method method : pageLayout.getClass().getMethods()) {
            if(method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                try {
                    printerAttributes.getItems().add((method.getName().substring(3) + ": " + method.invoke(pageLayout)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    ExceptionLogger.log(e);
                }
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public SettingsPrinter() {
        super("Printer", "Printer");
    }

    public void listPrinters(ActionEvent event) {


        try(FileInputStream imageStream = new FileInputStream("E:\\Java\\Zdjecia\\zdjecia\\2018-08-19\\00\\IMG_3269.JPG")) {
            job.getJobSettings().setPageLayout(pageLayout);
            ImageView image = new ImageView(new Image(imageStream, 0, 0, true, true));
            image.setPreserveRatio(true);
            if(image.getImage().getWidth() > image.getImage().getHeight()) {
                image.setRotate(90);
                double imageRatio = image.getImage().getWidth() / image.getImage().getHeight();
                double paperRatio = pageLayout.getPrintableHeight() / pageLayout.getPrintableWidth();
                System.out.println(imageRatio + " " + paperRatio + " " + (imageRatio > paperRatio));
                if(imageRatio > paperRatio) {
                    image.setFitHeight(pageLayout.getPrintableWidth()+4);
                } else {
                    image.setFitWidth(pageLayout.getPrintableHeight()+4);
                }
                image.setX((pageLayout.getPrintableWidth() - image.getBoundsInLocal().getWidth()) / 2);
                image.setY((pageLayout.getPrintableHeight() - image.getBoundsInLocal().getHeight()) / 2);
            } else {
                double imageRatio = image.getImage().getHeight() / image.getImage().getWidth();
                double paperRatio = pageLayout.getPrintableHeight() / pageLayout.getPrintableWidth();
                System.out.println(imageRatio + " " + paperRatio + " " + (imageRatio > paperRatio));
                if(imageRatio > paperRatio) {
                    image.setFitWidth(pageLayout.getPrintableWidth()+4);
                } else {
                    image.setFitHeight(pageLayout.getPrintableHeight()+4);
                }
                image.setX((pageLayout.getPrintableWidth() - image.getBoundsInLocal().getWidth()) / 2);
                image.setY((pageLayout.getPrintableHeight() - image.getBoundsInLocal().getHeight()) / 2);
            }
            print(job, image);
        } catch (IOException e) {
            ExceptionLogger.log(e);
        }
    }

    private void print(PrinterJob job, Node node) {
        printerStatus.textProperty().bind(job.jobStatusProperty().asString());

        boolean printed = job.printPage(node);

        if(printed) {
            job.endJob();
        }
    }

}