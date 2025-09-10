package task.apriori;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormController {
    @FXML
    private Label formLabel;

    @FXML
    private Label noteLabel;

    @FXML
    private TextField numberFld;

    @FXML
    private TextField minCredFld;

    @FXML
    private TextField minSupportFld;

    @FXML
    private Button sendBtn;

    private Stage stage;

    @FXML
    private void initialize() {
        String note = "Note: enter the maximum number of products \nthat can be in the same transaction.\n" +
                "A number between one and your maximum\nwill be generated.";
        noteLabel.setText(note);
    }

    public void handleGetInfo(ActionEvent actionEvent) {
        String numberValue = numberFld.getText();
        String minSupport = minSupportFld.getText();
        String minCredibility = minCredFld.getText();
        int maxProductsInTransaction;
        double minSupportValue, minCredibilityVal;

        if(numberValue.isEmpty() || minSupport.isEmpty() || minCredibility.isEmpty()) {
            showAlert("Please enter number into the textfield");
            return;
        } else {
            try {
                maxProductsInTransaction = Integer.parseInt(numberValue);
                if(maxProductsInTransaction <= 0) {
                    showAlert("You can't put negative or zero value");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for products in one transaction");
                return;
            }

            try {
                minCredibilityVal = Double.parseDouble(minCredibility);
                if(minCredibilityVal < 0) {
                    showAlert("You can't put negative value");
                    return;
                }
                if(minCredibilityVal > 1) {
                    showAlert("You can't put value bigger than one");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for min credibility");
                return;
            }

            try {
                minSupportValue = Double.parseDouble(minSupport);
                if(minSupportValue < 0) {
                    showAlert("You can't put negative value");
                    return;
                }
                if(minSupportValue > 1) {
                    showAlert("You can't put value bigger than one");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for min support");
                return;
            }
        }

        sendInfoToGenerator(maxProductsInTransaction, minCredibilityVal, minSupportValue);
    }

    private void sendInfoToGenerator(int maxProductsInTransaction, double minCredibilityVal, double minSupportValue) {
        Generator generator = new Generator(maxProductsInTransaction, minCredibilityVal, minSupportValue, stage);
        generator.start();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setContentText(message);
        alert.setHeaderText("Error alert");
        alert.showAndWait();
    }
}
