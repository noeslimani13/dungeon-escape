package com.dungeon.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages game sounds and music with JavaFX Media API.
 * Supports background music, sound effects, volume control, and pause/resume.
 * Thread-safe implementation using Singleton pattern.
 */
public class SoundManager {
    private static volatile SoundManager instance;

    private boolean soundEnabled;
    private boolean musicEnabled;
    private double soundVolume;
    private double musicVolume;

    private MediaPlayer musicPlayer;
    private Map<String, Media> soundEffects;
    private String currentMusic;
    private Duration pausedMusicPosition;
    private final AtomicInteger activeSoundPlayers;
    private static final int MAX_CONCURRENT_SOUNDS = 5;

    private final boolean audioSystemAvailable;

    private SoundManager() {
        this.soundEnabled = true;
        this.musicEnabled = true;
        this.soundVolume = 0.5;
        this.musicVolume = 0.3;
        this.soundEffects = new HashMap<>();
        this.currentMusic = null;
        this.pausedMusicPosition = Duration.ZERO;
        this.activeSoundPlayers = new AtomicInteger(0);

        boolean audioAvailable = true;
        try {
            System.out.println("🔊 Initializing sound system...");
            Class.forName("javafx.scene.media.MediaPlayer");
        } catch (Exception e) {
            System.err.println("⚠️  Audio not supported in this environment (WSL/headless)");
            audioAvailable = false;
            this.soundEnabled = false;
            this.musicEnabled = false;
        }
        this.audioSystemAvailable = audioAvailable;
        if (audioAvailable) {
            initializeSoundEffects();
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) {
                    instance = new SoundManager();
                }
            }
        }
        return instance;
    }

    private void initializeSoundEffects() {
        String[] soundNames = {"collect", "damage", "victory", "gameover", "levelup", "click"};
        String[] extensions = {".wav", ".mp3", ".ogg"};

        for (String soundName : soundNames) {
            boolean loaded = false;
            for (String ext : extensions) {
                try {
                    URL resource = getClass().getResource("/sounds/effects/" + soundName + ext);
                    if (resource != null) {
                        Media media = new Media(resource.toString());
                        soundEffects.put(soundName, media);
                        System.out.println("✓ Loaded sound: " + soundName + ext);
                        loaded = true;
                        break;
                    }
                } catch (Exception e) {
                    // Try next format
                }
            }
            if (!loaded && !isWslEnvironment()) {
                System.err.println("✗ Could not load sound: " + soundName + " (tried .wav, .mp3, .ogg)");
            }
        }
        if (soundEffects.isEmpty()) {
            System.out.println("⚠️ No sound effects loaded. Audio may not be supported in this environment.");
        } else {
            System.out.println("✓ Loaded " + soundEffects.size() + " sound effects");
        }
    }

    private boolean isWslEnvironment() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        String osVersion = System.getProperty("os.version", "").toLowerCase();
        return osName.contains("linux") && (osVersion.contains("microsoft") || osVersion.contains("wsl"));
    }

    public void playMusic(String musicName) {
        if (!musicEnabled || !audioSystemAvailable) {
            return;
        }
        stopMusic();
        try {
            URL resource = getClass().getResource("/sounds/music/" + musicName + ".mp3");
            if (resource == null) {
                resource = getClass().getResource("/sounds/music/" + musicName + ".wav");
            }
            if (resource != null) {
                Media media = new Media(resource.toString());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setVolume(musicVolume);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.setOnError(() -> {
                    System.err.println("Error playing music: " + musicPlayer.getError());
                });
                musicPlayer.play();
                currentMusic = musicName;
                pausedMusicPosition = Duration.ZERO;
                System.out.println("♪ Playing music: " + musicName);
            } else {
                System.err.println("✗ Music file not found: " + musicName);
            }
        } catch (Exception e) {
            System.err.println("Error loading music: " + e.getMessage());
        }
    }

    public void pauseMusic() {
        if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            pausedMusicPosition = musicPlayer.getCurrentTime();
            musicPlayer.pause();
            System.out.println("⏸ Music paused at: " + pausedMusicPosition.toSeconds() + "s");
        }
    }

    public void resumeMusic() {
        if (musicPlayer != null && musicEnabled) {
            if (musicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                musicPlayer.play();
                System.out.println("▶ Music resumed from: " + pausedMusicPosition.toSeconds() + "s");
            } else if (currentMusic != null && musicPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                playMusic(currentMusic);
            }
        }
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
        }
        currentMusic = null;
        pausedMusicPosition = Duration.ZERO;
    }

    public void playSound(String soundName) {
        if (!soundEnabled || !audioSystemAvailable) {
            return;
        }
        if (activeSoundPlayers.get() >= MAX_CONCURRENT_SOUNDS) {
            return;
        }
        Media media = soundEffects.get(soundName);
        if (media == null) {
            System.err.println("✗ Sound not found: " + soundName);
            return;
        }
        new Thread(() -> {
            try {
                activeSoundPlayers.incrementAndGet();
                String url = media.getSource();
                Media newMedia = new Media(url);
                final MediaPlayer player = new MediaPlayer(newMedia); // FINAL

                player.setVolume(soundVolume);

                player.setOnEndOfMedia(() -> {
                    activeSoundPlayers.decrementAndGet();
                    player.dispose();
                    System.out.println("🔊 Sound finished: " + soundName);
                });
                player.setOnError(() -> {
                    activeSoundPlayers.decrementAndGet();
                    player.dispose();
                    System.err.println("✗ Error playing sound: " + soundName);
                });

                player.play();

                while (
                    player.getStatus() != MediaPlayer.Status.DISPOSED &&
                    player.getStatus() != MediaPlayer.Status.STOPPED &&
                    player.getStatus() != MediaPlayer.Status.HALTED
                ) {
                    Thread.sleep(50);
                }

            } catch (Exception e) {
                activeSoundPlayers.decrementAndGet();
                System.err.println("✗ Exception: " + e.getMessage());
            }
        }, "SoundEffect-" + soundName).start();
    }

    public void playSound(String soundName, double volumeOverride) {
        double originalVolume = this.soundVolume;
        this.soundVolume = Math.max(0.0, Math.min(1.0, volumeOverride));
        playSound(soundName);
        this.soundVolume = originalVolume;
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (musicPlayer != null) {
            musicPlayer.setVolume(musicVolume);
        }
    }

    public void setSoundVolume(double volume) {
        this.soundVolume = Math.max(0.0, Math.min(1.0, volume));
    }

    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (!musicEnabled && musicPlayer != null) {
            pauseMusic();
        } else if (musicEnabled && musicPlayer != null) {
            resumeMusic();
        }
    }

    public void toggleSound() {
        soundEnabled = !soundEnabled;
    }

    public void fadeOutMusic(long durationMs) {
        if (musicPlayer == null) return;
        new Thread(() -> {
            double startVolume = musicVolume;
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < durationMs) {
                double progress = (double) (System.currentTimeMillis() - startTime) / durationMs;
                double newVolume = startVolume * (1.0 - progress);
                if (musicPlayer != null) {
                    musicPlayer.setVolume(Math.max(0.0, newVolume));
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
            stopMusic();
        }, "MusicFadeOut").start();
    }

    public void cleanup() {
        stopMusic();
        activeSoundPlayers.set(0);
        System.out.println("🔇 Sound system cleaned up");
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled && audioSystemAvailable;
    }
    public boolean isMusicEnabled() { return musicEnabled; }
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled && audioSystemAvailable;
        if (!enabled && musicPlayer != null) {
            pauseMusic();
        } else if (enabled && musicPlayer != null) {
            resumeMusic();
        }
    }
    public double getSoundVolume() { return soundVolume; }
    public double getMusicVolume() { return musicVolume; }
    public String getCurrentMusic() { return currentMusic; }
    public boolean isAudioSystemAvailable() { return audioSystemAvailable; }
    public int getActiveSoundCount() { return activeSoundPlayers.get(); }
    public MediaPlayer.Status getMusicStatus() {
        return musicPlayer != null ? musicPlayer.getStatus() : null;
    }
}
