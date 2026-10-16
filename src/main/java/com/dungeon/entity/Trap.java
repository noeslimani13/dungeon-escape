// ========== Trap.java (MODIFIÉ) ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Trap entity that damages player on contact.
 * Damage scales with level difficulty.
 */
public class Trap extends Entity {
    private int damage;
    private boolean triggered;
    private long triggerCooldown;
    private long lastTriggerTime;

    /**
     * Creates a trap with level-scaled damage.
     * 
     * @param position Trap position
     * @param levelNumber Current level (for difficulty scaling)
     */
    public Trap(Position position, int levelNumber) {
        super(position, Config.TRAP_SIZE, Config.TRAP_SIZE);
        this.damage = Config.getTrapDamage(levelNumber);
        this.triggered = false;
        this.triggerCooldown = Config.TRAP_COOLDOWN;
        this.lastTriggerTime = 0;
    }
    
    /**
     * Creates a trap with default damage (level 1).
     * 
     * @param position Trap position
     */
    public Trap(Position position) {
        this(position, 1);
    }

    @Override
    public void update(double deltaTime) {
        if (triggered && System.currentTimeMillis() - lastTriggerTime > triggerCooldown) {
            triggered = false;
        }
    }

    /**
     * Triggers trap on player.
     * 
     * @param player Player to damage
     */
    public void trigger(Player player) {
        if (!triggered) {
            player.takeDamage(damage);
            triggered = true;
            lastTriggerTime = System.currentTimeMillis();
        }
    }

    public boolean isTriggered() { return triggered; }
    public int getDamage() { return damage; }
}