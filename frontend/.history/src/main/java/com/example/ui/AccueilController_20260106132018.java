package com.example.ui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;

public class AccueilController {

    @FXML private ImageView bgImage1;
    @FXML private ImageView bgImage2;

    private List<Image> images;
    private int index = 0;
    private boolean showingFirst = true;

    // largeur visible (doit matcher ton fitWidth)
    private final double WIDTH = 1920;

    @FXML
    public void initialize() {
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
        bgImage2.setTranslateX(WIDTH); // hors écran à droite

        startSlide();
    }

    private void startSlide() {
        ImageView current = showingFirst ? bgImage1 : bgImage2;
        ImageView next    = showingFirst ? bgImage2 : bgImage1;

        // préparer l'image suivante
        int nextIndex = (index + 1) % images.size();
        next.setImage(images.get(nextIndex));

        // positionner la prochaine image à droite, hors écran
        next.setTranslateX(WIDTH);

        // animation : current sort à gauche, next entre depuis la droite
        TranslateTransition out = new TranslateTransition(Duration.seconds(0.8), current);
        out.setFromX(0);
        out.setToX(-WIDTH);

        TranslateTransition in = new TranslateTransition(Duration.seconds(0.8), next);
        in.setFromX(WIDTH);
        in.setToX(0);

        out.play();
        in.play();

        in.setOnFinished(e -> {
            // reset l'ancienne image pour la prochaine fois
            current.setTranslateX(0);

            // valider le changement
            index = nextIndex;
            showingFirst = !showingFirst;

            PauseTransition pause = new PauseTransition(Duration.seconds(2.0));
            pause.setOnFinished(ev -> startSlide());
            pause.play();
        });
    }
}
