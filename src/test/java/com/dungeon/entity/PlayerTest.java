// ========== PlayerTest.java ==========
package com.dungeon.entity;

import com.dungeon.physics.Direction;
import com.dungeon.physics.Position;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(new Position(100, 100));
    }

    @Test
    @DisplayName("Player should start with full health")
    void testPlayerInitialHealth() {
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getMaxHealth());
    }

    @Test
    @DisplayName("Player should take damage correctly")
    void testPlayerTakeDamage() {
        player.takeDamage(25);
        assertEquals(75, player.getHealth());
        assertFalse(player.isDead());
    }

    @Test
    @DisplayName("Player health should not go below zero")
    void testPlayerHealthMinimum() {
        player.takeDamage(150);
        assertEquals(0, player.getHealth());
        assertTrue(player.isDead());
    }

    @Test
    @DisplayName("Player should heal correctly")
    void testPlayerHealing() {
        player.takeDamage(50);
        player.heal(30);
        assertEquals(80, player.getHealth());
    }

    @Test
    @DisplayName("Player health should not exceed maximum")
    void testPlayerMaxHealth() {
        player.heal(50);
        assertEquals(100, player.getHealth());
    }

    @Test
    @DisplayName("Player should collect keys")
    void testPlayerKeyCollection() {
        assertEquals(0, player.getKeyCount());
        
        player.addKey(new Key(new Position(0, 0)));
        assertEquals(1, player.getKeyCount());
        
        player.addKey(new Key(new Position(0, 0)));
        assertEquals(2, player.getKeyCount());
    }

    @Test
    @DisplayName("Player should check for required keys")
    void testPlayerHasAllKeys() {
        assertFalse(player.hasAllKeys(2));
        
        player.addKey(new Key(new Position(0, 0)));
        assertFalse(player.hasAllKeys(2));
        
        player.addKey(new Key(new Position(0, 0)));
        assertTrue(player.hasAllKeys(2));
    }

    @Test
    @DisplayName("Shield should protect player from damage")
    void testPlayerShield() {
        player.activateShield(5000);
        assertTrue(player.hasShield());
        
        player.takeDamage(50);
        assertEquals(100, player.getHealth());
        assertFalse(player.hasShield());
    }

    @Test
    @DisplayName("Player movement should update position")
    void testPlayerMovement() {
        Position initialPos = player.getPosition();
        
        player.move(Direction.RIGHT, 1.0);
        assertTrue(player.getPosition().getX() > initialPos.getX());
        
        player.move(Direction.DOWN, 1.0);
        assertTrue(player.getPosition().getY() > initialPos.getY());
    }

    @Test
    @DisplayName("Player should be temporarily invulnerable after taking damage")
    void testPlayerInvulnerability() throws InterruptedException {
        player.takeDamage(10);
        assertTrue(player.isInvulnerable());
        
        Thread.sleep(1100);
        assertFalse(player.isInvulnerable());
    }
}
