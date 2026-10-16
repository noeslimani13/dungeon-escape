// ========== ChaseAI.java ==========
package com.dungeon.ai;

import com.dungeon.entity.Enemy;
import com.dungeon.entity.Player;
import com.dungeon.physics.Position;

/**
 * AI that makes enemy chase the player.
 * Demonstrates Strategy pattern implementation.
 */
public class ChaseAI implements AIBehavior {
    private static final double CHASE_RANGE = 200.0;

    @Override
    public void execute(Enemy enemy, Player player, double deltaTime) {
        Position enemyPos = enemy.getPosition();
        Position playerPos = player.getPosition();

        double distance = enemyPos.distanceTo(playerPos);

        if (distance < CHASE_RANGE && distance > 0) {
            int dx = playerPos.getX() - enemyPos.getX();
            int dy = playerPos.getY() - enemyPos.getY();

            double length = Math.sqrt(dx * dx + dy * dy);
            int moveX = (int)((dx / length) * enemy.getSpeed() * deltaTime);
            int moveY = (int)((dy / length) * enemy.getSpeed() * deltaTime);

            enemy.setPosition(new Position(
                enemyPos.getX() + moveX,
                enemyPos.getY() + moveY
            ));
        }
    }
}