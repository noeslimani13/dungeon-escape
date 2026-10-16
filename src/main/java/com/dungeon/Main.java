package com.dungeon;

import com.dungeon.ui.GameWindow;
import javafx.application.Application;

/**
 * Main entry point for Dungeon Escape game.
 * This class launches the JavaFX application.
 * 
 * @author Dungeon Escape Team
 * @version 1.0.0
 */
public class Main {
    /**
     * Main method that launches the JavaFX application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Application.launch(GameWindow.class, args);
    }
}