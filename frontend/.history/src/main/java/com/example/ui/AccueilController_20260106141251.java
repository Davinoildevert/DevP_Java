package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;

public class AccueilController {

    @FXML private StackPane sliderPane;

    @FXML private StackPane slideA;
    @FXML private ImageView bgBlurA;
    @FXML private ImageView bgMainA;

    @FXML private StackPane slideB;
    @FXML private ImageView bgBlurB;
    @FXML private ImageView bgMainB;

    private List<Image> images;
    private int index = 0;
    private boolean showingA = true;

    private static final Duration SLIDE_DURATION = Duration.seconds(1.2);
    private static final Duration PAUSE_DURATION = Duration.seconds(4.0);

    // ---------- Boutons bas ----------
    @FXML
    private void onFR(ActionEvent e) {
        System.out.println("FR");
    }

    @FXML
    private void onEN(ActionEvent e) {
        System.out.println("EN");
    }

    @FXML
    private void onAide(ActionEvent e) {
        System.out.println("Aide");
    }

    @FXML
    public void initialize() {

        // Clip
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

        images = List.of(
                load("/images/backgrounds/bg1.jpg"),
                load("/images/backgrounds/bg4.jpg"),
                load("/images/backgrounds/bg5.jpg")
        );

        setSlideImage(bgBlurA, bgMainA, images.get(0));

        Platform.runLater(() -> {
            slideB.setTranslateX(safeWidth());
            startSlide();
        });
    }

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
