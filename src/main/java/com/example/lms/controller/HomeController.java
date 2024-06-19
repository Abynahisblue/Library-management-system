package com.example.lms.controller;


import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class HomeController {

    public AnchorPane root;
    public ImageView patron;
    public ImageView books;
    public ImageView issue;
    public ImageView bk_return;
    public ImageView bk_search;

    // Navigate to different windows based on the clicked icon
    public void navigate(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            String fxmlFile = "";

            switch (icon.getId()) {
                case "patron":
                    fxmlFile = "/View/MembersFormView.fxml";
                    break;
                case "books":
                    fxmlFile = "/View/BooksFormView.fxml";
                    break;
                case "issue":
                    fxmlFile = "/View/BookIssueFormView.fxml";
                    break;
                case "bk_return":
                    fxmlFile = "/View/BookReturnFormView.fxml";
                    break;
                case "bk_search":
                    fxmlFile = "/View/BookSearchFormView.fxml";
                    break;
            }

            if (!fxmlFile.isEmpty()) {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();
            }
        }
    }

    // Mouse hover animations for entering
    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            applyAnimation(icon, 1.2, Color.YELLOW);
        }
    }

    // Mouse hover animations for exiting
    public void playMouseExitAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            applyAnimation(icon, 1, null);
        }
    }

    // Helper method to apply animation
    private void applyAnimation(ImageView icon, double scale, Color color) {
        ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
        scaleT.setToX(scale);
        scaleT.setToY(scale);
        scaleT.play();

        if (color != null) {
            DropShadow glow = new DropShadow();
            glow.setColor(color);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        } else {
            icon.setEffect(null);
        }
    }
}
