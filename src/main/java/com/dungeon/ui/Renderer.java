package com.dungeon.ui;

import com.dungeon.core.Game;
import com.dungeon.entity.*;
import com.dungeon.level.Level;
import com.dungeon.level.Tile;
import com.dungeon.level.TileType;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Renders game elements to canvas with improved visuals.
 * Supports animations, effects, and scalable rendering.
 */
public class Renderer {
    
    // Animation time for effects
    private static double animationTime = 0;
    
    // Font caching
    private static Font exitFont;
    
    static {
        exitFont = Font.font("Arial", FontWeight.BOLD, 10);
    }
    
    /**
     * Updates animation time for effects.
     * 
     * @param deltaTime Time since last frame
     */
    public static void updateAnimation(double deltaTime) {
        animationTime += deltaTime;
    }
    
    /**
     * Renders entire game state with viewport support.
     * 
     * @param gc Graphics context
     * @param game Game instance
     * @param viewportWidth Width of viewport
     * @param viewportHeight Height of viewport
     */
    public static void render(GraphicsContext gc, Game game, double viewportWidth, double viewportHeight) {
        Level level = game.getCurrentLevel();
        if (level == null) return;

        // Clear screen with a darker background
        gc.setFill(Color.rgb(20, 20, 20));
        gc.fillRect(0, 0, viewportWidth, viewportHeight);

        // Render tiles
        renderTiles(gc, level, viewportWidth, viewportHeight);

        // Render exit with required keys count
        if (level.getExit() != null) {
            renderExit(gc, level.getExit(), game.getPlayer(), level.getRequiredKeys());
        }

        // Render items with glow effect
        for (Item item : level.getItems()) {
            if (item.isActive()) {
                renderItem(gc, item);
            }
        }

        // Render traps
        for (Trap trap : level.getTraps()) {
            renderTrap(gc, trap);
        }

        // Render enemies
        for (Enemy enemy : level.getEnemies()) {
            if (enemy.isActive()) {
                renderEnemy(gc, enemy);
            }
        }

        // Render player last (on top)
        if (game.getPlayer() != null) {
            renderPlayer(gc, game.getPlayer());
        }
    }
    
    /**
     * Overloaded render method with default viewport size.
     */
    public static void render(GraphicsContext gc, Game game) {
        render(gc, game, 800, 520);
    }

    /**
     * Renders tiles with improved visuals.
     */
    private static void renderTiles(GraphicsContext gc, Level level, double viewportWidth, double viewportHeight) {
        int maxX = Math.min(level.getWidth(), (int) Math.ceil(viewportWidth / Config.TILE_SIZE) + 1);
        int maxY = Math.min(level.getHeight(), (int) Math.ceil(viewportHeight / Config.TILE_SIZE) + 1);
        
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                if (x >= level.getWidth() || y >= level.getHeight()) continue;
                
                Tile tile = level.getTile(x, y);
                int pixelX = x * Config.TILE_SIZE;
                int pixelY = y * Config.TILE_SIZE;
                
                if (tile.getType() == TileType.WALL) {
                    // Wall with 3D effect
                    gc.setFill(Color.rgb(80, 80, 80));
                    gc.fillRect(pixelX, pixelY, Config.TILE_SIZE, Config.TILE_SIZE);
                    
                    // Highlight edge for 3D effect
                    gc.setFill(Color.rgb(100, 100, 100));
                    gc.fillRect(pixelX, pixelY, Config.TILE_SIZE, 2);
                    gc.fillRect(pixelX, pixelY, 2, Config.TILE_SIZE);
                    
                    // Shadow edge
                    gc.setFill(Color.rgb(60, 60, 60));
                    gc.fillRect(pixelX, pixelY + Config.TILE_SIZE - 2, Config.TILE_SIZE, 2);
                    gc.fillRect(pixelX + Config.TILE_SIZE - 2, pixelY, 2, Config.TILE_SIZE);
                } else {
                    // Floor with checkerboard pattern
                    Color floorColor = ((x + y) % 2 == 0) 
                        ? Color.rgb(50, 50, 50) 
                        : Color.rgb(45, 45, 45);
                    gc.setFill(floorColor);
                    gc.fillRect(pixelX, pixelY, Config.TILE_SIZE, Config.TILE_SIZE);
                    
                    // Add subtle texture
                    gc.setFill(Color.rgb(40, 40, 40, 0.3));
                    gc.fillRect(pixelX + 1, pixelY + 1, Config.TILE_SIZE - 2, Config.TILE_SIZE - 2);
                }
            }
        }
    }

    /**
     * Renders player as an animated stick figure character.
     */
    private static void renderPlayer(GraphicsContext gc, Player player) {
        Position pos = player.getPosition();
        int size = Config.PLAYER_SIZE;

        // Shield effect with pulsing animation
        if (player.hasShield()) {
            double pulse = Math.sin(animationTime * 5) * 0.15 + 0.85;
            gc.setStroke(Color.CYAN.deriveColor(0, 1, 1, pulse));
            gc.setLineWidth(3);
            gc.strokeOval(pos.getX() - 4, pos.getY() - 4, size + 8, size + 8);
            
            // Inner glow
            gc.setStroke(Color.LIGHTCYAN.deriveColor(0, 1, 1, pulse * 0.5));
            gc.setLineWidth(1);
            gc.strokeOval(pos.getX() - 2, pos.getY() - 2, size + 4, size + 4);
        }

        // Invulnerability effect (flashing)
        double alpha = 1.0;
        if (player.isInvulnerable()) {
            alpha = Math.sin(animationTime * 10) * 0.3 + 0.7;
        }
        
        Color bodyColor = player.isInvulnerable() 
            ? Color.LIGHTBLUE.deriveColor(0, 1, 1, alpha)
            : Color.DODGERBLUE.deriveColor(0, 1, 1, alpha);

        // Draw stick figure character
        double centerX = pos.getX() + size / 2.0;
        //double centerY = pos.getY() + size / 2.0;

        // Head with outline
        gc.setFill(bodyColor);
        double headSize = size * 0.35;
        double headY = pos.getY() + 2;
        gc.fillOval(centerX - headSize / 2, headY, headSize, headSize);
        
        // Head outline
        gc.setStroke(bodyColor.darker());
        gc.setLineWidth(1);
        gc.strokeOval(centerX - headSize / 2, headY, headSize, headSize);

        // Body (line)
        gc.setStroke(bodyColor);
        gc.setLineWidth(3);
        double bodyTop = headY + headSize;
        double bodyBottom = bodyTop + size * 0.4;
        gc.strokeLine(centerX, bodyTop, centerX, bodyBottom);

        // Arms with slight animation
        double armWave = Math.sin(animationTime * 3) * 2;
        double armY = bodyTop + (bodyBottom - bodyTop) * 0.3;
        double armSpan = size * 0.4;
        gc.setLineWidth(2.5);
        gc.strokeLine(centerX - armSpan, armY + armWave, centerX, armY);
        gc.strokeLine(centerX, armY, centerX + armSpan, armY - armWave);

        // Legs
        double legSpan = size * 0.3;
        gc.strokeLine(centerX, bodyBottom, centerX - legSpan, pos.getY() + size - 2);
        gc.strokeLine(centerX, bodyBottom, centerX + legSpan, pos.getY() + size - 2);

        // Eyes
        gc.setFill(Color.WHITE);
        double eyeSize = 3;
        double eyeY = headY + headSize * 0.4;
        gc.fillOval(centerX - headSize / 3, eyeY, eyeSize, eyeSize);
        gc.fillOval(centerX + headSize / 3 - eyeSize, eyeY, eyeSize, eyeSize);
        
        // Pupils
        gc.setFill(Color.BLACK);
        double pupilSize = 1.5;
        gc.fillOval(centerX - headSize / 3 + 0.5, eyeY + 0.5, pupilSize, pupilSize);
        gc.fillOval(centerX + headSize / 3 - eyeSize + 0.5, eyeY + 0.5, pupilSize, pupilSize);
        
        // Health indicator (small bar above head)
        renderHealthBar(gc, pos.getX() + size / 2, pos.getY() - 5, size * 0.8, 3, 
                       player.getHealth(), player.getMaxHealth());
    }

    /**
     * Renders a health bar.
     */
    private static void renderHealthBar(GraphicsContext gc, double centerX, double y, 
                                       double width, double height, int health, int maxHealth) {
        double x = centerX - width / 2;
        
        // Background
        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRect(x, y, width, height);
        
        // Health fill
        double healthPercent = (double) health / maxHealth;
        Color healthColor;
        if (healthPercent > 0.6) {
            healthColor = Color.LIGHTGREEN;
        } else if (healthPercent > 0.3) {
            healthColor = Color.ORANGE;
        } else {
            healthColor = Color.RED;
        }
        
        gc.setFill(healthColor);
        gc.fillRect(x + 1, y + 1, (width - 2) * healthPercent, height - 2);
        
        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
    }

    /**
     * Renders enemy with type-specific appearance.
     */
    private static void renderEnemy(GraphicsContext gc, Enemy enemy) {
        Position pos = enemy.getPosition();
        int size = Config.ENEMY_SIZE;
        double centerX = pos.getX() + size / 2.0;
        //double centerY = pos.getY() + size / 2.0;

        if (enemy instanceof Ghost) {
            // Ghost with transparency and floating animation
            double floatOffset = Math.sin(animationTime * 2) * 3;
            double ghostY = pos.getY() + floatOffset;
            
            // Ghost body
            gc.setFill(Color.rgb(147, 112, 219, 0.8)); // Purple with transparency
            gc.fillOval(pos.getX(), ghostY, size, size);
            
            // Ghost tail (wavy bottom)
            gc.fillPolygon(
                new double[]{pos.getX(), centerX, pos.getX() + size},
                new double[]{ghostY + size * 0.7, ghostY + size, ghostY + size * 0.7},
                3
            );
            
            // Eyes
            gc.setFill(Color.LIGHTCYAN);
            double eyeSize = size * 0.2;
            gc.fillOval(pos.getX() + size * 0.25, ghostY + size * 0.3, eyeSize, eyeSize);
            gc.fillOval(pos.getX() + size * 0.55, ghostY + size * 0.3, eyeSize, eyeSize);
            
            // Pupils
            gc.setFill(Color.DARKBLUE);
            double pupilSize = eyeSize * 0.5;
            gc.fillOval(pos.getX() + size * 0.25 + eyeSize * 0.25, 
                       ghostY + size * 0.3 + eyeSize * 0.25, pupilSize, pupilSize);
            gc.fillOval(pos.getX() + size * 0.55 + eyeSize * 0.25, 
                       ghostY + size * 0.3 + eyeSize * 0.25, pupilSize, pupilSize);
            
        } else if (enemy instanceof Goblin) {
            // Goblin with body
            gc.setFill(Color.rgb(34, 139, 34)); // Forest green
            gc.fillOval(pos.getX(), pos.getY(), size, size);
            
            // Ears
            gc.fillOval(pos.getX() - 3, pos.getY() + size * 0.2, 6, 8);
            gc.fillOval(pos.getX() + size - 3, pos.getY() + size * 0.2, 6, 8);
            
            // Eyes
            gc.setFill(Color.YELLOW);
            double eyeSize = size * 0.25;
            gc.fillOval(pos.getX() + size * 0.2, pos.getY() + size * 0.3, eyeSize, eyeSize);
            gc.fillOval(pos.getX() + size * 0.55, pos.getY() + size * 0.3, eyeSize, eyeSize);
            
            // Pupils
            gc.setFill(Color.BLACK);
            double pupilSize = eyeSize * 0.4;
            gc.fillOval(pos.getX() + size * 0.2 + eyeSize * 0.3, 
                       pos.getY() + size * 0.3 + eyeSize * 0.3, pupilSize, pupilSize);
            gc.fillOval(pos.getX() + size * 0.55 + eyeSize * 0.3, 
                       pos.getY() + size * 0.3 + eyeSize * 0.3, pupilSize, pupilSize);
            
            // Nose
            gc.fillOval(centerX - 2, pos.getY() + size * 0.5, 4, 5);
            
        } else {
            // Generic enemy
            gc.setFill(Color.CRIMSON);
            gc.fillOval(pos.getX(), pos.getY(), size, size);
        }
    }

    /**
     * Renders item with glow effect.
     */
    private static void renderItem(GraphicsContext gc, Item item) {
        Position pos = item.getPosition();
        
        // Bobbing animation
        double bobOffset = Math.sin(animationTime * 3) * 2;
        double itemY = pos.getY() + bobOffset;

        if (item instanceof Key) {
            // Glow effect
            double glowIntensity = Math.sin(animationTime * 4) * 0.3 + 0.7;
            gc.setFill(Color.GOLD.deriveColor(0, 1, 1, glowIntensity * 0.3));
            gc.fillOval(pos.getX() - 6, itemY - 4, 28, 24);
            
            // Draw key shape
            gc.setFill(Color.GOLD);
            gc.fillRect(pos.getX(), itemY + 8, 16, 4); // key body
            gc.fillOval(pos.getX() - 4, itemY + 6, 8, 8); // key head
            
            // Key teeth
            gc.fillRect(pos.getX() + 12, itemY + 10, 2, 3);
            gc.fillRect(pos.getX() + 16, itemY + 10, 2, 3);
            
        } else if (item instanceof Potion) {
            // Glow effect
            double glowIntensity = Math.sin(animationTime * 4) * 0.3 + 0.7;
            gc.setFill(Color.RED.deriveColor(0, 1, 1, glowIntensity * 0.3));
            gc.fillOval(pos.getX() - 2, itemY, 28, 28);
            
            // Draw potion bottle
            gc.setFill(Color.RED);
            gc.fillRect(pos.getX() + 6, itemY + 8, 12, 12); // bottle
            gc.setFill(Color.CRIMSON);
            gc.fillRect(pos.getX() + 8, itemY + 4, 8, 6); // neck
            gc.setFill(Color.rgb(139, 69, 19));
            gc.fillRect(pos.getX() + 7, itemY + 2, 10, 4); // cap
            
            // Highlight
            gc.setFill(Color.rgb(255, 100, 100, 0.6));
            gc.fillOval(pos.getX() + 8, itemY + 10, 4, 4);
            
        } else if (item instanceof Shield) {
            // Rotating shield
            double rotation = animationTime * 2;
            gc.save();
            gc.translate(pos.getX() + Config.ITEM_SIZE / 2, itemY + Config.ITEM_SIZE / 2);
            gc.rotate(Math.toDegrees(rotation));
            
            // Shield with gradient effect
            gc.setFill(Color.CYAN);
            gc.fillOval(-Config.ITEM_SIZE / 2, -Config.ITEM_SIZE / 2, Config.ITEM_SIZE, Config.ITEM_SIZE);
            
            gc.setStroke(Color.DARKBLUE);
            gc.setLineWidth(2);
            gc.strokeOval(-Config.ITEM_SIZE / 2, -Config.ITEM_SIZE / 2, Config.ITEM_SIZE, Config.ITEM_SIZE);
            
            // Shield emblem
            gc.setStroke(Color.LIGHTCYAN);
            gc.setLineWidth(2);
            gc.strokeLine(-4, -4, 4, 4);
            gc.strokeLine(-4, 4, 4, -4);
            
            gc.restore();
        }
    }

    /**
     * Renders trap with animation.
     */
    private static void renderTrap(GraphicsContext gc, Trap trap) {
        Position pos = trap.getPosition();
        int size = Config.TRAP_SIZE;
        
        Color trapColor = trap.isTriggered() ? Color.DARKRED : Color.ORANGERED;
        
        // Base
        gc.setFill(trapColor.darker());
        gc.fillRect(pos.getX(), pos.getY(), size, size);
        
        // Animated spikes
        double spikeHeight = trap.isTriggered() ? 8 : 6;
        if (!trap.isTriggered()) {
            spikeHeight += Math.sin(animationTime * 5) * 2;
        }
        
        gc.setFill(trapColor);
        for (int i = 0; i < 4; i++) {
            int x = pos.getX() + i * 7 + 2;
            double[] xPoints = {x, x + 3, x + 6};
            double[] yPoints = {
                pos.getY() + size, 
                pos.getY() + size - spikeHeight, 
                pos.getY() + size
            };
            gc.fillPolygon(xPoints, yPoints, 3);
        }
        
        // Glow when triggered
        if (trap.isTriggered()) {
            gc.setFill(Color.RED.deriveColor(0, 1, 1, 0.3));
            gc.fillRect(pos.getX() - 2, pos.getY() - 2, size + 4, size + 4);
        }
    }

    /**
     * Renders exit door with key requirement indicator.
     */
    private static void renderExit(GraphicsContext gc, Exit exit, Player player, int requiredKeys) {
        Position pos = exit.getPosition();
        int size = Config.EXIT_SIZE;

        // Door frame
        gc.setFill(Color.rgb(101, 67, 33));
        gc.fillRect(pos.getX() - 2, pos.getY() - 2, size + 4, size + 4);

        // Door
        gc.setFill(Color.rgb(139, 69, 19));
        gc.fillRect(pos.getX(), pos.getY(), size, size);

        // Wood grain effect
        gc.setStroke(Color.rgb(120, 60, 15));
        gc.setLineWidth(1);
        for (int i = 0; i < 3; i++) {
            gc.strokeLine(pos.getX() + 8, pos.getY() + i * 10, pos.getX() + 24, pos.getY() + i * 10 + 5);
        }

        // Door handle
        gc.setFill(Color.GOLD);
        gc.fillOval(pos.getX() + size - 10, pos.getY() + size / 2 - 2, 6, 6);

        // "EXIT" text
        gc.setFont(exitFont);
        gc.setFill(Color.LIGHTGREEN);
        gc.fillText("EXIT", pos.getX() + 6, pos.getY() + 20);
        
        // Key requirement indicator
        if (player != null && !player.hasAllKeys(requiredKeys)) {
            // Locked indicator (pulsing)
            double pulse = Math.sin(animationTime * 3) * 0.2 + 0.8;
            gc.setFill(Color.RED.deriveColor(0, 1, 1, pulse * 0.5));
            gc.fillOval(pos.getX() + size / 2 - 4, pos.getY() - 8, 8, 8);
            
            // Lock icon
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeRect(pos.getX() + size / 2 - 2, pos.getY() - 6, 4, 4);
        } else if (player != null) {
            // Unlocked indicator (green check)
            gc.setFill(Color.LIGHTGREEN.deriveColor(0, 1, 1, 0.8));
            gc.fillOval(pos.getX() + size / 2 - 4, pos.getY() - 8, 8, 8);
        }
    }
}