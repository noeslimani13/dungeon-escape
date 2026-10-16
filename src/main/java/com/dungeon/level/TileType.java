// ========== TileType.java ==========
package com.dungeon.level;

/**
 * Enum representing different tile types in the level.
 */
public enum TileType {
    FLOOR('.', false),
    WALL('#', true),
    PLAYER_START('P', false),
    EXIT('E', false),
    KEY('K', false),
    POTION('H', false),
    SHIELD('S', false),
    TRAP('T', false),
    GHOST('G', false),
    GOBLIN('O', false);

    private final char symbol;
    private final boolean solid;

    TileType(char symbol, boolean solid) {
        this.symbol = symbol;
        this.solid = solid;
    }

    /**
     * Gets TileType from character symbol.
     * 
     * @param symbol Character representing tile
     * @return Corresponding TileType
     */
    public static TileType fromChar(char symbol) {
        for (TileType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        return FLOOR;
    }

    public char getSymbol() { return symbol; }
    public boolean isSolid() { return solid; }
}