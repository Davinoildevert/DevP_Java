package com.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ConfirmationController {

    @FXML private StackPane root;

    @FXML private Label orderNumberLabel;
    @FXML private GridPane itemsGrid;
    @FXML private Label totalLabel;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    @FXML
    public void initialize() {
        // Numéro de commande (exemple)
        int orderNo = ThreadLocalRandom.current().nextInt(1000, 9999);
        orderNumberLabel.setText("Commande N° #" + orderNo);

        // Remplir récap (depuis CartService)
        itemsGrid.getChildren().clear();

        int row = 0;
        for (CartService.Item it : CartService.items()) {
            Label name = new Label(it.qty() + "x " + it.name());
            name.getStyleClass().add("itemName");

            Label price = new Label(formatEuro(it.totalPrice()));
            price.getStyleClass().add("itemPrice");

            // col 0 = name, col 1 = price
            itemsGrid.add(name, 0, row);
            itemsGrid.add(price, 1, row);

            // aligner la colonne prix à droite via style
            GridPane.setFillWidth(price, false);

            row++;
        }

        totalLabel.setText(formatEuro(CartService.total()));
    }

    private String formatEuro(double v) {
        // "14,50 €" comme la maquette
        return money.format(v) + " €";
    }

    @FXML
    private void onNouvelleCommande() {
        // reset panier + retour catalogue (ou accueil selon ton besoin)
        CartService.clear();
        goTo("ui/catalogue/catalogue.fxml"); // adapte le chemin si besoin
    }

    private void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
            Parent newRoot = loader.load();

            // CSS de la page
            newRoot.getStylesheets().add(
                    getClass().getResource("/ui/confirmation/confirmation.css").toExternalForm()
            );

            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(newRoot);

        } catch (IOException e) {
            System.out.println("❌ Navigation impossible vers " + fxmlPath);
            e.printStackTrace();
        }
    }
}
