package com.example.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public final class NavService {

    private static final Deque<String> history = new ArrayDeque<>();

    private NavService() {}

    // Appeler ça quand tu changes de page
    public static void goTo(Stage stage, String fxmlPath) {
        try {
            // on mémorise la page actuelle
            Parent currentRoot = stage.getScene().getRoot();
            Object marker = currentRoot.getProperties().get("fxmlPath");
            if (marker instanceof String currentPath) {
                history.push(currentPath);
            }

            FXMLLoader loader = new FXMLLoader(NavService.class.getResource("/" + fxmlPath));
            Parent newRoot = loader.load();
            newRoot.getProperties().put("fxmlPath", fxmlPath);

            stage.getScene().setRoot(newRoot);

        } catch (IOException e) {
            System.out.println("❌ NavService.goTo impossible: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void back(Stage stage, String fallbackFxml) {
        String prev = history.isEmpty() ? fallbackFxml : history.pop();
        goToWithoutPush(stage, prev);
    }

    private static void goToWithoutPush(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavService.class.getResource("/" + fxmlPath));
            Parent newRoot = loader.load();
            newRoot.getProperties().put("fxmlPath", fxmlPath);
            stage.getScene().setRoot(newRoot);

        } catch (IOException e) {
            System.out.println("❌ NavService.back impossible: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // À appeler au tout début (quand tu affiches la 1ère page)
    public static void markCurrentRoot(Stage stage, String fxmlPath) {
        stage.getScene().getRoot().getProperties().put("fxmlPath", fxmlPath);
    }
}
