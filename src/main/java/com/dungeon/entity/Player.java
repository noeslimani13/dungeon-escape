package com.dungeon.entity;

import com.dungeon.physics.Direction;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Player entity representing the player character.
 * Demonstrates inheritance (extends Entity) and encapsulation.
 */
public class Player extends Entity {
    private int health;
    private int maxHealth;
    private int speed;
    private List<Key> keys;
    private boolean hasShield;
    private int shieldDuration;
    private Position previousPosition;
    private long lastDamageTime;
    private int damageInvulnerabilityDuration;

    /**
     * Constructs a player at given position.
     * 
     * @param startPosition Starting position
     */
    public Player(Position startPosition) {
        super(startPosition, Config.PLAYER_SIZE, Config.PLAYER_SIZE);
        this.health = Config.PLAYER_MAX_HEALTH;
        this.maxHealth = Config.PLAYER_MAX_HEALTH;
        this.speed = Config.PLAYER_SPEED;
        this.keys = new ArrayList<>();
        this.hasShield = false;
        this.shieldDuration = 0;
        this.previousPosition = new Position(startPosition.getX(), startPosition.getY());
        this.lastDamageTime = 0;
        this.damageInvulnerabilityDuration = Config.DAMAGE_INVULNERABILITY_MS;
    }

    @Override
    public void update(double deltaTime) {
        // Update shield duration
        if (hasShield && shieldDuration > 0) {
            shieldDuration -= deltaTime * 1000; // Convert to milliseconds
            if (shieldDuration <= 0) {
                hasShield = false;
            }
        }
    }

    /**
     * Saves current position before movement.
     * CRITICAL: Must be called BEFORE any movement.
     */
    public void savePosition() {
        previousPosition = new Position(position.getX(), position.getY());
    }

    /**
     * Moves player in specified direction.
     * 
     * @param direction Direction to move
     * @param deltaTime Time elapsed
     */
    public void move(Direction direction, double deltaTime) {
        int moveAmount = (int)(speed * deltaTime);
        
        switch (direction) {
            case UP:
                position = new Position(position.getX(), position.getY() - moveAmount);
                break;
            case DOWN:
                position = new Position(position.getX(), position.getY() + moveAmount);
                break;
            case LEFT:
                position = new Position(position.getX() - moveAmount, position.getY());
                break;
            case RIGHT:
                position = new Position(position.getX() + moveAmount, position.getY());
                break;
        }
    }

    /**
     * Reverts player to previous position (for collision resolution).
     */
    public void revertPosition() {
        if (previousPosition != null) {
            position = new Position(previousPosition.getX(), previousPosition.getY());
        }
    }

    /**
     * Player takes damage.
     * 
     * @param damage Amount of damage
     */
    public void takeDamage(int damage) {
        if (isInvulnerable()) {
            return;
        }
        
        if (hasShield) {
            hasShield = false;
            shieldDuration = 0;
            return;
        }
        
        health -= damage;
        if (health < 0) health = 0;
        lastDamageTime = System.currentTimeMillis();
    }

    /**
     * Heals player.
     * 
     * @param amount Amount to heal
     */
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    /**
     * Adds a key to player's inventory.
     * 
     * @param key Key to add
     */
    public void addKey(Key key) {
        keys.add(key);
    }

    /**
     * Activates shield for player.
     * 
     * @param duration Duration in milliseconds
     */
    public void activateShield(int duration) {
        this.hasShield = true;
        this.shieldDuration = duration;
    }

    /**
     * Checks if player has all required keys.
     * 
     * @param requiredKeys Number of keys required
     * @return true if player has enough keys
     */
    public boolean hasAllKeys(int requiredKeys) {
        return keys.size() >= requiredKeys;
    }

    /**
     * Checks if player is currently invulnerable.
     * 
     * @return true if invulnerable
     */
    public boolean isInvulnerable() {
        return System.currentTimeMillis() - lastDamageTime < damageInvulnerabilityDuration;
    }

    /**
     * Checks if player is dead.
     * 
     * @return true if health is 0
     */
    public boolean isDead() {
        return health <= 0;
    }

    // Getters
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getSpeed() { return speed; }
    public int getKeyCount() { return keys.size(); }
    public boolean hasShield() { return hasShield; }
    public Position getPreviousPosition() { return previousPosition; }
}