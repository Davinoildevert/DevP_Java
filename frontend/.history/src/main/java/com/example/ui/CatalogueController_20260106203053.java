package com.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;

public class CatalogueController {

    @FXML private GridPane grid;

    @FXML private Button tabEntrees;
    @FXML private Button tabPlats;
    @FXML private Button tabDesserts;
    @FXML private Button tabBoissons;

    @FXML private Label totalLabel;

    // données temporaires (on remplacera par des vraies)
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
        totalLabel.setText("0,00 €");
    }

    // ===== Actions onglets =====
    @FXML private void onEntrees()  { setActiveTab(tabEntrees);  showProducts(entrees); }
    @FXML private void onPlats()    { setActiveTab(tabPlats);    showProducts(plats); }
    @FXML private void onDesserts() { setActiveTab(tabDesserts); showProducts(desserts); }
    @FXML private void onBoissons() { setActiveTab(tabBoissons); showProducts(boissons); }

    // ===== Navigation bas (on fera après) =====
    @FXML private void onBackAccueil() { System.out.println("Retour Accueil"); }
    @FXML private void onOpenPanier()  { System.out.println("Ouvrir panier"); }

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
        System.out.println("Ajouter: " + product.name());
        // Étape suivante : mettre à jour totalLabel + panier
    }

    // ===== Mini modèle =====
    public record Product(String name, double price) {}
}
