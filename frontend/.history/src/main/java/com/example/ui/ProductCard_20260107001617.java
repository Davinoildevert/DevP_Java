package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class ProductCard {

    private final HBox root;

    public ProductCard(CatalogueController.Product product,
                       Consumer<CatalogueController.Product> onAdd) {

        // Zone image (placeholder gris)
        Rectangle imagePlaceholder = new Rectangle(170, 130);
        imagePlaceholder.getStyleClass().add("thumb");

        StackPane imageBox = new StackPane(imagePlaceholder);
        imageBox.setMinSize(170, 130);
        imageBox.setMaxSize(170, 130);

        // Texte
        Label name = new Label(product.name());
        name.getStyleClass().add("productName");

        Label price = new Label(String.format("%.2f €", product.price()).replace('.', ','));
        price.getStyleClass().add("productPrice");

        Button addBtn = new Button("Ajouter");
        addBtn.getStyleClass().add("addBtn");
        addBtn.setOnAction(e -> onAdd.accept(product));

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
