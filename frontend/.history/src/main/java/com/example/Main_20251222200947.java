
package com.example;

import com.example.service.UserApiService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Label status = new Label("API: http://localhost:7070");

        TextField nameField = new TextField();
        nameField.setPromptText("Nom");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button loadBtn = new Button("Charger users (GET /users)");
        Button addBtn = new Button("Ajouter (POST /users)");

        ListView<String> listView = new ListView<>();

        loadBtn.setOnAction(e -> {
            status.setText("Chargement...");
            new Thread(() -> {
                try {
                    var users = UserApiService.getInstance().getUsers();
                    Platform.runLater(() -> {
                        listView.getItems().clear();
                        for (var u : users) {
                            listView.getItems().add(u.id + " - " + u.name + " (" + u.email + ")");
                        }
                        status.setText("OK : " + users.size() + " users chargés");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> status.setText("Erreur GET: " + ex.getMessage()));
                }
            }).start();
        });

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                status.setText("Nom + email obligatoires");
                return;
            }

            status.setText("Ajout...");
            new Thread(() -> {
                try {
                    var created = UserApiService.getInstance().createUser(name, email);
                    Platform.runLater(() -> {
                        status.setText("Créé : id=" + created.id);
                        nameField.clear();
                        emailField.clear();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> status.setText("Erreur POST: " + ex.getMessage()));
                }
            }).start();
        });

        HBox form = new HBox(10, nameField, emailField, addBtn);
        VBox root = new VBox(10, status, loadBtn, form, listView);

        stage.setTitle("JavaFX - Users");
        stage.setScene(new Scene(root, 700, 420));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
