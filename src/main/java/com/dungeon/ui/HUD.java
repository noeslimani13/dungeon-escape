// ========== HUD.java ==========
package com.dungeon.ui;

import com.dungeon.core.Game;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Heads-Up Display showing game information.
 */
public class HUD extends HBox {
    private Game game;
    private Text healthText;
    private Text keysText;
    private Text scoreText;
    private Text timeText;
    private Text levelText;

    /**
     * Creates HUD.
     * 
     * @param game Game instance
     */
    public HUD(Game game) {
        this.game = game;
        
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #2a2a2a;");

        // Create text elements
        levelText = createText("Level: 1");
        healthText = createText("Health: 100");
        keysText = createText("Keys: 0");
        timeText = createText("Time: 03:00");
        scoreText = createText("Score: 0");

        getChildren().addAll(levelText, healthText, keysText, timeText, scoreText);
    }

    private Text createText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font(18));
        text.setFill(Color.WHITE);
        return text;
    }

    /**
     * Updates HUD with current game state.
     */
    public void update() {
        if (game.getPlayer() != null) {
            levelText.setText("Level: " + game.getCurrentLevelNumber());
            healthText.setText("Health: " + game.getPlayer().getHealth());
            keysText.setText("Keys: " + game.getPlayer().getKeyCount());
            timeText.setText("Time: " + game.getGameTimer().getFormattedTime());
            scoreText.setText("Score: " + game.getScore());
        }
    }
}