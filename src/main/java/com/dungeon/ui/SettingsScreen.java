package com.dungeon.ui;

import com.dungeon.utils.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Settings screen with audio, control settings, and visual customization.
 * Provides comprehensive configuration options for the game.
 */
public class SettingsScreen extends VBox {
    private final GameWindow gameWindow;
    private final SoundManager soundManager;

    // Style constants
    private static final String BACKGROUND_COLOR = "#1a1a1a";
    private static final String SECTION_COLOR = "#2a2a2a";
    private static final String BUTTON_COLOR = "#4a4a4a";
    private static final String BUTTON_HOVER_COLOR = "#6a6a6a";
    private static final String KEY_DISPLAY_COLOR = "#3a3a3a";

    /**
     * Creates settings screen with all configuration options.
     * 
     * @param gameWindow Game window instance
     */
    public SettingsScreen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.soundManager = SoundManager.getInstance();

        setupLayout();
        buildUI();
    }

    /**
     * Sets up the base layout configuration.
     */
    private void setupLayout() {
        setAlignment(Pos.CENTER);
        setSpacing(8);             // Reduced vertical spacing for compactness
        setPadding(new Insets(15)); // Slightly reduced padding
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        setCursor(Cursor.DEFAULT);
        setOnMouseEntered(e -> setCursor(Cursor.DEFAULT));
    }

    /**
     * Builds the complete UI.
     */
    private void buildUI() {
        getChildren().add(createTitle());

        getChildren().add(createAudioSettings());

        getChildren().addAll(
            createControlsSection(),
            createMouseControlsSection()
        );

        getChildren().add(createAdditionalSettings());

        getChildren().add(createActionButtons());
    }

    /**
     * Creates the main title.
     */
    private Text createTitle() {
        Text title = new Text("⚙ SETTINGS ⚙");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setFill(Color.LIGHTBLUE);
        return title;
    }

    /**
     * Creates audio settings section with volume controls.
     */
    private VBox createAudioSettings() {
        VBox audioBox = new VBox(2);
        VBox aiSection = createAISettings();
        getChildren().add(aiSection);
        audioBox.setAlignment(Pos.CENTER);
        audioBox.setPadding(new Insets(6));
        audioBox.setStyle("-fx-background-color: " + SECTION_COLOR + "; -fx-background-radius: 10;");
        audioBox.setMaxWidth(800);  // Harmonized width

        Text audioTitle = new Text("🔊 AUDIO SETTINGS");
        audioTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        audioTitle.setFill(Color.YELLOW);
        audioBox.getChildren().add(audioTitle);

        audioBox.getChildren().addAll(
            createMusicCheckbox(),
            createMusicVolumeControl()
        );

        audioBox.getChildren().add(createSeparator());

        audioBox.getChildren().addAll(
            createSoundCheckbox(),
            createSoundVolumeControl()
        );

        audioBox.getChildren().add(createTestSoundButton());

        return audioBox;
    }

    private CheckBox createMusicCheckbox() {
        CheckBox musicCheckbox = new CheckBox("Background Music");
        styleCheckbox(musicCheckbox);
        musicCheckbox.setSelected(soundManager.isMusicEnabled());
        musicCheckbox.setOnAction(e -> {
            soundManager.setMusicEnabled(musicCheckbox.isSelected());
            if (musicCheckbox.isSelected()) {
                soundManager.playMusic("menu");
            } else {
                soundManager.stopMusic();
            }
        });
        return musicCheckbox;
    }

    private VBox createMusicVolumeControl() {
        VBox volumeBox = new VBox(5);
        volumeBox.setAlignment(Pos.CENTER);

        Label musicVolumeLabel = new Label("Music Volume: " + (int)(soundManager.getMusicVolume() * 100) + "%");
        musicVolumeLabel.setTextFill(Color.LIGHTGRAY);
        musicVolumeLabel.setFont(Font.font(14));

        Slider musicVolumeSlider = createVolumeSlider(soundManager.getMusicVolume() * 100);

        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            soundManager.setMusicVolume(volume);
            musicVolumeLabel.setText("Music Volume: " + newVal.intValue() + "%");
        });

        volumeBox.getChildren().addAll(musicVolumeLabel, musicVolumeSlider);
        return volumeBox;
    }

    
    private CheckBox createSoundCheckbox() {
        CheckBox soundCheckbox = new CheckBox("Sound Effects");
        styleCheckbox(soundCheckbox);
        soundCheckbox.setSelected(soundManager.isSoundEnabled());
        soundCheckbox.setOnAction(e -> {
            soundManager.setSoundEnabled(soundCheckbox.isSelected());
            if (soundCheckbox.isSelected()) {
                soundManager.playSound("click");
            }
        });
        return soundCheckbox;
    }

    private VBox createSoundVolumeControl() {
        VBox volumeBox = new VBox(5);
        volumeBox.setAlignment(Pos.CENTER);

        Label soundVolumeLabel = new Label("Sound Volume: " + (int)(soundManager.getSoundVolume() * 100) + "%");
        soundVolumeLabel.setTextFill(Color.LIGHTGRAY);
        soundVolumeLabel.setFont(Font.font(14));

        Slider soundVolumeSlider = createVolumeSlider(soundManager.getSoundVolume() * 100);

        soundVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            soundManager.setSoundVolume(volume);
            soundVolumeLabel.setText("Sound Volume: " + newVal.intValue() + "%");
        });

        volumeBox.getChildren().addAll(soundVolumeLabel, soundVolumeSlider);
        return volumeBox;
    }

    

    // Nouvelle méthode
    private VBox createAISettings() {
        VBox aiBox = new VBox(2);
        aiBox.setAlignment(Pos.CENTER);
        aiBox.setPadding(new Insets(8));
        aiBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 10;");
        aiBox.setMaxWidth(800);  // Harmonized width

        Text aiTitle = new Text("🤖 AI AUTOPLAY");
        aiTitle.setFont(Font.font(24));
        aiTitle.setFill(Color.YELLOW);
        aiBox.getChildren().add(aiTitle);

        CheckBox aiCheckbox = new CheckBox("Enable AI Autoplay");
        aiCheckbox.setTextFill(Color.WHITE);
        aiCheckbox.setFont(Font.font(16));
        aiCheckbox.setSelected(gameWindow.getGame().getAutoplayController().isActive());
        aiCheckbox.setCursor(Cursor.HAND);
        aiCheckbox.setOnAction(e -> {
        gameWindow.getGame().getAutoplayController().setActive(aiCheckbox.isSelected());
        });
        aiBox.getChildren().add(aiCheckbox);

        Label aiInfo = new Label("AI will play automatically and try to win the game");
        aiInfo.setTextFill(Color.LIGHTGRAY);
        aiInfo.setFont(Font.font(12));
        aiBox.getChildren().add(aiInfo);

        return aiBox;
    }

    private Button createTestSoundButton() {
        Button testButton = new Button("🔔 Test Sound");
        testButton.setFont(Font.font(14));
        testButton.setMinWidth(150);
        styleButton(testButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);
        testButton.setOnAction(e -> soundManager.playSound("collect"));
        return testButton;
    }

    /**
     * Creates keyboard controls section.
     */
    private VBox createControlsSection() {
        VBox controlsBox = new VBox(2); // less spacing
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(6, 15, 12, 15)); // tighter padding
        controlsBox.setStyle("-fx-background-color: " + SECTION_COLOR + "; -fx-background-radius: 10;");
        controlsBox.setMaxWidth(800);  // Harmonized width

        Text controlsTitle = new Text("⌨️ KEYBOARD CONTROLS");
        controlsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        controlsTitle.setFill(Color.YELLOW);
        controlsBox.getChildren().add(controlsTitle);

        GridPane controlsGrid = new GridPane();
        controlsGrid.setAlignment(Pos.CENTER);
        controlsGrid.setHgap(10);  // tighter gaps
        controlsGrid.setVgap(8);

        // Set column widths to control overall layout width
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(140);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(100);
        controlsGrid.getColumnConstraints().addAll(col1, col2);

        addControlRow(controlsGrid, 0, "Move Up:", "W or ↑");
        addControlRow(controlsGrid, 1, "Move Down:", "S or ↓");
        addControlRow(controlsGrid, 2, "Move Left:", "A or ←");
        addControlRow(controlsGrid, 3, "Move Right:", "D or →");
        addControlRow(controlsGrid, 4, "Pause Game:", "ESC or P");
        addControlRow(controlsGrid, 5, "Toggle Fullscreen:", "F11");

        controlsBox.getChildren().add(controlsGrid);
        return controlsBox;
    }

    /**
     * Creates mouse controls section.
     */
    private VBox createMouseControlsSection() {
        VBox mouseBox = new VBox(2); // less spacing
        mouseBox.setAlignment(Pos.CENTER);
        mouseBox.setPadding(new Insets(8, 15, 12, 15)); // tighter padding
        mouseBox.setStyle("-fx-background-color: " + SECTION_COLOR + "; -fx-background-radius: 10;");
        mouseBox.setMaxWidth(800); // Harmonized width

        Text mouseTitle = new Text("🖱️ MOUSE CONTROLS");
        mouseTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        mouseTitle.setFill(Color.YELLOW);
        mouseBox.getChildren().add(mouseTitle);

        GridPane mouseGrid = new GridPane();
        mouseGrid.setAlignment(Pos.CENTER);
        mouseGrid.setHgap(10);
        mouseGrid.setVgap(8);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(140);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(100);
        mouseGrid.getColumnConstraints().addAll(col1, col2);

        addControlRow(mouseGrid, 0, "Move Player:", "Click & Hold");
        addControlRow(mouseGrid, 1, "Direction:", "Follow Cursor");
        addControlRow(mouseGrid, 2, "Release:", "Stop Movement");

        mouseBox.getChildren().add(mouseGrid);
        return mouseBox;
    }

    /**
     * Creates additional settings section.
     */
    private VBox createAdditionalSettings() {
        VBox additionalBox = new VBox(2);
        additionalBox.setAlignment(Pos.CENTER);
        additionalBox.setPadding(new Insets(8));
        additionalBox.setStyle("-fx-background-color: " + SECTION_COLOR + "; -fx-background-radius: 10;");
        additionalBox.setMaxWidth(800);  // Harmonized width

        Text additionalTitle = new Text("🎮 GAME INFO");
        additionalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        additionalTitle.setFill(Color.YELLOW);
        additionalBox.getChildren().add(additionalTitle);

        String audioStatus = soundManager.isAudioSystemAvailable()
            ? "✓ Audio System: Available"
            : "✗ Audio System: Unavailable (WSL/Headless)";
        Label audioStatusLabel = new Label(audioStatus);
        audioStatusLabel.setTextFill(soundManager.isAudioSystemAvailable() ? Color.LIGHTGREEN : Color.ORANGE);
        audioStatusLabel.setFont(Font.font(14));

        Label activeSoundsLabel = new Label("Active Sounds: " + soundManager.getActiveSoundCount() + " / 5");
        activeSoundsLabel.setTextFill(Color.LIGHTGRAY);
        activeSoundsLabel.setFont(Font.font(14));

        additionalBox.getChildren().addAll(audioStatusLabel, activeSoundsLabel);
        return additionalBox;
    }

    /**
     * Creates action buttons at the bottom.
     */
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button resetButton = new Button("🔄 Reset to Default");
        resetButton.setFont(Font.font(18));
        resetButton.setMinWidth(180);
        styleButton(resetButton, "#8b4513", "#a0522d");
        resetButton.setOnAction(e -> {
            resetToDefaults();
            soundManager.playSound("click");
        });

        Button backButton = new Button("← Back to Menu");
        backButton.setFont(Font.font(18));
        backButton.setMinWidth(180);
        styleButton(backButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);
        backButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showMenu();
        });

        buttonBox.getChildren().addAll(resetButton, backButton);
        return buttonBox;
    }

    private void resetToDefaults() {
        soundManager.setMusicVolume(0.3);
        soundManager.setSoundVolume(0.5);
        soundManager.setMusicEnabled(true);
        soundManager.setSoundEnabled(true);

        gameWindow.showSettings();
    }

    private Slider createVolumeSlider(double initialValue) {
        Slider slider = new Slider(0, 100, initialValue);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(4);
        slider.setBlockIncrement(5);
        slider.setPrefWidth(350);
        slider.setMaxWidth(400);
        slider.setCursor(Cursor.HAND);
        slider.setStyle(
            "-fx-control-inner-background: #3a3a3a;" +
            "-fx-text-fill: white;"
        );
        return slider;
    }

    private void styleCheckbox(CheckBox checkbox) {
        checkbox.setTextFill(Color.WHITE);
        checkbox.setFont(Font.font(16));
        checkbox.setCursor(Cursor.HAND);
        checkbox.setStyle("-fx-text-fill: white;");
    }

    private void styleButton(Button button, String normalColor, String hoverColor) {
        button.setCursor(Cursor.HAND);
        String baseStyle =
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 5px; " +
            "-fx-border-radius: 5px;";

        button.setStyle(String.format(baseStyle, normalColor));

        button.setOnMouseEntered(e ->
            button.setStyle(String.format(baseStyle, hoverColor))
        );
        button.setOnMouseExited(e ->
            button.setStyle(String.format(baseStyle, normalColor))
        );
    }

    private void addControlRow(GridPane grid, int row, String label, String key) {
        Label labelNode = new Label(label);
        labelNode.setTextFill(Color.WHITE);
        labelNode.setFont(Font.font(16));

        Label keyNode = new Label(key);
        keyNode.setTextFill(Color.YELLOW);
        keyNode.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        keyNode.setStyle(
            "-fx-background-color: " + KEY_DISPLAY_COLOR + "; " +
            "-fx-padding: 5px 15px; " +
            "-fx-background-radius: 3px; " +
            "-fx-border-color: #5a5a5a; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 3px;"
        );

        grid.add(labelNode, 0, row);
        grid.add(keyNode, 1, row);
    }

    private javafx.scene.layout.Region createSeparator() {
        javafx.scene.layout.Region separator = new javafx.scene.layout.Region();
        separator.setPrefHeight(1);
        separator.setMaxWidth(Double.MAX_VALUE);
        separator.setStyle("-fx-background-color: #4a4a4a;");
        return separator;
    }
}
