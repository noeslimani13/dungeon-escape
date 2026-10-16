# Game Design Document

## Dungeon Escape

**Version:** 2.0.0  
**Date:** Novembre 2025  
**Équipe 14:** Gérard, Noé, Emma

***

## 1. Vision du Jeu

### 1.1 Concept de Base  
Dungeon Escape est un jeu de labyrinthe 2D en vue de dessus où le joueur incarne un aventurier prisonnier d'un donjon. L'objectif est de trouver les clés, éviter ou combattre les monstres, éviter les pièges et atteindre la sortie avant la fin du temps imparti.

### 1.2 Genre  
- Principal : Labyrinthe / Dungeon Crawler  
- Sous-genres : Action, Puzzle, Arcade  
- Perspective : 2D Top-down (vue de dessus)

### 1.3 Public Cible  
- Joueurs casual et intermédiaires  
- Fans de jeux rétro et dungeon crawlers  
- Âge recommandé : 8+

### 1.4 Plateformes  
- PC (Windows, Linux, macOS)  
- Nécessite Java 21+ et JavaFX 21+

### 1.5S Points Uniques (USP)  
1. Difficulté progressive sur 10 niveaux  
2. Double système de contrôle (clavier ET souris)  
3. Gestion du temps créant de l'urgence  
4. Design minimaliste mais engageant

***

## 2. Histoire et Univers

### 2.1 Scénario  
Un aventurier intrépide s'est aventuré trop profondément dans les donjons abandonnés sous la montagne de Greyspire. Piégé par un éboulement, il doit maintenant trouver une autre sortie en traversant 10 niveaux de plus en plus dangereux.

### 2.2 Univers  
- Ambiance : Donjon médiéval fantastique sombre  
- Ennemis : Créatures magiques (fantômes) et monstres (gobelins)  
- Environnement : Couloirs de pierre, pièges anciens, passages secrets

### 2.3 Progression Narrative  
- Niveaux 1-3 : Les caves d'entrée (facile)  
- Niveaux 4-6 : Les couloirs hantés (moyen)  
- Niveaux 7-9 : Les profondeurs maudites (difficile)  
- Niveau 10 : Le sanctuaire final (boss)

***

## 3. Gameplay

### 3.1 Objectif Principal  
À chaque niveau, le joueur doit :  
1. Explorer le labyrinthe  
2. Collecter TOUTES les clés  
3. Éviter les ennemis et les pièges  
4. Atteindre la sortie avant la fin du timer

### 3.2 Contrôles  

#### Clavier (WASD + Flèches)  
| Action | Touches |  
|--------|---------|  
| Haut | W ou ↑ |  
| Bas | S ou ↓ |  
| Gauche | A ou ← |  
| Droite | D ou → |  
| Pause | ESC |  

#### Souris  
| Action | Input |  
|--------|-------|  
| Déplacement | Clic gauche maintenu |  
| Direction | Le joueur suit le curseur |

*Clavier et souris peuvent être utilisés simultanément.*

### 3.3 Mécaniques de Base  

- Vitesse : 150 pixels/seconde  
- Collision : Impossible de traverser les murs  
- Bordures : Le joueur est confiné dans le niveau  

### Système de Vie  
- Vie initiale : 100 HP  
- Mort : HP = 0 → Game Over  
- Invulnérabilité de 1 seconde après dégâts  
- Indicateur : Joueur bleu clair quand invulnérable

### Timer  
- Temps décroissant par niveau (3:00 → 1:30)  
- Visible dans le HUD  
- Expiration = Game Over

### 3.4 Items Collectibles  

| Item    | Symbole | Couleur | Effet                 | Points |  
|---------|---------|---------|-----------------------|--------|  
| Clé     | K       | Or      | Nécessaire pour sortir| +100   |  
| Potion  | H       | Rouge   | +30 HP                | +50    |  
| Bouclier| S       | Cyan    | Protection 5s         | +75    |  

### 3.5 Ennemis  

#### Ghost (Fantôme) - Symbole: G  
- Couleur : Violet  
- IA : Poursuit le joueur dans un rayon de 200px  
- Niveau 1 : 60 vitesse, 10 dégâts  
- Niveau 10 : 150 vitesse, 28 dégâts  
- Progression : +10 vitesse, +2 dégâts par niveau

#### Goblin (Gobelin) - Symbole: O  
- Couleur : Vert  
- IA : Patrouille entre deux points (100px)  
- Niveau 1 : 50 vitesse, 8 dégâts  
- Niveau 10 : 122 vitesse, 26 dégâts  
- Progression : +8 vitesse, +2 dégâts par niveau

### 3.6 Obstacles  

#### Murs - Symbole: #  
- Bloque le passage, structure le labyrinthe  

#### Pièges - Symbole: T  
- Couleur : Orange (inactif) / Rouge (actif)  
- Niveau 1 : 15 dégâts  
- Niveau 10 : 42 dégâts  
- Cooldown : 2 secondes  
- Progression : +3 dégâts par niveau

***

## 4. Système de Score

### 4.1 Points par Item  
- Clé (K) : 100 points  
- Potion (H) : 50 points  
- Bouclier (S) : 75 points

### 4.2 Bonus de Niveau  

| Niveau | Bonus  |  
|--------|--------|  
| 1      | 500    |  
| 2      | 600    |  
| 3      | 700    |  
| ...    | +100/niveau |  
| 10     | 1400   |  

### 4.3 Bonus de Temps  
- 10 points par seconde restante

### 4.4 Exemple de Calcul  
Niveau 5 terminé avec 30 sec restantes :  
- Items collectés : 3 clés + 1 potion = 350  
- Bonus niveau : 900  
- Bonus temps : 30 × 10 = 300  
- Total niveau 5 : 1550 points

***

## 5. Les 10 Niveaux

### 5.1 Vue d'Ensemble  

| Niv. | Nom           | Temps | Ennemis | Clés | Difficulté |  
|------|---------------|-------|---------|------|------------|  
| 1    | L'Éveil       | 3:00  | 1       | 1    | ⭐☆☆☆☆     |  
| 2    | Premiers Pas  | 2:50  | 2       | 2    | ⭐⭐☆☆☆     |  
| 3    | L'Exploration | 2:40  | 3       | 2    | ⭐⭐⭐☆☆     |  
| 4    | La Complexité | 2:30  | 4       | 3    | ⭐⭐⭐☆☆     |  
| 5    | Le Challenge  | 2:20  | 5       | 3    | ⭐⭐⭐⭐☆     |  
| 6    | La Maîtrise   | 2:10  | 6       | 4    | ⭐⭐⭐⭐☆     |  
| 7    | Expert        | 2:00  | 7       | 4    | ⭐⭐⭐⭐⭐     |  
| 8    | Hardcore      | 1:50  | 8       | 5    | ⭐⭐⭐⭐⭐     |  
| 9    | Cauchemar     | 1:40  | 9       | 5    | ⭐⭐⭐⭐⭐     |  
| 10   | Sanctuaire    | 1:30  | 11      | 6    | ⭐⭐⭐⭐⭐     |

*NB: Ennemis = Ghosts + Goblins*

### 5.2 Symboles des Niveaux  

| Symbole | Nom     | Description            |  
|---------|---------|------------------------|  
| `#`     | Mur     | Bloque le passage      |  
| `.`     | Sol     | Chemin libre           |  
| `P`     | Player  | Position de départ     |  
| `E`     | Exit    | Sortie du niveau       |  
| `K`     | Key     | Clé (or)               |  
| `H`     | Health  | Potion (rouge)         |  
| `S`     | Shield  | Bouclier (cyan)        |  
| `T`     | Trap    | Piège (orange)         |  
| `G`     | Ghost   | Fantôme (violet)       |  
| `O`     | gOblin  | Gobelin (vert)         |

### 5.3 Détail des Niveaux  

- **Niveau 1 - "L'Éveil"**  
  Tutoriel, 3 min, 1 Goblin, 1 clé, 1 potion, 1 piège  

- **Niveau 5 - "Le Challenge"**  
  Test maîtrise, 2min 20s, 3 Ghosts + 2 Goblins, 3 clés, 1 potion + 1 bouclier, 2 pièges  

- **Niveau 10 - "Le Sanctuaire"**  
  Niveau final extrême, 1min 30s, 6 Ghosts + 5 Goblins rapides, 6 clés, 1 potion + 1 bouclier, 2 pièges

***

## 6. Interface Utilisateur

### 6.1 Menu Principal  
```
┌─────────────────────┐  
│   DUNGEON ESCAPE    │  
├─────────────────────┤  
│    [New Game]       │  
│    [Leaderboard]    │  
│    [Settings]       │  
│    [Exit]           │  
└─────────────────────┘  
```

### 6.2 HUD (In-Game)  
```
┌─────────────────────────────────────────────────────────────┐  
│Level: 5 │ Health: 75 │ Keys: 2/3 │ Time: 01:45 │ Score: 1250│  
└─────────────────────────────────────────────────────────────┘  
```

### 6.3 Menu Pause (ESC)  
```
┌─────────────────────┐  
│    GAME PAUSED      │  
├─────────────────────┤  
│    [Resume]         │  
│    [New Game]       │  
│    [Leaderboard]    │  
│    [Settings]       │  
│    [Exit]           │  
└─────────────────────┘  
```

***

## 7. Style Visuel

### 7.1 Direction Artistique  
- Style : Minimaliste géométrique  
- Palette : Sombre avec accents colorés  
- Rendu : Formes simples (cercles, carrés, lignes)

### 7.2 Palette de Couleurs  

| Élément               | Couleur     | Code Hex  |  
|-----------------------|-------------|-----------|  
| Background            | Noir        | #000000   |  
| Murs                  | Gris        | #808080   |  
| Sol                   | Gris foncé  | #404040   |  
| Joueur                | Bleu        | #0000FF   |  
| Joueur invulnérable   | Bleu clair  | #ADD8E6   |  
| Ghost                 | Violet      | #800080   |  
| Goblin                | Vert        | #00FF00   |  
| Clé                   | Or          | #FFD700   |  
| Potion                | Rouge       | #FF0000   |  
| Bouclier              | Cyan        | #00FFFF   |  
| Piège inactif         | Orange      | #FFA500   |  
| Piège actif           | Rouge foncé | #8B0000   |  
| Sortie                | Marron      | #8B4513   |  
| Texte EXIT            | Vert clair  | #90EE90   |

### 7.3 Représentation du Joueur  
Le joueur est un petit bonhomme (stick figure) avec tête ronde (30% taille), corps ligne verticale, bras étendus, deux jambes et deux yeux blancs.

***

## 8. Architecture Technique

### 8.1 Stack Technologique  
- Langage : Java 21  
- Framework UI : JavaFX 21  
- Build Tool : Maven 3.8+  
- Tests : JUnit 5  
- Couverture : JaCoCo

### 8.2 Architecture OOP  

Hiérarchie Entités :  
```
Entity (abstract)  
├── Player (move(), takeDamage(), heal())  
├── Enemy (abstract)  
│   ├── Ghost (ChaseAI)  
│   └── Goblin (PatrolAI)  
├── Item (abstract)  
│   ├── Key  
│   ├── Potion  
│   └── Shield  
├── Trap  
└── Exit  
```

### Design Patterns  
1. Strategy Pattern : AIBehavior (ChaseAI, PatrolAI)  
2. State Pattern : GameState (MENU, PLAYING, PAUSED, GAME_OVER, VICTORY)  
3. Template Method : Entity.update()  
4. Observer-like : Input handling

### 8.3 Packages  
```
com.dungeon  
├── core        # Game, GameState, GameEngine  
├── entity      # Player, Enemy, Item, Trap, Exit  
├── physics     # Position, Direction, CollisionDetector  
├── level       # Level, LevelManager, Tile, TileType  
├── ai          # AIBehavior, ChaseAI, PatrolAI  
├── input       # KeyboardHandler, MouseHandler  
├── ui          # GameWindow, MenuScreen, GameScreen, HUD  
└── utils       # Config, Timer, SoundManager  
```

### 8.4 Performance  
- Target FPS : 60  
- Frame time : ~16.67ms  
- Collision : AABB (Axis-Aligned Bounding Box)  
- Memory : < 100MB

***

## 9. Conditions de Fin

### 9.1 Victoire  
- Terminer les 10 niveaux  
- Collecter toutes les clés  
- Atteindre la sortie avant expiration du timer

### 9.2 Défaite (Game Over)  
- Points de vie = 0  
- Timer expire

***

## 10. Développement

### 10.1 Roadmap  

| Phase     | Description               | Statut |  
|-----------|---------------------------|--------|  
| Core      | Structure, entités, physique| ✅ Done |  
| Gameplay  | Ennemis, items, niveaux   | ✅ Done |  
| UI        | Menu, HUD, settings       | ✅ Done |  
| Polish    | 10 niveaux, difficulté    | ✅ Done |  
| Tests     | JUnit, JaCoCo             | ✅ Done |  
| Docs      | README, GDD, Javadoc      | ✅ Done |

### 10.2 Métriques  

| Métrique       | Cible | Actuel |  
|----------------|-------|--------|  
| Test Coverage  | >70%  | ~75%   |  
| Niveaux       | 10    | 10     |  
| FPS           | 60    | 60     |  
| Bugs critiques| 0     | 0      |

***

## 11. Crédits

### 11.1 Développement  
- Projet T-JAV-501 Epitech  
- Architecture OOP complète  
- JavaFX pour l'interface

### 11.2 Inspirations  
- Pac-Man : mécaniques de poursuite  
- The Legend of Zelda : exploration de donjon  
- Boulder Dash : navigation grille

***

## 12. Annexes

### A. Glossaire  
- **AABB** : Axis-Aligned Bounding Box (détection collision)  
- **FPS** : Frames Per Second  
- **HP** : Health Points (points de vie)  
- **HUD** : Heads-Up Display  
- **OOP** : Object-Oriented Programming

### B. Changelog  

**Version 2.0.0 (Novembre 2025)**  
- Extension à 10 niveaux  
- Difficulté progressive  
- Correction des collisions murs  
- Ajout contrôle souris  
- Nouveau design du joueur (stick figure)  
- Documentation complète

