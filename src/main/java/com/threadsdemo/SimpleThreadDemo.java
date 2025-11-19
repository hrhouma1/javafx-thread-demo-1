package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Leçon 1: Introduction simple aux threads
 * Montre comment créer et démarrer un thread
 */
public class SimpleThreadDemo {
    private final VBox mainLayout;
    private final TextArea codeArea;
    private final TextArea outputArea;
    private final ProgressBar progressBar;
    private final Label statusLabel;
    private MonThread monThread;

    public SimpleThreadDemo() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Titre
        Label title = new Label("Lecon 1: Mon Premier Thread");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        // Explication
        TextArea explanation = new TextArea(
            "OBJECTIF: Comprendre ce qu'est un thread\n\n" +
            "Un THREAD (fil d'execution) est comme un employe qui peut travailler en parallele.\n" +
            "Votre programme principal est un thread, mais vous pouvez en creer d'autres!\n\n" +
            "COMMENT CREER UN THREAD?\n" +
            "1. Creer une classe qui etend Thread\n" +
            "2. Redefinir la methode run() avec le code a executer\n" +
            "3. Creer une instance et appeler start()\n\n" +
            "IMPORTANT: Appelez start(), PAS run() directement!"
        );
        explanation.setPrefHeight(180);
        explanation.setWrapText(true);
        explanation.setEditable(false);
        explanation.setStyle("-fx-font-size: 13px; -fx-background-color: #f0f8ff;");

        // Code exemple
        Label codeLabel = new Label("Code source simplifie:");
        codeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        codeArea = new TextArea(
            "// Etape 1: Creer une classe qui etend Thread\n" +
            "class MonThread extends Thread {\n" +
            "    \n" +
            "    // Etape 2: Redefinir la methode run()\n" +
            "    public void run() {\n" +
            "        // Code qui s'execute dans le thread\n" +
            "        for (int i = 1; i <= 10; i++) {\n" +
            "            System.out.println(\"Compteur: \" + i);\n" +
            "            Thread.sleep(500); // Pause de 500ms\n" +
            "        }\n" +
            "    }\n" +
            "}\n\n" +
            "// Etape 3: Creer et demarrer le thread\n" +
            "MonThread thread = new MonThread();\n" +
            "thread.start();  // Demarre le thread"
        );
        codeArea.setPrefHeight(200);
        codeArea.setEditable(false);
        codeArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-background-color: #2b2b2b; -fx-text-fill: #a9b7c6;");

        // Zone de démonstration
        VBox demoBox = new VBox(15);
        demoBox.setPadding(new Insets(20));
        demoBox.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 3; -fx-background-color: #f9f9f9;");

        Label demoLabel = new Label("Demonstration Interactive");
        demoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600);
        progressBar.setPrefHeight(35);

        statusLabel = new Label("Pret a demarrer");
        statusLabel.setStyle("-fx-font-size: 16px;");

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button startButton = new Button("DEMARRER LE THREAD");
        startButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-padding: 15 40; " +
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5;"
        );
        
        Button stopButton = new Button("ARRETER");
        stopButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-padding: 15 40; " +
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5;"
        );
        stopButton.setDisable(true);

        buttonBox.getChildren().addAll(startButton, stopButton);

        // Output
        Label outputLabel = new Label("Sortie du programme:");
        outputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        outputArea = new TextArea();
        outputArea.setPrefHeight(120);
        outputArea.setEditable(false);
        outputArea.setStyle("-fx-font-family: monospace;");

        demoBox.getChildren().addAll(demoLabel, progressBar, statusLabel, buttonBox);

        // Events
        startButton.setOnAction(e -> {
            demarrerThread();
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });

        stopButton.setOnAction(e -> {
            arreterThread();
            startButton.setDisable(false);
            stopButton.setDisable(true);
        });

        mainLayout.getChildren().addAll(
            title, explanation, 
            new Separator(),
            codeLabel, codeArea,
            new Separator(),
            demoBox, outputLabel, outputArea
        );
    }

    private void demarrerThread() {
        outputArea.clear();
        progressBar.setProgress(0);
        log("Creation du thread...");
        log("Appel de thread.start()");
        log("Le thread demarre son execution!\n");

        monThread = new MonThread();
        monThread.start();
    }

    private void arreterThread() {
        if (monThread != null && monThread.isAlive()) {
            monThread.arreter();
            log("\nThread arrete par l'utilisateur");
        }
    }

    private void log(String message) {
        Platform.runLater(() -> outputArea.appendText(message + "\n"));
    }

    public VBox getView() {
        return mainLayout;
    }

    /**
     * Classe interne qui représente notre thread
     */
    private class MonThread extends Thread {
        private volatile boolean continuer = true;

        @Override
        public void run() {
            for (int i = 1; i <= 10 && continuer; i++) {
                final int compteur = i;
                
                // Mettre à jour l'interface
                Platform.runLater(() -> {
                    progressBar.setProgress(compteur / 10.0);
                    statusLabel.setText("Compteur: " + compteur + " / 10");
                    statusLabel.setTextFill(Color.GREEN);
                    log("Thread en action - Compteur: " + compteur);
                });

                try {
                    Thread.sleep(800); // Pause de 800ms
                } catch (InterruptedException e) {
                    break;
                }
            }

            // Thread terminé
            Platform.runLater(() -> {
                if (continuer) {
                    statusLabel.setText("Thread termine!");
                    statusLabel.setTextFill(Color.BLUE);
                    log("\nLe thread a termine son travail");
                }
            });
        }

        public void arreter() {
            continuer = false;
            interrupt();
        }
    }
}
