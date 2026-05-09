package BrotatoBuildPlanner.Vista;

import BrotatoBuildPlanner.Vista.Components.PanelBuild;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ventana principal JavaFX.
 */
public class Window extends Application {

    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        PanelBuild root = new PanelBuild(stage);
        Scene scene = new Scene(root, 1200, 760);
        stage.setTitle("Brotato Build Planner");
        stage.setScene(scene);
        stage.show();
    }
}
