package com.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmationController {

    @FXML private Label orderLabel;
    @FXML private VBox recapBox;
    @FXML private Label totalLabel;

    private int orderNo;

    @FXML
    public void initialize() {
        orderNo = CartService.nextOrderNumber();
        orderLabel.setText("Commande N° #" + orderNo);

        recapBox.getChildren().clear();
        for (CartService.CartItem it : CartService.getItems()) {
            String line = it.qty() + "x " + it.name();
            String price = String.format("%.2f €", it.price() * it.qty()).replace('.', ',');

            Label l = new Label(line + "   " + price);
            l.getStyleClass().add("recapLine");
            recapBox.getChildren().add(l);
        }

        totalLabel.setText("Total  " + String.format("%.2f €", CartService.total()).replace('.', ','));
    }

    @FXML
    private void onNewOrder() {
        CartService.clear();
        goTo("ui/accueil/accueil.fxml");
    }

    private void goTo(String fxmlPath) {
        try {
            var url = getClass().getClassLoader().getResource(fxmlPath);
            if (url == null) throw new IllegalStateException("FXML introuvable: " + fxmlPath);

            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) orderLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
