package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PaiementController {

    @FXML private StackPane root;
    @FXML private ImageView bgImage;

    @FXML private Label amountLabel;
    @FXML private Label statusLabel;

    @FXML private RadioButton cardRadio;
    @FXML private RadioButton nfcRadio;

    @FXML private VBox cardBox;
    @FXML private VBox nfcBox;

    @FXML private TextField cardNumberField;
    @FXML private TextField expField;
    @FXML private TextField cvvField;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    private static final double TAX_RATE = 0.15;

    @FXML
    public void initialize() {

        // ✅ Afficher le total (à partir du panier, puisque panier pas encore clear)
        double sub = CartService.total();
        double tax = sub * TAX_RATE;
        double total = sub + tax;
        if (amountLabel != null) amountLabel.setText(formatEuro(total));

        // ✅ Si jamais on arrive ici sans orderId (erreur de flow)
        // On ne casse pas : on laisse continuer en simulation.
        int orderId = LastOrder.get(); // doit exister chez toi (sinon je te donne alternative)
        if (orderId <= 0) {
            System.out.println("⚠️ Paiement: aucun orderId (LastOrder.get() <= 0). Flow à vérifier.");
        }

        // ToggleGroup
        ToggleGroup group = new ToggleGroup();
        if (cardRadio != null) cardRadio.setToggleGroup(group);
        if (nfcRadio != null) nfcRadio.setToggleGroup(group);

        // Par défaut carte
        if (cardRadio != null) cardRadio.setSelected(true);
        showCard(true);

        group.selectedToggleProperty().addListener((obs, old, val) -> {
            showCard(val == cardRadio);
            if (statusLabel != null) statusLabel.setText("");
        });

        // Fond (optionnel)
        loadBgIfExists("/images/backgrounds/bg1.jpg");
    }
    private final HttpClient http = HttpClient.newHttpClient();

    private String last4(String num) {
        String n = safe(num).replace(" ", "");
        if (n.length() < 4) return "";
        return n.substring(n.length() - 4);
    }

    private void showCard(boolean card) {
        if (cardBox != null) {
            cardBox.setVisible(card);
            cardBox.setManaged(card);
        }
        if (nfcBox != null) {
            nfcBox.setVisible(!card);
            nfcBox.setManaged(!card);
        }
    }

    @FXML
    private void onRetour() {
        goTo("ui/panier/panier.fxml");
    }

    @FXML
    private void onPayer() {
        if (statusLabel != null) statusLabel.setText("");


        String method = (cardRadio != null && cardRadio.isSelected()) ? "CARD" : "NFC";

        // Validation légère si carte
        String cardLast4 = "";
        if ("CARD".equals(method)) {
            String num = safe(cardNumberField.getText()).replace(" ", "");
            String exp = safe(expField.getText());
            String cvv = safe(cvvField.getText());

            if (num.length() < 12) { showBigStatus("CARTE INVALIDE", false); return; }
            if (!exp.matches("\\d{2}/\\d{2}")) { showBigStatus("DATE INVALIDE", false); return; }
            if (!cvv.matches("\\d{3}")) { showBigStatus("CVV INVALIDE", false); return; }

            cardLast4 = last4(num);
        }

        showBigStatus("PAIEMENT EN COURS...", true);

        int orderId = LastOrder.get();
        if (orderId <= 0) {
            showBigStatus("ERREUR: AUCUNE COMMANDE", false);
            return;
        }

        // JSON request (simple)
        String json = "{"
                + "\"orderId\":" + orderId + ","
                + "\"method\":\"" + method + "\""
                + (cardLast4.isBlank() ? "" : ",\"cardLast4\":\"" + cardLast4 + "\"")
                + "}";

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7070/api/paiement"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(res -> javafx.application.Platform.runLater(() -> {
                        if (res.statusCode() == 200) {
                            showBigStatus("PAIEMENT ACCEPTÉ ✅", true);

                            PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
                            pause.setOnFinished(e -> {
                                CartService.clear();
                                goTo("ui/confirmation/confirmation.fxml");
                            });
                            pause.play();

                        } else {
                            // refus / erreur
                            showBigStatus("PAIEMENT REFUSÉ ❌", false);
                        }
                    }))
                    .exceptionally(ex -> {
                        javafx.application.Platform.runLater(() -> showBigStatus("ERREUR RÉSEAU ❌", false));
                        return null;
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
            showBigStatus("ERREUR ❌", false);
        }
    }
    private void showBigStatus(String msg, boolean ok) {
        if (statusLabel == null) return;

        statusLabel.setText(msg);
        statusLabel.setStyle(
                "-fx-font-size: 36px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-padding: 18 22;" +
                        "-fx-background-radius: 18;" +
                        "-fx-text-fill: white;" +
                        (ok
                                ? "-fx-background-color: rgba(46, 204, 113, 0.85);"   // vert
                                : "-fx-background-color: rgba(231, 76, 60, 0.85);"    // rouge
                        )
        );
    }


    private String safe(String s) { return s == null ? "" : s.trim(); }

    private String formatEuro(double v) {
        return money.format(v) + " €";
    }

    private void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
            Parent newRoot = loader.load();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(newRoot);
        } catch (IOException e) {
            System.out.println("❌ Navigation impossible vers /" + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadBgIfExists(String path) {
        if (bgImage == null) return;
        var url = getClass().getResource(path);
        if (url != null) {
            bgImage.setOpacity(0.22);
            bgImage.setImage(new Image(url.toExternalForm()));
        }
    }
}
