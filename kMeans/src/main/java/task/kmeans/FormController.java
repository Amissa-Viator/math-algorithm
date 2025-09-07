package task.kmeans;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class FormController {
    @FXML
    private RadioButton EuclideanBtn;

    @FXML
    private RadioButton ManhattanBtn;

    @FXML
    private TextField clustersFld;

    @FXML
    private FontAwesomeIcon selectFileIcon;

    @FXML
    private TextField selectFilePath;

    @FXML
    private Button sendBtn;
    private Stage stage;
    private String filePath = "";
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        ToggleGroup metricsGroup = new ToggleGroup();
        EuclideanBtn.setToggleGroup(metricsGroup);
        ManhattanBtn.setToggleGroup(metricsGroup);
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File initialDirectory = new File("D:\\Education\\Data mining\\Labs");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            selectFilePath.setText(filePath);
        }
    }

    @FXML
    void handleGetInfo(ActionEvent event) {
        String clusters = clustersFld.getText();
        int clustersAmount = 0;
        boolean isEuclideanMetricSelected = false;
        boolean idManhattanMetricSelected = false;

        if(filePath.isEmpty()) {
            showAlert("Error: wasn't selected file");
        }

        if(clusters.isEmpty()) {
            showAlert("Error: unfilled text field");
        } else {
            try {
                clustersAmount = Integer.parseInt(clusters);
                if (clustersAmount <= 0) {
                    showAlert("You can't put zero or negative value to indicate clusters");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format");
                return;
            }
        }

        if (EuclideanBtn.isSelected()) {
            isEuclideanMetricSelected = true;
        } else if (ManhattanBtn.isSelected()) {
            idManhattanMetricSelected = true;
        } else {
            showAlert("Choose one of the metrics to work with");
        }

        KMeans kMeans = new KMeans(clustersAmount, isEuclideanMetricSelected, idManhattanMetricSelected, filePath);
        switchScenes(kMeans);
    }

    private void switchScenes(KMeans kmeans) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graph.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 844, 483);
            ChartController controller = fxmlLoader.getController();
            controller.setStage(stage);
            controller.start(kmeans);
            stage.setTitle("K-means algorithm");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setContentText(s);
        alert.setHeaderText("Error alert");
        alert.showAndWait();
    }
}