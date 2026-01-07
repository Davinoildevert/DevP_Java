package com.example.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DetailController {

    @FXML private BorderPane root;

    @FXML private Label nameLabel;
    @FXML private Label descLabel;
    @FXML private Label priceLabel;

    @FXML private Label qtyLabel;
    @FXML private Button cartTotalBtn;

    private int qty = 1;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    @FXML
    public void initialize() {
        var p = SelectedProduct.get();
        if (p != null) {
            nameLabel.setText(p.name());
            // si tu n’as pas de description, laisse un texte fixe
            descLabel.setText("Nouilles sautées avec crevettes, légumes et sauce tamarin");
            priceLabel.setText(formatEuro(p.price()));
        }
        updateCartTotal();
        qtyLabel.setText(String.valueOf(qty));
    }

    @FXML
    private void onPlus(ActionEvent e) {
        qty++;
        qtyLabel.setText(String.valueOf(qty));
    }

    @FXML
    private void onMinus(ActionEvent e) {
        if (qty > 1) qty--;
        qtyLabel.setText(String.valueOf(qty));
    }

    @FXML
    private void onAddToCart(ActionEvent e) {
        var p = SelectedProduct.get();
        if (p == null) return;

        for (int i = 0; i < qty; i++) {
            CartService.add(p.name(), p.price());
        }
        updateCartTotal();
    }

    @FXML
    private void onBackCatalogue(ActionEvent e) {
        goTo("ui/catalogue/catalogue.fxml"); // adapte si besoin
    }

    @FXML
    private void onOpenPanier(ActionEvent e) {
        if (CartService.isEmpty()) return;
        goTo("ui/panier/panier.fxml"); // adapte si besoin
    }

    private void updateCartTotal() {
        String txt = "🧺  " + formatEuro(CartService.total());
        cartTotalBtn.setText(txt);
    }

    private String formatEuro(double v) {
        return money.format(v) + " €";
    }

    private void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
            Parent newRoot = loader.load();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(newRoot);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
