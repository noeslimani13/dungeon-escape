package com.dungeon.ui;

import com.dungeon.utils.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

/**
 * Dialog for entering player name for high score.
 * Features animated entrance and input validation.
 */
public class NameInputDialog extends VBox {
    private GameWindow gameWindow;
    private SoundManager soundManager;
    private TextField nameField;
    private int finalScore;
    private int levelReached;
    private long playTime;
    private boolean isVictory;
    private Text errorText;

    /**
     * Creates name input dialog with animations.
     * 
     * @param gameWindow Game window
     * @param finalScore Final score
     * @param levelReached Level reached
     * @param playTime Play time in seconds
     * @param isVictory True if game was won
     */
    public NameInputDialog(GameWindow gameWindow, int finalScore, int levelReached, 
                           long playTime, boolean isVictory) {
        this.gameWindow = gameWindow;
        this.soundManager = SoundManager.getInstance();
        this.finalScore = finalScore;
        this.levelReached = levelReached;
        this.playTime = playTime;
        this.isVictory = isVictory;
        
        setupLayout();
        buildUI();
        setupAnimations();
    }

    /**
     * Sets up the base layout.
     */
    private void setupLayout() {
        setAlignment(Pos.CENTER);
        setSpacing(25);
        setPadding(new Insets(50));
        setStyle("-fx-background-color: #1a1a1a;");
    }

    /**
     * Builds the UI elements.
     */
    private void buildUI() {
        // Title with appropriate message
        Text title = new Text(isVictory ? "🎉 VICTORY! 🎉" : "💀 GAME OVER 💀");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(isVictory ? Color.GOLD : Color.RED);
        getChildren().add(title);

        // Score display with formatting
        Text scoreText = new Text(String.format("Final Score: %,d", finalScore));
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        scoreText.setFill(Color.YELLOW);
        getChildren().add(scoreText);

        // Level reached
        Text levelText = new Text("Level Reached: " + levelReached);
        levelText.setFont(Font.font(20));
        levelText.setFill(Color.LIGHTBLUE);
        getChildren().add(levelText);

        // Play time
        Text timeText = new Text("Time Played: " + formatTime(playTime));
        timeText.setFont(Font.font(18));
        timeText.setFill(Color.LIGHTGRAY);
        getChildren().add(timeText);

        // High score announcement
        Text highScoreText = new Text("🏆 NEW HIGH SCORE! 🏆");
        highScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        highScoreText.setFill(Color.GOLD);
        getChildren().add(highScoreText);

        // Spacer
        VBox spacer = new VBox();
        spacer.setMinHeight(10);
        getChildren().add(spacer);

        // Name input prompt
        Text prompt = new Text("Enter your name for the leaderboard:");
        prompt.setFont(Font.font(18));
        prompt.setFill(Color.WHITE);
        getChildren().add(prompt);

        // Name input field
        nameField = new TextField();
        nameField.setPromptText("Enter your name (max 20 characters)");
        nameField.setMaxWidth(350);
        nameField.setFont(Font.font(16));
        nameField.setStyle(
            "-fx-background-color: #3a3a3a; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: gray; " +
            "-fx-padding: 12px; " +
            "-fx-background-radius: 5px; " +
            "-fx-border-color: #5a5a5a; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5px;"
        );
        
        // Character limit enforcement
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 20) {
                nameField.setText(oldVal);
            }
            if (errorText != null) {
                errorText.setVisible(false);
            }
        });
        
        getChildren().add(nameField);

        // Error text (initially hidden)
        errorText = new Text("");
        errorText.setFont(Font.font(14));
        errorText.setFill(Color.RED);
        errorText.setVisible(false);
        getChildren().add(errorText);

        // Submit button
        Button submitButton = createStyledButton("✓ Submit Score", "#4a9d4a", "#5abd5a");
        submitButton.setOnAction(e -> submitScore());
        getChildren().add(submitButton);

        // Skip button
        Button skipButton = createStyledButton("→ Skip", "#8b4513", "#a0522d");
        skipButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showMenu();
        });
        getChildren().add(skipButton);

        // Focus on name field
        nameField.requestFocus();
        
        // Enter key submits
        nameField.setOnAction(e -> submitScore());
    }

    /**
     * Sets up entrance animations.
     */
    private void setupAnimations() {
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Scale animation
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), this);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        fadeIn.play();
        scaleIn.play();
    }

    /**
     * Formats time in seconds to MM:SS format.
     */
    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    /**
     * Validates and submits the score.
     */
    private void submitScore() {
        String name = nameField.getText().trim();
        
        // Validation
        if (name.isEmpty()) {
            showError("Please enter a name or use 'Skip'");
            return;
        }
        
        // Check for invalid characters
        if (!name.matches("[a-zA-Z0-9 _-]+")) {
            showError("Name can only contain letters, numbers, spaces, - and _");
            return;
        }
        
        // Limit name length
        if (name.length() > 20) {
            name = name.substring(0, 20);
        }
        
        // Save score
        soundManager.playSound("levelup");
        com.dungeon.utils.ScoreManager.getInstance()
            .addScore(name, finalScore, levelReached, playTime);
        
        // Show leaderboard
        gameWindow.showLeaderboard();
    }

    /**
     * Shows an error message.
     */
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
        soundManager.playSound("damage");
        
        // Shake animation for name field
        nameField.setStyle(
            "-fx-background-color: #4a2a2a; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: gray; " +
            "-fx-padding: 12px; " +
            "-fx-background-radius: 5px; " +
            "-fx-border-color: red; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5px;"
        );
    }

    /**
     * Creates a styled button with custom colors.
     */
    private Button createStyledButton(String text, String normalColor, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setMinWidth(220);
        button.setCursor(Cursor.HAND);
        
        String baseStyle = 
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 5px; " +
            "-fx-border-radius: 5px;";
        
        button.setStyle(String.format(baseStyle, normalColor));
        
        button.setOnMouseEntered(e -> 
            button.setStyle(String.format(baseStyle, hoverColor))
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(String.format(baseStyle, normalColor))
        );
        
        return button;
    }
}
