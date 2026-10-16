// ========== PatrolAI.java ==========
package com.dungeon.ai;

import com.dungeon.entity.Enemy;
import com.dungeon.entity.Player;
import com.dungeon.physics.Position;

/**
 * AI that makes enemy patrol between two points.
 * Demonstrates Strategy pattern implementation.
 */
public class PatrolAI implements AIBehavior {
    private Position patrolStart;
    private Position patrolEnd;
    private boolean movingToEnd;

    /**
     * Creates patrol AI.
     * 
     * @param start Start position
     * @param end End position
     */
    public PatrolAI(Position start, Position end) {
        this.patrolStart = start;
        this.patrolEnd = end;
        this.movingToEnd = true;
    }

    @Override
    public void execute(Enemy enemy, Player player, double deltaTime) {
        Position enemyPos = enemy.getPosition();
        Position target = movingToEnd ? patrolEnd : patrolStart;

        int dx = target.getX() - enemyPos.getX();
        int dy = target.getY() - enemyPos.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 5) {
            movingToEnd = !movingToEnd;
            return;
        }

        int moveX = (int)((dx / distance) * enemy.getSpeed() * deltaTime);
        int moveY = (int)((dy / distance) * enemy.getSpeed() * deltaTime);

        enemy.setPosition(new Position(
            enemyPos.getX() + moveX,
            enemyPos.getY() + moveY
        ));
    }
}
