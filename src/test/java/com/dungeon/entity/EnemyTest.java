// ========== EnemyTest.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Position;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    @Test
    @DisplayName("Ghost should be created with correct properties")
    void testGhostCreation() {
        Ghost ghost = new Ghost(new Position(100, 100));
        
        assertNotNull(ghost);
        assertEquals(100, ghost.getPosition().getX());
        assertEquals(100, ghost.getPosition().getY());
        assertTrue(ghost.getDamage() > 0);
        assertTrue(ghost.getSpeed() > 0);
    }

    @Test
    @DisplayName("Goblin should be created with correct properties")
    void testGoblinCreation() {
        Goblin goblin = new Goblin(new Position(100, 100));
        
        assertNotNull(goblin);
        assertEquals(100, goblin.getPosition().getX());
        assertTrue(goblin.getDamage() > 0);
    }

    @Test
    @DisplayName("Enemy should have AI behavior")
    void testEnemyAIBehavior() {
        Enemy ghost = new Ghost(new Position(100, 100));
        Player player = new Player(new Position(150, 100));
        
        Position initialPos = new Position(ghost.getPosition().getX(), 
                                          ghost.getPosition().getY());
        
        ghost.update(1.0, player);
        
        assertNotEquals(initialPos.getX(), ghost.getPosition().getX());
    }

    @Test
    @DisplayName("Enemy should be deactivatable")
    void testEnemyActivation() {
        Enemy enemy = new Ghost(new Position(0, 0));
        
        assertTrue(enemy.isActive());
        enemy.setActive(false);
        assertFalse(enemy.isActive());
    }
}
