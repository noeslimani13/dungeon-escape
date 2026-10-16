// ========== PositionTest.java ==========
package com.dungeon.physics;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    @DisplayName("Position should store coordinates correctly")
    void testPositionCreation() {
        Position pos = new Position(100, 200);
        
        assertEquals(100, pos.getX());
        assertEquals(200, pos.getY());
    }

    @Test
    @DisplayName("Position distance calculation should be correct")
    void testPositionDistance() {
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(3, 4);
        
        assertEquals(5.0, pos1.distanceTo(pos2), 0.01);
    }

    @Test
    @DisplayName("Position setters should work correctly")
    void testPositionSetters() {
        Position pos = new Position(0, 0);
        
        pos.setX(50);
        pos.setY(75);
        
        assertEquals(50, pos.getX());
        assertEquals(75, pos.getY());
    }
}