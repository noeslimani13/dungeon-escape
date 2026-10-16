// ========== CollisionDetector.java ==========
package com.dungeon.physics;

import com.dungeon.entity.Entity;

/**
 * Handles collision detection between entities.
 * Uses AABB (Axis-Aligned Bounding Box) collision detection.
 */
public class CollisionDetector {

    /**
     * Checks if two entities are colliding using AABB.
     * 
     * @param entity1 First entity
     * @param entity2 Second entity
     * @return true if entities overlap
     */
    public boolean checkCollision(Entity entity1, Entity entity2) {
        if (entity1 == null || entity2 == null) {
            return false;
        }
        
        if (!entity1.isActive() || !entity2.isActive()) {
            return false;
        }

        Position pos1 = entity1.getPosition();
        Position pos2 = entity2.getPosition();

        return pos1.getX() < pos2.getX() + entity2.getWidth() &&
               pos1.getX() + entity1.getWidth() > pos2.getX() &&
               pos1.getY() < pos2.getY() + entity2.getHeight() &&
               pos1.getY() + entity1.getHeight() > pos2.getY();
    }

    /**
     * Checks if a point is inside an entity.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param entity Entity to check
     * @return true if point is inside
     */
    public boolean pointInEntity(int x, int y, Entity entity) {
        if (entity == null) {
            return false;
        }
        
        Position pos = entity.getPosition();
        return x >= pos.getX() && x <= pos.getX() + entity.getWidth() &&
               y >= pos.getY() && y <= pos.getY() + entity.getHeight();
    }
}