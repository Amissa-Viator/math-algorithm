module algorithm.kmedoids {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires fontawesomefx;


    opens algorithm.kmedoids to javafx.fxml;
    exports algorithm.kmedoids;
}