package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Démonstration : Barres de progression circulaires
 */
public class CircularProgressDemo {
    private final VBox mainLayout;
    private final TextArea logArea;
    private Canvas canvas1, canvas2, canvas3, canvas4, canvas5;
    private WorkerThread thread1, thread2, thread3, thread4, thread5;

    public CircularProgressDemo() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Titre
        Label title = new Label("DEMONSTRATION : BARRES DE PROGRESSION CIRCULAIRES");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Explication
        TextArea explanation = new TextArea(
            "CONCEPT : Visualisation circulaire de la progression\n\n" +
            "Cinq threads travaillent en parallele avec des vitesses differentes.\n" +
            "Chaque cercle se remplit progressivement de 0% a 100%.\n\n" +
            "Thread 1 : Tres lent (200ms par etape)\n" +
            "Thread 2 : Lent (120ms par etape)\n" +
            "Thread 3 : Moyen (80ms par etape)\n" +
            "Thread 4 : Rapide (50ms par etape)\n" +
            "Thread 5 : Tres rapide (30ms par etape)\n\n" +
            "Ce type de visualisation est utilise dans les applications modernes."
        );
        explanation.setPrefHeight(160);
        explanation.setWrapText(true);
        explanation.setEditable(false);
        explanation.setStyle("-fx-font-size: 13px; -fx-background-color: #f3e5f5;");

        // Zone des cercles de progression
        HBox circlesBox = new HBox(25);
        circlesBox.setAlignment(Pos.CENTER);
        circlesBox.setPadding(new Insets(30));
        circlesBox.setStyle("-fx-background-color: #fafafa; -fx-border-color: #666; -fx-border-width: 2;");

        // Créer 5 cercles de progression
        canvas1 = createCircularProgress("THREAD 1", Color.web("#2196F3"));
        canvas2 = createCircularProgress("THREAD 2", Color.web("#4CAF50"));
        canvas3 = createCircularProgress("THREAD 3", Color.web("#FF9800"));
        canvas4 = createCircularProgress("THREAD 4", Color.web("#E91E63"));
        canvas5 = createCircularProgress("THREAD 5", Color.web("#9C27B0"));

        VBox box1 = createCircleContainer(canvas1, "THREAD 1\nTres Lent", Color.web("#2196F3"));
        VBox box2 = createCircleContainer(canvas2, "THREAD 2\nLent", Color.web("#4CAF50"));
        VBox box3 = createCircleContainer(canvas3, "THREAD 3\nMoyen", Color.web("#FF9800"));
        VBox box4 = createCircleContainer(canvas4, "THREAD 4\nRapide", Color.web("#E91E63"));
        VBox box5 = createCircleContainer(canvas5, "THREAD 5\nTres Rapide", Color.web("#9C27B0"));

        circlesBox.getChildren().addAll(box1, box2, box3, box4, box5);

        // Boutons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        Button startButton = new Button("DEMARRER");
        startButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20 60; " +
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;"
        );

        Button resetButton = new Button("REINITIALISER");
        resetButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20 60; " +
            "-fx-background-color: #FF5722; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;"
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
            title, explanation, circlesBox, buttonBox,
            new Separator(), logLabel, logArea
        );

        // Initialiser les cercles à 0%
        drawCircularProgress(canvas1, 0, Color.web("#2196F3"));
        drawCircularProgress(canvas2, 0, Color.web("#4CAF50"));
        drawCircularProgress(canvas3, 0, Color.web("#FF9800"));
        drawCircularProgress(canvas4, 0, Color.web("#E91E63"));
        drawCircularProgress(canvas5, 0, Color.web("#9C27B0"));

        log("Pret a demarrer");
    }

    private Canvas createCircularProgress(String name, Color color) {
        Canvas canvas = new Canvas(150, 150);
        return canvas;
    }

    private VBox createCircleContainer(Canvas canvas, String label, Color color) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-alignment: center;");
        nameLabel.setTextFill(color);

        container.getChildren().addAll(canvas, nameLabel);
        return container;
    }

    private void drawCircularProgress(Canvas canvas, double progress, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 15;

        // Effacer le canvas
        gc.clearRect(0, 0, width, height);

        // Cercle de fond (gris clair)
        gc.setStroke(Color.web("#E0E0E0"));
        gc.setLineWidth(12);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Arc de progression (coloré)
        if (progress > 0) {
            gc.setStroke(color);
            gc.setLineWidth(12);
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            
            double startAngle = -90; // Commence en haut
            double sweepAngle = progress * 360; // Angle basé sur le progrès
            
            gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 
                        startAngle, sweepAngle, javafx.scene.shape.ArcType.OPEN);
        }

        // Texte du pourcentage au centre
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        String text = String.format("%.0f%%", progress * 100);
        
        javafx.scene.text.Text tempText = new javafx.scene.text.Text(text);
        tempText.setFont(gc.getFont());
        double textWidth = tempText.getLayoutBounds().getWidth();
        double textHeight = tempText.getLayoutBounds().getHeight();
        
        gc.fillText(text, centerX - textWidth / 2, centerY + textHeight / 4);
    }

    private void startThreads() {
        stopThreads();
        
        logArea.clear();
        log("=== Demarrage de 5 threads en parallele ===");

        thread1 = new WorkerThread(canvas1, Color.web("#2196F3"), 200, 1, "Tres lent");
        thread2 = new WorkerThread(canvas2, Color.web("#4CAF50"), 120, 2, "Lent");
        thread3 = new WorkerThread(canvas3, Color.web("#FF9800"), 80, 3, "Moyen");
        thread4 = new WorkerThread(canvas4, Color.web("#E91E63"), 50, 4, "Rapide");
        thread5 = new WorkerThread(canvas5, Color.web("#9C27B0"), 30, 5, "Tres rapide");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        log("Thread 1 demarre (tres lent - 200ms)");
        log("Thread 2 demarre (lent - 120ms)");
        log("Thread 3 demarre (moyen - 80ms)");
        log("Thread 4 demarre (rapide - 50ms)");
        log("Thread 5 demarre (tres rapide - 30ms)");
        log("=== Observez les progressions differentes ===");
    }

    private void stopThreads() {
        if (thread1 != null && thread1.isAlive()) thread1.stopRunning();
        if (thread2 != null && thread2.isAlive()) thread2.stopRunning();
        if (thread3 != null && thread3.isAlive()) thread3.stopRunning();
        if (thread4 != null && thread4.isAlive()) thread4.stopRunning();
        if (thread5 != null && thread5.isAlive()) thread5.stopRunning();
    }

    private void reset() {
        stopThreads();
        
        drawCircularProgress(canvas1, 0, Color.web("#2196F3"));
        drawCircularProgress(canvas2, 0, Color.web("#4CAF50"));
        drawCircularProgress(canvas3, 0, Color.web("#FF9800"));
        drawCircularProgress(canvas4, 0, Color.web("#E91E63"));
        drawCircularProgress(canvas5, 0, Color.web("#9C27B0"));
        
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
        private final Canvas canvas;
        private final Color color;
        private final int delay;
        private final int id;
        private final String speed;
        private volatile boolean running = true;

        public WorkerThread(Canvas canvas, Color color, int delay, int id, String speed) {
            this.canvas = canvas;
            this.color = color;
            this.delay = delay;
            this.id = id;
            this.speed = speed;
            setName("CircularWorkerThread-" + id);
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
                    Platform.runLater(() -> drawCircularProgress(canvas, finalProgress, color));

                } catch (InterruptedException e) {
                    break;
                }
            }

            if (progress >= 1.0) {
                log("Thread " + id + " (" + speed + ") a termine - 100%");
            }
        }

        public void stopRunning() {
            running = false;
            interrupt();
        }
    }
}

