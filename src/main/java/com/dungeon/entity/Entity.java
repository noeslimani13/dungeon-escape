// ========== Entity.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;

/**
 * Abstract base class for all game entities.
 * Demonstrates abstraction principle of OOP.
 */
public abstract class Entity {
    protected Position position;
    protected int width;
    protected int height;
    protected boolean active;

    /**
     * Constructs an entity at given position.
     * 
     * @param position Initial position
     * @param width Entity width in pixels
     * @param height Entity height in pixels
     */
    public Entity(Position position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.active = true;
    }

    /**
     * Updates entity state.
     * 
     * @param deltaTime Time elapsed since last update
     */
    public abstract void update(double deltaTime);

    // Getters and setters demonstrating encapsulation
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
