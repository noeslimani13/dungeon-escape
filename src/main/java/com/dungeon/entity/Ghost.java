// ========== Ghost.java (MODIFIÉ) ==========
package com.dungeon.entity;

import com.dungeon.ai.ChaseAI;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Ghost enemy that chases the player.
 * Demonstrates inheritance and polymorphism.
 * Difficulty scales with level number.
 */
public class Ghost extends Enemy {
    
    /**
     * Constructs a ghost enemy with level-scaled difficulty.
     * 
     * @param position Starting position
     * @param levelNumber Current level (for difficulty scaling)
     */
    public Ghost(Position position, int levelNumber) {
        super(position, 
              Config.getGhostDamage(levelNumber), 
              Config.getGhostSpeed(levelNumber));
        this.aiBehavior = new ChaseAI();
    }
    
    /**
     * Constructs a ghost enemy with default difficulty (level 1).
     * 
     * @param position Starting position
     */
    public Ghost(Position position) {
        this(position, 1);
    }
}