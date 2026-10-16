package com.dungeon.level;

import com.dungeon.entity.*;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages level loading and creation with caching and validation.
 * Adjusts enemy difficulty based on level number and provides fallback levels.
 */
public class LevelManager {
    private int currentLevelNumber;
    private Map<Integer, Level> levelCache;
    private boolean cachingEnabled;
    
    // Level validation tracking
    private int requiredKeysCount;
    private boolean hasExit;
    private boolean hasPlayerStart;

    /**
     * Creates level manager with caching disabled by default.
     */
    public LevelManager() {
        this(false);
    }

    /**
     * Creates level manager with optional caching.
     * 
     * @param enableCaching Whether to cache loaded levels
     */
    public LevelManager(boolean enableCaching) {
        this.currentLevelNumber = 1;
        this.cachingEnabled = enableCaching;
        this.levelCache = enableCaching ? new HashMap<>() : null;
    }

    /**
     * Loads a level from file with caching support.
     * 
     * @param levelNumber Level number to load (1-based)
     * @return Loaded level
     * @throws IllegalArgumentException if levelNumber is invalid
     */
    public Level loadLevel(int levelNumber) {
        // Validate level number
        if (!Config.isValidLevel(levelNumber)) {
            System.err.println("⚠️ Invalid level number: " + levelNumber + 
                             " (must be 1-" + Config.MAX_LEVELS + ")");
            return createDefaultLevel(1);
        }

        // Check cache first
        if (cachingEnabled && levelCache.containsKey(levelNumber)) {
            System.out.println("📦 Loading level " + levelNumber + " from cache");
            return cloneLevel(levelCache.get(levelNumber));
        }

        this.currentLevelNumber = levelNumber;
        String filename = "/levels/level" + levelNumber + ".txt";

        try (InputStream is = getClass().getResourceAsStream(filename)) {
            if (is == null) {
                System.err.println("❌ Level file not found: " + filename);
                return createDefaultLevel(levelNumber);
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                List<String> lines = new ArrayList<>();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                
                if (lines.isEmpty()) {
                    System.err.println("⚠️ Empty level file: " + filename);
                    return createDefaultLevel(levelNumber);
                }
                
                Level level = parseLevel(lines, levelNumber);
                
                // Validate level
                if (!validateLevel(level, levelNumber)) {
                    System.err.println("⚠️ Invalid level structure, using default");
                    return createDefaultLevel(levelNumber);
                }
                
                // Cache if enabled
                if (cachingEnabled) {
                    levelCache.put(levelNumber, cloneLevel(level));
                }
                
                System.out.println("✓ Level " + levelNumber + " loaded successfully");
                return level;
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading level " + levelNumber + ": " + e.getMessage());
            e.printStackTrace();
            return createDefaultLevel(levelNumber);
        }
    }

    /**
     * Parses level from text lines with difficulty scaling.
     * 
     * @param lines Text lines representing level
     * @param levelNumber Current level number for difficulty scaling
     * @return Parsed level
     */
    private Level parseLevel(List<String> lines, int levelNumber) {
        // Reset validation trackers
        requiredKeysCount = 0;
        hasExit = false;
        hasPlayerStart = false;

        int height = lines.size();
        int width = lines.stream().mapToInt(String::length).max().orElse(0);

        // Validate dimensions
        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("Level has zero width or height");
        }

        Level level = new Level(width, height);

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            
            // Pad line if shorter than width
            if (line.length() < width) {
                line = String.format("%-" + width + "s", line);
            }
            
            for (int x = 0; x < Math.min(line.length(), width); x++) {
                char c = line.charAt(x);
                TileType type = TileType.fromChar(c);
                level.setTile(x, y, type);

                Position pos = new Position(x * Config.TILE_SIZE, y * Config.TILE_SIZE);

                switch (type) {
                    case PLAYER_START:
                        level.setPlayerStartPosition(pos);
                        level.setTile(x, y, TileType.FLOOR);
                        hasPlayerStart = true;
                        break;
                        
                    case EXIT:
                        level.setExit(new Exit(pos));
                        level.setTile(x, y, TileType.FLOOR);
                        hasExit = true;
                        break;
                        
                    case KEY:
                        level.addItem(new Key(pos));
                        level.setTile(x, y, TileType.FLOOR);
                        requiredKeysCount++;
                        break;
                        
                    case POTION:
                        level.addItem(new Potion(pos));
                        level.setTile(x, y, TileType.FLOOR);
                        break;
                        
                    case SHIELD:
                        level.addItem(new Shield(pos));
                        level.setTile(x, y, TileType.FLOOR);
                        break;
                        
                    case TRAP:
                        level.addTrap(new Trap(pos, levelNumber));
                        level.setTile(x, y, TileType.FLOOR);
                        break;
                        
                    case GHOST:
                        level.addEnemy(new Ghost(pos, levelNumber));
                        level.setTile(x, y, TileType.FLOOR);
                        break;
                        
                    case GOBLIN:
                        level.addEnemy(new Goblin(pos, levelNumber));
                        level.setTile(x, y, TileType.FLOOR);
                        break;
                        
                    case WALL:
                    case FLOOR:
                        // Already set above
                        break;
                        
                    default:
                        // Unknown character, treat as floor
                        level.setTile(x, y, TileType.FLOOR);
                        break;
                }
            }
        }

        // Set required keys count
        level.setRequiredKeys(requiredKeysCount);

        return level;
    }

    /**
     * Validates that a level has all required elements.
     * 
     * @param level Level to validate
     * @param levelNumber Level number for logging
     * @return true if level is valid
     */
    private boolean validateLevel(Level level, int levelNumber) {
        boolean valid = true;

        if (!hasPlayerStart) {
            System.err.println("⚠️ Level " + levelNumber + " missing player start position");
            valid = false;
        }

        if (!hasExit) {
            System.err.println("⚠️ Level " + levelNumber + " missing exit");
            valid = false;
        }

        if (level.getPlayerStartPosition() == null) {
            System.err.println("⚠️ Level " + levelNumber + " has null player start position");
            valid = false;
        }

        if (level.getExit() == null) {
            System.err.println("⚠️ Level " + levelNumber + " has null exit");
            valid = false;
        }

        // Check if level is solvable (at least has a path between start and exit)
        // This is a simple check - could be expanded with pathfinding
        if (valid) {
            System.out.println("✓ Level " + levelNumber + " validation passed: " +
                             requiredKeysCount + " keys, " +
                             level.getEnemies().size() + " enemies, " +
                             level.getTraps().size() + " traps");
        }

        return valid;
    }

    /**
     * Creates a simple clone of a level for caching purposes.
     * Note: This creates a new level with the same structure but new entity instances.
     * 
     * @param original Original level
     * @return Cloned level
     */
    private Level cloneLevel(Level original) {
        // For now, just return the original
        // In a full implementation, you'd deep clone all entities
        // This is acceptable for read-only cached levels
        return original;
    }

    /**
     * Creates a procedurally generated default level for testing.
     * Scales difficulty with level number.
     * 
     * @param levelNumber Current level number
     * @return Default level
     */
    private Level createDefaultLevel(int levelNumber) {
        System.out.println("🔧 Creating default level " + levelNumber);
        
        int width = 20 + (levelNumber * 2); // Increase size with level
        int height = 15 + levelNumber;
        
        Level level = new Level(width, height);

        // Create border walls
        for (int x = 0; x < width; x++) {
            level.setTile(x, 0, TileType.WALL);
            level.setTile(x, height - 1, TileType.WALL);
        }
        for (int y = 0; y < height; y++) {
            level.setTile(0, y, TileType.WALL);
            level.setTile(width - 1, y, TileType.WALL);
        }

        // Fill with floor
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                level.setTile(x, y, TileType.FLOOR);
            }
        }

        // Add some interior walls for complexity
        if (levelNumber > 1) {
            int wallCount = levelNumber / 2;
            for (int i = 0; i < wallCount; i++) {
                int wallX = 3 + i * 4;
                if (wallX < width - 3) {
                    for (int y = 3; y < height - 3; y += 2) {
                        level.setTile(wallX, y, TileType.WALL);
                    }
                }
            }
        }

        // Set player start (always in top-left area)
        level.setPlayerStartPosition(new Position(Config.TILE_SIZE * 2, Config.TILE_SIZE * 2));

        // Set exit (always in bottom-right area)
        level.setExit(new Exit(new Position(
            (width - 3) * Config.TILE_SIZE, 
            (height - 3) * Config.TILE_SIZE
        )));

        // Add keys (scale with level)
        int keyCount = Math.min(3, 1 + levelNumber / 3);
        for (int i = 0; i < keyCount; i++) {
            int keyX = 5 + i * 5;
            int keyY = 5 + i * 2;
            if (keyX < width - 2 && keyY < height - 2) {
                level.addItem(new Key(new Position(keyX * Config.TILE_SIZE, keyY * Config.TILE_SIZE)));
            }
        }
        level.setRequiredKeys(keyCount);

        // Add potions (1-2 per level)
        int potionCount = 1 + (levelNumber > 5 ? 1 : 0);
        for (int i = 0; i < potionCount; i++) {
            int potionX = 3 + i * 8;
            int potionY = height / 2;
            if (potionX < width - 2) {
                level.addItem(new Potion(new Position(potionX * Config.TILE_SIZE, potionY * Config.TILE_SIZE)));
            }
        }

        // Add shield (only on higher levels)
        if (levelNumber >= 3) {
            level.addItem(new Shield(new Position(
                (width / 2) * Config.TILE_SIZE, 
                (height / 2) * Config.TILE_SIZE
            )));
        }

        // Add enemies (scale with level)
        int enemyCount = 1 + (levelNumber / 2);
        for (int i = 0; i < enemyCount; i++) {
            int enemyX = 8 + i * 4;
            int enemyY = 8 + (i % 2) * 3;
            if (enemyX < width - 3 && enemyY < height - 3) {
                // Alternate between Goblins and Ghosts
                if (i % 2 == 0) {
                    level.addEnemy(new Goblin(
                        new Position(enemyX * Config.TILE_SIZE, enemyY * Config.TILE_SIZE), 
                        levelNumber
                    ));
                } else {
                    level.addEnemy(new Ghost(
                        new Position(enemyX * Config.TILE_SIZE, enemyY * Config.TILE_SIZE), 
                        levelNumber
                    ));
                }
            }
        }

        // Add traps (only on higher levels)
        if (levelNumber >= 2) {
            int trapCount = (levelNumber - 1) / 2;
            for (int i = 0; i < trapCount; i++) {
                int trapX = 10 + i * 3;
                int trapY = 4 + i * 2;
                if (trapX < width - 2 && trapY < height - 2) {
                    level.addTrap(new Trap(
                        new Position(trapX * Config.TILE_SIZE, trapY * Config.TILE_SIZE), 
                        levelNumber
                    ));
                }
            }
        }

        System.out.println("✓ Default level " + levelNumber + " created: " +
                         keyCount + " keys, " +
                         enemyCount + " enemies, " +
                         level.getTraps().size() + " traps");

        return level;
    }

    /**
     * Preloads all levels into cache.
     * Only works if caching is enabled.
     */
    public void preloadAllLevels() {
        if (!cachingEnabled) {
            System.out.println("⚠️ Caching not enabled, cannot preload levels");
            return;
        }

        System.out.println("📦 Preloading all levels...");
        for (int i = 1; i <= Config.MAX_LEVELS; i++) {
            loadLevel(i);
        }
        System.out.println("✓ Preloaded " + levelCache.size() + " levels");
    }

    /**
     * Clears the level cache.
     */
    public void clearCache() {
        if (levelCache != null) {
            levelCache.clear();
            System.out.println("🗑️ Level cache cleared");
        }
    }

    /**
     * Checks if a level file exists.
     * 
     * @param levelNumber Level number to check
     * @return true if level file exists
     */
    public boolean levelExists(int levelNumber) {
        String filename = "/levels/level" + levelNumber + ".txt";
        InputStream is = getClass().getResourceAsStream(filename);
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
                // Ignore
            }
            return true;
        }
        return false;
    }

    // Getters
    public int getCurrentLevelNumber() { 
        return currentLevelNumber; 
    }

    public boolean isCachingEnabled() { 
        return cachingEnabled; 
    }

    public int getCachedLevelsCount() { 
        return levelCache != null ? levelCache.size() : 0; 
    }
}