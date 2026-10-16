// ========== GameState.java ==========
package com.dungeon.core;

/**
 * Enumeration representing different states of the game.
 * Implements the State pattern for game flow management.
 */
public enum GameState {
    /** Main menu state - displayed when game starts */
    MENU,
    
    /** Active gameplay state */
    PLAYING,
    
    /** Game paused state */
    PAUSED,
    
    /** Game over state - player lost */
    GAME_OVER,
    
    /** Victory state - player won the level */
    VICTORY,
    
    /** Settings menu state */
    SETTINGS
}