package com.dungeon.input;

import com.dungeon.entity.Player;
import com.dungeon.physics.Direction;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles keyboard input for player movement.
 * Supports both WASD and Arrow keys.
 */
public class KeyboardHandler {
    private Set<KeyCode> pressedKeys;
    private CollisionChecker collisionChecker;

    /**
     * Interface for checking collisions.
     */
    public interface CollisionChecker {
        boolean wouldCollideWithWall(Player player);
    }

    /**
     * Creates keyboard handler.
     */
    public KeyboardHandler() {
        this.pressedKeys = new HashSet<>();
        this.collisionChecker = null;
    }

    /**
     * Sets the collision checker.
     * 
     * @param checker Collision checker
     */
    public void setCollisionChecker(CollisionChecker checker) {
        this.collisionChecker = checker;
    }

    /**
     * Registers a key press.
     * 
     * @param code Key code
     */
    public void keyPressed(KeyCode code) {
        pressedKeys.add(code);
    }

    /**
     * Registers a key release.
     * 
     * @param code Key code
     */
    public void keyReleased(KeyCode code) {
        pressedKeys.remove(code);
    }

    /**
     * Handles player movement based on pressed keys.
     * Now checks for collisions before applying movement.
     * 
     * @param player Player to move
     * @param deltaTime Time elapsed
     */
    public void handleMovement(Player player, double deltaTime) {
        // UP: W or UP arrow
        if (isKeyPressed(KeyCode.W) || isKeyPressed(KeyCode.UP)) {
            player.savePosition();
            player.move(Direction.UP, deltaTime);
            if (collisionChecker != null && collisionChecker.wouldCollideWithWall(player)) {
                player.revertPosition();
            }
        }
        
        // DOWN: S or DOWN arrow
        if (isKeyPressed(KeyCode.S) || isKeyPressed(KeyCode.DOWN)) {
            player.savePosition();
            player.move(Direction.DOWN, deltaTime);
            if (collisionChecker != null && collisionChecker.wouldCollideWithWall(player)) {
                player.revertPosition();
            }
        }
        
        // LEFT: A or LEFT arrow
        if (isKeyPressed(KeyCode.A) || isKeyPressed(KeyCode.LEFT)) {
            player.savePosition();
            player.move(Direction.LEFT, deltaTime);
            if (collisionChecker != null && collisionChecker.wouldCollideWithWall(player)) {
                player.revertPosition();
            }
        }
        
        // RIGHT: D or RIGHT arrow
        if (isKeyPressed(KeyCode.D) || isKeyPressed(KeyCode.RIGHT)) {
            player.savePosition();
            player.move(Direction.RIGHT, deltaTime);
            if (collisionChecker != null && collisionChecker.wouldCollideWithWall(player)) {
                player.revertPosition();
            }
        }
    }

    /**
     * Checks if a key is currently pressed.
     * 
     * @param code Key code to check
     * @return true if key is pressed
     */
    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.contains(code);
    }

    /**
     * Clears all pressed keys.
     */
    public void clear() {
        pressedKeys.clear();
    }

    /**
     * Gets current key bindings as string for display.
     * 
     * @return String describing key bindings
     */
    public String getKeyBindingsInfo() {
        return "Movement: WASD or Arrow Keys\nPause: ESC";
    }
}