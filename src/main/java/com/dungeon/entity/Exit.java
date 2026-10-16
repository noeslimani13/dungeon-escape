// ========== Exit.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Exit entity representing level exit.
 */
public class Exit extends Entity {
    
    /**
     * Creates a new exit.
     * 
     * @param position Exit position
     */
    public Exit(Position position) {
        super(position, Config.EXIT_SIZE, Config.EXIT_SIZE);
    }

    @Override
    public void update(double deltaTime) {
        // Exit doesn't update
    }
}

