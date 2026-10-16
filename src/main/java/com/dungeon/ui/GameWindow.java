package com.dungeon.ui;

import com.dungeon.ai.AutoplayController;
import com.dungeon.core.Game;
import com.dungeon.core.GameState;
import com.dungeon.input.KeyboardHandler;
import com.dungeon.input.MouseHandler;
import com.dungeon.utils.Config;
import com.dungeon.utils.ScoreManager;
import com.dungeon.utils.SoundManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main window class for the game.
 * Manages screens, game loop, input handling, and lifecycle.
 */
public class GameWindow extends Application {
    private Game game;
    private Scene scene;
    private StackPane root;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    private LeaderboardScreen leaderboardScreen;
    private KeyboardHandler keyboardHandler;
    private MouseHandler mouseHandler;
    private AnimationTimer gameLoop;
    private long lastFrameTime;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    
    // Window state tracking
    private boolean isFullScreen = false;
    private double windowWidth = Config.WINDOW_WIDTH;
    private double windowHeight = Config.WINDOW_HEIGHT;

    @Override
    public void start(Stage primaryStage) {
        // Initialize managers
        scoreManager = ScoreManager.getInstance();
        soundManager = SoundManager.getInstance();
        
        // Initialize game and handlers
        game = new Game();
        keyboardHandler = new KeyboardHandler();
        mouseHandler = new MouseHandler();

        // Connect collision checkers
        keyboardHandler.setCollisionChecker(player -> game.isPlayerCollidingWithWall(player));
        mouseHandler.setCollisionChecker(player -> game.isPlayerCollidingWithWall(player));

        // Create root pane
        root = new StackPane();
        scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // Setup input handling
        setupInputHandlers(scene, primaryStage);

        // Create screens
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(game, keyboardHandler);
        settingsScreen = new SettingsScreen(this);
        leaderboardScreen = new LeaderboardScreen(this);

        // Show menu initially
        showMenu();

        // Configure stage
        setupStage(primaryStage);

        // Start game loop
        startGameLoop();
        
        // Setup cleanup on close
        primaryStage.setOnCloseRequest(event -> {
            cleanup();
        });
        
        System.out.println("✓ Game window initialized");
    }

    /**
     * Configures the primary stage with window settings.
     */
    private void setupStage(Stage primaryStage) {
        primaryStage.setTitle(Config.GAME_TITLE);
        primaryStage.setScene(scene);
        
        // Window sizing options
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Choose ONE of these options:
        
        // Option 1: Standard windowed mode (recommended for development)
        // primaryStage.setWidth(1280);
        // primaryStage.setHeight(720);
        
        // Option 2: Maximized window (uncomment to use)
        // primaryStage.setMaximized(true);
        
        // Option 3: Full screen (uncomment to use)
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("Press ESC to exit fullscreen");
        isFullScreen = true;
        
        // Track window size changes
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            windowWidth = newVal.doubleValue();
        });
        
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            windowHeight = newVal.doubleValue();
        });
        
        primaryStage.show();
    }

    /**
     * Sets up keyboard and mouse input handlers.
     */
    private void setupInputHandlers(Scene scene, Stage stage) {
        scene.setOnKeyPressed(event -> {
            keyboardHandler.keyPressed(event.getCode());

            // Handle ESC key
            if (event.getCode() == KeyCode.ESCAPE) {
                handleEscapeKey(stage);
            }
            
            // F11 for fullscreen toggle
            if (event.getCode() == KeyCode.F11) {
                toggleFullScreen(stage);
            }
            
            // P for pause (alternative to ESC)
            if (event.getCode() == KeyCode.P) {
                if (game.getGameState() == GameState.PLAYING) {
                    pauseGame();
                }
            }

            // NOUVEAU: Raccourci pour activer/désactiver l'AI
            if (event.getCode() == KeyCode.F1) {
                AutoplayController ai = game.getAutoplayController();
                ai.setActive(!ai.isActive());
            }
        });

        scene.setOnKeyReleased(event -> {
            keyboardHandler.keyReleased(event.getCode());
        });

        scene.setOnMouseMoved(event -> {
            mouseHandler.updateMousePosition(event.getX(), event.getY());
        });

        scene.setOnMouseDragged(event -> {
            mouseHandler.updateMousePosition(event.getX(), event.getY());
        });

        scene.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mouseHandler.setMousePressed(true);
                mouseHandler.updateMousePosition(event.getX(), event.getY());
            }
        });

        scene.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mouseHandler.setMousePressed(false);
            }
        });
    }

    /**
     * Handles ESC key press based on current game state.
     */
    private void handleEscapeKey(Stage stage) {
        GameState state = game.getGameState();
        
        if (state == GameState.PLAYING) {
            // Pause game and show menu
            pauseGame();
        } else if (isFullScreen) {
            // Exit fullscreen
            stage.setFullScreen(false);
            isFullScreen = false;
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    private void toggleFullScreen(Stage stage) {
        isFullScreen = !isFullScreen;
        stage.setFullScreen(isFullScreen);
        System.out.println(isFullScreen ? "Entering fullscreen" : "Exiting fullscreen");
    }

    /**
     * Pauses the game and returns to menu.
     */
    private void pauseGame() {
        game.pause();
        soundManager.playSound("click");
        showMenu();
    }

    /**
     * Starts the main game loop.
     */
    private void startGameLoop() {
        lastFrameTime = System.nanoTime();

        gameLoop = new AnimationTimer() {
            private static final double MAX_DELTA_TIME = 0.1; // Cap at 100ms to prevent huge jumps
            
            @Override
            public void handle(long now) {
                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = now;
                
                // Cap delta time to prevent physics issues after lag/pause
                deltaTime = Math.min(deltaTime, MAX_DELTA_TIME);

                if (game.getGameState() == GameState.PLAYING) {
                    // Handle input
                    keyboardHandler.handleMovement(game.getPlayer(), deltaTime);
                    
                    if (mouseHandler.isMousePressed()) {
                        mouseHandler.handleMovement(game.getPlayer(), deltaTime);
                    }

                    // Update game
                    game.update(deltaTime);

                    // Update screen
                    gameScreen.update();

                    // Check for state changes
                    GameState currentState = game.getGameState();
                    if (currentState == GameState.GAME_OVER) {
                        handleGameEnd(false);
                    } else if (currentState == GameState.VICTORY) {
                        handleGameEnd(true);
                    }
                }
            }
        };

        gameLoop.start();
        System.out.println("✓ Game loop started");
    }

    /**
     * Handles game end (victory or game over).
     * 
     * @param isVictory True if player won
     */
    private void handleGameEnd(boolean isVictory) {
        int finalScore = game.getScore();
        int levelReached = game.getCurrentLevelNumber();
        long playTime = game.getTotalPlayTime();

        System.out.println("Game ended - Victory: " + isVictory + 
                         ", Score: " + finalScore + 
                         ", Level: " + levelReached + 
                         ", Time: " + playTime + "s");

        // Check if it's a high score
        if (scoreManager.isHighScore(finalScore)) {
            showNameInput(finalScore, levelReached, playTime, isVictory);
        } else {
            if (isVictory) {
                showVictory();
            } else {
                showGameOver();
            }
        }
    }

    /**
     * Shows name input dialog for high score.
     */
    private void showNameInput(int score, int level, long time, boolean victory) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            NameInputDialog nameDialog = new NameInputDialog(this, score, level, time, victory);
            root.getChildren().add(nameDialog);
        });
    }

    /**
     * Shows the main menu screen.
     */
    public void showMenu() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            menuScreen = new MenuScreen(this);
            root.getChildren().add(menuScreen);
            
            // Play menu music if not already playing
            if (game.getGameState() != GameState.PLAYING) {
                soundManager.stopMusic();
                soundManager.playMusic("menu");
            }
        });
    }

    /**
     * Shows the game screen and starts game.
     */
    public void showGame() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.getChildren().add(gameScreen);
            game.initialize();
            
            // Reconnect collision checkers
            keyboardHandler.setCollisionChecker(player -> game.isPlayerCollidingWithWall(player));
            mouseHandler.setCollisionChecker(player -> game.isPlayerCollidingWithWall(player));
            
            // Clear input states
            keyboardHandler.clear();
            mouseHandler.setMousePressed(false);
            
            // Reset frame time to prevent huge first delta
            lastFrameTime = System.nanoTime();
            
            System.out.println("✓ Game started");
        });
    }

    /**
     * Shows settings screen.
     */
    public void showSettings() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            settingsScreen = new SettingsScreen(this); // Refresh to get latest settings
            root.getChildren().add(settingsScreen);
            soundManager.playSound("click");
        });
    }

    /**
     * Shows leaderboard screen.
     */
    public void showLeaderboard() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            leaderboardScreen = new LeaderboardScreen(this); // Refresh
            root.getChildren().add(leaderboardScreen);
            soundManager.playSound("click");
        });
    }

    /**
     * Shows game over screen.
     */
    private void showGameOver() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            MenuScreen gameOverScreen = new MenuScreen(this, true, false);
            root.getChildren().add(gameOverScreen);
        });
    }

    /**
     * Shows victory screen.
     */
    private void showVictory() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            MenuScreen victoryScreen = new MenuScreen(this, false, true);
            root.getChildren().add(victoryScreen);
        });
    }

    /**
     * Resumes game from pause.
     */
    public void resumeGame() {
        Platform.runLater(() -> {
            game.resume();
            root.getChildren().clear();
            root.getChildren().add(gameScreen);
            soundManager.playSound("click");
            
            // Reset frame time to prevent huge delta after pause
            lastFrameTime = System.nanoTime();
            
            System.out.println("✓ Game resumed");
        });
    }

    /**
     * Cleans up resources before exit.
     */
    private void cleanup() {
        System.out.println("Cleaning up resources...");
        
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        soundManager.cleanup();
        
        System.out.println("✓ Cleanup complete");
    }

    /**
     * Exits the application gracefully.
     */
    public void exitGame() {
        cleanup();
        Platform.exit();
        System.exit(0);
    }

    // Getters
    public Game getGame() { return game; }
    public KeyboardHandler getKeyboardHandler() { return keyboardHandler; }
    public MouseHandler getMouseHandler() { return mouseHandler; }
    public SoundManager getSoundManager() { return soundManager; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public double getWindowWidth() { return windowWidth; }
    public double getWindowHeight() { return windowHeight; }
    public boolean isFullScreen() { return isFullScreen; }
}