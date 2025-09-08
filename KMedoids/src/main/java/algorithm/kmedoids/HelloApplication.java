package algorithm.kmedoids;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 512, 397);
        FormController controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.setTitle("K-medoids algorithm");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
