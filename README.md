# Démonstration Pédagogique des Threads - JavaFX

Application JavaFX professionnelle pour enseigner les concepts des threads en Java.

## Contenu de l'Application

L'application contient **8 leçons interactives** :

#### Leçon 1 : Premier Thread
- Comprendre ce qu'est un thread
- Créer et démarrer son premier thread
- Différence entre `run()` et `start()`

#### Leçon 2 : Plusieurs Threads
- Exécution parallèle de plusieurs threads
- Chaque thread progresse indépendamment
- Visualisation du parallélisme réel

#### Leçon 3 : Synchronisation
- Problème de "race condition"
- Démonstration visuelle du bug
- Solution avec le mot-clé `synchronized`

#### Leçon 4 : Compte Bancaire
- **Scénario réaliste de double retrait**
- Vous et votre conjoint(e) avec la même carte
- Démonstration du hack bancaire SANS synchronisation
- Solution sécurisée AVEC synchronisation
- 2000$ retirés avec seulement 1000$ (race condition)

#### Leçon 5 : Progression Circulaire
- **5 barres de progression circulaires**
- Comme dans les applications modernes
- Chaque cercle se remplit de 0% à 100%
- Vitesses différentes pour chaque thread
- Design professionnel et moderne

#### Leçon 6 : Trois Horloges
- 3 horloges animées en parallèle
- Chaque horloge tourne à sa propre vitesse
- Visualisation du temps réel

#### Leçon 7 : Progression Verticale
- 3 barres verticales
- Progression de bas en haut
- Différentes vitesses

#### Leçon 8 : Progression Horizontale
- 4 barres horizontales
- Comme un gestionnaire de téléchargement
- États visibles (En attente, En cours, Terminé)

## Lancement Rapide

### Option 1 : Script Windows
```
run.bat
```

### Option 2 : Maven
```bash
mvn clean compile
mvn javafx:run
```

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- JavaFX 21 (téléchargé automatiquement)

## Points Forts

- Interface professionnelle sans emojis
- **Gros boutons** faciles à cliquer
- Barres circulaires modernes
- Code source simple et commenté
- Explications claires en français
- Démonstrations progressives

## Structure

```
threadsdemo1/
├── src/main/java/com/threadsdemo/
│   ├── MainApp.java                   # Application principale
│   ├── SimpleThreadDemo.java          # Leçon 1
│   ├── MultiThreadDemo.java           # Leçon 2
│   ├── SyncProblemDemo.java           # Leçon 3
│   ├── BankAccountDemo.java           # Leçon 4
│   ├── CircularProgressDemo.java      # Leçon 5
│   ├── ClockDemo.java                 # Leçon 6
│   ├── VerticalProgressDemo.java      # Leçon 7
│   └── HorizontalProgressDemo.java    # Leçon 8
├── documentation/
│   └── GUIDE_UTILISATION.md           # Guide complet avec Mermaid
├── pom.xml
├── run.bat
└── README.md
```

## Concepts Enseignés

- Création de threads (extends Thread)
- Méthodes run() et start()
- Thread.sleep()
- Exécution parallèle
- Race condition
- Synchronisation (synchronized)
- Variables partagées
- Variables volatiles
- Cas réel : compte bancaire

## Utilisation en Classe

1. **Leçons théoriques** (1, 2, 3, 4) : Concepts fondamentaux des threads
2. **Leçons visuelles** (5, 6, 7, 8) : Applications pratiques et animations
3. Les étudiants peuvent **expérimenter** avec les paramètres
4. Le **journal d'exécution** montre les événements en temps réel

## Caractéristiques Techniques

- JavaFX Canvas pour les dessins personnalisés
- GraphicsContext pour les cercles et horloges
- Platform.runLater() pour la mise à jour UI
- Threads avec volatile pour l'arrêt propre
- Gestion mémoire optimisée
- ScrollPane pour contenu responsive

## Pour les Étudiants

Chaque démonstration est autonome :
- Explications claires
- Code visible
- Boutons intuitifs
- Journal détaillé
- Réinitialisation facile

## Dépannage

**Erreur JavaFX manquant** :
```bash
mvn clean install
```

**Erreur version Java** :
```bash
java -version
# Doit afficher version 17 ou plus
```

## Licence

Projet éducatif libre d'utilisation pour l'enseignement.

---

**Version professionnelle avec compte bancaire et guide complet**
