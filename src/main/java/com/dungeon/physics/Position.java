// ========== Position.java ==========
package com.dungeon.physics;

/**
 * Represents a 2D position in the game world.
 * Demonstrates encapsulation.
 */
public class Position {
    private int x;
    private int y;

    /**
     * Creates a new position.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates distance to another position.
     * 
     * @param other Other position
     * @return Distance in pixels
     */
    public double distanceTo(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
