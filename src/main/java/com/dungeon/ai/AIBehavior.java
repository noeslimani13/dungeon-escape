// ========== AIBehavior.java ==========
package com.dungeon.ai;

import com.dungeon.entity.Enemy;
import com.dungeon.entity.Player;

/**
 * Interface for enemy AI behaviors.
 * Demonstrates Strategy pattern and abstraction.
 */
public interface AIBehavior {
    /**
     * Executes AI behavior for an enemy.
     * 
     * @param enemy The enemy to control
     * @param player Reference to player
     * @param deltaTime Time elapsed since last update
     */
    void execute(Enemy enemy, Player player, double deltaTime);
}
