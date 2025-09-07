package task.kmeans;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KMeans {
    private int clustersAmount;
    private boolean isEuclideanMetric;
    private boolean isManhattanMetric;
    private String filePath;
    private List<double[]> dataPoints = null;
    private List<double[]> centroids = new ArrayList<>();
    private List<double[]> data = new ArrayList<>();
    private ChartController chart;
    private double sumOfDistancesSquares = 0.0;
    private int[] correlation;

    public KMeans(int clustersAmount, boolean isEuclideanMetric, boolean isManhattanMetric, String filePath) {
        this.clustersAmount = clustersAmount;
        this.isEuclideanMetric = isEuclideanMetric;
        this.isManhattanMetric = isManhattanMetric;
        this.filePath = filePath;
    }

    public void start() {
        readDataFromExcel();
        initializeCentroids();

        Task<Void> kMeansTask = new Task<Void>() {
            @Override
            protected Void call() {
                kMeansAlgorithm();
                return null;
            }
        };

        Thread kMeansThread = new Thread(kMeansTask);
        kMeansThread.setDaemon(true);
        kMeansThread.start();
    }

    private void initializeCentroids() {
        Random random = new Random();

        for (int i = 0; i < clustersAmount; i++) {
            int randomIndex = random.nextInt(dataPoints.size());
            double[] centroid = dataPoints.get(randomIndex);
            centroids.add(centroid.clone());
        }
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
            if(columns != null) {
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

    private void kMeansAlgorithm() {
        boolean centroidsChanged = true;

        while(centroidsChanged) {
            centroidsChanged = false;
            sumOfDistancesSquares = 0.0;

            for (int i = 0; i < dataPoints.size(); i++) {
                double[] dataPoint = dataPoints.get(i);
                int closestCentroid = getClosestCentroid(dataPoint);

                if (closestCentroid != correlation[i]) {
                    centroidsChanged = true;
                    correlation[i] = closestCentroid;
                }
            }

            showChart();
            updateCentroids();
        }
    }

    private void showChart() {
        Platform.runLater(() -> {
            chart.changeChartAndData(data, centroids, sumOfDistancesSquares, correlation);
        });
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateCentroids() {
        int[] amountOfPoints = new int[clustersAmount];
        double[][] newCentroids = new double[clustersAmount][dataPoints.get(0).length];

        for (int i = 0; i < dataPoints.size(); i++) {
            int clusterIndex = correlation[i];
            double[] data = dataPoints.get(i);
            for (int j = 0; j < data.length; j++) {
                newCentroids[clusterIndex][j] += data[j];
            }
            amountOfPoints[clusterIndex]++;
        }

        for (int i = 0; i < clustersAmount; i++) {
            for (int j = 0; j < newCentroids[i].length; j++) {
                newCentroids[i][j] /= amountOfPoints[i];
            }
        }

        centroids.clear();
        Collections.addAll(centroids, newCentroids);
    }

    private double euclideanMetric(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i]-point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    private double manhattanMetric(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.abs(point1[i]-point2[i]);
        }
        return sum;
    }

    private int getClosestCentroid(double[] dataPoint) {
        double minDistance = Double.MAX_VALUE;
        int closestCentroid = -1;
        for (int i = 0; i < centroids.size(); i++) {
            double[] centroid = centroids.get(i);
            double distance = isEuclideanMetric ? euclideanMetric(dataPoint, centroid) : manhattanMetric(dataPoint, centroid);
            if (distance < minDistance) {
                minDistance = distance;
                closestCentroid = i;
            }
        }
        sumOfDistancesSquares += Math.pow(minDistance, 2);
        return closestCentroid;
    }

    public void setChartController(ChartController chartController) {
        this.chart = chartController;
    }
}
