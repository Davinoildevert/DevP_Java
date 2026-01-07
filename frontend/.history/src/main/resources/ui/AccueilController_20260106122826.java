package com.example.ui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;

public class AccueilController {

    @FXML
    private ImageView bgImage1;

    @FXML
    private ImageView bgImage2;

    private List<Image> images;
    private int index = 0;
    private boolean showingFirst = true;

    @FXML
    public void initialize() {

        images = List.of(
                new Image(getClass().getResource("/images/backgrounds/bg1.jpg").toExternalForm()),
                new Image(getClass().getResource("/images/backgrounds/bg2.jpg").toExternalForm()),
                new Image(getClass().getResource("/images/backgrounds/bg3.jpg").toExternalForm())
        );

        bgImage1.setImage(images.get(0));
        bgImage2.setOpacity(0);

        startSlideshow();
    }

    private void startSlideshow() {

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.2),
                showingFirst ? bgImage1 : bgImage2);

        FadeTransition fadeIn = ExplanationFadeIn();

        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            index = (index + 1) % images.size();

            ImageView next = showingFirst ? bgImage2 : bgImage1;
            next.setImage(images.get(index));

            fadeIn.play();
            showingFirst = !showingFirst;
        });

        fadeOut.play();
    }

    private FadeTransition ExplanationFadeIn() {
        ImageView target = showingFirst ? bgImage2 : bgImage1;

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), target);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeIn.setOnFinished(e -> {
            // délai avant image suivante
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(Duration.seconds(2.0));
            pause.setOnFinished(ev -> startSlideshow());
            pause.play();
        });

        return fadeIn;
    }
}
