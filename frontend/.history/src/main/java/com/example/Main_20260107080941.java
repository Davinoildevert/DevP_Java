package com.example;

import com.example.ui.NavService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        var fxml = getClass().getClassLoader()
                .getResource("ui/accueil/accueil.fxml");

        if (fxml == null) {
            throw new IllegalStateException("FXML introuvable");
        }

        Parent root = FXMLLoader.load(fxml);

        Scene scene = new Scene(root, 1920, 1080);

        var css = getClass().getClassLoader()
                .getResource("styles/kiosk.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Asiatik Express");
        stage.setMaximized(true);
        stage.setScene(scene);
         // ✅ POINT 5 : on dit que la page actuelle est le catalogue
    NavService.markCurrentRoot(stage, "ui/catalogue/catalogue.fxml");

        stage.show();
        
        // --- WOW animations: neon frame breathing + background haze ---
var frame = scene.lookup(".neonFrame");
if (frame != null) {
    var fade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.6), frame);
    fade.setFromValue(0.65);
    fade.setToValue(0.95);
    fade.setAutoReverse(true);
    fade.setCycleCount(javafx.animation.Animation.INDEFINITE);
    fade.play();
}

var haze = scene.lookup(".bgNeonSweep");
if (haze != null) {
    var drift = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(2.4), haze);
    drift.setFromValue(0.35);
    drift.setToValue(0.70);
    drift.setAutoReverse(true);
    drift.setCycleCount(javafx.animation.Animation.INDEFINITE);
    drift.play();
}

    }
     
    
    public static void main(String[] args) {
        launch(args);
    }
}
