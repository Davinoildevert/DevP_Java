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

        ImageView current = showingFirst ? bgImage1 : bgImage2;
        ImageView next = showingFirst ? bgImage2 : bgImage1;

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.2), current);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), next);

        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeOut.setOnFinished(e -> {
            index = (index + 1) % images.size();
            next.setImage(images.get(index));
            fadeIn.play();
            showingFirst = !showingFirst;
        });

        fadeIn.setOnFinished(e -> {
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> startSlideshow());
            pause.play();
        });

        fadeOut.play();
    }
}
