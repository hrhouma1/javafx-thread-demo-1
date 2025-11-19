package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Démonstration : Barres de progression horizontales
 */
public class HorizontalProgressDemo {
    private final VBox mainLayout;
    private final TextArea logArea;
    private ProgressBar bar1, bar2, bar3, bar4;
    private Label label1, label2, label3, label4;
    private WorkerThread thread1, thread2, thread3, thread4;

    public HorizontalProgressDemo() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Titre
        Label title = new Label("DEMONSTRATION : BARRES DE PROGRESSION HORIZONTALES");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Explication
        TextArea explanation = new TextArea(
            "CONCEPT : Quatre threads travaillant en parallele\n\n" +
            "Chaque barre represente un thread travaillant independamment.\n" +
            "Tous les threads progressent simultanement a des vitesses differentes.\n\n" +
            "Thread 1 : Tres lent (simulation tache lourde)\n" +
            "Thread 2 : Lent\n" +
            "Thread 3 : Rapide\n" +
            "Thread 4 : Tres rapide (simulation tache legere)\n\n" +
            "Ce type de visualisation est utilise dans les gestionnaires de telechargement."
        );
        explanation.setPrefHeight(150);
        explanation.setWrapText(true);
        explanation.setEditable(false);
        explanation.setStyle("-fx-font-size: 13px; -fx-background-color: #e3f2fd;");

        // Zone des barres
        VBox barsBox = new VBox(25);
        barsBox.setPadding(new Insets(30));
        barsBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #666; -fx-border-width: 2;");

        // Barre 1
        VBox bar1Box = createHorizontalBar("THREAD 1 - Tres Lent", Color.RED);
        bar1 = (ProgressBar) bar1Box.getChildren().get(1);
        label1 = (Label) ((HBox)bar1Box.getChildren().get(2)).getChildren().get(0);
        barsBox.getChildren().add(bar1Box);

        // Barre 2
        VBox bar2Box = createHorizontalBar("THREAD 2 - Lent", Color.ORANGE);
        bar2 = (ProgressBar) bar2Box.getChildren().get(1);
        label2 = (Label) ((HBox)bar2Box.getChildren().get(2)).getChildren().get(0);
        barsBox.getChildren().add(bar2Box);

        // Barre 3
        VBox bar3Box = createHorizontalBar("THREAD 3 - Rapide", Color.BLUE);
        bar3 = (ProgressBar) bar3Box.getChildren().get(1);
        label3 = (Label) ((HBox)bar3Box.getChildren().get(2)).getChildren().get(0);
        barsBox.getChildren().add(bar3Box);

        // Barre 4
        VBox bar4Box = createHorizontalBar("THREAD 4 - Tres Rapide", Color.GREEN);
        bar4 = (ProgressBar) bar4Box.getChildren().get(1);
        label4 = (Label) ((HBox)bar4Box.getChildren().get(2)).getChildren().get(0);
        barsBox.getChildren().add(bar4Box);

        // Boutons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        Button startButton = new Button("DEMARRER TOUS LES THREADS");
        startButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20 50; " +
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8;"
        );

        Button resetButton = new Button("REINITIALISER");
        resetButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20 50; " +
            "-fx-background-color: #FF5722; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8;"
        );

        buttonBox.getChildren().addAll(startButton, resetButton);

        // Journal
        Label logLabel = new Label("JOURNAL D'EXECUTION :");
        logLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        logArea = new TextArea();
        logArea.setPrefHeight(100);
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: monospace;");

        // Events
        startButton.setOnAction(e -> startThreads());
        resetButton.setOnAction(e -> reset());

        mainLayout.getChildren().addAll(
            title, explanation, barsBox, buttonBox,
            new Separator(), logLabel, logArea
        );

        log("Pret a demarrer - Cliquez sur DEMARRER");
    }

    private VBox createHorizontalBar(String name, Color color) {
        VBox container = new VBox(8);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: white; -fx-border-color: " + toHex(color) + "; -fx-border-width: 2;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setTextFill(color);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(700);
        progressBar.setPrefHeight(35);
        progressBar.setStyle("-fx-accent: " + toHex(color) + ";");

        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("0%");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label stateLabel = new Label("En attente");
        stateLabel.setStyle("-fx-font-size: 12px;");
        statusBox.getChildren().addAll(statusLabel, new Label("-"), stateLabel);

        container.getChildren().addAll(nameLabel, progressBar, statusBox);
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
        log("=== Demarrage de 4 threads en parallele ===");

        thread1 = new WorkerThread(bar1, label1, 200, 1, "Tres lent");
        thread2 = new WorkerThread(bar2, label2, 120, 2, "Lent");
        thread3 = new WorkerThread(bar3, label3, 60, 3, "Rapide");
        thread4 = new WorkerThread(bar4, label4, 30, 4, "Tres rapide");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        log("Thread 1 demarre (tres lent)");
        log("Thread 2 demarre (lent)");
        log("Thread 3 demarre (rapide)");
        log("Thread 4 demarre (tres rapide)");
        log("=== Tous les threads s'executent en parallele ===");
    }

    private void stopThreads() {
        if (thread1 != null && thread1.isAlive()) thread1.stopRunning();
        if (thread2 != null && thread2.isAlive()) thread2.stopRunning();
        if (thread3 != null && thread3.isAlive()) thread3.stopRunning();
        if (thread4 != null && thread4.isAlive()) thread4.stopRunning();
    }

    private void reset() {
        stopThreads();
        bar1.setProgress(0);
        bar2.setProgress(0);
        bar3.setProgress(0);
        bar4.setProgress(0);
        label1.setText("0%");
        label2.setText("0%");
        label3.setText("0%");
        label4.setText("0%");
        
        // Reset state labels
        ((Label)((HBox)((VBox)bar1.getParent()).getChildren().get(2)).getChildren().get(2)).setText("En attente");
        ((Label)((HBox)((VBox)bar2.getParent()).getChildren().get(2)).getChildren().get(2)).setText("En attente");
        ((Label)((HBox)((VBox)bar3.getParent()).getChildren().get(2)).getChildren().get(2)).setText("En attente");
        ((Label)((HBox)((VBox)bar4.getParent()).getChildren().get(2)).getChildren().get(2)).setText("En attente");
        
        logArea.clear();
        log("Reinitialise - Tous les threads arretes");
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public VBox getView() {
        return mainLayout;
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
            // Update state label
            Platform.runLater(() -> {
                VBox parent = (VBox) progressBar.getParent();
                HBox statusBox = (HBox) parent.getChildren().get(2);
                Label stateLabel = (Label) statusBox.getChildren().get(2);
                stateLabel.setText("En cours...");
                stateLabel.setTextFill(Color.GREEN);
            });

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
                Platform.runLater(() -> {
                    VBox parent = (VBox) progressBar.getParent();
                    HBox statusBox = (HBox) parent.getChildren().get(2);
                    Label stateLabel = (Label) statusBox.getChildren().get(2);
                    stateLabel.setText("TERMINE");
                    stateLabel.setTextFill(Color.BLUE);
                });
                log("Thread " + id + " (" + speed + ") a termine son travail");
            }
        }

        public void stopRunning() {
            running = false;
            interrupt();
        }
    }
}
