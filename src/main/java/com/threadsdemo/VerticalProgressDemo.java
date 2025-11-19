package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Démonstration : Barres de progression verticales
 */
public class VerticalProgressDemo {
    private final VBox mainLayout;
    private final TextArea logArea;
    private ProgressBar bar1, bar2, bar3;
    private Label label1, label2, label3;
    private WorkerThread thread1, thread2, thread3;

    public VerticalProgressDemo() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Titre
        Label title = new Label("DEMONSTRATION : BARRES DE PROGRESSION VERTICALES");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Explication
        TextArea explanation = new TextArea(
            "CONCEPT : Progression verticale de trois threads\n\n" +
            "Trois threads travaillent independamment avec des vitesses differentes.\n" +
            "Les barres verticales progressent de bas en haut.\n\n" +
            "Thread 1 : Vitesse lente\n" +
            "Thread 2 : Vitesse moyenne\n" +
            "Thread 3 : Vitesse rapide\n\n" +
            "Observez comment chaque thread progresse a son propre rythme."
        );
        explanation.setPrefHeight(140);
        explanation.setWrapText(true);
        explanation.setEditable(false);
        explanation.setStyle("-fx-font-size: 13px; -fx-background-color: #fff3e0;");

        // Zone des barres
        HBox barsBox = new HBox(100);
        barsBox.setAlignment(Pos.CENTER);
        barsBox.setPadding(new Insets(50));
        barsBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #666; -fx-border-width: 4;");

        // Barre 1
        VBox bar1Box = createVerticalBar("THREAD 1\nLent", Color.RED);
        bar1 = (ProgressBar) ((StackPane)bar1Box.getChildren().get(1)).getChildren().get(0);
        label1 = (Label) bar1Box.getChildren().get(2);
        barsBox.getChildren().add(bar1Box);

        // Barre 2
        VBox bar2Box = createVerticalBar("THREAD 2\nMoyen", Color.BLUE);
        bar2 = (ProgressBar) ((StackPane)bar2Box.getChildren().get(1)).getChildren().get(0);
        label2 = (Label) bar2Box.getChildren().get(2);
        barsBox.getChildren().add(bar2Box);

        // Barre 3
        VBox bar3Box = createVerticalBar("THREAD 3\nRapide", Color.GREEN);
        bar3 = (ProgressBar) ((StackPane)bar3Box.getChildren().get(1)).getChildren().get(0);
        label3 = (Label) bar3Box.getChildren().get(2);
        barsBox.getChildren().add(bar3Box);

        // Boutons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        Button startButton = new Button("DEMARRER");
        startButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20 60; " +
            "-fx-background-color: #2196F3; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8;"
        );

        Button resetButton = new Button("REINITIALISER");
        resetButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20 60; " +
            "-fx-background-color: #FF9800; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8;"
        );

        buttonBox.getChildren().addAll(startButton, resetButton);

        // Journal
        Label logLabel = new Label("JOURNAL D'EXECUTION :");
        logLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1976D2;");
        logArea = new TextArea();
        logArea.setPrefHeight(200);  // BEAUCOUP PLUS GRAND!
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-control-inner-background: #f9f9f9;");

        // Events
        startButton.setOnAction(e -> startThreads());
        resetButton.setOnAction(e -> reset());

        mainLayout.getChildren().addAll(
            title, explanation, barsBox, buttonBox,
            new Separator(), logLabel, logArea
        );

        log("Pret a demarrer");
    }

    private VBox createVerticalBar(String name, Color color) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-text-alignment: center;");
        nameLabel.setTextFill(color);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // StackPane pour la rotation avec dimensions fixes
        StackPane rotationContainer = new StackPane();
        rotationContainer.setPrefSize(90, 550);  // TRES GRAND!
        rotationContainer.setMinSize(90, 550);
        rotationContainer.setMaxSize(90, 550);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(530);  // TRES LARGE!
        progressBar.setPrefHeight(80);   // TRES EPAIS!
        progressBar.setStyle("-fx-accent: " + toHex(color) + ";");
        progressBar.setRotate(-90);  // Rotation sur la barre elle-même

        rotationContainer.getChildren().add(progressBar);

        Label statusLabel = new Label("0%");
        statusLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        container.getChildren().addAll(nameLabel, rotationContainer, statusLabel);
        return container;
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    private void startThreads() {
        stopThreads();
        
        logArea.clear();
        log("Demarrage de trois threads...");

        thread1 = new WorkerThread(bar1, label1, 150, 1, "Lent");
        thread2 = new WorkerThread(bar2, label2, 80, 2, "Moyen");
        thread3 = new WorkerThread(bar3, label3, 40, 3, "Rapide");

        thread1.start();
        thread2.start();
        thread3.start();

        log("Thread 1 demarre (lent)");
        log("Thread 2 demarre (moyen)");
        log("Thread 3 demarre (rapide)");
    }

    private void stopThreads() {
        if (thread1 != null && thread1.isAlive()) thread1.stopRunning();
        if (thread2 != null && thread2.isAlive()) thread2.stopRunning();
        if (thread3 != null && thread3.isAlive()) thread3.stopRunning();
    }

    private void reset() {
        stopThreads();
        bar1.setProgress(0);
        bar2.setProgress(0);
        bar3.setProgress(0);
        label1.setText("0%");
        label2.setText("0%");
        label3.setText("0%");
        logArea.clear();
        log("Reinitialise");
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public VBox getView() {
        // Envelopper dans un ScrollPane pour tout le panneau
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        return wrapper;
    }

    private class WorkerThread extends Thread {
        private final ProgressBar progressBar;
        private final Label label;
        private final int delay;
        private final int id;
        private final String speed;
        private volatile boolean running = true;

        public WorkerThread(ProgressBar progressBar, Label label, int delay, int id, String speed) {
            this.progressBar = progressBar;
            this.label = label;
            this.delay = delay;
            this.id = id;
            this.speed = speed;
            setName("WorkerThread-" + id);
        }

        @Override
        public void run() {
            double progress = 0;
            while (running && progress < 1.0) {
                try {
                    Thread.sleep(delay);
                    progress += 0.02;
                    if (progress > 1.0) progress = 1.0;

                    final double finalProgress = progress;
                    Platform.runLater(() -> {
                        progressBar.setProgress(finalProgress);
                        label.setText(String.format("%.0f%%", finalProgress * 100));
                    });

                } catch (InterruptedException e) {
                    break;
                }
            }

            if (progress >= 1.0) {
                log("Thread " + id + " (" + speed + ") termine");
            }
        }

        public void stopRunning() {
            running = false;
            interrupt();
        }
    }
}
