# 🏰 Dungeon Escape

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Coverage](https://img.shields.io/badge/coverage->70%25-green.svg)]()
[![Levels](https://img.shields.io/badge/levels-10-purple.svg)]()

Un jeu de labyrinthe 2D développé en Java avec JavaFX, démontrant les principes de la Programmation Orientée Objet (OOP).

---

## 🎮 Description

**Tu incarnes un aventurier prisonnier d'un donjon labyrinthique rempli d'ennemis et de pièges.**

**Ton objectif :**
- 🔑 Trouver toutes les clés
- 👹 Éviter ou combattre les monstres
- ⚠️ Éviter les pièges
- 🚪 Atteindre la sortie avant la fin du temps imparti

**Chaque niveau devient plus difficile :** ennemis plus rapides, labyrinthes plus complexes, moins de temps !

---

## ✨ Fonctionnalités

### 🎯 Gameplay
- ✅ **10 niveaux** avec difficulté progressive
- ✅ **Triple contrôle** : Clavier (WASD/Flèches), Souris, et Mode Auto (F1)
- ✅ **2 types d'ennemis** : Ghosts (poursuivent) et Goblins (patrouillent)
- ✅ **3 types d'items** : Clés, Potions, Boucliers
- ✅ **Pièges** avec dégâts croissants
- ✅ **Timer** décroissant par niveau (3:00 → 1:30)
- ✅ **Système de score** avec bonus temps
- ✅ **Collisions parfaites** avec les murs
- ✅ **Mode Autoplay** (démonstration IA)

### 🔊 Audio
- ✅ Musique de fond (menu, gameplay)
- ✅ Effets sonores (collecte, dégâts, victoire, game over)
- ✅ Contrôle du volume
- ✅ Toggle on/off (M pour musique, N pour sons)

### 🖥️ Interface
- ✅ Menu principal complet
- ✅ HUD temps réel (Vie, Clés, Temps, Score, Temps de jeu)
- ✅ Écran de paramètres (contrôles + audio)
- ✅ Écrans Game Over et Victoire
- ✅ Menu pause (ESC)
- ✅ Indicateur autoplay (🤖 AUTO)

### 🏗️ Architecture OOP
- ✅ **Encapsulation** : Attributs privés, getters/setters
- ✅ **Héritage** : Entity → Player, Enemy, Item
- ✅ **Polymorphisme** : Différents comportements d'ennemis
- ✅ **Abstraction** : Interfaces (AIBehavior) et classes abstraites
- ✅ **Design Patterns** : Singleton, Strategy, State, Observer

---

## 🎮 Contrôles

### ⌨️ Clavier
| Action | Touches |
|--------|---------|
| Haut | `W` ou `↑` |
| Bas | `S` ou `↓` |
| Gauche | `A` ou `←` |
| Droite | `D` ou `→` |
| Pause | `ESC` |

### 🖱️ Souris
| Action | Input |
|--------|-------|
| Déplacement | Clic gauche maintenu |
| Direction | Le joueur suit le curseur |

### ⚡ Raccourcis
| Action | Touche |
|--------|--------|
| Toggle Autoplay | `F1` |
| Toggle Musique | `M` |
| Toggle Sons | `N` |

**💡 Astuce :** Clavier ET souris peuvent être utilisés simultanément !

---

## 🤖 Mode Autoplay

Le jeu inclut un mode de démonstration automatique :

- **Activation :** Appuyez sur `F1` pendant le jeu
- **Désactivation :** Appuyez sur `F1` ou cliquez avec la souris
- **Comportement :** L'IA contrôle le joueur automatiquement
  - Évite les ennemis proches
  - Collecte les clés en priorité
  - Prend les potions si vie < 50%
  - Se dirige vers la sortie quand toutes les clés sont collectées

**Indicateur :** 🤖 AUTO s'affiche dans le HUD quand actif

---

## 📊 Les 10 Niveaux

| Niveau | Temps | Ennemis | Clés | Difficulté |
|--------|-------|---------|------|------------|
| 1 | 3:00 | 1 | 1 | ⭐☆☆☆☆ |
| 2 | 2:50 | 2 | 2 | ⭐⭐☆☆☆ |
| 3 | 2:40 | 3 | 2 | ⭐⭐⭐☆☆ |
| 4 | 2:30 | 4 | 3 | ⭐⭐⭐☆☆ |
| 5 | 2:20 | 5 | 3 | ⭐⭐⭐⭐☆ |
| 6 | 2:10 | 6 | 4 | ⭐⭐⭐⭐☆ |
| 7 | 2:00 | 7 | 4 | ⭐⭐⭐⭐⭐ |
| 8 | 1:50 | 8 | 5 | ⭐⭐⭐⭐⭐ |
| 9 | 1:40 | 9 | 5 | ⭐⭐⭐⭐⭐ |
| 10 | 1:30 | 11 | 6 | ⭐⭐⭐⭐⭐ |

### 📈 Progression de Difficulté
- **Ennemis plus rapides** : +8 à +10 vitesse par niveau
- **Dégâts augmentés** : +2 à +3 par niveau
- **Moins de temps** : -10 secondes par niveau
- **Plus de clés** : 1 → 6 clés à trouver

---

## 🗺️ Légende des Niveaux

| Symbole | Nom | Description |
|---------|-----|-------------|
| `#` | Mur | Bloque le passage |
| `.` | Sol | Chemin libre |
| `P` | Player | Position de départ |
| `E` | Exit | Sortie du niveau |
| `K` | Key | Clé dorée (+100 pts) |
| `H` | Health | Potion rouge (+30 HP) |
| `S` | Shield | Bouclier cyan (5s) |
| `T` | Trap | Piège (dégâts variables) |
| `G` | Ghost | Fantôme violet (poursuit) |
| `O` | gOblin | Gobelin vert (patrouille) |

---

## 📦 Prérequis

- **Java JDK** : Version 21 ou supérieure
- **Maven** : Version 3.8 ou supérieure
- **IDE recommandé** : VS Code, IntelliJ IDEA, Eclipse

### Vérification
```bash
java -version
# java version "21.x.x"

mvn -version
# Apache Maven 3.8.x

for f in *.mp3; do ffmpeg -i "$f" "${f%.mp3}.wav"; done
# Convertion des songs en wav
```

---

## 🚀 Installation

### 1. Cloner le Projet
```bash
git clone <repository-url>
cd dungeon-escape
```

### 2. Compiler
```bash
mvn clean compile
```

### 3. Tester
```bash
mvn test
```

### 4. Lancer le Jeu
```bash
mvn javafx:run
```

---

## 🎯 Commandes Maven

```bash
# Compilation
mvn clean compile

# Exécution
mvn javafx:run

# Tests unitaires
mvn test

# Rapport de couverture
mvn jacoco:report

# Documentation Javadoc
mvn javadoc:javadoc

# Tout générer
mvn clean install javadoc:javadoc jacoco:report
```

---

## 🧪 Tests

### Exécution
```bash
mvn test
```

### Couverture de Code
```bash
mvn jacoco:report
```
**Couverture cible :** > 70%  
**Rapport :** `target/site/jacoco/index.html`

---

## 📁 Structure du Projet

```
dungeon-escape/
├── pom.xml
├── README.md
├── dungeon_scores.dat         # Fichier pour stocker la performance des joueurs
├── .gitignore
├── docs/                      # Documentation (9 fichiers)
│   ├── GDD.md
│   ├── *.png (images des diagrammes)
│   └── *.puml (diagrammes UML)
├── src/
│   ├── main/
│   │   ├── java/com/dungeon/
│   │   │   ├── Main.java      # Code source (38 classes Java)
│   │   │   ├── core/          # Game, GameState
│   │   │   ├── entity/        # Player, Enemy, Item, ...
│   │   │   ├── physics/       # Position, Collision, ...
│   │   │   ├── level/         # Level, LevelManager, ...
│   │   │   ├── ai/            # AIBehavior, ChaseAI, AutoplayController, ...
│   │   │   ├── input/         # Keyboard, Mouse, ...
│   │   │   ├── ui/            # GameWindow, Menu, HUD, Renderer, ...
│   │   │   └── utils/         # Config, Timer, SoundManager, ...
│   │   └── resources/         # Ressources (19 fichiers)
│   │       ├── levels/        # level1.txt ... level10.txt    
|   │       └── sounds
|   │           ├── effects    # Effets songs (collecte, click, etc.;)
|   |           └── music      # Songs sur le jeu (menu principal, interface du jeu, etc.;)
│   └── test/                  # Tests unitaires (5 classes)
└── target/                    # Généré par Maven
```

---

## 🏗️ Architecture OOP

### Principes Implémentés

#### 1. Encapsulation ✅
```java
public class Player extends Entity {
    private int health;        // Attribut privé
    public int getHealth() { return health; }  // Getter
}
```

#### 2. Héritage ✅
```
Entity (abstract)
├── Player
├── Enemy (abstract)
│   ├── Ghost
│   └── Goblin
├── Item (abstract)
│   ├── Key, Potion, Shield
├── Trap
└── Exit
```

#### 3. Polymorphisme ✅
```java
for (Enemy enemy : level.getEnemies()) {
    enemy.update(deltaTime, player);  // Comportement polymorphe
}
```

#### 4. Abstraction ✅
```java
public interface AIBehavior {
    void execute(Enemy enemy, Player player, double deltaTime);
}
```

### Design Patterns

| Pattern | Utilisation |
|---------|-------------|
| **Singleton** | SoundManager |
| **Strategy** | AIBehavior (ChaseAI, PatrolAI) |
| **State** | GameState |
| **Observer-like** | Input handling |

---

## 🎓 Critères du Projet T-JAV-501

### Principes OOP ✅
- [x] **oop-classes** : Organisation en packages
- [x] **oop-inheritance** : Hiérarchie Entity
- [x] **oop-encapsulation** : Attributs privés
- [x] **oop-abstraction** : Interfaces + abstraites
- [x] **oop-polymorphism** : Comportements variés
- [x] **oop-fantastic4** : Tous appliqués

### Features ✅
- [x] **gui-basic** : Interface JavaFX
- [x] **gui-full** : Menu, HUD, Settings
- [x] **demo** : Jeu jouable
- [x] **full-game** : 10 niveaux, distribuable
- [x] **features-basic** : Mouvement, collision, ennemis
- [x] **features-basic-all** : Toutes mécaniques
- [x] **features-extra** : Autoplay, sons, souris
- [x] **customization** : Audio, contrôles

### Tests et Documentation ✅
- [x] **tests-unit** : 25+ tests
- [x] **tests-sequence** : Maven
- [x] **tests-coverage** : JaCoCo > 70%
- [x] **tests-automation** : `mvn test`
- [x] **doc-diagram** : UML
- [x] **doc-gdd** : Game Design Document
- [x] **doc-automation** : Javadoc
- [x] **doc-basic** : README

### Versioning et Livraison ✅
- [x] **versioning_basics** : Git + .gitignore
- [x] **functional-delivery** : Compilable
- [x] **presentation** : Prêt pour démo

---

## 🏆 Conditions de Fin

### Victoire 🎉
- Terminer les **10 niveaux**
- Collecter toutes les clés
- Atteindre la sortie dans le temps

### Game Over 💀
- Points de vie = 0
- Timer expire

---

## 📈 Statistiques du Projet

| Métrique | Valeur |
|----------|--------|
| Fichiers Java | ~45 |
| Lignes de code | ~3500 |
| Tests unitaires | 25+ |
| Couverture | >70% |
| Niveaux | 10 |
| Design Patterns | 4 |

---

## 👥 Auteur

Groupe 14 :
- Gérard
- Noé
- Emma

---

## ⚡ Démarrage Rapide

```bash
# 1. Compiler
mvn clean compile

# 2. Jouer !
mvn javafx:run

# Contrôles : WASD/Flèches ou Souris
# Autoplay : F1
# Objectif : 10 niveaux, toutes les clés, EXIT !
```

**Bonne chance, aventurier ! 🎮⚔️🔑**
