package com.example.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailController {

    // ✅ Root StackPane (car on met le background dessous)
    @FXML private StackPane root;

    // ✅ Background slideshow
    @FXML private ImageView bgImage;

    // ✅ Image plat
    @FXML private ImageView productImage;

    // ✅ Infos
    @FXML private Label nameLabel;
    @FXML private Label descLabel;
    @FXML private Label priceLabel;

    // ✅ Quantité + panier
    @FXML private Label qtyLabel;
    @FXML private Button cartTotalBtn;

    private int qty = 1;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    // ===== Background slideshow =====
    private final List<Image> backgrounds = new ArrayList<>();
    private int bgIndex = 0;
    private Timeline bgTimeline;

    @FXML
    public void initialize() {
        // ✅ Produit sélectionné
        CatalogueController.Product p = SelectedProduct.get();

        if (p != null) {
            if (nameLabel != null) nameLabel.setText(p.name());
            if (descLabel != null) descLabel.setText("Nouilles sautées avec crevettes, légumes et sauce tamarin");
            if (priceLabel != null) priceLabel.setText(formatEuro(p.price()));

            // ✅ Image du plat
            if (productImage != null && p.imagePath() != null) {
                var url = getClass().getResource(p.imagePath());
                if (url != null) {
                    productImage.setImage(new Image(url.toExternalForm()));
                } else {
                    System.out.println("❌ Image détail introuvable: " + p.imagePath());
                }
            }
        } else {
            System.out.println("⚠️ Aucun produit sélectionné (SelectedProduct.get() == null)");
        }

        // ✅ Quantité
        qty = 1;
        if (qtyLabel != null) qtyLabel.setText(String.valueOf(qty));

        // ✅ Panier total
        updateCartTotal();

        // ✅ Fond animé comme catalogue
        initBackgroundSlideshow();
    }

    // ===== Quantité =====
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

    // ===== Ajout panier =====
    @FXML
    private void onAddToCart(ActionEvent e) {
        CatalogueController.Product p = SelectedProduct.get();
        if (p == null) return;

        for (int i = 0; i < qty; i++) {
            CartService.add(p.name(), p.price());
        }
        updateCartTotal();
    }

    // ===== Navigation =====
    @FXML
    private void onBackCatalogue(ActionEvent e) {
        goTo("ui/catalogue/catalogue.fxml");
    }

    @FXML
    private void onOpenPanier(ActionEvent e) {
        if (CartService.isEmpty()) return;
        goTo("ui/panier/panier.fxml");
    }

    private void goTo(String fxmlPath) {
        try {
            URL url = getClass().getResource("/" + fxmlPath);
            if (url == null) {
                System.out.println("❌ FXML introuvable: /" + fxmlPath);
                return;
            }

            Parent newRoot = FXMLLoader.load(url);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(newRoot);

        } catch (IOException ex) {
            System.out.println("❌ Erreur chargement FXML: /" + fxmlPath);
            ex.printStackTrace();
        }
    }

    // ===== Helpers =====
    private void updateCartTotal() {
        if (cartTotalBtn == null) return;
        cartTotalBtn.setText("🧺  " + formatEuro(CartService.total()));
    }

    private String formatEuro(double v) {
        return money.format(v) + " €";
    }

    // ===== Background slideshow =====
    private void initBackgroundSlideshow() {
        if (bgImage == null) return;

        // ✅ mêmes backgrounds que le catalogue
        loadBg("/images/backgrounds/bg1.jpg");
        loadBg("/images/backgrounds/bg4.jpg");
        loadBg("/images/backgrounds/bg5.jpg");

        if (backgrounds.isEmpty()) {
            System.out.println("⚠️ Aucune image de fond chargée (Detail)");
            return;
        }

        // Option : rendu doux (pas trop net)
        bgImage.setOpacity(0.25);

        bgImage.setImage(backgrounds.get(0));

        bgTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> switchBackground()));
        bgTimeline.setCycleCount(Timeline.INDEFINITE);
        bgTimeline.play();
    }

    private void loadBg(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            System.out.println("❌ Image introuvable: " + path);
            return;
        }
        backgrounds.add(new Image(url.toExternalForm()));
    }

    private void switchBackground() {
        if (backgrounds.isEmpty() || bgImage == null) return;

        bgIndex = (bgIndex + 1) % backgrounds.size();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(450), bgImage);
        fadeOut.setFromValue(0.25);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(ev -> {
            bgImage.setImage(backgrounds.get(bgIndex));

            FadeTransition fadeIn = new FadeTransition(Duration.millis(450), bgImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(0.25);
            fadeIn.play();
        });

        fadeOut.play();
    }
}
