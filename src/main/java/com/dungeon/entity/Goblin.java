// ========== Goblin.java (MODIFIÉ) ==========
package com.dungeon.entity;

import com.dungeon.ai.PatrolAI;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

/**
 * Goblin enemy that patrols an area.
 * Demonstrates inheritance and polymorphism.
 * Difficulty scales with level number.
 */
public class Goblin extends Enemy {
    private Position patrolStart;
    private Position patrolEnd;

    /**
     * Constructs a goblin enemy with level-scaled difficulty.
     * 
     * @param position Starting position
     * @param levelNumber Current level (for difficulty scaling)
     */
    public Goblin(Position position, int levelNumber) {
        super(position, 
              Config.getGoblinDamage(levelNumber), 
              Config.getGoblinSpeed(levelNumber));
        this.patrolStart = new Position(position.getX(), position.getY());
        this.patrolEnd = new Position(position.getX() + 100, position.getY());
        this.aiBehavior = new PatrolAI(patrolStart, patrolEnd);
    }
    
    /**
     * Constructs a goblin enemy with default difficulty (level 1).
     * 
     * @param position Starting position
     */
    public Goblin(Position position) {
        this(position, 1);
    }

    public Position getPatrolStart() { return patrolStart; }
    public Position getPatrolEnd() { return patrolEnd; }
}