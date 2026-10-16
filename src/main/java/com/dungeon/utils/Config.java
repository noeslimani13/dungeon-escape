package com.dungeon.utils;

/**
 * Configuration constants for the game.
 * Centralized configuration following best practices.
 * Final class, private constructor to prevent instantiation.
 */
public final class Config {
    // Window settings
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final String GAME_TITLE = "Dungeon Escape";

    // Tile settings
    public static final int TILE_SIZE = 32;

    // Player settings
    public static final int PLAYER_SIZE = 28;
    public static final int PLAYER_SPEED = 150;
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final int DAMAGE_INVULNERABILITY_MS = 1000;

    // Enemy settings - Base values (increase per level)
    public static final int ENEMY_SIZE = 28;
    public static final int GHOST_SPEED_BASE = 60; // Level 1
    public static final int GHOST_SPEED_INCREMENT = 10; // Per level
    public static final int GHOST_DAMAGE_BASE = 10;
    public static final int GHOST_DAMAGE_INCREMENT = 2;

    public static final int GOBLIN_SPEED_BASE = 50;
    public static final int GOBLIN_SPEED_INCREMENT = 8;
    public static final int GOBLIN_DAMAGE_BASE = 8;
    public static final int GOBLIN_DAMAGE_INCREMENT = 2;

    // Item settings
    public static final int ITEM_SIZE = 24;
    public static final int POTION_HEAL_AMOUNT = 30;
    public static final int SHIELD_DURATION = 5000; // milliseconds

    // Trap settings
    public static final int TRAP_SIZE = 28;
    public static final int TRAP_DAMAGE_BASE = 15;
    public static final int TRAP_DAMAGE_INCREMENT = 3; // Per level
    public static final long TRAP_COOLDOWN = 2000;

    // Exit settings
    public static final int EXIT_SIZE = 32;

    // Game settings - 10 LEVELS
    public static final int LEVEL_TIME_LIMIT_BASE = 180; // 3 minutes for level 1
    public static final int TIME_DECREASE_PER_LEVEL = 10; // Decrease 10s per level
    public static final int MAX_LEVELS = 10;

    // Scoring
    public static final int KEY_POINTS = 100;
    public static final int POTION_POINTS = 50;
    public static final int SHIELD_POINTS = 75;
    public static final int LEVEL_COMPLETE_BONUS_BASE = 500;
    public static final int LEVEL_BONUS_INCREMENT = 100; // Per level
    public static final int TIME_BONUS_MULTIPLIER = 10;

    // Frame rate
    public static final double TARGET_FPS = 60.0;
    public static final double FRAME_TIME = 1.0 / TARGET_FPS;
    
    // Sound cooldown settings
    public static final long SOUND_COOLDOWN_MS = 200; // 200ms between similar sounds
    
    // Collision detection settings
    public static final int COLLISION_CHECK_STEP = 4; // Pixel step for collision checks

    // Private constructor to prevent instantiation
    private Config() {}

    // Méthodes utilitaires (inchangées)
    public static int getGhostSpeed(int level) {
        return GHOST_SPEED_BASE + (level - 1) * GHOST_SPEED_INCREMENT;
    }
    public static int getGhostDamage(int level) {
        return GHOST_DAMAGE_BASE + (level - 1) * GHOST_DAMAGE_INCREMENT;
    }
    public static int getGoblinSpeed(int level) {
        return GOBLIN_SPEED_BASE + (level - 1) * GOBLIN_SPEED_INCREMENT;
    }
    public static int getGoblinDamage(int level) {
        return GOBLIN_DAMAGE_BASE + (level - 1) * GOBLIN_DAMAGE_INCREMENT;
    }
    public static int getTrapDamage(int level) {
        return TRAP_DAMAGE_BASE + (level - 1) * TRAP_DAMAGE_INCREMENT;
    }
    public static int getLevelTimeLimit(int level) {
        return Math.max(60, LEVEL_TIME_LIMIT_BASE - (level - 1) * TIME_DECREASE_PER_LEVEL);
    }
    public static int getLevelCompleteBonus(int level) {
        return LEVEL_COMPLETE_BONUS_BASE + (level - 1) * LEVEL_BONUS_INCREMENT;
    }
    public static int getItemPoints(String itemType) {
        return switch (itemType) {
            case "Key" -> KEY_POINTS;
            case "Potion" -> POTION_POINTS;
            case "Shield" -> SHIELD_POINTS;
            default -> 0;
        };
    }
    public static boolean isValidLevel(int level) {
        return level >= 1 && level <= MAX_LEVELS;
    }
}
