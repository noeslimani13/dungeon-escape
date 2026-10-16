package com.dungeon.ai;

import com.dungeon.core.Game;
import com.dungeon.entity.*;
import com.dungeon.level.Level;
import com.dungeon.physics.Position;
import com.dungeon.utils.Config;

import java.util.*;

/**
 * Contrôleur IA qui joue automatiquement au jeu,
 * collecte les clés, évite les ennemis et pièges,
 * puis va à la sortie.
 */
public class AutoplayController {
    private final Game game;
    private boolean isActive;
    private AIState currentState;
    private final Set<Item> collectedItems;
    private long lastActionTime;
    private static final long ACTION_DELAY = 50; // millisecondes entre actions

    private enum AIState {
        COLLECTING_KEYS,
        GOING_TO_EXIT,
        AVOIDING_DANGER,
        IDLE
    }

    public AutoplayController(Game game) {
        this.game = game;
        this.isActive = false;
        this.currentState = AIState.IDLE;
        this.collectedItems = new HashSet<>();
        this.lastActionTime = System.currentTimeMillis();
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            System.out.println("🤖 AI Autoplay activé");
            this.currentState = AIState.COLLECTING_KEYS;
            collectedItems.clear();
        } else {
            System.out.println("🤖 AI Autoplay désactivé");
            this.currentState = AIState.IDLE;
        }
    }

    public void update(double deltaTime) {
        if (!isActive) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_DELAY) return;
        lastActionTime = currentTime;

        Player player = game.getPlayer();
        Level level = game.getCurrentLevel();

        if (player == null || level == null || player.isDead()) return;

        updateAIState(player, level);

        switch (currentState) {
            case COLLECTING_KEYS:
                collectKeys(player, level);
                break;
            case GOING_TO_EXIT:
                goToExit(player, level);
                break;
            case AVOIDING_DANGER:
                avoidDanger(player, level);
                break;
            case IDLE:
                // Rien à faire
                break;
        }
    }

    private void updateAIState(Player player, Level level) {
        if (isInDanger(player, level)) {
            currentState = AIState.AVOIDING_DANGER;
            return;
        }
        int requiredKeys = level.getRequiredKeys();
        if (player.hasAllKeys(requiredKeys)) {
            currentState = AIState.GOING_TO_EXIT;
        } else {
            currentState = AIState.COLLECTING_KEYS;
        }
    }

    private void collectKeys(Player player, Level level) {
        Item nearestKey = findNearestItem(player, level, Key.class);
        if (nearestKey != null) {
            moveTowards(player, nearestKey.getPosition(), level);
            if (isClose(player.getPosition(), nearestKey.getPosition())) {
                nearestKey.collect(player);
                collectedItems.add(nearestKey);
            }
        } else {
            Item nearestItem = findNearestItem(player, level, Item.class);
            if (nearestItem != null && !collectedItems.contains(nearestItem)) {
                moveTowards(player, nearestItem.getPosition(), level);
                if (isClose(player.getPosition(), nearestItem.getPosition())) {
                    nearestItem.collect(player);
                    collectedItems.add(nearestItem);
                }
            }
        }
    }

    private void goToExit(Player player, Level level) {
        Exit exit = level.getExit();
        if (exit != null) {
            moveTowards(player, exit.getPosition(), level);
        }
    }

    private void avoidDanger(Player player, Level level) {
        Position safePosition = findSafePosition(player, level);
        if (safePosition != null) {
            moveTowards(player, safePosition, level);
        }
    }

    private boolean isInDanger(Player player, Level level) {
        Position playerPos = player.getPosition();
        int dangerRadius = Config.TILE_SIZE * 2;

        for (Enemy enemy : level.getEnemies()) {
            if (enemy.isActive() && distance(playerPos, enemy.getPosition()) < dangerRadius
                    && player.getHealth() < player.getMaxHealth() / 2) {
                return true;
            }
        }
        for (Trap trap : level.getTraps()) {
            if (trap.isActive() && distance(playerPos, trap.getPosition()) < dangerRadius) {
                return true;
            }
        }
        return false;
    }

    private Position findSafePosition(Player player, Level level) {
        Position currentPos = player.getPosition();
        int searchRadius = Config.TILE_SIZE * 5;
        Position safest = null;
        double maxSafetyScore = -Double.MAX_VALUE;

        for (int dx = -searchRadius; dx <= searchRadius; dx += Config.TILE_SIZE) {
            for (int dy = -searchRadius; dy <= searchRadius; dy += Config.TILE_SIZE) {
                Position testPos = new Position(currentPos.getX() + dx, currentPos.getY() + dy);

                if (isPositionValid(testPos, level)) {
                    double safetyScore = calculateSafetyScore(testPos, level);
                    if (safetyScore > maxSafetyScore) {
                        maxSafetyScore = safetyScore;
                        safest = testPos;
                    }
                }
            }
        }
        return safest;
    }

    private double calculateSafetyScore(Position pos, Level level) {
        double score = 100.0;

        for (Enemy enemy : level.getEnemies()) {
            if (enemy.isActive()) {
                double dist = distance(pos, enemy.getPosition());
                if (dist < Config.TILE_SIZE * 3) {
                    score -= (Config.TILE_SIZE * 3 - dist) / 10.0;
                }
            }
        }

        for (Trap trap : level.getTraps()) {
            if (trap.isActive()) {
                double dist = distance(pos, trap.getPosition());
                if (dist < Config.TILE_SIZE * 2) {
                    score -= (Config.TILE_SIZE * 2 - dist) / 5.0;
                }
            }
        }

        return score;
    }

    private Item findNearestItem(Player player, Level level, Class<?> itemType) {
        Position playerPos = player.getPosition();
        Item nearest = null;
        double minDist = Double.MAX_VALUE;

        for (Item item : level.getItems()) {
            if (itemType.isInstance(item) && !collectedItems.contains(item)) {
                double dist = distance(playerPos, item.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = item;
                }
            }
        }
        return nearest;
    }

    private void moveTowards(Player player, Position target, Level level) {
        Position playerPos = player.getPosition();
        int dx = target.getX() - playerPos.getX();
        int dy = target.getY() - playerPos.getY();
        int moveSpeed = Config.PLAYER_SPEED;

        List<Position> candidates = new ArrayList<>();
        if (Math.abs(dx) > Math.abs(dy)) {
            candidates.add(new Position(playerPos.getX() + (dx > 0 ? moveSpeed : -moveSpeed), playerPos.getY()));
            candidates.add(new Position(playerPos.getX(), playerPos.getY() + (dy > 0 ? moveSpeed : -moveSpeed)));
        } else {
            candidates.add(new Position(playerPos.getX(), playerPos.getY() + (dy > 0 ? moveSpeed : -moveSpeed)));
            candidates.add(new Position(playerPos.getX() + (dx > 0 ? moveSpeed : -moveSpeed), playerPos.getY()));
        }

        for (Position move : candidates) {
            if (isPositionValid(move, level)) {
                player.setPosition(move);
                break;
            }
        }
    }

    private boolean isPositionValid(Position pos, Level level) {
        if (pos.getX() < 0 || pos.getY() < 0) return false;
        int tileX = pos.getX() / Config.TILE_SIZE;
        int tileY = pos.getY() / Config.TILE_SIZE;
        if (tileX >= level.getWidth() || tileY >= level.getHeight()) return false;
        return !level.getTile(tileX, tileY).isWall();
    }

    private double distance(Position a, Position b) {
        return a.distanceTo(b);
    }

    private boolean isClose(Position a, Position b) {
        return distance(a, b) < 10;
    }

    public boolean isActive() {
        return isActive;
    }

    public AIState getCurrentState() {
        return currentState;
    }
}
