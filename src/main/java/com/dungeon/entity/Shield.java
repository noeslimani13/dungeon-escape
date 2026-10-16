// ========== Shield.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Shield item that protects player from one hit.
 */
public class Shield extends Item {
    
    /**
     * Creates a new shield.
     * 
     * @param position Shield position
     */
    public Shield(Position position) {
        super(position);
    }

    @Override
    public void collect(Player player) {
        player.activateShield(Config.SHIELD_DURATION);
        this.active = false;
    }
}
