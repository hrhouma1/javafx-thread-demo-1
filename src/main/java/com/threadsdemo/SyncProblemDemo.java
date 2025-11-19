package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Leçon 3: Synchronisation - Version très visuelle et pédagogique
 */
public class SyncProblemDemo {
    private final VBox mainLayout;
    private final TextArea outputArea;
    private Label compteurLabel;
    private Label attenduLabel;
    private Label resultatLabel;
    private final VBox threadsVisuBox;
    private RadioButton avecSync;
    private RadioButton sansSync;
    private CompteurPartage compteur;
    private Thread[] threads;
    private Circle[] threadIndicators;
    private Label[] threadLabels;
    private ProgressBar[] threadProgressBars;

    private static final Color[] THREAD_COLORS = {
        Color.web("#2196F3"), Color.web("#4CAF50"), Color.web("#FF9800")
    };

    public SyncProblemDemo() {
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        // Titre
        Label title = new Label("LECON 3 : SYNCHRONISATION");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #E91E63;");

        // Explication courte et claire
        Label explanation = new Label(
            "PROBLEME : Plusieurs threads modifient le MEME compteur en meme temps.\n" +
            "RESULTAT SANS sync : Des increments sont PERDUS (Race Condition)\n" +
            "SOLUTION : Le mot-cle 'synchronized' empeche les conflits"
        );
        explanation.setWrapText(true);
        explanation.setPadding(new Insets(12));
        explanation.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-background-color: #FFEBEE; " +
            "-fx-border-color: #E91E63; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        // Zone compteur et visualisation
        HBox mainContentBox = new HBox(20);
        mainContentBox.setAlignment(Pos.CENTER);

        // GAUCHE: Compteur principal
        VBox compteurBox = createCompteurBox();

        // DROITE: Visualisation des threads
        threadsVisuBox = createThreadsVisualizationBox();

        mainContentBox.getChildren().addAll(compteurBox, threadsVisuBox);

        // Options et contrôles
        HBox controlsBox = createControlsBox();

        // Journal
        Label outputLabel = new Label("JOURNAL EN TEMPS REEL :");
        outputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #E91E63;");
        
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(160);
        outputArea.setStyle(
            "-fx-font-family: monospace; " +
            "-fx-font-size: 13px; " +
            "-fx-control-inner-background: #FFF9C4; " +
            "-fx-border-color: #666; " +
            "-fx-border-width: 2;"
        );

        ScrollPane mainScrollPane = new ScrollPane();
        mainScrollPane.setContent(new VBox(15,
            title, explanation,
            new Separator(),
            mainContentBox,
            new Separator(),
            controlsBox,
            new Separator(),
            outputLabel, outputArea
        ));
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainLayout.getChildren().add(mainScrollPane);

        log("Pret a demarrer la demonstration!");
        log("Essayez d'abord SANS synchronisation pour voir le probleme.");
    }

    private VBox createCompteurBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(25));
        box.setPrefWidth(400);
        box.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E91E63; " +
            "-fx-border-width: 4; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3);"
        );

        Label titre = new Label("COMPTEUR PARTAGE");
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #E91E63;");

        compteurLabel = new Label("0");
        compteurLabel.setStyle(
            "-fx-font-size: 80px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #E91E63; " +
            "-fx-background-color: #FFF3E0; " +
            "-fx-padding: 20; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );

        attenduLabel = new Label("Attendu : 0");
        attenduLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");

        resultatLabel = new Label("");
        resultatLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        box.getChildren().addAll(titre, compteurLabel, attenduLabel, resultatLabel);
        return box;
    }

    private VBox createThreadsVisualizationBox() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setPrefWidth(500);
        box.setStyle(
            "-fx-background-color: #F5F5F5; " +
            "-fx-border-color: #666; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );

        Label titre = new Label("VISUALISATION DES 3 THREADS");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        threadIndicators = new Circle[3];
        threadLabels = new Label[3];
        threadProgressBars = new ProgressBar[3];

        for (int i = 0; i < 3; i++) {
            HBox threadRow = new HBox(12);
            threadRow.setAlignment(Pos.CENTER_LEFT);
            threadRow.setPadding(new Insets(10));
            threadRow.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: " + toHex(THREAD_COLORS[i]) + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;"
            );

            Circle indicator = new Circle(12);
            indicator.setFill(Color.GRAY);
            indicator.setStroke(THREAD_COLORS[i]);
            indicator.setStrokeWidth(2);
            threadIndicators[i] = indicator;

            Label threadLabel = new Label("Thread " + (i + 1));
            threadLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            threadLabel.setTextFill(THREAD_COLORS[i]);
            threadLabel.setPrefWidth(100);
            threadLabels[i] = threadLabel;

            ProgressBar progressBar = new ProgressBar(0);
            progressBar.setPrefWidth(200);
            progressBar.setPrefHeight(20);
            progressBar.setStyle("-fx-accent: " + toHex(THREAD_COLORS[i]) + ";");
            threadProgressBars[i] = progressBar;

            Label statusLabel = new Label("Pret");
            statusLabel.setPrefWidth(80);

            threadRow.getChildren().addAll(indicator, threadLabel, progressBar, statusLabel);
            box.getChildren().add(threadRow);
        }

        box.getChildren().add(0, titre);
        return box;
    }

    private HBox createControlsBox() {
        HBox mainBox = new HBox(30);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: #ccc; -fx-border-width: 2;");

        // Options de synchronisation
        VBox optionsBox = new VBox(15);
        optionsBox.setAlignment(Pos.CENTER_LEFT);

        Label modeLabel = new Label("MODE DE TEST :");
        modeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        ToggleGroup group = new ToggleGroup();
        
        sansSync = new RadioButton("SANS synchronisation - Voir le probleme");
        sansSync.setToggleGroup(group);
        sansSync.setSelected(true);
        sansSync.setStyle("-fx-font-size: 16px;");

        avecSync = new RadioButton("AVEC synchronisation - Solution");
        avecSync.setToggleGroup(group);
        avecSync.setStyle("-fx-font-size: 16px;");

        // Code affiché
        TextArea codeArea = new TextArea();
        codeArea.setPrefHeight(110);
        codeArea.setPrefWidth(350);
        codeArea.setEditable(false);
        codeArea.setStyle(
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 13px; " +
            "-fx-control-inner-background: #263238; " +
            "-fx-text-fill: #A5D6A7;"
        );
        
        sansSync.setOnAction(e -> codeArea.setText(
            "// SANS synchronized\n" +
            "public void incrementer() {\n" +
            "    compteur++;\n" +
            "    // DANGER: Peut perdre\n" +
            "    // des increments!\n" +
            "}"
        ));
        avecSync.setOnAction(e -> codeArea.setText(
            "// AVEC synchronized\n" +
            "public synchronized\n" +
            "void incrementer() {\n" +
            "    compteur++;\n" +
            "    // SUR: Un seul a la fois\n" +
            "}"
        ));
        sansSync.fire();

        optionsBox.getChildren().addAll(modeLabel, sansSync, avecSync, codeArea);

        // Bouton de test
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button testButton = new Button("LANCER LE TEST");
        testButton.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 25 50; " +
            "-fx-background-color: #E91E63; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand;"
        );

        Label testInfo = new Label("3 threads × 100 increments\n= 300 attendu\n(Animation fluide\nERREUR visible SANS sync!)");
        testInfo.setStyle("-fx-font-size: 13px; -fx-text-alignment: center;");
        testInfo.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        testButton.setOnAction(e -> lancerTest());

        buttonBox.getChildren().addAll(testButton, testInfo);

        mainBox.getChildren().addAll(optionsBox, new Separator(), buttonBox);
        return mainBox;
    }

    private void lancerTest() {
        outputArea.clear();
        compteurLabel.setText("0");
        resultatLabel.setText("");
        attenduLabel.setText("Attendu : 300");

        boolean useSynchronization = avecSync.isSelected();
        compteur = new CompteurPartage(useSynchronization);

        final int NB_THREADS = 3;
        final int NB_INCREMENTS = 100;  // Seulement 100 pour bien voir
        final int ATTENDU = NB_THREADS * NB_INCREMENTS;

        log("=== DEMARRAGE DU TEST ===");
        log("Mode: " + (useSynchronization ? "AVEC synchronized" : "SANS synchronized"));
        log("3 threads vont incrementer de 1 a 100 (tres lent)");
        log("Valeur attendue: " + ATTENDU + "\n");

        // Réinitialiser les indicateurs visuels
        for (int i = 0; i < 3; i++) {
            threadIndicators[i].setFill(Color.GRAY);
            threadProgressBars[i].setProgress(0);
        }

        threads = new Thread[NB_THREADS];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NB_THREADS; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                // Indicateur de démarrage
                Platform.runLater(() -> {
                    threadIndicators[threadId].setFill(THREAD_COLORS[threadId]);
                    log("Thread " + (threadId + 1) + " DEMARRE");
                });

                for (int j = 0; j < NB_INCREMENTS; j++) {
                    compteur.incrementer();
                    
                    // Pause TRES courte pour créer de la contention SANS sync
                    // mais assez longue pour voir l'animation
                    try {
                        Thread.sleep(3);  // Seulement 3ms - les threads se chevauchent!
                    } catch (InterruptedException e) {
                        break;
                    }
                    
                    // Mise à jour visuelle tous les 2 increments pour fluidité
                    if (j % 2 == 0) {
                        final int progress = j;
                        final int increment = j + 1;
                        Platform.runLater(() -> {
                            compteurLabel.setText(String.valueOf(compteur.getValeur()));
                            threadProgressBars[threadId].setProgress((double) progress / NB_INCREMENTS);
                            
                            // Log chaque dizaine pour ne pas surcharger
                            if (increment % 10 == 0) {
                                log("Thread " + (threadId + 1) + " : " + increment + "/100");
                            }
                        });
                    }
                }

                // Thread terminé
                Platform.runLater(() -> {
                    threadIndicators[threadId].setFill(Color.web("#4CAF50"));
                    threadProgressBars[threadId].setProgress(1.0);
                    log("Thread " + (threadId + 1) + " TERMINE");
                });
            });
            threads[i].setName("IncrementThread-" + (i + 1));
            threads[i].start();
        }

        // Thread de surveillance
        new Thread(() -> {
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();
            int valeurFinale = compteur.getValeur();

            Platform.runLater(() -> {
                compteurLabel.setText(String.valueOf(valeurFinale));
                
                log("\n=== RESULTAT FINAL ===");
                log("Temps: " + (endTime - startTime) + " ms");
                log("Valeur finale: " + valeurFinale);
                log("Valeur attendue: " + ATTENDU);
                
                if (valeurFinale == ATTENDU) {
                    compteurLabel.setStyle(
                        compteurLabel.getStyle() + "-fx-text-fill: #4CAF50;"
                    );
                    resultatLabel.setText("SUCCES !");
                    resultatLabel.setTextFill(Color.web("#4CAF50"));
                    log("\n SUCCES! Le compteur est CORRECT!");
                    log("La synchronisation a fonctionne!");
                } else {
                    compteurLabel.setStyle(
                        compteurLabel.getStyle() + "-fx-text-fill: #F44336;"
                    );
                    resultatLabel.setText("ERREUR: " + (ATTENDU - valeurFinale) + " PERDUS!");
                    resultatLabel.setTextFill(Color.web("#F44336"));
                    log("\n PROBLEME! " + (ATTENDU - valeurFinale) + " increments PERDUS!");
                    log("C'est la RACE CONDITION!");
                    log("Essayez maintenant AVEC synchronisation!");
                }
            });
        }).start();
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    private void log(String message) {
        Platform.runLater(() -> outputArea.appendText(message + "\n"));
    }

    public Pane getView() {
        return mainLayout;
    }

    /**
     * Compteur partagé avec ou sans synchronisation
     */
    private static class CompteurPartage {
        private int compteur = 0;
        private final boolean useSynchronization;

        public CompteurPartage(boolean useSynchronization) {
            this.useSynchronization = useSynchronization;
        }

        public void incrementer() {
            if (useSynchronization) {
                synchronized (this) {
                    compteur++;
                }
            } else {
                compteur++;
            }
        }

        public synchronized int getValeur() {
            return compteur;
        }
    }
}
