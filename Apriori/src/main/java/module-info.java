module task.apriori {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens task.apriori to javafx.fxml;
    exports task.apriori;
}