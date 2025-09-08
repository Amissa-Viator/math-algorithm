package algorithm.kmedoids;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KMedoids {
    private int clustersAmount;
    private boolean isEuclideanMetric;
    private boolean isManhattanMetric;
    private String filePath;
    private List<double[]> dataPoints = null;
    private List<double[]> medoids = new ArrayList<>();
    private List<double[]> data = new ArrayList<>();
    private ChartController chart;
    private double sumOfDistances = 0.0;
    private int[] correlation;

    public KMedoids(int clustersAmount, boolean isEuclideanMetric, boolean isManhattanMetric, String filePath) {
        this.clustersAmount = clustersAmount;
        this.isEuclideanMetric = isEuclideanMetric;
        this.isManhattanMetric = isManhattanMetric;
        this.filePath = filePath;
    }

    public void start() {
        readDataFromExcel();
        initializeMedoids();

        Task<Void> kMedoidsTask = new Task<Void>() {
            @Override
            protected Void call() {
                kMedoidsAlgorithm();
                return null;
            }
        };

        Thread kMedoidsThread = new Thread(kMedoidsTask);
        kMedoidsThread.setDaemon(true);
        kMedoidsThread.start();
    }

    private void initializeMedoids() {
        Random random = new Random();
        for (int i = 0; i < clustersAmount; i++) {
            int randomIndex = random.nextInt(dataPoints.size());
            double[] medoid = dataPoints.get(randomIndex);
            medoids.add(medoid.clone());
        }
    }

    private void kMedoidsAlgorithm() {
        boolean medoidsChanged = true;
        double totalDistance = 0.0;

        updateClusters();
        totalDistance = sumOfDistances;

        while (medoidsChanged) {
            medoidsChanged = false;
            double bestExchangeRate = Double.MAX_VALUE;
            int bestMedoidIndex = -1;
            double[] bestMedoid = null;

            for (int i = 0; i < medoids.size(); i++) {
                double[] originalMedoid = medoids.get(i);

                for (int j = 0; j < dataPoints.size(); j++) {
                    double[] candidateMedoid = dataPoints.get(j);

                    if (isMedoid(candidateMedoid)) {
                        continue;
                    }

                    medoids.set(i, candidateMedoid);
                    updateClusters();

                    double exchangeRate = sumOfDistances;
                    if (exchangeRate < bestExchangeRate) {
                        bestExchangeRate = exchangeRate;
                        bestMedoidIndex = i;
                        bestMedoid = candidateMedoid;
                    }

                    medoids.set(i, originalMedoid);
                }
            }

            if (bestExchangeRate < totalDistance) {
                medoids.set(bestMedoidIndex, bestMedoid);
                totalDistance = bestExchangeRate;
                medoidsChanged = true;
            }
            showChart();
        }
    }


    private void updateClusters() {
        sumOfDistances = 0.0;
        for (int j = 0; j < dataPoints.size(); j++) {
            double[] dataPoint = dataPoints.get(j);
            int closestMedoid = getClosestMedoid(dataPoint);

            if (closestMedoid != correlation[j]) {
                correlation[j] = closestMedoid;
            }

            sumOfDistances += calculateDistance(dataPoint, medoids.get(closestMedoid));
        }
    }

    private boolean isMedoid(double[] point) {
        for (double[] medoid : medoids) {
            if (Arrays.equals(medoid, point)) {
                return true;
            }
        }
        return false;
    }

    private int getClosestMedoid(double[] dataPoint) {
        double minDistance = Double.MAX_VALUE;
        int closestCentroid = -1;
        for (int i = 0; i < medoids.size(); i++) {
            double[] medoid = medoids.get(i);
            double distance = calculateDistance(dataPoint, medoid);
            if (distance < minDistance) {
                minDistance = distance;
                closestCentroid = i;
            }
        }
        return closestCentroid;
    }

    private double calculateDistance(double[] point1, double[] point2) {
        if (isEuclideanMetric) {
            return euclideanMetric(point1, point2);
        } else {
            return manhattanMetric(point1, point2);
        }
    }

    private double euclideanMetric(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    private double manhattanMetric(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.abs(point1[i] - point2[i]);
        }
        return sum;
    }

    private void readDataFromExcel() {
        try {
            dataPoints = ExcelReader.readAllDataFromFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        correlation = new int[dataPoints.size()];

        checkSizeToVisualize();
    }

    private void checkSizeToVisualize() {
        int amountOfPoints = dataPoints.get(0).length;
        if (amountOfPoints > 2) {
            List<Integer> columns = showChooser(amountOfPoints);
            if (columns != null) {
                for (int i = 0; i < dataPoints.size(); i++) {
                    double[] selectedPoints = new double[columns.size()];
                    double[] points = dataPoints.get(i);
                    for (int j = 0; j < columns.size(); j++) {
                        selectedPoints[j] = points[columns.get(j)];
                    }
                    data.add(selectedPoints);
                }
            }
        } else {
            data = dataPoints;
        }
    }

    public List<Integer> showChooser(int columnCount) {
        List<Integer> selectedColumns = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogForm.fxml"));
            Scene scene = new Scene(loader.load());
            SelectColumnsController controller = loader.getController();
            Stage dialogStage = new Stage();
            controller.setStage(dialogStage);
            controller.initialize(columnCount);

            dialogStage.setTitle("Select Columns");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            selectedColumns = controller.getSelectedColumns();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return selectedColumns;
    }

    private void showChart() {
        Platform.runLater(() -> {
            chart.changeChartAndData(data, medoids, correlation);
        });
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setChartController(ChartController chartController) {
        this.chart = chartController;
    }
}
