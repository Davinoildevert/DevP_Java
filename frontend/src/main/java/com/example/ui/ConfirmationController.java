package com.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ConfirmationController {

    @FXML private StackPane root;

    @FXML private Label orderNumberLabel;
    @FXML private GridPane itemsGrid;
    @FXML private Label totalLabel;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    @FXML
    public void initialize() {
        // ✅ Numéro de commande (depuis ton service)
        int orderId = LastOrder.get();
        orderNumberLabel.setText("Commande n° " + orderId);


        // ✅ Colonnes GridPane : 1) nom à gauche, 2) prix à droite
        setupGridColumns();

        // ✅ Remplir la liste
        itemsGrid.getChildren().clear();

        int row = 0;
        for (CartService.CartItem it : LastOrderSummary.getItems()) {
            Label name = new Label(it.qty() + "x " + it.name());
            name.getStyleClass().add("itemName");

            double lineTotal = it.price() * it.qty();
            Label price = new Label(formatEuro(lineTotal));
            price.getStyleClass().add("itemPrice");

            itemsGrid.add(name, 0, row);
            itemsGrid.add(price, 1, row);

            // prix bien collé à droite
            GridPane.setHalignment(price, HPos.RIGHT);

            row++;
        }

        // ✅ Total global
        totalLabel.setText(formatEuro(LastOrderSummary.getTotal()));
    }

    private void setupGridColumns() {
        // évite d'ajouter 20 fois si initialize est rappelé
        if (!itemsGrid.getColumnConstraints().isEmpty()) return;

        ColumnConstraints colName = new ColumnConstraints();
        colName.setPercentWidth(80);

        ColumnConstraints colPrice = new ColumnConstraints();
        colPrice.setPercentWidth(20);
        colPrice.setHalignment(HPos.RIGHT);

        itemsGrid.getColumnConstraints().addAll(colName, colPrice);
    }

    private String formatEuro(double v) {
        return money.format(v) + " €"; // ex: 14,50 €
    }

    @FXML
    private void onNouvelleCommande() {
        LastOrderSummary.clear();
        CartService.clear();
        goTo("ui/accueil/accueil.fxml");

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
}
