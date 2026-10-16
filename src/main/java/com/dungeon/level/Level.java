package com.dungeon.level;

import com.dungeon.entity.*;
import com.dungeon.physics.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game level with tiles, entities, and exit.
 */
public class Level {
    private int width;
    private int height;
    private Tile[][] tiles;
    private Position playerStartPosition;
    private Exit exit;
    private List<Enemy> enemies;
    private List<Item> items;
    private List<Trap> traps;
    private int requiredKeys; // Number of keys needed to complete the level

    /**
     * Creates a new level.
     * 
     * @param width Level width in tiles
     * @param height Level height in tiles
     */
    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.requiredKeys = 0;
        
        // Initialize with floor tiles - CORRECTION: ajout des coordonnées
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(TileType.FLOOR, x, y);
            }
        }
    }

    /**
     * Gets tile at position.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @return Tile at position
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return new Tile(TileType.WALL, x, y); // CORRECTION: ajout des coordonnées
        }
        return tiles[y][x];
    }

    /**
     * Sets tile at position.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param type Tile type
     */
    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[y][x] = new Tile(type, x, y); // CORRECTION: ajout des coordonnées
        }
    }

    /**
     * Adds an enemy to the level.
     * 
     * @param enemy Enemy to add
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * Adds an item to the level.
     * 
     * @param item Item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Adds a trap to the level.
     * 
     * @param trap Trap to add
     */
    public void addTrap(Trap trap) {
        traps.add(trap);
    }

    /**
     * Sets the number of keys required to complete the level.
     * 
     * @param count Number of keys required
     */
    public void setRequiredKeys(int count) {
        this.requiredKeys = Math.max(0, count);
    }

    /**
     * Gets the number of keys required to complete the level.
     * 
     * @return Number of required keys
     */
    public int getRequiredKeys() {
        return requiredKeys;
    }

    // Getters and setters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Position getPlayerStartPosition() { return playerStartPosition; }
    public void setPlayerStartPosition(Position pos) { this.playerStartPosition = pos; }
    public Exit getExit() { return exit; }
    public void setExit(Exit exit) { this.exit = exit; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Item> getItems() { return items; }
    public List<Trap> getTraps() { return traps; }
}