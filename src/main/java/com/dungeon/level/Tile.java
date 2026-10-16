// ========== Tile.java ==========
package com.dungeon.level;

/**
 * Represents a single tile in the level grid.
 */
public class Tile {
    private TileType type;
    private int x;
    private int y;

    /**
     * Creates a new tile.
     * 
     * @param type Tile type
     * @param x X coordinate in grid
     * @param y Y coordinate in grid
     */
    public Tile(TileType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if this tile is a wall.
     * 
     * @return true if tile blocks movement
     */
    // public boolean isWall() {
    //     return type.isSolid();
    // }

    public boolean isWall() {
    return this.type == TileType.WALL;
    }


    public TileType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
}
