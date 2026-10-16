// ========== Enemy.java ==========
package com.dungeon.entity;

import com.dungeon.ai.AIBehavior;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Abstract base class for all enemies.
 * Demonstrates polymorphism through different enemy types.
 */
public abstract class Enemy extends Entity {
    protected int damage;
    protected int speed;
    protected AIBehavior aiBehavior;

    /**
     * Constructs an enemy.
     * 
     * @param position Starting position
     * @param damage Damage dealt to player
     * @param speed Movement speed
     */
    public Enemy(Position position, int damage, int speed) {
        super(position, Config.ENEMY_SIZE, Config.ENEMY_SIZE);
        this.damage = damage;
        this.speed = speed;
    }

    /**
     * Updates enemy with AI behavior.
     * 
     * @param deltaTime Time elapsed
     * @param player Reference to player for AI
     */
    public void update(double deltaTime, Player player) {
        if (aiBehavior != null && active) {
            aiBehavior.execute(this, player, deltaTime);
        }
    }

    @Override
    public void update(double deltaTime) {
        // Default update without player reference
    }

    /**
     * Sets AI behavior for this enemy.
     * Demonstrates Strategy pattern.
     * 
     * @param behavior AI behavior to use
     */
    public void setAIBehavior(AIBehavior behavior) {
        this.aiBehavior = behavior;
    }

    public int getDamage() { return damage; }
    public int getSpeed() { return speed; }
}
