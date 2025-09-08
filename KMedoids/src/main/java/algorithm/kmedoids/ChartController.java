package algorithm.kmedoids;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.List;

public class ChartController {
    @FXML
    private Label medoidsLabel;
    @FXML
    private ScatterChart<Number, Number> chartOfClusters;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void changeChartAndData(List<double[]> data, List<double[]> medoids, int[] correlation) {
        updateChart(data, correlation);
        updateDataLabels(medoids);
    }

    private void updateDataLabels(List<double[]> medoids) {
        StringBuilder text = new StringBuilder("Medoids: ");
        for (double[] medoid : medoids) {
            text.append("(");
            for (double value : medoid) {
                text.append(String.format("%.2f ",value));
            }
            text.append(") \n");
        }
        medoidsLabel.setText(text.toString());
    }


    private void updateChart(List<double[]> data, int[] correlation) {
        chartOfClusters.getData().clear();

        double[] maxValues = getMaxValues(data);
        NumberAxis xAxis = (NumberAxis) chartOfClusters.getXAxis();
        NumberAxis yAxis = (NumberAxis) chartOfClusters.getYAxis();
        xAxis.setUpperBound(maxValues[0] + 1);
        yAxis.setUpperBound(maxValues[1] + 1);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Cluster Data");

        Color[] colors = {
                Color.RED, Color.GREEN,
                Color.BLUE, Color.AQUA,
                Color.CRIMSON, Color.YELLOW,
                Color.ORANGE, Color.DARKGREEN,
                Color.PINK, Color.PURPLE
        };

        for (int i = 0; i < data.size(); i++) {
            double[] point = data.get(i);
            int clusterIndex = correlation[i];

            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(point[0], point[1]);
            dataPoint.setNode(new Circle(5, colors[clusterIndex]));
            series.getData().add(dataPoint);
        }

        chartOfClusters.getData().clear();
        chartOfClusters.getData().add(series);
    }

    private double[] getMaxValues(List<double[]> data) {
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (double[] point : data) {
            if (point[0] > maxX) {
                maxX = point[0];
            }
            if (point[1] > maxY) {
                maxY = point[1];
            }
        }

        return new double[]{maxX, maxY};
    }

    public void start(KMedoids kMedoids) {
        kMedoids.setChartController(this);
        kMedoids.start();
    }
}

