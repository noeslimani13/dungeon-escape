package com.dungeon.core;

import com.dungeon.ai.AutoplayController;
import com.dungeon.entity.*;
import com.dungeon.level.Level;
import com.dungeon.level.LevelManager;
import com.dungeon.physics.CollisionDetector;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;
import com.dungeon.utils.SoundManager;
import com.dungeon.utils.Timer;

import java.util.Iterator;

/**
 * Main game class managing game logic, entities, and state.
 * Includes sound effects, play time tracking, and proper audio management.
 */
public class Game {
    private AutoplayController autoplayController;
    private Player player;
    private Level currentLevel;
    private LevelManager levelManager;
    private CollisionDetector collisionDetector;
    private Timer gameTimer;
    private GameState gameState;
    private int currentLevelNumber;
    private int score;
    private boolean isRunning;
    private SoundManager soundManager;
    private long gameStartTime;
    private long totalPlayTime; // in seconds
    private long pauseStartTime = 0;
    private long totalPauseTime = 0;
    
    // Sound cooldown to prevent audio overload
    private long lastDamageSoundTime = 0;
    private long lastCollectSoundTime = 0;
    private static final long SOUND_COOLDOWN = 200; // 200ms between sounds

    /**
     * Constructs a new Game instance.
     */
    public Game() {
        this.levelManager = new LevelManager();
        this.collisionDetector = new CollisionDetector();
        this.gameTimer = new Timer(Config.getLevelTimeLimit(1));
        this.gameState = GameState.MENU;
        this.currentLevelNumber = 1;
        this.score = 0;
        this.isRunning = false;
        this.soundManager = SoundManager.getInstance();
        this.gameStartTime = 0;
        this.totalPlayTime = 0;
        this.autoplayController = new AutoplayController(this);
    }

    /**
     * Initializes a new game session.
     */
    public void initialize() {
        this.currentLevelNumber = 1;
        this.score = 0;
        this.gameStartTime = System.currentTimeMillis();
        this.totalPlayTime = 0;
        this.totalPauseTime = 0;
        loadLevel(currentLevelNumber);
        this.gameState = GameState.PLAYING;
        this.isRunning = true;
        
        // Play game music
        soundManager.playMusic("gameplay");
    }

    /**
     * Loads a specific level with adaptive difficulty.
     * 
     * @param levelNumber The level number to load
     */
    public void loadLevel(int levelNumber) {
        this.currentLevel = levelManager.loadLevel(levelNumber);
        this.player = new Player(currentLevel.getPlayerStartPosition());
        
        // Set adaptive time limit based on level
        int timeLimit = Config.getLevelTimeLimit(levelNumber);
        this.gameTimer = new Timer(timeLimit);
        this.gameTimer.reset();
        
        System.out.println("Level " + levelNumber + " loaded - Time limit: " + timeLimit + "s");
    }

    /**
     * Checks if player would collide with a wall at current position.
     * 
     * @param player Player to check
     * @return true if player is colliding with any wall
     */
    public boolean isPlayerCollidingWithWall(Player player) {
        Position pos = player.getPosition();
        int size = Config.PLAYER_SIZE;
        
        // Check ALL points on the player's hitbox
        for (int x = 0; x <= size; x += Config.COLLISION_CHECK_STEP) {
            if (isWallAtPixel(pos.getX() + x, pos.getY())) return true;
        }
        
        for (int x = 0; x <= size; x += Config.COLLISION_CHECK_STEP) {
            if (isWallAtPixel(pos.getX() + x, pos.getY() + size - 1)) return true;
        }
        
        for (int y = 0; y <= size; y += Config.COLLISION_CHECK_STEP) {
            if (isWallAtPixel(pos.getX(), pos.getY() + y)) return true;
        }
        
        for (int y = 0; y <= size; y += Config.COLLISION_CHECK_STEP) {
            if (isWallAtPixel(pos.getX() + size - 1, pos.getY() + y)) return true;
        }
        
        return false;
    }

    /**
     * Checks if a pixel position is inside a wall.
     */
    private boolean isWallAtPixel(int pixelX, int pixelY) {
        if (pixelX < 0 || pixelY < 0) return true;
        
        int tileX = pixelX / Config.TILE_SIZE;
        int tileY = pixelY / Config.TILE_SIZE;
        
        if (tileX < 0 || tileX >= currentLevel.getWidth() || 
            tileY < 0 || tileY >= currentLevel.getHeight()) {
            return true;
        }
        
        return currentLevel.getTile(tileX, tileY).isWall();
    }

    /**
     * Updates game state for one frame.
     */
    public void update(double deltaTime) {
        if (gameState != GameState.PLAYING || !isRunning) {
            return;
        }

        // Update autoplay AVANT l'update du player
        if (autoplayController.isActive()) {
            autoplayController.update(deltaTime);
        }

    

        // Update timer
        gameTimer.update(deltaTime);
        if (gameTimer.isExpired()) {
            gameOver();
            return;
        }

        // Update total play time (excluding pauses)
        updatePlayTime();

        // Update player
        player.update(deltaTime);

        // Constrain to level bounds
        constrainPlayerToBounds();

        // Update enemies
        for (Enemy enemy : currentLevel.getEnemies()) {
            enemy.update(deltaTime, player);
            
            // Check enemy collision with player
            if (collisionDetector.checkCollision(player, enemy) && !player.isInvulnerable()) {
                player.takeDamage(enemy.getDamage());
                playDamageSoundIfReady();
                
                if (player.isDead()) {
                    gameOver();
                    return;
                }
            }
        }

        // Check collisions with traps
        for (Trap trap : currentLevel.getTraps()) {
            trap.update(deltaTime);
            if (trap.isActive() && collisionDetector.checkCollision(player, trap)) {
                trap.trigger(player);
                playDamageSoundIfReady();
                
                if (player.isDead()) {
                    gameOver();
                    return;
                }
            }
        }

        // Check collisions with items
        Iterator<Item> itemIterator = currentLevel.getItems().iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (collisionDetector.checkCollision(player, item)) {
                item.collect(player);
                itemIterator.remove();
                playCollectSoundIfReady();
                
                // Award points for collecting items using Config method
                score += Config.getItemPoints(item.getClass().getSimpleName());
            }
        }

        // Check if player reached exit
        Exit exit = currentLevel.getExit();
        if (exit != null && collisionDetector.checkCollision(player, exit)) {
            if (player.hasAllKeys(currentLevel.getRequiredKeys())) {
                levelComplete();
            }
        }
    }

    /**
     * Updates total play time excluding pauses.
     */
    private void updatePlayTime() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        totalPlayTime = (elapsed - totalPauseTime) / 1000;
    }

    /**
     * Plays damage sound with cooldown to prevent audio spam.
     */
    private void playDamageSoundIfReady() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDamageSoundTime > SOUND_COOLDOWN) {
            soundManager.playSound("damage");
            lastDamageSoundTime = currentTime;
        }
    }

    /**
     * Plays collect sound with cooldown to prevent audio spam.
     */
    private void playCollectSoundIfReady() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCollectSoundTime > SOUND_COOLDOWN) {
            soundManager.playSound("collect");
            lastCollectSoundTime = currentTime;
        }
    }

    /**
     * Constrains player movement within level boundaries.
     */
    private void constrainPlayerToBounds() {
        Position pos = player.getPosition();
        int maxX = currentLevel.getWidth() * Config.TILE_SIZE - Config.PLAYER_SIZE;
        int maxY = currentLevel.getHeight() * Config.TILE_SIZE - Config.PLAYER_SIZE;
        
        int x = Math.max(0, Math.min(pos.getX(), maxX));
        int y = Math.max(0, Math.min(pos.getY(), maxY));
        
        if (x != pos.getX() || y != pos.getY()) {
            player.setPosition(new Position(x, y));
        }
    }

    /**
     * Handles level completion logic with proper audio sequencing.
     */
    private void levelComplete() {
        // Calculate bonuses
        score += gameTimer.getRemainingTime() * Config.TIME_BONUS_MULTIPLIER;
        score += Config.getLevelCompleteBonus(currentLevelNumber);
        
        // Play level up sound
        soundManager.playSound("levelup");
        
        currentLevelNumber++;
        
        if (currentLevelNumber > Config.MAX_LEVELS) {
            // Game completed! Victory sequence
            gameState = GameState.VICTORY;
            isRunning = false;
            
            // Stop music then play victory sound with delay
            soundManager.stopMusic();
            
            // Use a thread to add delay before victory sound
            new Thread(() -> {
                try {
                    Thread.sleep(300); // 300ms delay
                    soundManager.playSound("victory");
                } catch (InterruptedException e) {
                    System.err.println("Victory sound interrupted: " + e.getMessage());
                }
            }, "VictorySound").start();
            
            System.out.println("🎉 Victory! Final Score: " + score);
        } else {
            // Load next level
            loadLevel(currentLevelNumber);
        }
    }

    /**
     * Handles game over logic with proper audio sequencing.
     */
    private void gameOver() {
        System.out.println("💀 Game Over - Score: " + score + ", Level: " + currentLevelNumber);
        
        gameState = GameState.GAME_OVER;
        isRunning = false;
        
        // Stop music first
        soundManager.stopMusic();
        
        // Play game over sound with delay to ensure music stops
        new Thread(() -> {
            try {
                Thread.sleep(500); // 500ms delay
                soundManager.playSound("gameover");
                System.out.println("🔊 Game over sound played");
            } catch (InterruptedException e) {
                System.err.println("Game over sound interrupted: " + e.getMessage());
            }
        }, "GameOverSound").start();
    }

    /**
     * Pauses the game and music.
     */
    public void pause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            gameTimer.pause();
            soundManager.pauseMusic();
            pauseStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Resumes the game from pause.
     */
    public void resume() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            gameTimer.resume();
            soundManager.resumeMusic();
            
            // Update total pause time
            if (pauseStartTime > 0) {
                totalPauseTime += System.currentTimeMillis() - pauseStartTime;
                pauseStartTime = 0;
            }
        }
    }

    /**
     * Resets the game to initial state.
     */
    public void reset() {
        initialize();
    }

    // Getters
    public Player getPlayer() { 
        return player; 
    }
    
    public Level getCurrentLevel() { 
        return currentLevel; 
    }
    
    public GameState getGameState() { 
        return gameState; 
    }
    
    public int getCurrentLevelNumber() { 
        return currentLevelNumber; 
    }
    
    public int getScore() { 
        return score; 
    }
    
    public Timer getGameTimer() { 
        return gameTimer; 
    }
    
    public boolean isRunning() { 
        return isRunning; 
    }
    
    /**
     * Gets total play time in seconds, excluding pauses.
     * 
     * @return Play time in seconds
     */
    public long getTotalPlayTime() { 
        if (gameState == GameState.PLAYING) {
            updatePlayTime();
        }
        return totalPlayTime; 
    }
    
    public void setGameState(GameState state) { 
        this.gameState = state; 
    }

    // Ajoutez un getter
    public AutoplayController getAutoplayController() {
        return autoplayController;
    }
    
}