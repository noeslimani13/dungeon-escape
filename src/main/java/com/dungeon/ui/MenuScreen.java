package com.dungeon.ui;

import com.dungeon.core.GameState;
import com.dungeon.utils.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

/**
 * Main menu screen with game options.
 * Dynamically adapts based on game state.
 */
public class MenuScreen extends VBox {
    private GameWindow gameWindow;
    private SoundManager soundManager;
    private boolean isGameOver;
    private boolean isVictory;

    /**
     * Creates main menu screen.
     */
    public MenuScreen(GameWindow gameWindow) {
        this(gameWindow, false, false);
    }

    /**
     * Creates menu screen with optional game over/victory message.
     */
    public MenuScreen(GameWindow gameWindow, boolean isGameOver, boolean isVictory) {
        this.gameWindow = gameWindow;
        this.soundManager = SoundManager.getInstance();
        this.isGameOver = isGameOver;
        this.isVictory = isVictory;
        
        setupLayout();
        buildUI();
        setupAnimations();
    }

    /**
     * Sets up base layout.
     */
    private void setupLayout() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1a1a1a;");
    }

    /**
     * Builds the UI elements.
     */
    private void buildUI() {
        // Title with state-dependent styling
        Text title = createTitle();
        getChildren().add(title);

        // Subtitle for victory/game over
        if (isVictory) {
            Text scoreText = new Text(String.format("★ Final Score: %,d ★", 
                gameWindow.getGame().getScore()));
            scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            scoreText.setFill(Color.YELLOW);
            getChildren().add(scoreText);
            
            Text levelText = new Text("Level " + gameWindow.getGame().getCurrentLevelNumber() + 
                " • Time: " + formatTime(gameWindow.getGame().getTotalPlayTime()));
            levelText.setFont(Font.font(18));
            levelText.setFill(Color.LIGHTGRAY);
            getChildren().add(levelText);
        } else if (isGameOver) {
            Text encouragement = new Text("Don't give up! Try again!");
            encouragement.setFont(Font.font(18));
            encouragement.setFill(Color.ORANGE);
            getChildren().add(encouragement);
        }

        // Spacer
        VBox spacer = new VBox();
        spacer.setMinHeight(20);
        getChildren().add(spacer);

        // Add appropriate buttons
        addButtons();
    }

    /**
     * Creates the title text.
     */
    private Text createTitle() {
        Text title;
        if (isGameOver) {
            title = new Text("💀 GAME OVER 💀");
            title.setFill(Color.RED);
        } else if (isVictory) {
            title = new Text("🎉 VICTORY! 🎉");
            title.setFill(Color.GOLD);
        } else {
            title = new Text("⚔ DUNGEON ESCAPE ⚔");
            title.setFill(Color.LIGHTBLUE);
        }
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        return title;
    }

    /**
     * Adds appropriate buttons based on state.
     */
    private void addButtons() {
        if (isGameOver || isVictory) {
            addEndGameButtons();
        } else {
            addMainMenuButtons();
        }
        
        // Exit button always present
        addExitButton();
    }

    /**
     * Adds end game buttons (retry/menu).
     */
    private void addEndGameButtons() {
        Button retryButton = createStyledButton("↻ Try Again");
        retryButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showGame();
        });
        getChildren().add(retryButton);

        Button menuButton = createStyledButton("◄ Main Menu");
        menuButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.getGame().setGameState(GameState.MENU);
            gameWindow.showMenu();
        });
        getChildren().add(menuButton);
    }

    /**
     * Adds main menu buttons.
     */
    private void addMainMenuButtons() {
        // New Game / Resume button
        GameState currentState = gameWindow.getGame().getGameState();
        
        if (currentState == GameState.PAUSED) {
            Button resumeButton = createStyledButton("▶ Resume Game");
            resumeButton.setStyle(getButtonStyle("#4a9d4a")); // Green
            resumeButton.setOnMouseEntered(e -> 
                resumeButton.setStyle(getButtonStyle("#5abd5a"))
            );
            resumeButton.setOnMouseExited(e -> 
                resumeButton.setStyle(getButtonStyle("#4a9d4a"))
            );
            resumeButton.setOnAction(e -> {
                soundManager.playSound("click");
                gameWindow.resumeGame();
            });
            getChildren().add(resumeButton);
        }

        Button newGameButton = createStyledButton("► New Game");
        newGameButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showGame();
        });
        getChildren().add(newGameButton);

        // Leaderboard button
        Button leaderboardButton = createStyledButton("♔ Leaderboard");
        leaderboardButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showLeaderboard();
        });
        getChildren().add(leaderboardButton);

        // Settings button
        Button settingsButton = createStyledButton("⚙ Settings");
        settingsButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showSettings();
        });
        getChildren().add(settingsButton);
    }

    /**
     * Adds exit button.
     */
    private void addExitButton() {
        Button exitButton = createStyledButton("✕ Exit Game");
        exitButton.setStyle(getButtonStyle("#8b0000")); // Dark red
        exitButton.setOnMouseEntered(e -> 
            exitButton.setStyle(getButtonStyle("#a00000"))
        );
        exitButton.setOnMouseExited(e -> 
            exitButton.setStyle(getButtonStyle("#8b0000"))
        );
        exitButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.exitGame();
        });
        getChildren().add(exitButton);
    }

    /**
     * Sets up fade-in animation.
     */
    private void setupAnimations() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Formats time in seconds.
     */
    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    /**
     * Gets button style string.
     */
    private String getButtonStyle(String color) {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 5px; " +
            "-fx-border-radius: 5px;",
            color
        );
    }

    /**
     * Creates a styled button.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        button.setMinWidth(250);
        button.setCursor(Cursor.HAND);
        button.setStyle(getButtonStyle("#4a4a4a"));
        
        button.setOnMouseEntered(e -> 
            button.setStyle(getButtonStyle("#6a6a6a"))
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(getButtonStyle("#4a4a4a"))
        );
        
        return button;
    }
}
