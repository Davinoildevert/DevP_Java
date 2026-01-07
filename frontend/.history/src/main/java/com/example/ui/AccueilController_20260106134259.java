package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;

public class AccueilController {

    @FXML private StackPane sliderPane;
    @FXML private ImageView bgImage1;
    @FXML private ImageView bgImage2;

    private List<Image> images;
    private int index = 0;
    private boolean showingFirst = true;

    // Durées (modifiables)
    private static final Duration SLIDE_DURATION = Duration.seconds(0.8);
    private static final Duration PAUSE_DURATION = Duration.seconds(2.0);

    @FXML
    public void initialize() {

        // ✅ Clip responsive : empêche de voir les images hors écran pendant le slide
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(sliderPane.widthProperty());
        clip.heightProperty().bind(sliderPane.heightProperty());
        sliderPane.setClip(clip);

        // ✅ Les ImageView prennent toujours la taille du sliderPane
        bgImage1.fitWidthProperty().bind(sliderPane.widthProperty());
        bgImage1.fitHeightProperty().bind(sliderPane.heightProperty());
        bgImage2.fitWidthProperty().bind(sliderPane.widthProperty());
        bgImage2.fitHeightProperty().bind(sliderPane.heightProperty());

        // (important) : preserveRatio est dans ton FXML -> pas de déformation

        // ✅ Charger les images (avec check null pour éviter NPE si un fichier manque)
        images = List.of(
                load("/images/backgrounds/bg1.jpg"),
                load("/images/backgrounds/bg2.jpg"),
                load("/images/backgrounds/bg3.jpg"),
                load("/images/backgrounds/bg4.jpg"),
                load("/images/backgrounds/bg5.jpg")
        );

        // ✅ état initial
        index = 0;
        showingFirst = true;

        bgImage1.setImage(images.get(0));
        bgImage1.setTranslateX(0);

        bgImage2.setOpacity(1);

        // ✅ Un seul runLater : attendre que le layout calcule la largeur réelle
        Platform.runLater(() -> {
            double width = safeWidth();
            bgImage2.setTranslateX(width); // hors écran à droite
            startSlide();
        });
    }

    private Image load(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Image introuvable: " + path);
        }
        return new Image(url.toExternalForm());
    }

    private double safeWidth() {
        double width = sliderPane.getWidth();
        // ✅ Bonus : fallback sûr si width pas prêt
        if (width <= 0) width = 1920;
        return width;
    }

    private void startSlide() {
        double width = safeWidth();

        ImageView current = showingFirst ? bgImage1 : bgImage2;
        ImageView next    = showingFirst ? bgImage2 : bgImage1;

        int nextIndex = (index + 1) % images.size();

        // ✅ préparer l’image suivante AVANT l’animation
        next.setImage(images.get(nextIndex));

        // ✅ positionner next à droite, hors écran
        next.setTranslateX(width);

        // animation : current sort à gauche, next entre depuis la droite
        TranslateTransition out = new TranslateTransition(SLIDE_DURATION, current);
        out.setFromX(0);
        out.setToX(-width);

        TranslateTransition in = new TranslateTransition(SLIDE_DURATION, next);
        in.setFromX(width);
        in.setToX(0);

        out.play();
        in.play();

        in.setOnFinished(e -> {
            // reset l’ancienne image (pour éviter accumulation)
            current.setTranslateX(0);

            // valider le changement
            index = nextIndex;
            showingFirst = !showingFirst;

            PauseTransition pause = new PauseTransition(PAUSE_DURATION);
            pause.setOnFinished(ev -> startSlide());
            pause.play();
        });
    }
}
