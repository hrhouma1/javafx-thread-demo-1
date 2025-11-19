package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Démonstration : Trois horloges tournant en parallèle
 * Chaque horloge est gérée par son propre thread
 */
public class ClockDemo {
    private final VBox mainLayout;
    private final TextArea logArea;
    private final Canvas clock1Canvas;
    private final Canvas clock2Canvas;
    private final Canvas clock3Canvas;
    private ClockThread thread1, thread2, thread3;

    public ClockDemo() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Titre
        Label title = new Label("DEMONSTRATION : TROIS HORLOGES EN PARALLELE");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Explication
        TextArea explanation = new TextArea(
            "CONCEPT : Trois threads independants\n\n" +
            "Chaque horloge tourne a sa propre vitesse grace a un thread dedie.\n" +
            "Horloge 1 : Vitesse normale (1 seconde)\n" +
            "Horloge 2 : Vitesse rapide (0.5 seconde)\n" +
            "Horloge 3 : Vitesse lente (2 secondes)\n\n" +
            "Observez comment elles tournent independamment et simultanement."
        );
        explanation.setPrefHeight(120);
        explanation.setWrapText(true);
        explanation.setEditable(false);
        explanation.setStyle("-fx-font-size: 13px; -fx-background-color: #e8f5e9;");

        // Zone des horloges
        HBox clocksBox = new HBox(30);
        clocksBox.setAlignment(Pos.CENTER);
        clocksBox.setPadding(new Insets(20));
        clocksBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #666; -fx-border-width: 2;");

        // Horloge 1
        VBox clock1Box = createClockBox("HORLOGE 1\nVitesse Normale", Color.BLUE);
        clock1Canvas = new Canvas(180, 180);
        clock1Box.getChildren().add(clock1Canvas);

        // Horloge 2
        VBox clock2Box = createClockBox("HORLOGE 2\nVitesse Rapide", Color.GREEN);
        clock2Canvas = new Canvas(180, 180);
        clock2Box.getChildren().add(clock2Canvas);

        // Horloge 3
        VBox clock3Box = createClockBox("HORLOGE 3\nVitesse Lente", Color.RED);
        clock3Canvas = new Canvas(180, 180);
        clock3Box.getChildren().add(clock3Canvas);

        clocksBox.getChildren().addAll(clock1Box, clock2Box, clock3Box);

        // Boutons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        Button startButton = new Button("DEMARRER LES HORLOGES");
        startButton.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40; " +
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5;"
        );

        Button stopButton = new Button("ARRETER");
        stopButton.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40; " +
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5;"
        );
        stopButton.setDisable(true);

        buttonBox.getChildren().addAll(startButton, stopButton);

        // Journal
        Label logLabel = new Label("JOURNAL D'EXECUTION :");
        logLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        logArea = new TextArea();
        logArea.setPrefHeight(100);
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: monospace;");

        // Events
        startButton.setOnAction(e -> {
            startClocks();
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });

        stopButton.setOnAction(e -> {
            stopClocks();
            startButton.setDisable(false);
            stopButton.setDisable(true);
        });

        mainLayout.getChildren().addAll(
            title, explanation, clocksBox, buttonBox,
            new Separator(), logLabel, logArea
        );

        // Dessiner les horloges initiales
        drawClock(clock1Canvas, 0, Color.BLUE);
        drawClock(clock2Canvas, 0, Color.GREEN);
        drawClock(clock3Canvas, 0, Color.RED);
    }

    private VBox createClockBox(String name, Color color) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-border-color: " + toHex(color) + "; -fx-border-width: 2;");

        Label label = new Label(name);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-alignment: center;");
        label.setTextFill(color);

        box.getChildren().add(label);
        return box;
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    private void startClocks() {
        logArea.clear();
        log("Demarrage des trois threads d'horloges...");
        
        thread1 = new ClockThread(clock1Canvas, Color.BLUE, 1000, 1);
        thread2 = new ClockThread(clock2Canvas, Color.GREEN, 500, 2);
        thread3 = new ClockThread(clock3Canvas, Color.RED, 2000, 3);
        
        thread1.start();
        thread2.start();
        thread3.start();
        
        log("Thread 1 : Vitesse normale (1 sec)");
        log("Thread 2 : Vitesse rapide (0.5 sec)");
        log("Thread 3 : Vitesse lente (2 sec)");
    }

    private void stopClocks() {
        if (thread1 != null) thread1.stopRunning();
        if (thread2 != null) thread2.stopRunning();
        if (thread3 != null) thread3.stopRunning();
        log("Tous les threads arretes");
    }

    private void drawClock(Canvas canvas, double angle, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 10;

        // Fond
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Cercle extérieur
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Marques des heures
        for (int i = 0; i < 12; i++) {
            double hourAngle = Math.toRadians(i * 30 - 90);
            double x1 = centerX + Math.cos(hourAngle) * radius * 0.9;
            double y1 = centerY + Math.sin(hourAngle) * radius * 0.9;
            double x2 = centerX + Math.cos(hourAngle) * radius;
            double y2 = centerY + Math.sin(hourAngle) * radius;
            
            gc.setLineWidth(2);
            gc.strokeLine(x1, y1, x2, y2);
        }

        // Aiguille
        double radians = Math.toRadians(angle - 90);
        double handLength = radius * 0.7;
        double handX = centerX + Math.cos(radians) * handLength;
        double handY = centerY + Math.sin(radians) * handLength;

        gc.setStroke(color);
        gc.setLineWidth(4);
        gc.strokeLine(centerX, centerY, handX, handY);

        // Centre
        gc.setFill(color);
        gc.fillOval(centerX - 5, centerY - 5, 10, 10);
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public VBox getView() {
        return mainLayout;
    }

    private class ClockThread extends Thread {
        private final Canvas canvas;
        private final Color color;
        private final int delay;
        private final int id;
        private volatile boolean running = true;
        private double angle = 0;

        public ClockThread(Canvas canvas, Color color, int delay, int id) {
            this.canvas = canvas;
            this.color = color;
            this.delay = delay;
            this.id = id;
            setName("ClockThread-" + id);
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(delay);
                    angle = (angle + 6) % 360; // 6 degrés par tick
                    
                    Platform.runLater(() -> drawClock(canvas, angle, color));
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        public void stopRunning() {
            running = false;
            interrupt();
        }
    }
}
