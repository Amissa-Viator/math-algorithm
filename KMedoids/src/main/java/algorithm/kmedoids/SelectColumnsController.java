package algorithm.kmedoids;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class SelectColumnsController {

    @FXML
    private ComboBox<Integer> firstColBox;

    @FXML
    private ComboBox<Integer> secondColBox;

    @FXML
    private Button sendBtn;

    private Stage stage;
    private List<Integer> selectedColumns;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize(int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            firstColBox.getItems().add(i);
            secondColBox.getItems().add(i);
        }
    }

    @FXML
    public void handleGetData(ActionEvent actionEvent) {
        Integer firstCol = firstColBox.getValue();
        Integer secondCol = secondColBox.getValue();

        if (firstCol == null || secondCol == null) {
            showAlert("Error: select both columns");
            return;
        }
        if (firstCol.equals(secondCol)) {
            showAlert("Error: select two different columns");
            return;
        }

        selectedColumns = Arrays.asList(firstCol, secondCol);
        stage.close();
    }

    private void showAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setContentText(s);
        alert.setHeaderText("Error alert");
        alert.showAndWait();
    }

    public List<Integer> getSelectedColumns() {
        return selectedColumns;
    }
}

