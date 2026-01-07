package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        stage.show();

        // WOW: animate the CTA shine overlay (optional)
        var shine = scene.lookup(".ctaShine");
        if (shine != null) {
            var t = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.seconds(2.2), shine);
            t.setFromX(-260);
            t.setToX(260);
            t.setCycleCount(javafx.animation.Animation.INDEFINITE);
            t.setAutoReverse(true);
            t.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
