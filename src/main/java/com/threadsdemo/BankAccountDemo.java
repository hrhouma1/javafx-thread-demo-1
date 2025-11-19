package com.threadsdemo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Démonstration : Problème de compte bancaire avec synchronisation
 * Scénario : Deux personnes tentent de retirer 1000$ simultanément d'un compte avec 1000$
 */
public class BankAccountDemo {
    private final VBox mainLayout;
    private final TextArea logArea;
    private Label soldeLabel;
    private Label resultatLabel;
    private Circle person1Indicator;
    private Circle person2Indicator;
    private CompteBancaire compte;
    private volatile boolean testEnCours = false;

    public BankAccountDemo() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Titre
        Label title = new Label("DEMONSTRATION : PROBLEME DE COMPTE BANCAIRE");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        // Explication
        Label explanation = new Label(
            "SCENARIO : ATTAQUE PAR DOUBLE RETRAIT\n\n" +
            "• Vous et votre conjoint(e) avez chacun une copie de la carte bancaire\n" +
            "• Solde du compte : 1000$\n" +
            "• Vous faites TOUS LES DEUX un achat de 1000$ EN MEME TEMPS\n\n" +
            "SANS Synchronisation : Les deux retraits reussissent = 2000$ retires! (HACK!)\n" +
            "AVEC Synchronisation : Un seul retrait reussit = 1000$ retires (CORRECT)"
        );
        explanation.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: #fff3e0; " +
            "-fx-padding: 15; " +
            "-fx-border-color: #ff9800; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        // Zone d'affichage du compte
        VBox accountBox = new VBox(15);
        accountBox.setAlignment(Pos.CENTER);
        accountBox.setPadding(new Insets(25));
        accountBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #2196F3; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );

        Label soldeTitle = new Label("SOLDE DU COMPTE");
        soldeTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        soldeLabel = new Label("1000 $");
        soldeLabel.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-padding: 15; " +
            "-fx-background-color: #e8f5e9; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );

        accountBox.getChildren().addAll(soldeTitle, soldeLabel);

        // Zone des personnes
        HBox peopleBox = new HBox(80);
        peopleBox.setAlignment(Pos.CENTER);
        peopleBox.setPadding(new Insets(20));
        peopleBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #666; -fx-border-width: 2;");

        VBox person1Box = createPersonBox("VOUS", Color.BLUE);
        person1Indicator = (Circle) ((HBox)person1Box.getChildren().get(0)).getChildren().get(0);
        
        VBox person2Box = createPersonBox("VOTRE CONJOINT(E)", Color.PURPLE);
        person2Indicator = (Circle) ((HBox)person2Box.getChildren().get(0)).getChildren().get(0);

        peopleBox.getChildren().addAll(person1Box, person2Box);

        // Boutons de test
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        Button testSansSync = new Button("TEST SANS SYNCHRONISATION");
        testSansSync.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 25 40; " +
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand;"
        );

        Button testAvecSync = new Button("TEST AVEC SYNCHRONISATION");
        testAvecSync.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 25 40; " +
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand;"
        );

        Button resetButton = new Button("REINITIALISER");
        resetButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 25 40; " +
            "-fx-background-color: #FF9800; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand;"
        );

        buttonBox.getChildren().addAll(testSansSync, testAvecSync, resetButton);

        // Zone de résultat
        resultatLabel = new Label("");
        resultatLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 20; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        resultatLabel.setMaxWidth(Double.MAX_VALUE);
        resultatLabel.setAlignment(Pos.CENTER);

        // Journal
        Label logLabel = new Label("JOURNAL D'EXECUTION :");
        logLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1976D2;");
        
        logArea = new TextArea();
        logArea.setPrefHeight(200);
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: monospace; -fx-font-size: 13px; -fx-control-inner-background: #f9f9f9;");

        // Events
        testSansSync.setOnAction(e -> lancerTest(false));
        testAvecSync.setOnAction(e -> lancerTest(true));
        resetButton.setOnAction(e -> reinitialiser());

        mainLayout.getChildren().addAll(
            title, explanation, accountBox, peopleBox, 
            buttonBox, resultatLabel,
            new Separator(), logLabel, logArea
        );

        log("Pret a tester. Solde initial : 1000$");
    }

    private VBox createPersonBox(String name, Color color) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        HBox indicatorBox = new HBox(10);
        indicatorBox.setAlignment(Pos.CENTER);

        Circle indicator = new Circle(15);
        indicator.setFill(Color.GRAY);
        indicator.setStroke(Color.BLACK);
        indicator.setStrokeWidth(2);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        nameLabel.setTextFill(color);

        indicatorBox.getChildren().addAll(indicator, nameLabel);

        Label actionLabel = new Label("Tente de retirer :\n1000 $");
        actionLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-alignment: center; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #c62828;"
        );
        actionLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        box.getChildren().addAll(indicatorBox, actionLabel);
        return box;
    }

    private void lancerTest(boolean avecSync) {
        if (testEnCours) {
            log("Test deja en cours, veuillez attendre...");
            return;
        }

        testEnCours = true;
        logArea.clear();
        resultatLabel.setText("");
        
        compte = new CompteBancaire(1000, avecSync);
        
        log("========================================");
        log(avecSync ? "TEST AVEC SYNCHRONISATION" : "TEST SANS SYNCHRONISATION");
        log("========================================");
        log("Solde initial : " + compte.getSolde() + "$");
        log("Deux personnes tentent de retirer 1000$ simultanement...\n");

        // Lancer les deux threads de retrait
        Thread thread1 = new Thread(() -> effectuerRetrait("VOUS", 1000, person1Indicator));
        Thread thread2 = new Thread(() -> effectuerRetrait("VOTRE CONJOINT(E)", 1000, person2Indicator));

        thread1.start();
        thread2.start();

        // Attendre la fin et afficher le résultat
        new Thread(() -> {
            try {
                thread1.join();
                thread2.join();
                Thread.sleep(500);
                afficherResultat(avecSync);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                testEnCours = false;
            }
        }).start();
    }

    private void effectuerRetrait(String personne, int montant, Circle indicator) {
        try {
            // Indiquer que la personne commence
            Platform.runLater(() -> indicator.setFill(Color.ORANGE));
            
            Thread.sleep(100); // Petite attente pour simuler le traitement
            
            boolean succes = compte.retirer(montant, personne);
            
            // Mettre à jour l'interface
            Platform.runLater(() -> {
                if (succes) {
                    indicator.setFill(Color.GREEN);
                    log(personne + " : Retrait REUSSI de " + montant + "$");
                } else {
                    indicator.setFill(Color.RED);
                    log(personne + " : Retrait REFUSE (solde insuffisant)");
                }
                soldeLabel.setText(compte.getSolde() + " $");
                
                if (compte.getSolde() < 0) {
                    soldeLabel.setStyle(
                        "-fx-font-size: 48px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #c62828; " +
                        "-fx-padding: 15; " +
                        "-fx-background-color: #ffebee; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8;"
                    );
                } else if (compte.getSolde() == 0) {
                    soldeLabel.setStyle(
                        "-fx-font-size: 48px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #FF9800; " +
                        "-fx-padding: 15; " +
                        "-fx-background-color: #fff3e0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8;"
                    );
                } else {
                    soldeLabel.setStyle(
                        "-fx-font-size: 48px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #4CAF50; " +
                        "-fx-padding: 15; " +
                        "-fx-background-color: #e8f5e9; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8;"
                    );
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void afficherResultat(boolean avecSync) {
        Platform.runLater(() -> {
            log("\n========================================");
            log("RESULTAT FINAL");
            log("========================================");
            log("Solde final : " + compte.getSolde() + "$");
            log("Total retire : " + compte.getTotalRetire() + "$");
            
            if (!avecSync && compte.getSolde() < 0) {
                resultatLabel.setText("⚠ HACK REUSSI ! 2000$ retires avec seulement 1000$ ! ⚠");
                resultatLabel.setStyle(
                    "-fx-font-size: 20px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 20; " +
                    "-fx-background-color: #ffebee; " +
                    "-fx-text-fill: #c62828; " +
                    "-fx-border-color: #c62828; " +
                    "-fx-border-width: 3; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8;"
                );
                log("\n⚠ PROBLEME : La race condition a permis deux retraits !");
                log("⚠ C'est comme si vous aviez 'vole' 1000$ a la banque !");
            } else if (avecSync) {
                resultatLabel.setText("✓ SECURISE ! Un seul retrait autorise. Solde protege. ✓");
                resultatLabel.setStyle(
                    "-fx-font-size: 20px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 20; " +
                    "-fx-background-color: #e8f5e9; " +
                    "-fx-text-fill: #2e7d32; " +
                    "-fx-border-color: #4CAF50; " +
                    "-fx-border-width: 3; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8;"
                );
                log("\n✓ SOLUTION : La synchronisation a protege le compte !");
                log("✓ Un seul retrait a ete autorise, l'autre a ete refuse.");
            }
        });
    }

    private void reinitialiser() {
        testEnCours = false;
        compte = null;
        soldeLabel.setText("1000 $");
        soldeLabel.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-padding: 15; " +
            "-fx-background-color: #e8f5e9; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        person1Indicator.setFill(Color.GRAY);
        person2Indicator.setFill(Color.GRAY);
        resultatLabel.setText("");
        logArea.clear();
        log("Reinitialise. Solde : 1000$");
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public VBox getView() {
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        return wrapper;
    }

    /**
     * Classe représentant un compte bancaire
     */
    private class CompteBancaire {
        private int solde;
        private int totalRetire = 0;
        private final boolean avecSynchronisation;

        public CompteBancaire(int soldeInitial, boolean avecSynchronisation) {
            this.solde = soldeInitial;
            this.avecSynchronisation = avecSynchronisation;
        }

        public boolean retirer(int montant, String personne) {
            if (avecSynchronisation) {
                return retirerAvecSync(montant, personne);
            } else {
                return retirerSansSync(montant, personne);
            }
        }

        private synchronized boolean retirerAvecSync(int montant, String personne) {
            try {
                // Simuler le temps de vérification
                Thread.sleep(50);
                
                if (solde >= montant) {
                    // Simuler le temps de traitement
                    Thread.sleep(100);
                    solde -= montant;
                    totalRetire += montant;
                    return true;
                } else {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }

        private boolean retirerSansSync(int montant, String personne) {
            try {
                // RACE CONDITION : Pas de synchronisation !
                // Lire le solde
                int soldeActuel = solde;
                
                // Simuler le temps de vérification
                Thread.sleep(50);
                
                // Vérifier si le solde est suffisant
                if (soldeActuel >= montant) {
                    // ATTENTION : Entre la vérification et le retrait,
                    // un autre thread peut aussi passer ici !
                    Thread.sleep(100);
                    
                    // Retirer le montant
                    solde -= montant;
                    totalRetire += montant;
                    return true;
                } else {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }

        public int getSolde() {
            return solde;
        }

        public int getTotalRetire() {
            return totalRetire;
        }
    }
}

