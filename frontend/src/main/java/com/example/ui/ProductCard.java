package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.util.function.Consumer;

public class ProductCard {

    private final HBox root;

    public ProductCard(CatalogueController.Product product,
                       Consumer<CatalogueController.Product> onAdd) {

        // Zone image (placeholder gris)
        ImageView img = new ImageView();
img.setFitWidth(170);
img.setFitHeight(130);
img.setPreserveRatio(false);
img.getStyleClass().add("thumb");

// charge l'image depuis resources
var url = getClass().getResource(product.imagePath());
if (url != null) {
    img.setImage(new Image(url.toExternalForm()));
} else {
    System.out.println("❌ Image plat introuvable: " + product.imagePath());
}

StackPane imageBox = new StackPane(img);
imageBox.setMinSize(170, 130);
imageBox.setMaxSize(170, 130);


        
        imageBox.setMinSize(170, 130);
        imageBox.setMaxSize(170, 130);

        // Texte
        Label name = new Label(product.name());
        name.getStyleClass().add("productName");

        Label price = new Label(String.format("%.2f €", product.price()).replace('.', ','));
        price.getStyleClass().add("productPrice");

        Button addBtn = new Button("Ajouter");
        addBtn.getStyleClass().add("addBtn");
        addBtn.setOnAction(e -> {
    e.consume();          // ✅ empêche le clic de remonter à la card
    onAdd.accept(product);
});


        VBox info = new VBox(10, name, price, addBtn);
        info.setAlignment(Pos.CENTER_LEFT);

        // Carte
        root = new HBox(18, imageBox, info);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(18));
        root.getStyleClass().add("productCard");

        HBox.setHgrow(info, Priority.ALWAYS);
    }

    public Parent getRoot() {
        return root;
    }
}
