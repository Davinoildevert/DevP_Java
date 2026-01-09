package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AccueilController {

    // ===== Slider nodes =====
    @FXML private StackPane sliderPane;

    @FXML private StackPane slideA;
    @FXML private ImageView bgBlurA;
    @FXML private ImageView bgMainA;

    @FXML private StackPane slideB;
    @FXML private ImageView bgBlurB;
    @FXML private ImageView bgMainB;

    // ===== Overlay info (V2) =====
    @FXML private StackPane infoOverlay;
    @FXML private Label infoTitleLabel;
    @FXML private Label infoMessageLabel;

    // ===== Bottom buttons =====
    @FXML private Button btnFR;
    @FXML private Button btnEN;
    @FXML private Button btnAide;

    @FXML private Button startBtn;

    // ===== Slider state =====
    private List<Image> images;
    private int index = 0;
    private boolean showingA = true;

    // ===== Timing =====
    private static final Duration SLIDE_DURATION = Duration.seconds(1.2);
    private static final Duration PAUSE_DURATION = Duration.seconds(4.0);

    @FXML
    public void initialize() {

        System.out.println("✅ AccueilController OK - startBtn=" + startBtn);

        if (startBtn != null) {
            startBtn.setOnAction(this::onStart);
        }

        if (sliderPane != null) {
            sliderPane.toBack();
        }

        // Clip (rien ne dépasse)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(sliderPane.widthProperty());
        clip.heightProperty().bind(sliderPane.heightProperty());
        sliderPane.setClip(clip);

        // Layers
        setupBlurLayer(bgBlurA);
        setupBlurLayer(bgBlurB);
        setupMainLayer(bgMainA);
        setupMainLayer(bgMainB);

        // Position initiale (anti-flash)
        slideA.setTranslateX(0);
        slideB.setTranslateX(1920);

        // Images
        images = List.of(
                load("/images/backgrounds/bg1.jpg"),
                load("/images/backgrounds/bg4.jpg"),
                load("/images/backgrounds/bg5.jpg")
        );

        // Première image
        setSlideImage(bgBlurA, bgMainA, images.get(0));

        // Etat initial : FR actif
        setActive(btnFR);

        Platform.runLater(() -> {
            slideB.setTranslateX(safeWidth());
            startSlide();
        });

        // ✅ V2: clic sur le fond pour fermer l’overlay
        if (infoOverlay != null) {
            infoOverlay.setVisible(false);
            infoOverlay.setOnMouseClicked(ev -> {
                if (ev.getTarget() == infoOverlay) infoOverlay.setVisible(false);
            });
        }
    }

    // ===== Button logic (active = jaune) =====
    private void setActive(Button activeBtn) {
        btnFR.getStyleClass().remove("active");
        btnEN.getStyleClass().remove("active");
        btnAide.getStyleClass().remove("active");

        if (!activeBtn.getStyleClass().contains("active")) {
            activeBtn.getStyleClass().add("active");
        }
    }

    @FXML
    private void onFR(ActionEvent e) {
        setActive(btnFR);
        System.out.println("Langue : FR");
    }

    // ✅ V2: EN affiche overlay info
    @FXML
    private void onEN(ActionEvent e) {
        setActive(btnEN);
        showInfo("Indisponible", "Désolé, la version anglaise n'est pas encore disponible.");
    }

    // ✅ V2: Aide affiche overlay info
    @FXML
    private void onAide(ActionEvent e) {
        setActive(btnAide);
        showInfo("Aide", "Cette fonctionnalité sera bientôt disponible. Merci de votre compréhension.");
    }

    private void showInfo(String title, String message) {
        if (infoTitleLabel != null) infoTitleLabel.setText(title);
        if (infoMessageLabel != null) infoMessageLabel.setText(message);
        if (infoOverlay != null) infoOverlay.setVisible(true);
    }

    // si ton FXML a un bouton fermer (fx:id + onAction="#onCloseInfo")
    @FXML
    private void onCloseInfo(ActionEvent e) {
        if (infoOverlay != null) infoOverlay.setVisible(false);
        setActive(btnFR); // optionnel
    }

    @FXML
    private void onStart(ActionEvent e) {
        System.out.println("CLICK START ✅");

        URL url = getClass().getResource("/ui/catalogue/catalogue.fxml");
        System.out.println("URL catalogue.fxml = " + url);

        if (url == null) {
            System.out.println("❌ catalogue.fxml introuvable. Vérifie son chemin dans src/main/resources");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent newRoot = loader.load();

            Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(newRoot);

            System.out.println("✅ Navigation vers catalogue OK");
        } catch (IOException ex) {
            System.out.println("❌ Erreur au chargement de catalogue.fxml : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ===== Slider helpers =====
    private void setupBlurLayer(ImageView iv) {
        iv.fitWidthProperty().bind(sliderPane.widthProperty());
        iv.fitHeightProperty().bind(sliderPane.heightProperty());
        iv.setPreserveRatio(false);
        iv.setSmooth(true);
        iv.setEffect(new GaussianBlur(30));
        iv.setOpacity(0.55);
    }

    private void setupMainLayer(ImageView iv) {
        iv.fitWidthProperty().bind(sliderPane.widthProperty());
        iv.fitHeightProperty().bind(sliderPane.heightProperty());
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
    }

    private void setSlideImage(ImageView blur, ImageView main, Image img) {
        blur.setImage(img);
        main.setImage(img);
    }

    private Image load(String path) {
        URL url = getClass().getResource(path);
        if (url == null) throw new IllegalStateException("Image introuvable: " + path);
        return new Image(url.toExternalForm());
    }

    private double safeWidth() {
        double w = sliderPane.getWidth();
        return (w > 0) ? w : 1920;
    }

    private void startSlide() {
        double width = safeWidth();

        StackPane currentSlide = showingA ? slideA : slideB;
        StackPane nextSlide    = showingA ? slideB : slideA;

        ImageView nextBlur = showingA ? bgBlurB : bgBlurA;
        ImageView nextMain = showingA ? bgMainB : bgMainA;

        int nextIndex = (index + 1) % images.size();
        setSlideImage(nextBlur, nextMain, images.get(nextIndex));

        nextSlide.setTranslateX(width);

        TranslateTransition out = new TranslateTransition(SLIDE_DURATION, currentSlide);
        out.setFromX(0);
        out.setToX(-width);

        TranslateTransition in = new TranslateTransition(SLIDE_DURATION, nextSlide);
        in.setFromX(width);
        in.setToX(0);

        out.play();
        in.play();

        in.setOnFinished(e -> {
            currentSlide.setTranslateX(0);

            index = nextIndex;
            showingA = !showingA;

            PauseTransition pause = new PauseTransition(PAUSE_DURATION);
            pause.setOnFinished(ev -> startSlide());
            pause.play();
        });
    }
}
