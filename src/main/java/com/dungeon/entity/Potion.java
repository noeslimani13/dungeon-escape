// ========== Potion.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Potion item that heals the player.
 */
public class Potion extends Item {
    private int healAmount;

    /**
     * Creates a new potion.
     * 
     * @param position Potion position
     */
    public Potion(Position position) {
        super(position);
        this.healAmount = Config.POTION_HEAL_AMOUNT;
    }

    @Override
    public void collect(Player player) {
        player.heal(healAmount);
        this.active = false;
    }
}
