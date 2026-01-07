package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

    // ===== Bottom buttons =====
    @FXML private Button btnFR;
    @FXML private Button btnEN;
    @FXML private Button btnAide;

    // ===== Slider state =====
    private List<Image> images;
    private int index = 0;
    private boolean showingA = true;

    // ===== Timing =====
    private static final Duration SLIDE_DURATION = Duration.seconds(1.2);
    private static final Duration PAUSE_DURATION = Duration.seconds(4.0);

    @FXML
    public void initialize() {

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

        // Etat initial : FR actif (jaune)
        setActive(btnFR);

        Platform.runLater(() -> {
            slideB.setTranslateX(safeWidth());
            startSlide();
        });
    }

    // ===== Button logic (active = jaune) =====
    private void setActive(Button activeBtn) {
        // enlever "active" de tous
        btnFR.getStyleClass().remove("active");
        btnEN.getStyleClass().remove("active");
        btnAide.getStyleClass().remove("active");

        // ajouter "active" au bouton sélectionné
        if (!activeBtn.getStyleClass().contains("active")) {
            activeBtn.getStyleClass().add("active");
        }
    }

    @FXML
    private void onFR(ActionEvent e) {
        setActive(btnFR);
        System.out.println("Langue : FR");
        // TODO: switch langue FR (ResourceBundle / i18n)
    }

    @FXML
    private void onEN(ActionEvent e) {
        setActive(btnEN);
        System.out.println("Langue : EN");
        // TODO: switch langue EN (ResourceBundle / i18n)
    }

    @FXML
    private void onAide(ActionEvent e) {
        setActive(btnAide);
        System.out.println("Aide");
        // TODO: ouvrir popup/écran aide
    }

 @FXML
private void onStart(ActionEvent e) {
    System.out.println("CLICK START ✅ (onStart appelé)");
    ((Button) e.getSource()).setText("CLIC OK"); // feedback direct à l'écran
}

        Parent root = loader.load();

        // On récupère la fenêtre actuelle à partir du bouton cliqué
        Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();

        // On change la scène
        stage.setScene(new Scene(root, 1920, 1080));
        // stage.setFullScreen(true); // optionnel si borne

    } catch (IOException ex) {
        ex.printStackTrace();
    }
}


    // ===== Slider helpers =====
    private void setupBlurLayer(ImageView iv) {
        iv.fitWidthProperty().bind(sliderPane.widthProperty());
        iv.fitHeightProperty().bind(sliderPane.heightProperty());
        iv.setPreserveRatio(false);  // cover (remplit)
        iv.setSmooth(true);
        iv.setEffect(new GaussianBlur(30));
        iv.setOpacity(0.55);
    }

    private void setupMainLayer(ImageView iv) {
        iv.fitWidthProperty().bind(sliderPane.widthProperty());
        iv.fitHeightProperty().bind(sliderPane.heightProperty());
        iv.setPreserveRatio(true);   // contain (on voit tout)
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

        // prochain slide à droite
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
