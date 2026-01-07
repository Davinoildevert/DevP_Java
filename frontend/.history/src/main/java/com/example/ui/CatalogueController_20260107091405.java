package com.example.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.ui.CatalogueController.Product;

public class CatalogueController {

    @FXML private StackPane root;

    @FXML private ImageView bgImage; // ✅ ImageView du fond (slideshow)

    @FXML private GridPane grid;

    @FXML private Button tabEntrees;
    @FXML private Button tabPlats;
    @FXML private Button tabDesserts;
    @FXML private Button tabBoissons;

    @FXML private Button cartTotalBtn;
    @FXML private Label toastLabel;

    private double totalPanier = 0.0;

    // ===== Slideshow =====
    private final List<Image> backgrounds = new ArrayList<>();
    private int bgIndex = 0;
    private Timeline bgTimeline;

    
   // ===== Données temporaires =====
private final List<Product> entrees = List.of(
        new Product("Nems poulet", 6.90, "/images/plats/nems.jpg"),
        new Product("Soupe miso", 4.50, "/images/plats/soupe_miso.jpg"),
        new Product("Gyozas", 7.90, "/images/plats/gyozas.jpg"),
        new Product("Salade wakame", 5.90, "/images/plats/wakame.jpg")
);

private final List<Product> plats = List.of(
        new Product("Ramen miso", 12.90, "/images/plats/ramen.jpg"),
        new Product("Curry rouge", 14.50, "/images/plats/curry.jpg"),
        new Product("Pad Thai", 13.50, "/images/plats/padthai.jpg"),
        new Product("Bibimbap", 15.90, "/images/plats/bibimbap.jpg")
);

private final List<Product> desserts = List.of(
        new Product("Mochi", 4.90, "/images/plats/mochi.jpg"),
        new Product("Cheesecake", 5.50, "/images/plats/cheesecake.jpg"),
        new Product("Perles coco", 4.50, "/images/plats/perles_coco.jpg"),
        new Product("Dorayaki", 4.80, "/images/plats/dorayaki.jpg")
);

private final List<Product> boissons = List.of(
        new Product("Boba thé", 4.50, "/images/plats/boba.jpg"),
        new Product("Soda", 2.50, "/images/plats/soda.jpg"),
        new Product("Thé vert", 2.90, "/images/plats/the_vert.jpg"),
        new Product("Eau", 1.50, "/images/plats/eau.jpg")
);


    @FXML
    public void initialize() {
        setActiveTab(tabPlats);
        showProducts(plats);

        totalPanier = CartService.total();
        updateCartTotal();

        initBackgroundSlideshow(); // ✅ lance le fond qui défile
    }

    // ===== Onglets =====
    @FXML private void onEntrees()  { setActiveTab(tabEntrees);  showProducts(entrees); }
    @FXML private void onPlats()    { setActiveTab(tabPlats);    showProducts(plats); }
    @FXML private void onDesserts() { setActiveTab(tabDesserts); showProducts(desserts); }
    @FXML private void onBoissons() { setActiveTab(tabBoissons); showProducts(boissons); }

    // ===== Navigation =====
    @FXML
    private void onBackAccueil() {
        goTo("ui/accueil/accueil.fxml");
    }

    @FXML
    private void onOpenPanier(ActionEvent e) {
        if (CartService.isEmpty()) {
            showToast("Ajoutez au moins un plat avant d'aller au panier 🙂");
            return;
        }
        Stage stage = (Stage) root.getScene().getWindow();
        NavService.goTo(stage, "ui/panier/panier.fxml");
    }

    // ===== Produits =====
    private void showProducts(List<Product> products) {
        grid.getChildren().clear();

        for (int i = 0; i < Math.min(4, products.size()); i++) {
            Product p = products.get(i);

            ProductCard card = new ProductCard(p, prod -> {
                // bouton "Ajouter" -> ajoute direct au panier
                CartService.add(prod.name(), prod.price());
                updateCartTotal();
            });

            // clic sur la card -> page détail
            card.getRoot().setOnMouseClicked(ev -> openDetail(p));

            grid.add(card.getRoot(), i % 2, i / 2);
        }
    }

    private void openDetail(Product product) {
        SelectedProduct.set(product);
        Stage stage = (Stage) root.getScene().getWindow();
        NavService.goTo(stage, "ui/detail/detail.fxml");
    }

    // ===== UI helpers =====
    private void setActiveTab(Button active) {
        tabEntrees.getStyleClass().remove("active");
        tabPlats.getStyleClass().remove("active");
        tabDesserts.getStyleClass().remove("active");
        tabBoissons.getStyleClass().remove("active");

        if (!active.getStyleClass().contains("active")) {
            active.getStyleClass().add("active");
        }
    }

    private void updateCartTotal() {
        totalPanier = CartService.total();
        String txt = String.format("🧺  %.2f €", totalPanier).replace('.', ',');
        cartTotalBtn.setText(txt);
    }

    private void showToast(String message) {
        if (toastLabel == null) return;

        toastLabel.setText(message);
        toastLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> toastLabel.setVisible(false));
        pause.play();
    }

    // ===== Slideshow background =====
    private void initBackgroundSlideshow() {
        if (bgImage == null) return;

        // ✅ Remplace ces noms par TES vrais fichiers dans resources/images/
        loadBg("/images/backgrounds/bg1.jpg");
        loadBg("/images/backgrounds/bg4.jpg");
        loadBg("/images/backgrounds/bg5.jpg");

        if (backgrounds.isEmpty()) {
            System.out.println("⚠️ Aucune image chargée. Vérifie src/main/resources/images/");
            return;
        }

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

    // ===== Navigation générique (si tu en as encore besoin ailleurs) =====
    private void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
            Parent newRoot = loader.load();

            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(newRoot);

        } catch (IOException e) {
            System.out.println("❌ Navigation impossible vers " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ===== Modèle =====
    public record Product(String name, double price, String imagePath) {}

}
