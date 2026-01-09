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

        // ✅ Numéro de commande venant du BACKEND (LastOrder)
        int orderId = LastOrder.get();
        if (orderNumberLabel != null) {
            // texte libre : adapte si ton FXML met déjà "Commande N°"
            orderNumberLabel.setText("Commande N° #" + orderId);
        }

        // ✅ Colonnes GridPane : 1) nom à gauche, 2) prix à droite
        setupGridColumns();

        // ✅ Remplir la liste (depuis LastOrderSummary capturé avant clear)
        if (itemsGrid != null) itemsGrid.getChildren().clear();

        int row = 0;
        for (CartService.CartItem it : LastOrderSummary.getItems()) {
            Label name = new Label(it.qty() + "x " + it.name());
            name.getStyleClass().add("itemName");

            double lineTotal = it.price() * it.qty();
            Label price = new Label(formatEuro(lineTotal));
            price.getStyleClass().add("itemPrice");

            itemsGrid.add(name, 0, row);
            itemsGrid.add(price, 1, row);

            GridPane.setHalignment(price, HPos.RIGHT);
            row++;
        }

        // ✅ Total global
        if (totalLabel != null) {
            totalLabel.setText(formatEuro(LastOrderSummary.getTotal()));
        }
    }

    private void setupGridColumns() {
        if (itemsGrid == null) return;

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
        return money.format(v) + " €";
    }

    @FXML
    private void onNouvelleCommande() {
        // ✅ reset propre
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
