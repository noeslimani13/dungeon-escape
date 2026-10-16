// ========== Key.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;

/**
 * Key item required to open exit.
 */
public class Key extends Item {
    
    /**
     * Creates a new key.
     * 
     * @param position Key position
     */
    public Key(Position position) {
        super(position);
    }

    @Override
    public void collect(Player player) {
        player.addKey(this);
        this.active = false;
    }
}
