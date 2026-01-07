package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.layout.StackPane;




import java.io.IOException;
import java.util.List;

public class CatalogueController {

    @FXML private GridPane grid;

    @FXML private StackPane root;

    @FXML private Button tabEntrees;
    @FXML private Button tabPlats;
    @FXML private Button tabDesserts;
    @FXML private Button tabBoissons;

    @FXML private Button cartTotalBtn;

    @FXML private Label toastLabel;

    private double totalPanier = 0.0;

    // données temporaires
    private final List<Product> entrees = List.of(
            new Product("Nems poulet", 6.90),
            new Product("Soupe miso", 4.50),
            new Product("Gyozas", 7.90),
            new Product("Salade wakame", 5.90)
    );

    private final List<Product> plats = List.of(
            new Product("Ramen miso", 12.90),
            new Product("Curry rouge", 14.50),
            new Product("Pad Thai", 13.50),
            new Product("Bibimbap", 15.90)
    );

    private final List<Product> desserts = List.of(
            new Product("Mochi", 4.90),
            new Product("Cheesecake", 5.50),
            new Product("Perles coco", 4.50),
            new Product("Dorayaki", 4.80)
    );

    private final List<Product> boissons = List.of(
            new Product("Boba thé", 4.50),
            new Product("Soda", 2.50),
            new Product("Thé vert", 2.90),
            new Product("Eau", 1.50)
    );

    @FXML
    public void initialize() {
        setActiveTab(tabPlats);
        showProducts(plats);

        totalPanier = CartService.total();
        updateCartTotal();
    }

    // ===== Actions onglets =====
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
        goTo("ui/panier/panier.fxml");
;
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

    private void showProducts(List<Product> products) {
        grid.getChildren().clear();

        for (int i = 0; i < Math.min(4, products.size()); i++) {
            Product p = products.get(i);
            ProductCard card = new ProductCard(p, this::onAddToCart);
            grid.add(card.getRoot(), i % 2, i / 2);
        }
    }

    private void onAddToCart(Product product) {
        CartService.add(product.name(), product.price());
        totalPanier = CartService.total();
        updateCartTotal();
    }

    private void updateCartTotal() {
        if (cartTotalBtn == null) return;
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

    // ===== Mini modèle =====
    public record Product(String name, double price) {}
}
