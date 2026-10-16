package com.dungeon.level;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LevelManagerTest {
    private LevelManager levelManager;

    @BeforeEach
    void setUp() {
        // Activer le cache pour tester aussi ce comportement
        levelManager = new LevelManager(true);
    }

    @Test
    @DisplayName("Vérification du chargement d'un niveau")
    void testLevelLoading() {
        assertDoesNotThrow(() -> {
            Level level = levelManager.loadLevel(1);
            assertNotNull(level, "Le niveau chargé ne doit pas être nul");
        });
    }

    @Test
    @DisplayName("Vérification des dimensions d'un niveau")
    void testLevelDimensions() {
        Level level = levelManager.loadLevel(1);
        assertTrue(level.getWidth() > 0, "La largeur du niveau doit être > 0");
        assertTrue(level.getHeight() > 0, "La hauteur du niveau doit être > 0");
    }

    @Test
    @DisplayName("Vérification de la position de départ du joueur")
    void testLevelPlayerStart() {
        Level level = levelManager.loadLevel(1);
        assertNotNull(level.getPlayerStartPosition(), "La position de départ du joueur doit exister");
    }

    @Test
    @DisplayName("Vérification de la présence d'une sortie")
    void testLevelExit() {
        Level level = levelManager.loadLevel(1);
        assertNotNull(level.getExit(), "Le niveau doit avoir une sortie");
    }

    @Test
    @DisplayName("Vérification des murs aux bords")
    void testLevelWalls() {
        Level level = levelManager.loadLevel(1);
        assertTrue(level.getTile(0, 0).isWall(), "Le coin supérieur gauche doit être un mur");
        assertTrue(level.getTile(level.getWidth() - 1, 0).isWall(), "Le coin supérieur droit doit être un mur");
        assertTrue(level.getTile(0, level.getHeight() - 1).isWall(), "Le coin inférieur gauche doit être un mur");
        assertTrue(level.getTile(level.getWidth() - 1, level.getHeight() - 1).isWall(), "Le coin inférieur droit doit être un mur");
    }

    @Test
    @DisplayName("Le cache doit fonctionner")
    void testCacheLevels() {
        if (!levelManager.isCachingEnabled()) {
            levelManager = new LevelManager(true);
        }
        levelManager.clearCache();

        Level levelPremierChargement = levelManager.loadLevel(1);
        Level levelDeuxiemeChargement = levelManager.loadLevel(1);

        assertSame(levelPremierChargement, levelDeuxiemeChargement, "Les niveaux mis en cache doivent être les mêmes instances");
        assertEquals(1, levelManager.getCachedLevelsCount(), "Le cache doit contenir exactement 1 niveau");
    }

    @Test
    @DisplayName("Charger et valider tous les niveaux de 1 à 10")
    void testChargerTousLesNiveaux() {
        for (int i = 1; i <= 10; i++) {
            final int levelNum = i;  // variable finale pour lambda
            assertDoesNotThrow(() -> {
                Level level = levelManager.loadLevel(levelNum);
                assertNotNull(level, "Le niveau " + levelNum + " doit être chargé");
                assertTrue(level.getWidth() > 0, "Le niveau " + levelNum + " doit avoir une largeur > 0");
                assertTrue(level.getHeight() > 0, "Le niveau " + levelNum + " doit avoir une hauteur > 0");
                assertNotNull(level.getPlayerStartPosition(), "Le niveau " + levelNum + " doit avoir une position de départ");
                assertNotNull(level.getExit(), "Le niveau " + levelNum + " doit avoir une sortie");
            }, "Le chargement du niveau " + levelNum + " doit passer sans erreur");
        }
    }
}
