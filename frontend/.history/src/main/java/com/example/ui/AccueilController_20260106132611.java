package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class AccueilController {

    @FXML private StackPane sliderPane;
    @FXML private ImageView bgImage1;
    @FXML private ImageView bgImage2;

    private List<Image> images;
    private int index = 0;
    private boolean showingFirst = true;

    @FXML
    public void initialize() {

        // ✅ Clip responsive (empêche le débordement hors écran)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(sliderPane.widthProperty());
        clip.heightProperty().bind(sliderPane.heightProperty());
        sliderPane.setClip(clip);

        images = List.of(
                new Image(getClass().getResource("/images/backgrounds/bg1.jpg").toExternalForm()),
                new Image(getClass().getResource("/images/backgrounds/bg2.jpg").toExternalForm()),
                new Image(getClass().getResource("/images/backgrounds/bg3.jpg").toExternalForm()),
                new Image(getClass().getResource("/images/backgrounds/bg4.jpg").toExternalForm()),
                new Image(getClass().getResource("/images/backgrounds/bg5.jpg").toExternalForm())
        );

        bgImage1.setImage(images.get(0));
        bgImage1.setTranslateX(0);

        bgImage2.setOpacity(1);
        bgImage2.setTranslateX(0);

        // ✅ lancer après que le layout ait une vraie largeur
        javafx.application.Platform.runLater(this::startSlide);
    }

    private void startSlide() {
        double width = sliderPane.getWidth();
        if (width <= 0) width = sliderPane.getPrefWidth(); // fallback

        ImageView current = showingFirst ? bgImage1 : bgImage2;
        ImageView next    = showingFirst ? bgImage2 : bgImage1;

        int nextIndex = (index + 1) % images.size();
        next.setImage(images.get(nextIndex));

        // positionner la prochaine image à droite
        next.setTranslateX(width);

        TranslateTransition out = new TranslateTransition(Duration.seconds(0.8), current);
        out.setFromX(0);
        out.setToX(-width);

        TranslateTransition in = new TranslateTransition(Duration.seconds(0.8), next);
        in.setFromX(width);
        in.setToX(0);

        out.play();
        in.play();

        in.setOnFinished(e -> {
            // reset l'ancienne image pour ne pas l'accumuler hors champ
            current.setTranslateX(0);

            index = nextIndex;
            showingFirst = !showingFirst;

            PauseTransition pause = new PauseTransition(Duration.seconds(2.0));
            pause.setOnFinished(ev -> startSlide());
            pause.play();
        });
    }
}
