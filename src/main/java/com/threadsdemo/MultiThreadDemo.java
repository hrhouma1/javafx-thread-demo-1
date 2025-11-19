package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Leçon 2: Plusieurs threads en parallèle
 * Montre que plusieurs threads s'exécutent en même temps avec barres visuelles
 */
public class MultiThreadDemo {
    private final VBox mainLayout;
    private final TextArea outputArea;
    private final List<Pane> progressPanes = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();
    private final List<TravailleurThread> threads = new ArrayList<>();
    private final Spinner<Integer> threadCountSpinner;
    private VBox threadsBox;

    private static final Color[] COLORS = {
        Color.web("#2196F3"), // Bleu
        Color.web("#4CAF50"), // Vert
        Color.web("#FF9800"), // Orange
        Color.web("#E91E63"), // Rose
        Color.web("#9C27B0")  // Violet
    };

    public MultiThreadDemo() {
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        // Titre
        Label title = new Label("LECON 2 : PLUSIEURS THREADS EN PARALLELE");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        // Explication
        Label explanation = new Label(
            "OBJECTIF : Plusieurs threads travaillent EN MEME TEMPS a des vitesses differentes.\n" +
            "Chaque barre SE REMPLIT independamment et progressivement!"
        );
        explanation.setWrapText(true);
        explanation.setPadding(new Insets(10));
        explanation.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-background-color: #FFF9C4; " +
            "-fx-border-color: #FBC02D; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        // Contrôles
        HBox controls = new HBox(25);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));
        
        Label countLabel = new Label("Nombre de threads :");
        countLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        threadCountSpinner = new Spinner<>(1, 5, 3);
        threadCountSpinner.setPrefWidth(100);
        threadCountSpinner.setStyle("-fx-font-size: 18px;");

        Button startButton = new Button("DEMARRER TOUS LES THREADS");
        startButton.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-padding: 25 60; " +
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );

        Button resetButton = new Button("REINITIALISER");
        resetButton.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-padding: 25 60; " +
            "-fx-background-color: #FF9800; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );

        controls.getChildren().addAll(countLabel, threadCountSpinner, startButton, resetButton);

        // Zone des threads sans ScrollPane
        threadsBox = new VBox(18);
        threadsBox.setPadding(new Insets(15));
        threadsBox.setStyle(
            "-fx-background-color: #FAFAFA; " +
            "-fx-border-color: #2196F3; " +
            "-fx-border-width: 4;"
        );

        // Output avec ScrollPane agrandi
        Label outputLabel = new Label("JOURNAL D'EXECUTION :");
        outputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #1976D2;");
        
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: monospace; -fx-font-size: 15px; -fx-control-inner-background: #F5F5F5;");
        
        ScrollPane outputScrollPane = new ScrollPane(outputArea);
        outputScrollPane.setFitToWidth(true);
        outputScrollPane.setFitToHeight(true);
        outputScrollPane.setPrefHeight(220);
        outputScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        outputScrollPane.setStyle("-fx-border-color: #666; -fx-border-width: 3;");

        // Events
        startButton.setOnAction(e -> demarrerThreads());
        resetButton.setOnAction(e -> {
            arreterThreads();
            initialiserThreads();
        });
        threadCountSpinner.valueProperty().addListener((obs, old, newVal) -> {
            arreterThreads();
            initialiserThreads();
        });

        mainLayout.getChildren().addAll(
            title, explanation,
            new Separator(),
            controls, threadsBox,
            new Separator(),
            outputLabel, outputScrollPane
        );

        initialiserThreads();
    }

    private void initialiserThreads() {
        arreterThreads();
        threads.clear();
        progressPanes.clear();
        labels.clear();
        threadsBox.getChildren().clear();
        outputArea.clear();

        int count = threadCountSpinner.getValue();
        log("=== Configuration : " + count + " thread(s) pret(s) ===");
        log("Cliquez sur le bouton DEMARRER pour lancer tous les threads!\n");

        for (int i = 0; i < count; i++) {
            VBox threadBox = creerThreadBox(i + 1, COLORS[i % COLORS.length]);
            threadsBox.getChildren().add(threadBox);
        }
    }

    private VBox creerThreadBox(int id, Color color) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: " + toHex(color) + "; " +
            "-fx-border-width: 4; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);"
        );

        Label nameLabel = new Label("THREAD #" + id);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px;");
        nameLabel.setTextFill(color);

        // Conteneur de la barre de progression personnalisée
        StackPane progressContainer = new StackPane();
        progressContainer.setPrefHeight(50);
        progressContainer.setMaxWidth(Double.MAX_VALUE);
        progressContainer.setStyle(
            "-fx-background-color: #E8E8E8; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #BDBDBD; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        );

        // Barre de remplissage
        Pane fillPane = new Pane();
        fillPane.setPrefHeight(50);
        fillPane.setMaxWidth(0); // Commence vide
        fillPane.setStyle(
            "-fx-background-color: " + toHex(color) + "; " +
            "-fx-background-radius: 8;"
        );
        
        // Label du pourcentage
        Label percentLabel = new Label("0%");
        percentLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #212121;"
        );

        progressContainer.getChildren().addAll(fillPane, percentLabel);
        StackPane.setAlignment(fillPane, Pos.CENTER_LEFT);
        progressPanes.add(fillPane);

        Label statusLabel = new Label("En attente de demarrage...");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        labels.add(statusLabel);

        box.getChildren().addAll(nameLabel, progressContainer, statusLabel);
        return box;
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    private void demarrerThreads() {
        outputArea.clear();
        log("=== DEMARRAGE DE TOUS LES THREADS EN PARALLELE ===\n");

        for (int i = 0; i < progressPanes.size(); i++) {
            TravailleurThread thread = new TravailleurThread(
                i + 1, 
                progressPanes.get(i), 
                labels.get(i),
                COLORS[i % COLORS.length]
            );
            threads.add(thread);
            thread.start();
            log("Thread #" + (i + 1) + " demarre");
        }

        log("\n>>> REGARDEZ : Ils travaillent TOUS EN MEME TEMPS <<<");
        log(">>> Chaque barre se REMPLIT a sa propre vitesse <<<");
    }

    private void arreterThreads() {
        for (TravailleurThread thread : threads) {
            if (thread != null && thread.isAlive()) {
                thread.arreter();
            }
        }
        threads.clear();
    }

    private void log(String message) {
        Platform.runLater(() -> outputArea.appendText(message + "\n"));
    }

    public ScrollPane getView() {
        ScrollPane globalScrollPane = new ScrollPane(mainLayout);
        globalScrollPane.setFitToWidth(true);
        globalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        globalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        globalScrollPane.setStyle("-fx-background: white;");
        return globalScrollPane;
    }

    /**
     * Thread travailleur qui remplit progressivement la barre
     */
    private class TravailleurThread extends Thread {
        private final int id;
        private final Pane progressPane;
        private final Label statusLabel;
        private final Color color;
        private volatile boolean continuer = true;
        private final Random random = new Random();

        public TravailleurThread(int id, Pane progressPane, Label statusLabel, Color color) {
            this.id = id;
            this.progressPane = progressPane;
            this.statusLabel = statusLabel;
            this.color = color;
            setName("Travailleur-" + id);
        }

        @Override
        public void run() {
            double progress = 0;

            while (continuer && progress < 1.0) {
                try {
                    // Chaque thread a sa propre vitesse (aléatoire)
                    int vitesse = 80 + random.nextInt(200);
                    Thread.sleep(vitesse);

                    progress += 0.02; // Progression plus fluide
                    if (progress > 1.0) progress = 1.0;

                    final double finalProgress = progress;
                    Platform.runLater(() -> {
                        // Remplir la barre visuellement (responsive)
                        StackPane container = (StackPane) progressPane.getParent();
                        double containerWidth = container.getWidth();
                        if (containerWidth > 0) {
                            double newWidth = containerWidth * finalProgress;
                            progressPane.setMaxWidth(newWidth);
                            progressPane.setPrefWidth(newWidth);
                        }
                        
                        // Mettre à jour le label
                        Label percentLabel = (Label) container.getChildren().get(1);
                        percentLabel.setText(String.format("%.0f%%", finalProgress * 100));
                        
                        statusLabel.setText(String.format("EN COURS - Progression : %.0f%%", finalProgress * 100));
                        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                        statusLabel.setTextFill(color);
                    });

                } catch (InterruptedException e) {
                    break;
                }
            }

            if (progress >= 1.0) {
                Platform.runLater(() -> {
                    statusLabel.setText("TERMINE - Travail accompli avec succes !");
                    statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                    statusLabel.setTextFill(Color.web("#4CAF50"));
                });
                log(">>> Thread #" + id + " TERMINE (100%) <<<");
            }
        }

        public void arreter() {
            continuer = false;
            interrupt();
        }
    }
}
