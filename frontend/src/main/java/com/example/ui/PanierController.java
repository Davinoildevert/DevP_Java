package com.example.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.example.config.AppConfig;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PanierController {

    @FXML private StackPane root;
    @FXML private ImageView bgImage;
    @FXML private StackPane confirmOverlay;


    @FXML private VBox itemsBox;

    @FXML private Label subTotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    @FXML private TextField tableClientField;

    private final DecimalFormat money = new DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(Locale.FRANCE));

    // ===== Background slideshow =====
    private final List<Image> backgrounds = new ArrayList<>();
    private int bgIndex = 0;
    private Timeline bgTimeline;

    private static final double TAX_RATE = 0.15;

    @FXML
    public void initialize() {
        renderItemsAndTotals();
        initBackgroundSlideshow();
        if (confirmOverlay != null) confirmOverlay.setVisible(false);
        CartService.debugPrint();

    }

    private void renderItemsAndTotals() {
        if (itemsBox != null) itemsBox.getChildren().clear();

        for (CartService.CartItem it : CartService.getItems()) {
            // ligne item (style "card")
            HBox row = new HBox(10);
            row.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 14;
                -fx-padding: 14;
                -fx-border-color: rgba(0,0,0,0.10);
                -fx-border-radius: 14;
            """);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(6);

            grid.getColumnConstraints().addAll(
                    colPct(55), colPct(15), colPct(15), colPct(15)
            );

            Label name = new Label(it.name());
            name.setStyle("-fx-font-size: 18px; -fx-font-weight: 900;");

            Label qty = new Label(String.valueOf(it.qty()));
            qty.setStyle("-fx-font-size: 16px;");

            Label unit = new Label(formatEuro(it.price()));
            unit.setStyle("-fx-font-size: 16px;");

            double lineTotal = it.price() * it.qty();
            Label total = new Label(formatEuro(lineTotal));
            total.setStyle("-fx-font-size: 16px; -fx-font-weight: 900;");

            GridPane.setConstraints(name, 0, 0);
            GridPane.setConstraints(qty, 1, 0);
            GridPane.setConstraints(unit, 2, 0);
            GridPane.setConstraints(total, 3, 0);

            grid.getChildren().addAll(name, qty, unit, total);
            row.getChildren().add(grid);

            if (itemsBox != null) itemsBox.getChildren().add(row);
        }

        double sub = CartService.total();
        double tax = sub * TAX_RATE;
        double total = sub + tax;

        if (subTotalLabel != null) subTotalLabel.setText(formatEuro(sub));
        if (taxLabel != null) taxLabel.setText(formatEuro(tax));
        if (totalLabel != null) totalLabel.setText(formatEuro(total));
    }

    private javafx.scene.layout.ColumnConstraints colPct(double pct) {
        javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
        c.setPercentWidth(pct);
        return c;
    }

    private String formatEuro(double v) {
        return money.format(v) + " €";
    }

    // ===== Boutons =====
    @FXML
    private void onModifier() {
        // ✅ retourne à la page précédente (catalogue) ou détail selon ton flow
        goTo("ui/catalogue/catalogue.fxml");
    }

 @FXML
private void onConfirmer() {
    confirmOverlay.setVisible(true);
}

@FXML
private void onCancelConfirm() {
    confirmOverlay.setVisible(false);
}

@FXML

private void onValidateConfirm() {
    confirmOverlay.setVisible(false);

    try {
        int orderId;

        // 🔀 selon le mode (mock ou api)
        if (AppConfig.isApiMode()) {
            orderId = ApiOrderService.sendOrder(
                    tableClientField.getText(),
                    CartService.getItems(),
                    0.15
            );
        } else {
            // mode mock → ancien comportement
            orderId = LastOrder.generateMockOrderId();

        }

        LastOrderSummary.capture(); // 👈 capture AVANT clear
        LastOrder.set(orderId);
        CartService.clear();
        goTo("ui/confirmation/confirmation.fxml");


    } catch (Exception e) {
        e.printStackTrace();

    }
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

    // ===== Background slideshow =====
    private void initBackgroundSlideshow() {
        if (bgImage == null) return;

        loadBg("/images/backgrounds/bg1.jpg");
        loadBg("/images/backgrounds/bg4.jpg");
        loadBg("/images/backgrounds/bg5.jpg");

        if (backgrounds.isEmpty()) {
            System.out.println("⚠️ Aucune image de fond chargée (Panier)");
            return;
        }

        bgImage.setOpacity(0.22);
        bgImage.setImage(backgrounds.get(0));

        bgTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> switchBackground()));
        bgTimeline.setCycleCount(Timeline.INDEFINITE);
        bgTimeline.play();
    }

    private void loadBg(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            System.out.println("❌ Image introuvable: " + path);
            return;
        }
        backgrounds.add(new Image(url.toExternalForm()));
    }

    private void switchBackground() {
        if (backgrounds.isEmpty() || bgImage == null) return;

        bgIndex = (bgIndex + 1) % backgrounds.size();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(450), bgImage);
        fadeOut.setFromValue(0.22);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(ev -> {
            bgImage.setImage(backgrounds.get(bgIndex));

            FadeTransition fadeIn = new FadeTransition(Duration.millis(450), bgImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(0.22);
            fadeIn.play();
        });

        fadeOut.play();
    }


}
