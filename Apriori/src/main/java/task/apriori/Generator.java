package task.apriori;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Generator {
    public static Map<String, Double> products = new HashMap<>();
    private List<Map<String, Double>> transactions = new ArrayList<>();
    private final int maxNumberOfProducts;
    private double minSupport, minCredibility;
    private Stage stage;


    public Generator(int maxNumberOfProducts, double minCredibility, double minSupport, Stage stage) {
        this.maxNumberOfProducts = maxNumberOfProducts;
        this.stage = stage;
        this.minCredibility = minCredibility;
        this.minSupport = minSupport;
    }

    public void start() {
        fillMapWithItems();
    }

    private void fillMapWithItems() {
        products.put("Sausage", 156.0);
        products.put("Chevapi", 189.90);
        products.put("Kefir", 52.50);
        products.put("Butter", 108.70);
        products.put("Eggs", 79.90);
        products.put("Cookies", 74.90);
        products.put("Chocolate", 49.90);
        products.put("Tea", 59.90);
        products.put("Instant coffee", 126.0);
        products.put("Lavash", 24.00);

        generateTransactions();
    }

    private void generateTransactions() {
        Random random = new Random();
        int count = 100;

        for(int i = 0; i < count; i++) {
            int numberOfProducts = random.nextInt(maxNumberOfProducts) + 1;
            Map<String, Double> transaction = new HashMap<>();
            List<String> keys = new ArrayList<>(products.keySet());
            Collections.shuffle(keys);

            for (int j = 0; j < numberOfProducts; j++) {
                String product = keys.get(j);
                transaction.put(product, products.get(product));
            }

            transactions.add(transaction);
        }

        saveToExcelFile();
    }

    private void saveToExcelFile() {
        boolean isSaved = ExcelManager.saveToExcel(transactions);
        if(isSaved) {
            showMessage();
        } else {
            showAlert("There are some problems with saving transactions");
            return;
        }

        startAnalysis();
    }

    private void startAnalysis() {
        try {
            List<Transaction> data = ExcelManager.readFromExcel();
            AprioriMethod method = new AprioriMethod(data, minSupport, minCredibility);
            method.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("success.fxml"));
            Parent parent = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Message");
            stage.setScene(new Scene(parent));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setContentText(message);
        alert.setHeaderText("Error alert");
        alert.showAndWait();
    }
}
