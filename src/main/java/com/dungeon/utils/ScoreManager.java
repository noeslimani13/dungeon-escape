package com.dungeon.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages high scores and game statistics.
 * Saves scores to file and provides leaderboard functionality.
 */
public class ScoreManager {
    private static final String SCORES_FILE = "dungeon_scores.dat";
    private static final int MAX_SCORES = 10;
    private static ScoreManager instance;
    
    private List<ScoreEntry> highScores;

    /**
     * Score entry class.
     */
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        private static final long serialVersionUID = 1L;
        
        private String playerName;
        private int score;
        private int levelReached;
        private String date;
        private long playTime; // in seconds

        public ScoreEntry(String playerName, int score, int levelReached, long playTime) {
            this.playerName = playerName;
            this.score = score;
            this.levelReached = levelReached;
            this.playTime = playTime;
            this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score); // Descending order
        }

        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public int getLevelReached() { return levelReached; }
        public String getDate() { return date; }
        public long getPlayTime() { return playTime; }
        
        public String getFormattedPlayTime() {
            long minutes = playTime / 60;
            long seconds = playTime % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    /**
     * Private constructor for Singleton.
     */
    private ScoreManager() {
        this.highScores = new ArrayList<>();
        loadScores();
    }

    /**
     * Gets singleton instance.
     * 
     * @return ScoreManager instance
     */
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    /**
     * Adds a new score entry.
     * 
     * @param playerName Player name
     * @param score Final score
     * @param levelReached Last level reached
     * @param playTime Total play time in seconds
     * @return true if score is a new high score
     */
    public boolean addScore(String playerName, int score, int levelReached, long playTime) {
        ScoreEntry entry = new ScoreEntry(playerName, score, levelReached, playTime);
        highScores.add(entry);
        Collections.sort(highScores);
        
        // Keep only top scores
        if (highScores.size() > MAX_SCORES) {
            highScores = highScores.subList(0, MAX_SCORES);
        }
        
        saveScores();
        
        // Check if it's a high score (top 10)
        return highScores.contains(entry);
    }

    /**
     * Gets the high scores list.
     * 
     * @return List of score entries
     */
    public List<ScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Gets the highest score.
     * 
     * @return Highest score or 0 if no scores
     */
    public int getHighestScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore();
    }

    /**
     * Checks if a score qualifies as a high score.
     * 
     * @param score Score to check
     * @return true if it's a high score
     */
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_SCORES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    /**
     * Gets the rank of a score.
     * 
     * @param score Score to rank
     * @return Rank (1-10) or 0 if not in top 10
     */
    public int getScoreRank(int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (score >= highScores.get(i).getScore()) {
                return i + 1;
            }
        }
        if (highScores.size() < MAX_SCORES) {
            return highScores.size() + 1;
        }
        return 0;
    }

    /**
     * Clears all high scores.
     */
    public void clearScores() {
        highScores.clear();
        saveScores();
    }

    /**
     * Saves scores to file.
     */
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SCORES_FILE))) {
            oos.writeObject(highScores);
            System.out.println("Scores saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    /**
     * Loads scores from file.
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SCORES_FILE);
        if (!file.exists()) {
            System.out.println("No previous scores found");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SCORES_FILE))) {
            highScores = (List<ScoreEntry>) ois.readObject();
            System.out.println("Scores loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading scores: " + e.getMessage());
            highScores = new ArrayList<>();
        }
    }

    /**
     * Gets statistics summary with symbols.
     * 
     * @return Statistics as formatted string
     */
    public String getStatistics() {
        if (highScores.isEmpty()) {
            return "No games played yet";
        }

        int totalGames = highScores.size();
        int totalScore = highScores.stream().mapToInt(ScoreEntry::getScore).sum();
        int avgScore = totalScore / totalGames;
        int maxLevel = highScores.stream().mapToInt(ScoreEntry::getLevelReached).max().orElse(0);

        return String.format(
            "♦ Games Played: %d\n" +
            "♦ Average Score: %,d\n" +
            "♦ Highest Score: %,d\n" +
            "♦ Max Level Reached: %d",
            totalGames, avgScore, getHighestScore(), maxLevel
        );
    }
    
    /**
     * Gets the medal symbol for a given rank.
     * 
     * @param rank The rank position (1-based)
     * @return Medal symbol string
     */
    public String getMedalForRank(int rank) {
        switch (rank) {
            case 1:
                return "♔"; // Gold crown
            case 2:
                return "♕"; // Silver crown
            case 3:
                return "♖"; // Bronze tower
            default:
                return ""; // No medal
        }
    }
    
    /**
     * Gets formatted rank string with medal.
     * 
     * @param rank The rank position (1-based)
     * @return Formatted rank string
     */
    public String getFormattedRank(int rank) {
        String medal = getMedalForRank(rank);
        if (!medal.isEmpty()) {
            return medal + " #" + rank;
        }
        return "#" + rank;
    }
}