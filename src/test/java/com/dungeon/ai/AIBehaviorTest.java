// ========== AIBehaviorTest.java ==========
package com.dungeon.ai;

import com.dungeon.entity.Enemy;
import com.dungeon.entity.Ghost;
import com.dungeon.entity.Player;
import com.dungeon.physics.Position;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AIBehaviorTest {

    @Test
    @DisplayName("ChaseAI should move enemy towards player")
    void testChaseAI() {
        ChaseAI chaseAI = new ChaseAI();
        Enemy enemy = new Ghost(new Position(100, 100));
        Player player = new Player(new Position(200, 100));
        
        int initialX = enemy.getPosition().getX();
        
        chaseAI.execute(enemy, player, 1.0);
        
        assertTrue(enemy.getPosition().getX() > initialX);
    }

    @Test
    @DisplayName("PatrolAI should move enemy between patrol points")
    void testPatrolAI() {
        Position start = new Position(100, 100);
        Position end = new Position(200, 100);
        PatrolAI patrolAI = new PatrolAI(start, end);
        
        Enemy enemy = new Ghost(start);
        Player player = new Player(new Position(0, 0));
        
        int initialX = enemy.getPosition().getX();
        
        for (int i = 0; i < 10; i++) {
            patrolAI.execute(enemy, player, 0.1);
        }
        
        assertNotEquals(initialX, enemy.getPosition().getX());
    }

    @Test
    @DisplayName("ChaseAI should not chase player beyond range")
    void testChaseAIRange() {
        ChaseAI chaseAI = new ChaseAI();
        Enemy enemy = new Ghost(new Position(100, 100));
        Player player = new Player(new Position(500, 100));
        
        int initialX = enemy.getPosition().getX();
        
        chaseAI.execute(enemy, player, 1.0);
        
        assertEquals(initialX, enemy.getPosition().getX());
    }
}