// ========== Item.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Abstract base class for collectible items.
 */
public abstract class Item extends Entity {
    
    /**
     * Creates a new item.
     * 
     * @param position Item position
     */
    public Item(Position position) {
        super(position, Config.ITEM_SIZE, Config.ITEM_SIZE);
    }

    /**
     * Called when item is collected by player.
     * 
     * @param player The player collecting the item
     */
    public abstract void collect(Player player);

    @Override
    public void update(double deltaTime) {
        // Items don't update by default
    }
}
