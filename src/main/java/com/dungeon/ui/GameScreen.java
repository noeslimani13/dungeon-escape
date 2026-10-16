// ========== GameScreen.java ==========
package com.dungeon.ui;

import com.dungeon.core.Game;
import com.dungeon.input.KeyboardHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;

/**
 * Main game screen showing the game canvas and HUD.
 */
public class GameScreen extends BorderPane {
    private Game game;
    private Canvas canvas;
    private GraphicsContext gc;
    private HUD hud;

    /**
     * Creates game screen.
     * 
     * @param game Game instance
     * @param keyboardHandler Keyboard handler
     */
    public GameScreen(Game game, KeyboardHandler keyboardHandler) {
        this.game = game;

        // Create canvas for game rendering
        canvas = new Canvas(800, 520);
        gc = canvas.getGraphicsContext2D();

        // Create HUD
        hud = new HUD(game);

        // Layout
        setCenter(canvas);
        setBottom(hud);

        setStyle("-fx-background-color: black;");
    }

    /**
     * Updates and renders the game.
     */
    public void update() {
        Renderer.render(gc, game);
        hud.update();
    }
}