package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        var fxml = getClass().getClassLoader().getResource("ui/accueil/accueil.fxml");
        if (fxml == null) {
            throw new IllegalStateException("FXML introuvable: ui/accueil/accueil.fxml (vérifie src/main/resources)");
        }

        var root = FXMLLoader.load(fxml);

        Scene scene = new Scene(root, 1920, 1080);

        var css = getClass().getClassLoader().getResource("styles/kiosk.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Asiatik Express");
        stage.setMaximized(true);     // ou stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
