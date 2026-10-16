package com.dungeon.input;

import com.dungeon.entity.Player;
import com.dungeon.physics.Position;

/**
 * Handles mouse input for player movement.
 * Player moves towards mouse cursor position.
 */
public class MouseHandler {
    private double mouseX;
    private double mouseY;
    private boolean isMousePressed;
    private static final double MOVEMENT_THRESHOLD = 5.0;
    private CollisionChecker collisionChecker;

    /**
     * Interface for checking collisions.
     */
    public interface CollisionChecker {
        boolean wouldCollideWithWall(Player player);
    }

    /**
     * Creates mouse handler.
     */
    public MouseHandler() {
        this.mouseX = 0;
        this.mouseY = 0;
        this.isMousePressed = false;
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
     * Updates mouse position.
     * 
     * @param x Mouse X coordinate
     * @param y Mouse Y coordinate
     */
    public void updateMousePosition(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    /**
     * Sets mouse pressed state.
     * 
     * @param pressed True if mouse is pressed
     */
    public void setMousePressed(boolean pressed) {
        this.isMousePressed = pressed;
    }

    /**
     * Handles player movement towards mouse cursor.
     * Now checks for wall collisions.
     * 
     * @param player Player to move
     * @param deltaTime Time elapsed
     */
    public void handleMovement(Player player, double deltaTime) {
        if (!isMousePressed) {
            return;
        }

        Position playerPos = player.getPosition();
        
        // Calculate direction to mouse
        double dx = mouseX - (playerPos.getX() + player.getWidth() / 2.0);
        double dy = mouseY - (playerPos.getY() + player.getHeight() / 2.0);
        
        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Only move if distance is significant
        if (distance > MOVEMENT_THRESHOLD) {
            // Normalize direction
            double dirX = dx / distance;
            double dirY = dy / distance;
            
            // Calculate movement
            int moveSpeed = player.getSpeed();
            int moveX = (int)(dirX * moveSpeed * deltaTime);
            int moveY = (int)(dirY * moveSpeed * deltaTime);
            
            // Save position before moving
            player.savePosition();
            
            // Apply movement
            Position newPos = new Position(
                playerPos.getX() + moveX,
                playerPos.getY() + moveY
            );
            player.setPosition(newPos);
            
            // Check collision and revert if necessary
            if (collisionChecker != null && collisionChecker.wouldCollideWithWall(player)) {
                player.revertPosition();
            }
        }
    }

    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public boolean isMousePressed() { return isMousePressed; }
}