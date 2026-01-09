package com.example.ui;

import com.example.model.menu.Plat;
import com.example.service.menu.MenuService;
import com.example.service.menu.MenuServiceFactory;
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

public class CatalogueController {

    @FXML private StackPane root;
    @FXML private ImageView bgImage; // fond slideshow
    @FXML private GridPane grid;

    @FXML private Button tabEntrees;
    @FXML private Button tabPlats;
    @FXML private Button tabDesserts;
    @FXML private Button tabBoissons;

    @FXML private Button cartTotalBtn;
    @FXML private Label toastLabel;

    private double totalPanier = 0.0;

    // ✅ garde service original (API/mock via factory)
    private final MenuService menuService = MenuServiceFactory.getInstance();

    // ===== Slideshow =====
    private final List<Image> backgrounds = new ArrayList<>();
    private int bgIndex = 0;
    private Timeline bgTimeline;

    @FXML
    public void initialize() {
        onPlats(); // tab par défaut
        totalPanier = CartService.total();
        updateCartTotal();
        initBackgroundSlideshow();
    }

    // ===== Onglets =====
    @FXML
    private void onEntrees() {
        setActiveTab(tabEntrees);
        showCategory(1);
    }

    @FXML
    private void onPlats() {
        setActiveTab(tabPlats);
        showCategory(2);
    }

    @FXML
    private void onDesserts() {
        setActiveTab(tabDesserts);
        showCategory(3);
    }

    @FXML
    private void onBoissons() {
        setActiveTab(tabBoissons);
        showCategory(4);
    }

    private void showCategory(int categorieId) {
        try {
            List<Plat> plats = menuService.getPlatsByCategorie(categorieId);
            showPlats(plats);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Erreur chargement menu");
        }
    }

    private Product toProduct(Plat plat) {
        String imagePath = null;
        if (plat.image != null && !plat.image.isBlank()) {
            imagePath = plat.image.startsWith("/") ? plat.image : "/images/plats/" + plat.image;
        }
        // ✅ On garde l'ID (indispensable pour envoyer commande au backend)
        return new Product(plat.id, plat.nom, plat.prix, imagePath);
    }

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
    private void showPlats(List<Plat> plats) {
        grid.getChildren().clear();

        // ✅ Fusion: on prend le comportement V2 -> afficher tous les plats (plus limité à 4)
        for (int i = 0; i < plats.size(); i++) {
            Plat plat = plats.get(i);
            Product p = toProduct(plat);

            ProductCard card = new ProductCard(p, prod -> {
                // ✅ logique originale: on garde l'id
                CartService.add(prod.id(), prod.name(), prod.price());
                updateCartTotal();
            });

            // clic sur la card -> détail
            card.getRoot().setOnMouseClicked(ev -> openDetail(plat));

            grid.add(card.getRoot(), i % 2, i / 2);
        }
    }

    private void openDetail(Plat plat) {
        SelectedProduct.set(plat);
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

        // ✅ Fusion: style V2 (sans emoji). Si tu veux garder l’emoji, remets "🧺  ".
        String txt = String.format("  %.2f €", totalPanier).replace('.', ',');
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

    // ===== Navigation générique =====
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

    // ✅ On garde le modèle ORIGINAL avec id (important)
    public record Product(int id, String name, double price, String imagePath) {}
}
