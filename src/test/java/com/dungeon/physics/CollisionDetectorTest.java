// ========== CollisionDetectorTest.java ==========
package com.dungeon.physics;

import com.dungeon.entity.Player;
import com.dungeon.entity.Enemy;
import com.dungeon.entity.Ghost;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class CollisionDetectorTest {
    private CollisionDetector detector;

    @BeforeEach
    void setUp() {
        detector = new CollisionDetector();
    }

    @Test
    @DisplayName("Overlapping entities should collide")
    void testBasicCollision() {
        Player player = new Player(new Position(50, 50));
        Enemy enemy = new Ghost(new Position(60, 60));
        
        assertTrue(detector.checkCollision(player, enemy));
    }

    @Test
    @DisplayName("Non-overlapping entities should not collide")
    void testNoCollision() {
        Player player = new Player(new Position(0, 0));
        Enemy enemy = new Ghost(new Position(200, 200));
        
        assertFalse(detector.checkCollision(player, enemy));
    }

    @Test
    @DisplayName("Inactive entities should not collide")
    void testInactiveEntityCollision() {
        Player player = new Player(new Position(50, 50));
        Enemy enemy = new Ghost(new Position(60, 60));
        
        enemy.setActive(false);
        assertFalse(detector.checkCollision(player, enemy));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0, 27, 0, true",
        "0, 0, 28, 0, false",
        "0, 0, 0, 27, true",
        "0, 0, 100, 100, false"
    })
    @DisplayName("Edge case collision detection")
    void testEdgeCaseCollisions(int x1, int y1, int x2, int y2, boolean shouldCollide) {
        Player player = new Player(new Position(x1, y1));
        Enemy enemy = new Ghost(new Position(x2, y2));
        
        assertEquals(shouldCollide, detector.checkCollision(player, enemy));
    }

    @Test
    @DisplayName("Point in entity detection")
    void testPointInEntity() {
        Player player = new Player(new Position(50, 50));
        
        assertTrue(detector.pointInEntity(60, 60, player));
        assertFalse(detector.pointInEntity(100, 100, player));
    }
}