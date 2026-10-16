package com.dungeon.ui;

import com.dungeon.utils.ScoreManager;
import com.dungeon.utils.ScoreManager.ScoreEntry;
import com.dungeon.utils.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.List;

public class LeaderboardScreen extends VBox {
    private final GameWindow gameWindow;
    private final ScoreManager scoreManager;
    private final SoundManager soundManager;

    public LeaderboardScreen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.scoreManager = ScoreManager.getInstance();
        this.soundManager = SoundManager.getInstance();

        setupLayout();
        buildUI();
        setupAnimations();
    }

    private void setupLayout() {
        setAlignment(Pos.CENTER); // Centre horizontalement les enfants
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a1a;");
        setFillWidth(true); // Permet aux enfants de s'étendre en largeur selon maxWidth défini
    }

    private void buildUI() {
        // Title
        Text title = new Text("♔ HIGH SCORES ♔");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        title.setFill(Color.GOLD);
        getChildren().add(title);

        // Scores section scrollable
        ScrollPane scrollPane = createScoresScrollPane();
        scrollPane.setMaxWidth(800); // même largeur max pour cohérence
        getChildren().add(scrollPane);

        // Statistics section
        VBox statsBox = createStatisticsBox();
        statsBox.setMaxWidth(800);  // même largeur max que scrollPane
        getChildren().add(statsBox);

        // Action buttons
        HBox buttonBox = createActionButtons();
        getChildren().add(buttonBox);
    }

    private ScrollPane createScoresScrollPane() {
        GridPane scoresGrid = createScoresGrid();

        ScrollPane scrollPane = new ScrollPane(scoresGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(400);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return scrollPane;
    }

    private GridPane createScoresGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setMaxWidth(800);
        grid.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 10;");

        addHeader(grid, 0, "♔ Rank");
        addHeader(grid, 1, "♦ Player");
        addHeader(grid, 2, "★ Score");
        addHeader(grid, 3, "◆ Level");
        addHeader(grid, 4, "◷ Time");
        addHeader(grid, 5, "◉ Date");

        List<ScoreEntry> scores = scoreManager.getHighScores();
        if (scores.isEmpty()) {
            Label noScores = new Label("🎮 No scores yet! Be the first to play! 🎮");
            noScores.setTextFill(Color.LIGHTGRAY);
            noScores.setFont(Font.font(18));
            noScores.setAlignment(Pos.CENTER);
            noScores.setPadding(new Insets(20));
            grid.add(noScores, 0, 1, 6, 1);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry entry = scores.get(i);
                int row = i + 1;
                addRankCell(grid, 0, row, i + 1);
                addScoreCell(grid, 1, row, entry.getPlayerName(), Color.LIGHTBLUE, true);
                addScoreCell(grid, 2, row, String.format("%,d", entry.getScore()), Color.YELLOW, true);
                addScoreCell(grid, 3, row, String.valueOf(entry.getLevelReached()), Color.LIGHTGREEN, false);
                addScoreCell(grid, 4, row, entry.getFormattedPlayTime(), Color.LIGHTGRAY, false);
                addScoreCell(grid, 5, row, entry.getDate(), Color.LIGHTGRAY, false);
            }
        }
        return grid;
    }

    private VBox createStatisticsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20));
        statsBox.setMaxWidth(800);
        statsBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 10;");

        Text statsTitle = new Text("📊 STATISTICS");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statsTitle.setFill(Color.LIGHTBLUE);
        statsBox.getChildren().add(statsTitle);

        String stats = scoreManager.getStatistics();
        String[] lines = stats.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                Text statText = new Text("  " + line);
                statText.setFont(Font.font("Courier New", 14));
                statText.setFill(Color.WHITE);
                statsBox.getChildren().add(statText);
            }
        }
        return statsBox;
    }

    private HBox createActionButtons() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button clearButton = createStyledButton("🗑 Clear Scores");
        clearButton.setStyle(getButtonStyle("#8b4513"));
        clearButton.setOnMouseEntered(e -> clearButton.setStyle(getButtonStyle("#a0522d")));
        clearButton.setOnMouseExited(e -> clearButton.setStyle(getButtonStyle("#8b4513")));
        clearButton.setOnAction(e -> {
            soundManager.playSound("click");
            scoreManager.clearScores();
            gameWindow.showLeaderboard(); // Refresh
        });

        Button backButton = createStyledButton("← Back to Menu");
        backButton.setOnAction(e -> {
            soundManager.playSound("click");
            gameWindow.showMenu();
        });

        buttonBox.getChildren().addAll(clearButton, backButton);
        return buttonBox;
    }

    private void setupAnimations() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void addHeader(GridPane grid, int col, String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setMinWidth(col == 1 ? 150 : 100);
        label.setAlignment(Pos.CENTER);
        grid.add(label, col, 0);
    }

    private void addRankCell(GridPane grid, int col, int row, int rank) {
        HBox rankBox = new HBox(8);
        rankBox.setAlignment(Pos.CENTER);
        rankBox.setMinWidth(100);

        String medalSymbol = getMedalSymbol(rank);
        Color rankColor = getRankColor(rank);

        if (!medalSymbol.isEmpty()) {
            Label medalLabel = new Label(medalSymbol);
            medalLabel.setFont(Font.font(22));
            medalLabel.setTextFill(rankColor);
            rankBox.getChildren().add(medalLabel);
        }

        Label rankLabel = new Label("#" + rank);
        rankLabel.setTextFill(rankColor);
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        rankBox.getChildren().add(rankLabel);

        grid.add(rankBox, col, row);
    }

    private String getMedalSymbol(int rank) {
        return switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "";
        };
    }

    private Color getRankColor(int rank) {
        return switch (rank) {
            case 1 -> Color.GOLD;
            case 2 -> Color.SILVER;
            case 3 -> Color.rgb(205, 127, 50); // Bronze
            default -> Color.WHITE;
        };
    }

    private void addScoreCell(GridPane grid, int col, int row, String text, Color color, boolean bold) {
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(bold ?
                Font.font("Arial", FontWeight.BOLD, 14) :
                Font.font("Courier New", 14));
        label.setMinWidth(col == 1 ? 150 : 100);
        label.setAlignment(Pos.CENTER);
        grid.add(label, col, row);
    }

    private String getButtonStyle(String color) {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 12px 24px; " +
                        "-fx-background-radius: 5px;",
                color
        );
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setMinWidth(200);
        button.setCursor(Cursor.HAND);
        button.setStyle(getButtonStyle("#4a4a4a"));

        button.setOnMouseEntered(e ->
                button.setStyle(getButtonStyle("#6a6a6a"))
        );

        button.setOnMouseExited(e ->
                button.setStyle(getButtonStyle("#4a4a4a"))
        );

        return button;
    }
}
