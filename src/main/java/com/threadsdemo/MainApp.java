package com.threadsdemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Application principale - Démonstration pédagogique des Threads en Java
 * Version simplifiée et professionnelle
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Demonstration Pedagogique des Threads - JavaFX");

        // Layout principal avec tabs pour différentes démos
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 14px;");

        // Tab 1: Exemple simple - Un seul thread
        Tab tab1 = new Tab("Leçon 1 : Premier Thread");
        tab1.setContent(new SimpleThreadDemo().getView());

        // Tab 2: Plusieurs threads en parallèle
        Tab tab2 = new Tab("Leçon 2 : Plusieurs Threads");
        tab2.setContent(new MultiThreadDemo().getView());

        // Tab 3: Problème de synchronisation
        Tab tab3 = new Tab("Leçon 3 : Synchronisation");
        tab3.setContent(new SyncProblemDemo().getView());

        // Tab 4: Compte bancaire
        Tab tab4 = new Tab("Leçon 4 : Compte Bancaire");
        tab4.setContent(new BankAccountDemo().getView());

        // Tab 5: Barres circulaires
        Tab tab5 = new Tab("Leçon 5 : Progression Circulaire");
        tab5.setContent(new CircularProgressDemo().getView());

        // Tab 6: Trois horloges
        Tab tab6 = new Tab("Leçon 6 : Trois Horloges");
        tab6.setContent(new ClockDemo().getView());

        // Tab 7: Barre verticale
        Tab tab7 = new Tab("Leçon 7 : Progression Verticale");
        tab7.setContent(new VerticalProgressDemo().getView());

        // Tab 8: Barre horizontale
        Tab tab8 = new Tab("Leçon 8 : Progression Horizontale");
        tab8.setContent(new HorizontalProgressDemo().getView());

        tabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5, tab6, tab7, tab8);

        Scene scene = new Scene(tabPane, 1200, 850);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Cleanup lors de la fermeture
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
