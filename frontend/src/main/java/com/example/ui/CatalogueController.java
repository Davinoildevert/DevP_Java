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
    @FXML private ImageView bgImage; // ImageView du fond (slideshow)
    @FXML private GridPane grid;

    @FXML private Button tabEntrees;
    @FXML private Button tabPlats;
    @FXML private Button tabDesserts;
    @FXML private Button tabBoissons;

    @FXML private Button cartTotalBtn;
    @FXML private Label toastLabel;

    private double totalPanier = 0.0;

    // ===== Données (source: mock JSON ou API plus tard) =====
    private final MenuService menuService = MenuServiceFactory.getInstance();

    // ===== Slideshow =====
    private final List<Image> backgrounds = new ArrayList<>();
    private int bgIndex = 0;
    private Timeline bgTimeline;

    @FXML
    public void initialize() {
        // Par défaut: onglet Plats
        onPlats();

        totalPanier = CartService.total();
        updateCartTotal();

        initBackgroundSlideshow();
    }

    // ===== Onglets =====
    @FXML
    private void onEntrees() {
        setActiveTab(tabEntrees);
        showCategory(1); // Entrées
    }

    @FXML
    private void onPlats() {
        setActiveTab(tabPlats);
        showCategory(2); // Plats
    }

    @FXML
    private void onDesserts() {
        setActiveTab(tabDesserts);
        showCategory(3); // Desserts
    }

    @FXML
    private void onBoissons() {
        setActiveTab(tabBoissons);
        showCategory(4); // Boissons
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


    private String getDataSourceLabel() {
        // petit helper visuel, sans dépendre d'autres classes
        return "mock";
    }

    private Product toProduct(Plat plat) {
        // Ton JSON contient "image" (ex: "ramen.jpg").
        // Ton UI attend un chemin resources genre "/images/plats/ramen.jpg".
        String imagePath = null;
        if (plat.image != null && !plat.image.isBlank()) {
            if (plat.image.startsWith("/")) {
                imagePath = plat.image;
            } else {
                imagePath = "/images/plats/" + plat.image;
            }
        }
        return new Product(plat.nom, plat.prix, imagePath);
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

        for (int i = 0; i < Math.min(4, plats.size()); i++) {
            Plat plat = plats.get(i);

            Product p = toProduct(plat);

            ProductCard card = new ProductCard(p, prod -> {
                CartService.add(prod.name(), prod.price());
                updateCartTotal();
            });

            // clic sur la card -> page détail
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

    // ===== Modèle local conservé pour ne pas casser ProductCard/SelectedProduct (étape suivante) =====
    public record Product(String name, double price, String imagePath) {}
}
