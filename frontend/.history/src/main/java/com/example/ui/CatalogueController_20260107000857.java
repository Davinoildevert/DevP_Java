package com.example.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.List;

public class CatalogueController {

    @FXML private GridPane grid;

    @FXML private Button tabEntrees;
    @FXML private Button tabPlats;
    @FXML private Button tabDesserts;
    @FXML private Button tabBoissons;

    // ✅ cadran en haut à droite (jaune)
    @FXML private Button cartTotalBtn;

    // ✅ total panier (simple)
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
        // état initial : Plats
        setActiveTab(tabPlats);
        showProducts(plats);

        // ✅ init cadran panier
        updateCartTotal();
    }

    // ===== Actions onglets =====
    @FXML private void onEntrees()  { setActiveTab(tabEntrees);  showProducts(entrees); }
    @FXML private void onPlats()    { setActiveTab(tabPlats);    showProducts(plats); }
    @FXML private void onDesserts() { setActiveTab(tabDesserts); showProducts(desserts); }
    @FXML private void onBoissons() { setActiveTab(tabBoissons); showProducts(boissons); }

    // ===== Navigation bas =====
    @FXML
    private void onBackAccueil(ActionEvent e) {
        System.out.println("Retour Accueil");
        // TODO: navigation vers accueil.fxml
    }

    @FXML
    private void onOpenPanier(ActionEvent e) {
        System.out.println("Ouvrir panier (total=" + totalPanier + ")");
        // TODO: ouvrir panier.fxml
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

        // 2 colonnes, 2 lignes (4 items)
        for (int i = 0; i < Math.min(4, products.size()); i++) {
            Product p = products.get(i);
            ProductCard card = new ProductCard(p, this::onAddToCart);
            grid.add(card.getRoot(), i % 2, i / 2);
        }
    }

    private void onAddToCart(Product product) {
        System.out.println("Ajouter: " + product.name() + " (" + product.price() + "€)");

        // ✅ update total
        totalPanier += product.price();
        updateCartTotal();
    }

    private void updateCartTotal() {
        if (cartTotalBtn == null) return;

        // format "0,00 €"
        String txt = String.format("🧺  %.2f €", totalPanier).replace('.', ',');
        cartTotalBtn.setText(txt);
    }

    // ===== Mini modèle =====
    public record Product(String name, double price) {}
}
