module task.kmeans {
    requires javafx.controls;
    requires javafx.fxml;
    requires fontawesomefx;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens task.kmeans to javafx.fxml;
    exports task.kmeans;
}