// ========== Timer.java ==========
package com.dungeon.utils;

/**
 * Game timer for tracking time limits.
 */
public class Timer {
    private int timeLimit; // in seconds
    private double elapsedTime;
    private boolean paused;
    private boolean expired;

    /**
     * Creates a new timer.
     * 
     * @param timeLimitSeconds Time limit in seconds
     */
    public Timer(int timeLimitSeconds) {
        this.timeLimit = timeLimitSeconds;
        this.elapsedTime = 0;
        this.paused = false;
        this.expired = false;
    }

    /**
     * Updates timer.
     * 
     * @param deltaTime Time elapsed in seconds
     */
    public void update(double deltaTime) {
        if (!paused && !expired) {
            elapsedTime += deltaTime;
            if (elapsedTime >= timeLimit) {
                expired = true;
            }
        }
    }

    /**
     * Resets timer to initial state.
     */
    public void reset() {
        elapsedTime = 0;
        expired = false;
        paused = false;
    }

    /**
     * Pauses the timer.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Resumes the timer.
     */
    public void resume() {
        paused = false;
    }

    /**
     * Gets remaining time in seconds.
     * 
     * @return Remaining seconds
     */
    public int getRemainingTime() {
        return Math.max(0, timeLimit - (int)elapsedTime);
    }

    /**
     * Gets elapsed time in seconds.
     * 
     * @return Elapsed seconds
     */
    public int getElapsedTime() {
        return (int)elapsedTime;
    }

    /**
     * Formats remaining time as MM:SS.
     * 
     * @return Formatted time string
     */
    public String getFormattedTime() {
        int remaining = getRemainingTime();
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isExpired() { return expired; }
    public boolean isPaused() { return paused; }
}
