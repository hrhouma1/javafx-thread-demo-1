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
        Tab tab1 = new Tab("Lecon 1 : Premier Thread");
        tab1.setContent(new SimpleThreadDemo().getView());

        // Tab 2: Plusieurs threads en parallèle
        Tab tab2 = new Tab("Lecon 2 : Plusieurs Threads");
        tab2.setContent(new MultiThreadDemo().getView());

        // Tab 3: Problème de synchronisation
        Tab tab3 = new Tab("Lecon 3 : Synchronisation");
        tab3.setContent(new SyncProblemDemo().getView());

        // Tab 4: Barres circulaires
        Tab tab4 = new Tab("Demo : Progression Circulaire");
        tab4.setContent(new CircularProgressDemo().getView());

        // Tab 5: Trois horloges
        Tab tab5 = new Tab("Demo : Trois Horloges");
        tab5.setContent(new ClockDemo().getView());

        // Tab 6: Barre verticale
        Tab tab6 = new Tab("Demo : Progression Verticale");
        tab6.setContent(new VerticalProgressDemo().getView());

        // Tab 7: Barre horizontale
        Tab tab7 = new Tab("Demo : Progression Horizontale");
        tab7.setContent(new HorizontalProgressDemo().getView());

        // Tab 8: Exemple de compte bancaire (HACK vs SECURISE)
        Tab tab8 = new Tab("Lecon 4 : Compte Bancaire");
        tab8.setContent(new BankAccountDemo().getView());

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
