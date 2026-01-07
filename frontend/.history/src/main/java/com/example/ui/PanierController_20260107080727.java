package com.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PanierController {

    @FXML private BorderPane root;

    @FXML private VBox itemsBox;

    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    @FXML private TextField clientField;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    private static final double TAX_RATE = 0.15;

    @FXML
    public void initialize() {
        renderItemsAndTotals();
    }

    private void renderItemsAndTotals() {
        itemsBox.getChildren().clear();

        for (CartService.CartItem it : CartService.getItems()) {
            // Une "carte" ligne comme sur la maquette
            GridPane row = new GridPane();
            row.setHgap(10);
            row.setVgap(4);
            row.getColumnConstraints().addAll(
                    col(55), col(15), col(15), col(15)
            );

            Label article = new Label(it.name());
            article.setStyle("-fx-font-size:18px; -fx-font-weight:800;");

            Label qty = new Label(String.valueOf(it.qty()));
            qty.setStyle("-fx-font-size:18px;");

            Label price = new Label(formatEuro(it.price()));
            price.setStyle("-fx-font-size:18px;");

            double lineTotal = it.price() * it.qty();
            Label total = new Label(formatEuro(lineTotal));
            total.setStyle("-fx-font-size:18px; -fx-font-weight:800;");

            row.add(article, 0, 0);
            row.add(qty,     1, 0);
            row.add(price,   2, 0);
            row.add(total,   3, 0);

            VBox card = new VBox(row);
            card.setStyle("""
                    -fx-background-color: #f6f6f6;
                    -fx-background-radius: 10;
                    -fx-padding: 16;
                    -fx-border-color: #cfcfcf;
                    -fx-border-radius: 10;
                    """);

            itemsBox.getChildren().add(card);
        }

        double subtotal = CartService.total();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText(formatEuro(subtotal));
        taxLabel.setText(formatEuro(tax));
        totalLabel.setText(formatEuro(total));
    }

    private javafx.scene.layout.ColumnConstraints col(double percent) {
        javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
        c.setPercentWidth(percent);
        return c;
    }

    private String formatEuro(double v) {
        return money.format(v) + " €";
    }

    @FXML
    private void onModifier() {
        // ✅ Retour page précédente (Catalogue ou Détail)
        Stage stage = (Stage) root.getScene().getWindow();
        NavService.back(stage, "ui/catalogue/catalogue.fxml"); // fallback
    }

    @FXML
    private void onConfirmer() {
        // Ici tu vas vers Confirmation (ta page confirmation.fxml)
        Stage stage = (Stage) root.getScene().getWindow();
        NavService.goTo(stage, "ui/confirmation/confirmation.fxml");
    }
}
