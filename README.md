# Demonstration Pedagogique des Threads - JavaFX

Application JavaFX professionnelle pour enseigner les concepts des threads en Java.

## Contenu de l'Application

L'application contient **7 demonstrations interactives** :

### LECONS PEDAGOGIQUES

#### Lecon 1 : Premier Thread
- Comprendre ce qu'est un thread
- Creer et demarrer son premier thread
- Difference entre `run()` et `start()`

#### Lecon 2 : Plusieurs Threads
- Execution parallele de plusieurs threads
- Chaque thread progresse independamment
- Visualisation du parallelisme reel

#### Lecon 3 : Synchronisation
- Probleme de "race condition"
- Demonstration visuelle du bug
- Solution avec le mot-cle `synchronized`

### DEMONSTRATIONS VISUELLES

#### Demo 4 : Progression Circulaire (NOUVEAU!)
- **5 barres de progression circulaires**
- Comme dans les applications modernes
- Chaque cercle se remplit de 0% a 100%
- Vitesses differentes pour chaque thread
- Design professionnel et moderne

#### Demo 5 : Trois Horloges
- 3 horloges animees en parallele
- Chaque horloge tourne a sa propre vitesse
- Visualisation du temps reel

#### Demo 6 : Progression Verticale
- 3 barres verticales
- Progression de bas en haut
- Differentes vitesses

#### Demo 7 : Progression Horizontale
- 4 barres horizontales
- Comme un gestionnaire de telechargement
- Etats visibles (En attente, En cours, Termine)

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

## Prerequis

- Java 17 ou superieur
- Maven 3.6+
- JavaFX 21 (telecharge automatiquement)

## Points Forts

- Interface professionnelle sans emojis
- **Gros boutons** faciles a cliquer
- Barres circulaires modernes
- Code source simple et commente
- Explications claires en francais
- Demonstrations progressives

## Structure

```
threadsdemo1/
├── src/main/java/com/threadsdemo/
│   ├── MainApp.java                   # Application principale
│   ├── SimpleThreadDemo.java          # Lecon 1
│   ├── MultiThreadDemo.java           # Lecon 2
│   ├── SyncProblemDemo.java           # Lecon 3
│   ├── CircularProgressDemo.java      # Demo circulaire (NOUVEAU)
│   ├── ClockDemo.java                 # Demo horloges
│   ├── VerticalProgressDemo.java      # Demo verticale
│   └── HorizontalProgressDemo.java    # Demo horizontale
├── pom.xml
├── run.bat
└── README.md
```

## Concepts Enseignes

- Creation de threads (extends Thread)
- Methodes run() et start()
- Thread.sleep()
- Execution parallele
- Race condition
- Synchronisation (synchronized)
- Variables partagees
- Variables volatiles

## Utilisation en Classe

1. **Commencez par les lecons** (1, 2, 3) pour la theorie
2. **Puis montrez les demos** (4, 5, 6, 7) pour les applications visuelles
3. Les etudiants peuvent **experimenter** avec les parametres
4. Le **journal d'execution** montre les evenements en temps reel

## Caracteristiques Techniques

- JavaFX Canvas pour les dessins personnalises
- GraphicsContext pour les cercles et horloges
- Platform.runLater() pour la mise a jour UI
- Threads avec volatile pour l'arret propre
- Gestion memoire optimisee

## Pour les Etudiants

Chaque demonstration est autonome :
- Explications claires
- Code visible
- Boutons intuitifs
- Journal detaille
- Reinitialisation facile

## Depannage

**Erreur JavaFX manquant** :
```bash
mvn clean install
```

**Erreur version Java** :
```bash
java -version
# Doit afficher version 17 ou plus
```

## License

Projet educatif libre d'utilisation pour l'enseignement.

---

**Version professionnelle avec barres circulaires**
