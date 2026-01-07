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
import java.net.URL;
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
        // ✅ Sécurité: si injection FXML ratée
        if (root == null) {
            System.out.println("❌ detail.fxml : fx:id=\"root\" manquant ou root n'est pas un BorderPane");
            return;
        }

        // ✅ récupérer le produit sélectionné (type explicite)
        CatalogueController.Product p = SelectedProduct.get();

        if (p != null) {
            if (nameLabel != null) nameLabel.setText(p.name());
            if (descLabel != null) descLabel.setText("Nouilles sautées avec crevettes, légumes et sauce tamarin");
            if (priceLabel != null) priceLabel.setText(formatEuro(p.price()));
        } else {
            System.out.println("⚠️ Aucun produit sélectionné (SelectedProduct.get() == null)");
        }

        qty = 1;
        if (qtyLabel != null) qtyLabel.setText(String.valueOf(qty));

        updateCartTotal();
    }

    @FXML
    private void onPlus(ActionEvent e) {
        qty++;
        if (qtyLabel != null) qtyLabel.setText(String.valueOf(qty));
    }

    @FXML
    private void onMinus(ActionEvent e) {
        if (qty > 1) qty--;
        if (qtyLabel != null) qtyLabel.setText(String.valueOf(qty));
    }

    @FXML
    private void onAddToCart(ActionEvent e) {
        CatalogueController.Product p = SelectedProduct.get();
        if (p == null) return;

        for (int i = 0; i < qty; i++) {
            CartService.add(p.name(), p.price());
        }
        updateCartTotal();
    }

    @FXML
    private void onBackCatalogue(ActionEvent e) {
        goTo("/ui/catalogue/catalogue.fxml"); // adapte si besoin
    }

    @FXML
    private void onOpenPanier(ActionEvent e) {
        if (CartService.isEmpty()) return;
        goTo("/ui/panier/panier.fxml"); // adapte si besoin
    }

    private void updateCartTotal() {
        if (cartTotalBtn == null) return;
        cartTotalBtn.setText("🧺  " + formatEuro(CartService.total()));
    }

    private String formatEuro(double v) {
        return money.format(v) + " €";
    }

    private void goTo(String absoluteFxmlPath) {
        try {
            URL url = getClass().getResource(absoluteFxmlPath);
            if (url == null) {
                System.out.println("❌ FXML introuvable: " + absoluteFxmlPath);
                return;
            }

            Parent newRoot = FXMLLoader.load(url);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(newRoot);

        } catch (IOException ex) {
            System.out.println("❌ Erreur chargement FXML: " + absoluteFxmlPath);
            ex.printStackTrace();
        }
    }
}
