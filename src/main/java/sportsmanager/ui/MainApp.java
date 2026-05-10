package sportsmanager.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import sportsmanager.core.*;
import sportsmanager.football.FootballFactory;
import sportsmanager.volleyball.VolleyballFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class MainApp extends Application {

    private static final String SAVE_DIR = "saves";
    private static final int SLOT_COUNT = 3;

    private Stage primaryStage;

    private ILeague currentLeague;
    private boolean isFootballSport;
    private ITeam managedTeam;
    private ISportFactory currentFactory;
    private String currentSportName;

    // Dashboard widgets
    private ListView<String> standingsView;
    private ListView<FixtureItem> fixtureView;
    private ListView<PlayerStatLine> goalsStatsView;
    private ListView<PlayerStatLine> cardsStatsView;
    private ListView<PlayerStatLine> availabilityStatsView;
    private Label nextMatchLabel;
    private Button playMatchBtn;
    private Label statusLabel;
    private boolean championCelebrationShown;

    // Match view widgets
    private IMatch currentMatch;
    private Label matchClockLabel;
    private Label matchHomeScoreLabel;
    private Label matchAwayScoreLabel;
    private ListView<MatchEvent> eventsView;
    private ListView<IPlayer> ourOnFieldView;
    private ListView<IPlayer> ourBenchView;
    private ListView<IPlayer> ourUnavailableView;
    private ListView<IPlayer> oppOnFieldView;
    private ListView<IPlayer> oppBenchView;
    private ListView<IPlayer> oppUnavailableView;
    private StackPane formationHolder;
    private Button substituteBtn;
    private Button quickFinishBtn;
    private Button secondHalfBtn;
    private Button continueBtn;
    private ToggleGroup speedGroup;
    private Timeline matchTimeline;
    private boolean animateNextFormation;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        ensureSaveDir();
        updateWindowTitle();
        primaryStage.setScene(buildMainMenuScene());
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(740);
        primaryStage.show();
    }

    // ============== MAIN MENU ==============

    private Scene buildMainMenuScene() {
        updateWindowTitle();
        Label title = new Label(I18n.t("main.title"));
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 2, 3);");
        Label subtitle = new Label(I18n.t("main.selectSport"));
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #ecf0f1; -fx-padding: 0 0 18 0;");

        Button btnFootball = bigMenuButton(I18n.t("main.football"), "#27ae60");
        Button btnVolleyball = bigMenuButton(I18n.t("main.volleyball"), "#e67e22");
        btnFootball.setOnAction(e -> openTeamSelect(new FootballFactory(), I18n.t("save.sportFootball")));
        btnVolleyball.setOnAction(e -> openTeamSelect(new VolleyballFactory(), I18n.t("save.sportVolleyball")));

        HBox sportRow = new HBox(28, btnFootball, btnVolleyball);
        sportRow.setAlignment(Pos.CENTER);

        Button mySaves = new Button(I18n.t("main.mySaves"));
        mySaves.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #8e44ad;"
                + " -fx-text-fill: white; -fx-pref-width: 220px; -fx-pref-height: 50px; -fx-cursor: hand;"
                + " -fx-background-radius: 22px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 4);");
        mySaves.setOnAction(e -> primaryStage.setScene(buildSavesManagerScene()));

        // Language toggle
        Label langLabel = new Label(I18n.t("main.lang.label"));
        langLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        ToggleGroup langGroup = new ToggleGroup();
        ToggleButton trBtn = langPill("TR", I18n.Lang.TR, langGroup);
        ToggleButton enBtn = langPill("EN", I18n.Lang.EN, langGroup);
        if (I18n.getLanguage() == I18n.Lang.TR) trBtn.setSelected(true); else enBtn.setSelected(true);
        HBox langRow = new HBox(8, langLabel, trBtn, enBtn);
        langRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, subtitle, sportRow, mySaves, langRow);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a2a6c, #b21f1f, #fdbb2d);");
        return new Scene(root, 1100, 740);
    }

    private ToggleButton langPill(String text, I18n.Lang lang, ToggleGroup group) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(group);
        tb.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.85);"
                + " -fx-text-fill: #2c3e50; -fx-padding: 6 14; -fx-cursor: hand; -fx-background-radius: 14;");
        tb.setOnAction(e -> {
            if (!tb.isSelected()) { tb.setSelected(true); return; }
            if (I18n.getLanguage() != lang) {
                I18n.setLanguage(lang);
                updateWindowTitle();
                primaryStage.setScene(buildMainMenuScene());
            }
        });
        return tb;
    }

    private void updateWindowTitle() {
        if (primaryStage != null) primaryStage.setTitle(I18n.t("main.title"));
    }

    private Button bigMenuButton(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: " + color + ";"
                + " -fx-text-fill: white; -fx-pref-width: 220px; -fx-pref-height: 70px; -fx-cursor: hand;"
                + " -fx-background-radius: 30px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        return b;
    }

    // ============== SAVES MANAGER ==============

    private Scene buildSavesManagerScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a2a6c, #b21f1f, #fdbb2d);");

        Label title = new Label(I18n.t("saves.title"));
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 2, 2);");

        VBox slotList = new VBox(8);
        slotList.setAlignment(Pos.CENTER);
        for (boolean foot : new boolean[]{true, false}) {
            for (int i = 1; i <= SLOT_COUNT; i++) {
                slotList.getChildren().add(buildSaveSlotRow(i, foot));
            }
        }

        Button back = secondaryButton(I18n.t("saves.back"), "#7f8c8d");
        back.setOnAction(e -> primaryStage.setScene(buildMainMenuScene()));

        ScrollPane scroll = new ScrollPane(slotList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setMaxHeight(500);

        VBox content = new VBox(16, title, scroll, back);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        content.setMaxWidth(820);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 14;");

        StackPane wrap = new StackPane(content);
        wrap.setPadding(new Insets(28));
        root.getChildren().addAll(wrap);
        return new Scene(root, 1100, 740);
    }

    private HBox buildSaveSlotRow(int slot, boolean football) {
        File f = new File(slotPathForSport(slot, football));
        boolean exists = f.exists();
        DataManager.SaveMeta meta = exists ? DataManager.loadMeta(slotPathForSport(slot, football)) : null;
        String sportTag = football ? "⚽ " + I18n.t("save.sportFootball")
                                   : "🏐 " + I18n.t("save.sportVolleyball");
        String name = (meta != null && meta.name != null && !meta.name.isBlank())
                ? meta.name
                : (exists ? I18n.t("save.unnamed") : "");
        long ts = (meta != null && meta.timestamp > 0) ? meta.timestamp : (exists ? f.lastModified() : 0);
        String when = ts > 0 ? new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(ts)) : "";
        String managed = (meta != null && meta.managedTeam != null && !meta.managedTeam.isBlank())
                ? meta.managedTeam : "";
        String progress = (meta != null && meta.matchesTotal > 0)
                ? I18n.f("saves.matches", meta.matchesPlayed, meta.matchesTotal) : "";

        Label slotLabel = new Label("Slot " + slot + "  ·  " + sportTag);
        slotLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        Label nameLabel = new Label(exists ? name : I18n.t("saves.empty"));
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label detailLabel = new Label(
                exists ? (when + (managed.isEmpty() ? "" : "  ·  " + managed)
                              + (progress.isEmpty() ? "" : "  ·  " + progress)) : "");
        detailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

        VBox info = new VBox(2, slotLabel, nameLabel, detailLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button loadBtn = new Button(I18n.t("saves.load"));
        loadBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #16a085;"
                + " -fx-text-fill: white; -fx-padding: 8 18; -fx-cursor: hand; -fx-background-radius: 8;");
        loadBtn.setDisable(!exists);
        loadBtn.setOnAction(e -> loadFromSlot(slot, football));

        Button delBtn = new Button(I18n.t("saves.delete"));
        delBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #c0392b;"
                + " -fx-text-fill: white; -fx-padding: 8 18; -fx-cursor: hand; -fx-background-radius: 8;");
        delBtn.setDisable(!exists);
        delBtn.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    I18n.t("saves.deleteConfirmBody"), ButtonType.OK, ButtonType.CANCEL);
            a.setHeaderText(I18n.t("saves.deleteConfirmTitle"));
            Optional<ButtonType> r = a.showAndWait();
            if (r.isPresent() && r.get() == ButtonType.OK) {
                deleteSlot(slot, football);
                primaryStage.setScene(buildSavesManagerScene());
            }
        });

        HBox row = new HBox(14, info, spacer, loadBtn, delBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle(exists
                ? "-fx-background-color: rgba(255,255,255,0.10); -fx-background-radius: 10;"
                : "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10;");
        return row;
    }

    private void loadFromSlot(int slot, boolean football) {
        try {
            ILeague loaded = DataManager.loadGame(slotPathForSport(slot, football));
            currentLeague = loaded;
            isFootballSport = football;
            currentFactory = football ? new FootballFactory() : new VolleyballFactory();
            currentSportName = football ? I18n.t("save.sportFootball") : I18n.t("save.sportVolleyball");
            managedTeam = currentLeague.getManagedTeam();
            if (managedTeam == null && !currentLeague.getTeams().isEmpty()) {
                managedTeam = currentLeague.getTeams().get(0);
                currentLeague.setManagedTeam(managedTeam);
            }
            primaryStage.setScene(buildDashboardScene());
            setStatus(I18n.t("saves.loadOk"));
        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR,
                    I18n.t("saves.loadError") + ex.getMessage(), ButtonType.OK);
            a.showAndWait();
        }
    }

    private void deleteSlot(int slot, boolean football) {
        File f = new File(slotPathForSport(slot, football));
        File meta = new File(slotPathForSport(slot, football) + ".meta");
        if (f.exists()) f.delete();
        if (meta.exists()) meta.delete();
    }

    // ============== TEAM SELECT ==============

    private void openTeamSelect(ISportFactory factory, String sportName) {
        championCelebrationShown = false;
        this.currentFactory = factory;
        this.currentSportName = sportName;
        this.isFootballSport = factory instanceof FootballFactory;
        this.currentLeague = factory.createLeague(I18n.f("league.name", sportName));

        String[] teamNames = isFootballSport ? Squads.FOOTBALL_TEAMS : Squads.VOLLEYBALL_TEAMS;
        for (String tn : teamNames) {
            ITeam t = factory.createTeam(tn);
            // Coach removed from game (the user is the coach)
            Squads.fillRoster(t, factory, isFootballSport);
            currentLeague.addTeam(t);
        }
        currentLeague.scheduleMatches();

        primaryStage.setScene(buildTeamSelectScene());
    }

    private Scene buildTeamSelectScene() {
        StackPane root = new StackPane();
        Pane field = isFootballSport ? FieldBackground.footballField() : FieldBackground.volleyballCourt();
        field.prefWidthProperty().bind(root.widthProperty());
        field.prefHeightProperty().bind(root.heightProperty());

        Label title = new Label((isFootballSport ? "⚽ " : "🏐 ") + I18n.t("teamSelect.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 8, 0, 1, 1);");

        VBox teamCards = new VBox(8);
        teamCards.setAlignment(Pos.CENTER);
        for (ITeam t : currentLeague.getTeams()) teamCards.getChildren().add(buildTeamCard(t));

        Button back = secondaryButton(I18n.t("teamSelect.back"), "#7f8c8d");
        back.setOnAction(e -> primaryStage.setScene(buildMainMenuScene()));

        VBox content = new VBox(14, title, teamCards, back);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        content.setMaxWidth(820);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 14;");

        StackPane wrap = new StackPane(content);
        wrap.setPadding(new Insets(24));
        root.getChildren().addAll(field, wrap);
        return new Scene(root, 1100, 740);
    }

    private HBox buildTeamCard(ITeam team) {
        Label name = new Label(team.getName());
        name.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");
        int skill = teamAverageSkill(team);
        Label info = new Label(I18n.t("teamSelect.avg") + ": " + skill
                + "  ·  " + team.getPlayers().size() + " " + I18n.t("teamSelect.players")
                + "  ·  " + I18n.t("teamSelect.stadium") + ": " + Squads.stadium(team.getName()));
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");
        VBox txt = new VBox(2, name, info);

        Button pick = new Button(I18n.t("teamSelect.pick"));
        pick.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #16a085;"
                + " -fx-text-fill: white; -fx-padding: 8 14; -fx-cursor: hand; -fx-background-radius: 8;");
        pick.setOnAction(e -> openDashboard(team));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(14, txt, spacer, pick);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 14, 8, 14));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-background-radius: 10;");
        return row;
    }

    private int teamAverageSkill(ITeam team) {
        if (team.getPlayers().isEmpty()) return 0;
        int sum = 0;
        for (IPlayer p : team.getPlayers()) sum += p.getSkillLevel();
        return sum / team.getPlayers().size();
    }

    // ============== DASHBOARD ==============

    private void openDashboard(ITeam team) {
        this.managedTeam = team;
        currentLeague.setManagedTeam(team);
        primaryStage.setScene(buildDashboardScene());
    }

    private Scene buildDashboardScene() {
        StackPane root = new StackPane();
        Pane field = isFootballSport ? FieldBackground.footballField() : FieldBackground.volleyballCourt();
        field.prefWidthProperty().bind(root.widthProperty());
        field.prefHeightProperty().bind(root.heightProperty());

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(14));
        content.setTop(buildDashboardTopBar());
        content.setCenter(buildDashboardCenter());
        content.setBottom(buildDashboardBottomBar());

        root.getChildren().addAll(field, content);
        refreshDashboard();
        return new Scene(root, 1100, 740);
    }

    private HBox buildDashboardTopBar() {
        Button back = secondaryButton(I18n.t("dash.mainMenuBtn"), "#7f8c8d");
        back.setOnAction(e -> confirmAndGoToMainMenu());

        String icon = isFootballSport ? "⚽" : "🏐";
        Label header = new Label(icon + "  " + currentLeague.getName().toUpperCase()
                + "  |  " + I18n.t("dash.myTeam") + ": " + managedTeam.getName());
        header.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button save = secondaryButton(I18n.t("dash.save"), "#2980b9");
        save.setOnAction(e -> openSaveDialog());

        HBox bar = new HBox(12, back, header, spacer, save);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(4, 6, 12, 6));
        return bar;
    }

    private HBox buildDashboardCenter() {
        standingsView = new ListView<>();
        standingsView.setStyle("-fx-font-size: 14px; -fx-font-family: 'Consolas','Courier New',monospace;"
                + " -fx-control-inner-background: rgba(255,255,255,0.95); -fx-background-radius: 10;");
        VBox standingsCard = card(I18n.t("dash.standings"), standingsView);

        fixtureView = new ListView<>();
        fixtureView.setStyle("-fx-control-inner-background: rgba(255,255,255,0.95); -fx-background-radius: 10;");
        fixtureView.setCellFactory(lv -> new FixtureCell());
        VBox fixtureCard = card(I18n.t("dash.fixture"), fixtureView);

        goalsStatsView = statList(GoalStatCell::new);
        cardsStatsView = statList(CardStatCell::new);
        availabilityStatsView = statList(AvailabilityStatCell::new);

        TabPane statsTabs = new TabPane();
        statsTabs.setStyle("-fx-tab-min-width: 92px;");
        Tab goalsTab = new Tab(I18n.t("dash.statsGoals"), goalsStatsView);
        Tab cardsTab = new Tab(I18n.t("dash.statsCards"), cardsStatsView);
        Tab availabilityTab = new Tab(I18n.t("dash.statsAvailability"), availabilityStatsView);
        goalsTab.setClosable(false);
        cardsTab.setClosable(false);
        availabilityTab.setClosable(false);
        statsTabs.getTabs().addAll(goalsTab, cardsTab, availabilityTab);
        VBox statsCard = card(I18n.t("dash.stats"), statsTabs);

        VBox right = new VBox(10, fixtureCard, statsCard);
        VBox.setVgrow(fixtureCard, Priority.ALWAYS);
        VBox.setVgrow(statsCard, Priority.SOMETIMES);

        HBox.setHgrow(standingsCard, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        return new HBox(12, standingsCard, right);
    }

    private ListView<PlayerStatLine> statList(Supplier<ListCell<PlayerStatLine>> cellFactory) {
        ListView<PlayerStatLine> view = new ListView<>();
        view.setCellFactory(lv -> cellFactory.get());
        view.setStyle("-fx-font-size: 13px; -fx-control-inner-background: rgba(245,248,255,0.95);"
                + " -fx-background-radius: 10;");
        return view;
    }

    private VBox buildDashboardBottomBar() {
        nextMatchLabel = new Label();
        nextMatchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        String btnColor = isFootballSport ? "#f1c40f" : "#2c3e50";
        String btnText = isFootballSport ? "black" : "white";
        playMatchBtn = new Button(I18n.t("dash.previewBtn"));
        playMatchBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: " + btnColor + ";"
                + " -fx-text-fill: " + btnText + "; -fx-padding: 10 24; -fx-cursor: hand; -fx-background-radius: 18;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 4);");
        playMatchBtn.setOnAction(e -> {
            IMatch next = currentLeague.getNextMatchForManaged();
            if (next != null) openPreMatchPreview(next);
        });

        statusLabel = new Label(I18n.t("dash.ready"));
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0, 1, 1);");

        HBox row = new HBox(20, playMatchBtn, statusLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        VBox v = new VBox(8, nextMatchLabel, row);
        v.setPadding(new Insets(10, 4, 4, 4));
        return v;
    }

    private void setStatus(String msg) { if (statusLabel != null) statusLabel.setText(msg); }

    private void refreshDashboard() {
        updateStandings();
        updateFixture();
        updateStats();
        updateNextMatch();
    }

    private void updateStandings() {
        standingsView.getItems().clear();
        standingsView.getItems().add(String.format(" #  %-22s  %s",
                I18n.t("dash.standingsHeader"), I18n.t("dash.points")));
        int rank = 1;
        for (ITeam team : currentLeague.getStandings()) {
            String marker = (team == managedTeam) ? "★ " : "  ";
            standingsView.getItems().add(String.format(" %d.%s%-22s   %3d",
                    rank, marker, team.getName(), team.getPoints()));
            rank++;
        }
    }

    private void updateFixture() {
        fixtureView.getItems().clear();
        Map<Integer, java.util.List<IMatch>> byRound = new LinkedHashMap<>();
        for (IMatch m : currentLeague.getScheduledMatches()) {
            int r = currentLeague.getRoundOf(m);
            byRound.computeIfAbsent(r, k -> new java.util.ArrayList<>()).add(m);
        }
        for (Map.Entry<Integer, java.util.List<IMatch>> e : byRound.entrySet()) {
            fixtureView.getItems().add(FixtureItem.header(e.getKey()));
            for (IMatch m : e.getValue()) {
                fixtureView.getItems().add(FixtureItem.match(e.getKey(), m, managedTeam));
            }
        }
        IMatch next = currentLeague.getNextMatchForManaged();
        if (next != null) {
            int targetRound = currentLeague.getRoundOf(next);
            int idx = 0;
            for (int i = 0; i < fixtureView.getItems().size(); i++) {
                FixtureItem it = fixtureView.getItems().get(i);
                if (it.isHeader && it.round == targetRound) { idx = i; break; }
            }
            int finalIdx = idx;
            javafx.application.Platform.runLater(() -> fixtureView.scrollTo(finalIdx));
        }
    }

    private void updateStats() {
        goalsStatsView.getItems().clear();
        cardsStatsView.getItems().clear();
        availabilityStatsView.getItems().clear();

        List<PlayerStatLine> goals = new ArrayList<>();
        List<PlayerStatLine> cards = new ArrayList<>();
        List<PlayerStatLine> availability = new ArrayList<>();
        for (ITeam team : currentLeague.getTeams()) {
            for (IPlayer p : team.getPlayers()) {
                PlayerStatLine line = new PlayerStatLine(p, team, team == managedTeam);
                if (p.getSeasonGoals() > 0) goals.add(line);
                if (p.getSeasonYellowCards() > 0 || p.getSeasonRedCards() > 0 || p.isSuspended()) cards.add(line);
                if (p.isSuspended() || p.isInjured()) availability.add(line);
            }
        }

        goals.sort(Comparator
                .comparingInt((PlayerStatLine line) -> line.player.getSeasonGoals()).reversed()
                .thenComparing(line -> line.player.getName()));
        cards.sort(Comparator
                .comparingInt((PlayerStatLine line) -> line.player.getSeasonRedCards()).reversed()
                .thenComparing(Comparator.comparingInt((PlayerStatLine line) -> line.player.getSeasonYellowCards()).reversed())
                .thenComparing(line -> line.player.getName()));
        availability.sort(Comparator
                .comparing((PlayerStatLine line) -> line.team.getName())
                .thenComparing(line -> line.player.getName()));

        int rank = 1;
        for (PlayerStatLine line : goals) {
            line.rank = rank++;
            goalsStatsView.getItems().add(line);
        }
        for (PlayerStatLine line : cards) cardsStatsView.getItems().add(line);
        for (PlayerStatLine line : availability) availabilityStatsView.getItems().add(line);

        if (goalsStatsView.getItems().isEmpty()) goalsStatsView.getItems().add(PlayerStatLine.empty(I18n.t("dash.noGoals")));
        if (cardsStatsView.getItems().isEmpty()) cardsStatsView.getItems().add(PlayerStatLine.empty(I18n.t("dash.noCards")));
        if (availabilityStatsView.getItems().isEmpty()) availabilityStatsView.getItems().add(PlayerStatLine.empty(I18n.t("dash.noAvailability")));
    }

    private void updateNextMatch() {
        IMatch next = currentLeague.getNextMatchForManaged();
        if (next == null) {
            ITeam champion = currentLeague.getStandings().isEmpty() ? null : currentLeague.getStandings().get(0);
            String champText = (champion != null) ? I18n.f("dash.champion", champion.getName()) : "";
            nextMatchLabel.setText(I18n.t("dash.seasonDone") + champText);
            playMatchBtn.setText(I18n.t("dash.newSeasonBtn"));
            playMatchBtn.setDisable(false);
            playMatchBtn.setOnAction(e -> startNewSeason());
            if (champion == managedTeam && !championCelebrationShown) {
                championCelebrationShown = true;
                Platform.runLater(() -> showChampionCelebration(champion));
            }
        } else {
            int round = currentLeague.getRoundOf(next);
            String home = next.getHomeTeam().getName();
            String away = next.getAwayTeam().getName();
            String homeAway = (next.getHomeTeam() == managedTeam) ? I18n.t("dash.home") :
                              (next.getAwayTeam() == managedTeam ? I18n.t("dash.away") : "");
            nextMatchLabel.setText(I18n.f("dash.nextMatch", round, next.getKickoffTime(), home, away, homeAway));
            playMatchBtn.setText(I18n.t("dash.previewBtn"));
            playMatchBtn.setDisable(false);
            playMatchBtn.setOnAction(e -> {
                IMatch n = currentLeague.getNextMatchForManaged();
                if (n != null) openPreMatchPreview(n);
            });
        }
    }

    private void startNewSeason() {
        championCelebrationShown = false;
        ISportFactory f = currentFactory;
        String sn = currentSportName;
        if (f == null) {
            f = isFootballSport ? new FootballFactory() : new VolleyballFactory();
            sn = isFootballSport ? I18n.t("save.sportFootball") : I18n.t("save.sportVolleyball");
        }
        openTeamSelect(f, sn);
    }

    private void showChampionCelebration(ITeam champion) {
        if (primaryStage == null || primaryStage.getScene() == null
                || !(primaryStage.getScene().getRoot() instanceof StackPane root)) return;

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.48);");
        Pane confetti = new Pane();
        confetti.setMouseTransparent(true);

        Label crown = new Label("★");
        crown.setStyle("-fx-font-size: 54px; -fx-text-fill: #f1c40f;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.65), 12, 0, 0, 4);");
        Label title = new Label(I18n.t("champ.title"));
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 8, 0, 0, 3);");
        Label body = new Label(I18n.f("champ.body", champion.getName()));
        body.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        Button close = secondaryButton(I18n.t("champ.continue"), "#27ae60");

        VBox message = new VBox(8, crown, title, body, close);
        message.setAlignment(Pos.CENTER);
        message.setPadding(new Insets(26, 42, 26, 42));
        message.setMaxWidth(560);
        message.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(39,174,96,0.95), rgba(41,128,185,0.95));"
                + " -fx-background-radius: 16; -fx-border-color: rgba(255,255,255,0.55);"
                + " -fx-border-width: 2; -fx-border-radius: 16;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 22, 0, 0, 8);");
        close.setOnAction(e -> root.getChildren().remove(overlay));

        overlay.getChildren().addAll(confetti, message);
        root.getChildren().add(overlay);

        Random random = new Random();
        Color[] colors = {
                Color.web("#f1c40f"), Color.web("#e74c3c"), Color.web("#3498db"),
                Color.web("#2ecc71"), Color.web("#ffffff"), Color.web("#9b59b6")
        };
        double width = root.getWidth() > 0 ? root.getWidth() : 1100;
        double height = root.getHeight() > 0 ? root.getHeight() : 740;
        for (int i = 0; i < 90; i++) {
            Node piece;
            if (random.nextBoolean()) {
                Rectangle rect = new Rectangle(5 + random.nextInt(7), 8 + random.nextInt(9));
                rect.setArcWidth(2);
                rect.setArcHeight(2);
                rect.setFill(colors[random.nextInt(colors.length)]);
                piece = rect;
            } else {
                Circle circle = new Circle(3 + random.nextInt(4), colors[random.nextInt(colors.length)]);
                piece = circle;
            }
            piece.setLayoutX(random.nextDouble() * width);
            piece.setLayoutY(-80 - random.nextDouble() * height * 0.45);
            piece.setRotate(random.nextDouble() * 180);
            confetti.getChildren().add(piece);

            TranslateTransition fall = new TranslateTransition(Duration.millis(1900 + random.nextInt(1800)), piece);
            fall.setByY(height + 180 + random.nextInt(160));
            fall.setByX(-90 + random.nextInt(181));
            fall.setCycleCount(1);
            fall.play();
        }
    }

    private VBox card(String title, Node body) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 0 0 4 4;");
        VBox v = new VBox(4, t, body);
        v.setPadding(new Insets(10));
        v.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 12;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 4);");
        VBox.setVgrow(body, Priority.ALWAYS);
        return v;
    }

    private Button secondaryButton(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: " + color + ";"
                + " -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 10;");
        return b;
    }

    private void confirmAndGoToMainMenu() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                I18n.t("dash.confirmExitBody"), ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(I18n.t("dash.confirmExitTitle"));
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            stopMatchTimeline();
            currentLeague = null; managedTeam = null; currentMatch = null;
            primaryStage.setScene(buildMainMenuScene());
        }
    }

    // ============== PRE-MATCH PREVIEW ==============

    private void openPreMatchPreview(IMatch match) {
        this.currentMatch = match;
        match.setUserTeam(managedTeam);
        if (!match.isStarted()) match.start();
        primaryStage.setScene(buildPreMatchScene());
    }

    private Scene buildPreMatchScene() {
        StackPane root = new StackPane();
        Pane field = isFootballSport ? FieldBackground.footballField() : FieldBackground.volleyballCourt();
        field.prefWidthProperty().bind(root.widthProperty());
        field.prefHeightProperty().bind(root.heightProperty());

        ITeam h = currentMatch.getHomeTeam();
        ITeam a = currentMatch.getAwayTeam();
        int round = currentLeague.getRoundOf(currentMatch);

        Label title = new Label(I18n.f("pm.title", round));
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        Label vsLine = new Label(h.getName() + (h == managedTeam ? " ★" : "")
                + "    vs    " + a.getName() + (a == managedTeam ? " ★" : ""));
        vsLine.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        Label hostLine = new Label(I18n.f("pm.host", h.getName()));
        hostLine.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");
        Label stadLine = new Label(I18n.f("pm.stadium", Squads.stadium(h.getName())));
        stadLine.setStyle("-fx-font-size: 13px; -fx-text-fill: #ecf0f1;");
        VBox hostBox = new VBox(2, hostLine, stadLine);
        hostBox.setAlignment(Pos.CENTER);
        Node coachPlan = buildCoachPlanPanel();

        Pane pitch = isFootballSport
                ? FormationView.footballPitch(700, 360, h, a,
                        currentMatch.getOnField(h), currentMatch.getOnField(a),
                        currentMatch.getBench(h), currentMatch.getBench(a))
                : FormationView.volleyballCourtView(700, 360, h, a,
                        currentMatch.getOnField(h), currentMatch.getOnField(a));

        VBox homeRoster = buildPreviewRoster(h, currentMatch.getOnField(h), currentMatch.getBench(h), missingPlayers(h));
        VBox awayRoster = buildPreviewRoster(a, currentMatch.getOnField(a), currentMatch.getBench(a), missingPlayers(a));
        HBox rosters = new HBox(14, homeRoster, awayRoster);
        HBox.setHgrow(homeRoster, Priority.ALWAYS);
        HBox.setHgrow(awayRoster, Priority.ALWAYS);

        Button back = secondaryButton(I18n.t("pm.back"), "#7f8c8d");
        back.setOnAction(e -> primaryStage.setScene(buildDashboardScene()));
        Button startBtn = new Button(I18n.t("pm.startMatch"));
        startBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #27ae60;"
                + " -fx-text-fill: white; -fx-padding: 10 24; -fx-cursor: hand; -fx-background-radius: 18;");
        startBtn.setOnAction(e -> openMatchView());

        HBox actions = new HBox(14, back, startBtn);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(10, title, vsLine, hostBox);
        if (coachPlan != null) content.getChildren().add(coachPlan);
        content.getChildren().addAll(pitch, rosters, actions);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16));
        content.setMaxWidth(1040);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-background-radius: 14;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        StackPane wrap = new StackPane(scroll);
        wrap.setPadding(new Insets(16));
        root.getChildren().addAll(field, wrap);
        return new Scene(root, 1100, 740);
    }

    private Node buildCoachPlanPanel() {
        if (!isFootballSport || managedTeam == null || currentMatch == null) return null;

        ComboBox<String> formation = new ComboBox<>();
        formation.getItems().addAll("4-4-2", "4-3-3", "3-5-2", "4-2-3-1");
        formation.setValue(extractPlanPart(0, "4-4-2"));

        ComboBox<String> discipline = new ComboBox<>();
        discipline.getItems().addAll("Balanced", "Attack", "Defensive");
        discipline.setValue(extractPlanPart(1, "Balanced"));

        Button applyPlan = new Button(I18n.t("pm.applyPlan"));
        applyPlan.setStyle("-fx-font-weight: bold; -fx-background-color: #2980b9; -fx-text-fill: white;");
        applyPlan.setOnAction(e -> {
            managedTeam.setTactic(formation.getValue() + " / " + discipline.getValue());
            primaryStage.setScene(buildPreMatchScene());
        });

        ITeam planTeam = managedTeam;
        ListView<IPlayer> starters = new ListView<>();
        starters.getItems().setAll(squadOrder(currentMatch.getOnField(planTeam)));
        starters.setCellFactory(lv -> new PlayerCell());
        starters.setPrefHeight(140);

        ListView<IPlayer> bench = new ListView<>();
        bench.getItems().setAll(squadOrder(currentMatch.getBench(planTeam)));
        bench.setCellFactory(lv -> new PlayerCell());
        bench.setPrefHeight(140);

        Button swap = new Button(I18n.t("pm.swap"));
        swap.setStyle("-fx-font-weight: bold; -fx-background-color: #16a085; -fx-text-fill: white;");
        swap.setOnAction(e -> {
            IPlayer out = starters.getSelectionModel().getSelectedItem();
            IPlayer in = bench.getSelectionModel().getSelectedItem();
            if (currentMatch.swapLineup(planTeam, out, in)) {
                primaryStage.setScene(buildPreMatchScene());
            } else {
                new Alert(Alert.AlertType.WARNING, I18n.t("pm.swapFailed"), ButtonType.OK).showAndWait();
            }
        });

        VBox left = new VBox(4, makeSquadHeader(I18n.t("pm.startingFootball"), "#16a085"), starters);
        VBox right = new VBox(4, makeSquadHeader(I18n.t("pm.bench"), "#7f8c8d"), bench);
        HBox lists = new HBox(8, left, swap, right);
        lists.setAlignment(Pos.CENTER);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox choices = new HBox(8,
                makePlanLabel(I18n.t("pm.formation")), formation,
                makePlanLabel(I18n.t("pm.discipline")), discipline,
                applyPlan);
        choices.setAlignment(Pos.CENTER);

        VBox panel = new VBox(8, choices, lists);
        panel.setPadding(new Insets(10));
        panel.setMaxWidth(900);
        panel.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 10;");
        return panel;
    }

    private Label makePlanLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");
        return label;
    }

    private String extractPlanPart(int index, String fallback) {
        String tactic = managedTeam == null ? null : managedTeam.getTactic();
        if (tactic == null || !tactic.contains("/")) return fallback;
        String[] parts = tactic.split("/");
        if (index >= parts.length) return fallback;
        String part = parts[index].trim();
        return part.isEmpty() ? fallback : part;
    }

    private VBox buildPreviewRoster(ITeam team, List<IPlayer> onField, List<IPlayer> bench, List<IPlayer> missing) {
        Label header = new Label(team.getName());
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label firstXIHeader = makeSquadHeader(
                isFootballSport ? I18n.t("pm.startingFootball") : I18n.t("pm.startingVolley"), "#16a085");
        ListView<IPlayer> field = new ListView<>();
        field.getItems().setAll(squadOrder(onField));
        field.setCellFactory(lv -> new PlayerCell());
        field.setPrefHeight(320);
        field.setStyle("-fx-control-inner-background: rgba(220,255,220,0.95); -fx-background-radius: 8;"
                + " -fx-font-size: 13px;");

        Label benchHeader = makeSquadHeader(I18n.t("pm.bench"), "#7f8c8d");
        ListView<IPlayer> benchView = new ListView<>();
        benchView.getItems().setAll(squadOrder(bench));
        benchView.setCellFactory(lv -> new PlayerCell());
        benchView.setPrefHeight(220);
        benchView.setStyle("-fx-control-inner-background: rgba(245,245,245,0.95); -fx-background-radius: 8;"
                + " -fx-font-size: 13px;");

        Label missingHeader = makeSquadHeader(I18n.t("pm.unavailable"), "#9b2c2c");
        ListView<String> missingView = new ListView<>();
        if (missing.isEmpty()) {
            missingView.getItems().add(I18n.t("pm.noMissing"));
        } else {
            for (IPlayer p : squadOrder(missing)) missingView.getItems().add(playerLineWithStatus(p));
        }
        missingView.setPrefHeight(Math.max(58, Math.min(120, missingView.getItems().size() * 28 + 10)));
        missingView.setStyle("-fx-control-inner-background: rgba(255,235,235,0.95); -fx-background-radius: 8;"
                + " -fx-font-size: 13px;");

        VBox box = new VBox(4, header, firstXIHeader, field, benchHeader, benchView, missingHeader, missingView);
        return box;
    }

    private Label makeSquadHeader(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-background-color: " + color + ";"
                + " -fx-padding: 5 14; -fx-background-radius: 6;");
        return l;
    }

    // ============== LIVE MATCH ==============

    private void openMatchView() {
        if (currentMatch == null) return;
        primaryStage.setScene(buildMatchViewScene());
        refreshMatchView();
        // Auto-start at 1x
        if (speedGroup != null) {
            for (Toggle t : speedGroup.getToggles()) {
                if ((Integer) t.getUserData() == 1) { t.setSelected(true); break; }
            }
        }
        applySpeed(1);
    }

    private Scene buildMatchViewScene() {
        StackPane root = new StackPane();
        Pane field = isFootballSport ? FieldBackground.footballField() : FieldBackground.volleyballCourt();
        field.prefWidthProperty().bind(root.widthProperty());
        field.prefHeightProperty().bind(root.heightProperty());

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(12));
        content.setTop(buildMatchTopBar());
        content.setCenter(buildMatchCenter());
        content.setBottom(buildMatchBottomBar());

        root.getChildren().addAll(field, content);
        setupTimeline();
        return new Scene(root, 1100, 740);
    }

    private VBox buildMatchTopBar() {
        Button leave = secondaryButton(I18n.t("mv.leave"), "#7f8c8d");
        leave.setOnAction(e -> confirmLeaveMatch());

        ITeam h = currentMatch.getHomeTeam();
        ITeam a = currentMatch.getAwayTeam();
        Label homeAbbrLabel = new Label(abbr(h.getName()) + (h == managedTeam ? " ★" : ""));
        homeAbbrLabel.setStyle(scoreTeamStyle());
        matchHomeScoreLabel = new Label("0");
        matchHomeScoreLabel.setStyle(scoreDigitStyle());
        Label sep = new Label("–"); sep.setStyle(scoreSepStyle());
        matchAwayScoreLabel = new Label("0");
        matchAwayScoreLabel.setStyle(scoreDigitStyle());
        Label awayAbbrLabel = new Label(abbr(a.getName()) + (a == managedTeam ? " ★" : ""));
        awayAbbrLabel.setStyle(scoreTeamStyle());

        matchClockLabel = new Label("0'");
        matchClockLabel.setStyle("-fx-font-family: 'Consolas','Courier New',monospace;"
                + " -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;"
                + " -fx-padding: 6 14;");

        HBox scoreboardInner = new HBox(14, homeAbbrLabel, matchHomeScoreLabel, sep,
                matchAwayScoreLabel, awayAbbrLabel);
        scoreboardInner.setAlignment(Pos.CENTER);
        scoreboardInner.setPadding(new Insets(8, 22, 8, 22));
        scoreboardInner.setStyle("-fx-background-color: linear-gradient(to bottom, #111111, #2c2c2c);"
                + " -fx-background-radius: 14; -fx-border-color: #f1c40f; -fx-border-width: 2;"
                + " -fx-border-radius: 14;");

        HBox scoreboard = new HBox(8, scoreboardInner, matchClockLabel);
        scoreboard.setAlignment(Pos.CENTER);
        scoreboard.setStyle("-fx-background-color: rgba(0,0,0,0.45); -fx-background-radius: 16;"
                + " -fx-padding: 6;");

        Region spacer1 = new Region(); HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox topRow = new HBox(12, leave, spacer1, scoreboard, spacer2);
        topRow.setAlignment(Pos.CENTER);
        VBox v = new VBox(topRow);
        v.setPadding(new Insets(2, 4, 8, 4));
        return v;
    }

    private String scoreDigitStyle() {
        return "-fx-font-family: 'Consolas','Courier New',monospace; -fx-font-size: 48px;"
                + " -fx-font-weight: bold; -fx-text-fill: #ffd54f;";
    }
    private String scoreTeamStyle() {
        return "-fx-font-family: 'Consolas','Courier New',monospace; -fx-font-size: 24px;"
                + " -fx-font-weight: bold; -fx-text-fill: #ecf0f1;";
    }
    private String scoreSepStyle() {
        return "-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;";
    }

    private String abbr(String name) {
        if (name == null) return "?";
        String[] parts = name.split("\\s+");
        StringBuilder sb = new StringBuilder();
        if (parts.length > 0) {
            String w = parts[0];
            sb.append(w.substring(0, Math.min(3, w.length())).toUpperCase());
        }
        if (parts.length > 1 && sb.length() < 5) {
            sb.append(parts[1].substring(0, 1).toUpperCase());
        }
        return sb.toString();
    }

    private HBox buildMatchCenter() {
        eventsView = new ListView<>();
        eventsView.setStyle("-fx-control-inner-background: rgba(255,255,255,0.95); -fx-background-radius: 10;");
        eventsView.setCellFactory(lv -> new EventCell());
        VBox eventsCard = card(I18n.t("mv.events"), eventsView);

        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-tab-min-width: 110px;");

        ourOnFieldView = playerList("rgba(220,255,220,0.95)");
        ourBenchView = playerList("rgba(245,245,245,0.95)");
        ourUnavailableView = playerList("rgba(255,235,235,0.95)");
        VBox ourBox = squadBox(
                isFootballSport ? I18n.t("mv.startingFootball") : I18n.t("mv.startingVolley"), ourOnFieldView,
                I18n.t("mv.bench"), ourBenchView,
                I18n.t("mv.unavailable"), ourUnavailableView);
        Tab ourTab = new Tab(I18n.t("mv.ourSquad"), scrollContent(ourBox)); ourTab.setClosable(false);

        oppOnFieldView = playerList("rgba(220,230,255,0.95)");
        oppBenchView = playerList("rgba(245,245,245,0.95)");
        oppUnavailableView = playerList("rgba(255,235,235,0.95)");
        VBox oppBox = squadBox(
                isFootballSport ? I18n.t("mv.startingFootballOpp") : I18n.t("mv.startingVolleyOpp"),
                oppOnFieldView, I18n.t("mv.benchOpp"), oppBenchView,
                I18n.t("mv.unavailable"), oppUnavailableView);
        Tab oppTab = new Tab(I18n.t("mv.oppSquad"), scrollContent(oppBox)); oppTab.setClosable(false);

        formationHolder = new StackPane();
        formationHolder.setPadding(new Insets(4));
        ScrollPane formationScroll = new ScrollPane(formationHolder);
        formationScroll.setFitToWidth(true);
        formationScroll.setFitToHeight(true);
        formationScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        Tab pitchTab = new Tab(I18n.t("mv.formation"), formationScroll); pitchTab.setClosable(false);

        tabs.getTabs().addAll(ourTab, oppTab, pitchTab);

        VBox tabsCard = card(I18n.t("mv.squads"), tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        HBox.setHgrow(eventsCard, Priority.ALWAYS);
        HBox.setHgrow(tabsCard, Priority.ALWAYS);
        return new HBox(10, eventsCard, tabsCard);
    }

    private ListView<IPlayer> playerList(String background) {
        ListView<IPlayer> view = new ListView<>();
        view.setCellFactory(lv -> new PlayerCell());
        view.setFixedCellSize(27);
        view.setStyle("-fx-control-inner-background: " + background + "; -fx-background-radius: 8;");
        return view;
    }

    private ScrollPane scrollContent(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private VBox squadBox(String t1, ListView<IPlayer> v1, String t2, ListView<IPlayer> v2,
                          String t3, ListView<IPlayer> v3) {
        Label l1 = makeSquadHeader(t1, "#16a085");
        Label l2 = makeSquadHeader(t2, "#7f8c8d");
        Label l3 = makeSquadHeader(t3, "#9b2c2c");
        VBox v = new VBox(4, l1, v1, l2, v2, l3, v3);
        VBox.setVgrow(v1, Priority.ALWAYS);
        VBox.setVgrow(v2, Priority.SOMETIMES);
        VBox.setVgrow(v3, Priority.SOMETIMES);
        v1.setPrefHeight(isFootballSport ? 305 : 180);
        v2.setPrefHeight(isFootballSport ? 205 : 180);
        v3.setPrefHeight(95);
        return v;
    }

    private VBox buildMatchBottomBar() {
        speedGroup = new ToggleGroup();
        ToggleButton pause = speedToggle("⏸", -1);
        ToggleButton x1 = speedToggle("▶ 1x", 1);
        ToggleButton x2 = speedToggle("▶▶ 2x", 2);
        ToggleButton x4 = speedToggle("▶▶▶ 4x", 4);
        ToggleButton x8 = speedToggle("▶▶▶▶ 8x", 8);
        HBox speedBar = new HBox(4, pause, x1, x2, x4, x8);

        quickFinishBtn = new Button(I18n.t("mv.quickFinish"));
        quickFinishBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #2980b9;"
                + " -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 12;");
        quickFinishBtn.setOnAction(e -> doQuickFinish());

        secondHalfBtn = new Button(I18n.t("mv.startSecondHalf"));
        secondHalfBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #27ae60;"
                + " -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 12;");
        secondHalfBtn.setOnAction(e -> {
            if (currentMatch == null) return;
            currentMatch.startSecondHalf();
            secondHalfBtn.setVisible(false);
            refreshMatchView();
            selectSpeed(1);
            applySpeed(1);
        });
        secondHalfBtn.setVisible(false);

        substituteBtn = new Button(I18n.t("mv.substitute"));
        substituteBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #d35400;"
                + " -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 12;");
        substituteBtn.setOnAction(e -> openSubDialog(false));

        HBox subsCluster = new HBox(6, substituteBtn);
        subsCluster.setAlignment(Pos.CENTER_LEFT);

        continueBtn = new Button(I18n.t("mv.continueBtn"));
        continueBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #27ae60;"
                + " -fx-text-fill: white; -fx-padding: 8 18; -fx-cursor: hand; -fx-background-radius: 12;");
        continueBtn.setOnAction(e -> finishAndReturn());
        continueBtn.setVisible(false);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(10, speedBar, quickFinishBtn, secondHalfBtn, spacer, subsCluster, continueBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        VBox v = new VBox(row);
        v.setPadding(new Insets(8, 4, 4, 4));
        return v;
    }

    private ToggleButton speedToggle(String text, int rate) {
        ToggleButton tb = new ToggleButton(text);
        tb.setUserData(rate);
        tb.setToggleGroup(speedGroup);
        tb.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.85);"
                + " -fx-text-fill: #2c3e50; -fx-padding: 8 12; -fx-cursor: hand;");
        tb.setOnAction(e -> {
            if (!tb.isSelected()) { tb.setSelected(true); return; }
            applySpeed(rate);
        });
        return tb;
    }

    private void setupTimeline() {
        if (matchTimeline != null) matchTimeline.stop();
        matchTimeline = new Timeline(new KeyFrame(Duration.seconds(1.0), e -> autoTickOnce()));
        matchTimeline.setCycleCount(Animation.INDEFINITE);
        matchTimeline.setRate(1.0);
    }

    private void applySpeed(int rate) {
        if (matchTimeline == null) return;
        if (rate <= 0 || currentMatch == null || currentMatch.isFinished()) {
            matchTimeline.pause();
            return;
        }
        if (pauseForSecondHalfIfNeeded()) return;
        matchTimeline.setRate(rate);
        matchTimeline.play();
    }

    private void stopMatchTimeline() {
        if (matchTimeline != null) {
            matchTimeline.stop();
            matchTimeline = null;
        }
    }

    private void autoTickOnce() {
        if (currentMatch == null || currentMatch.isFinished()) {
            matchTimeline.pause();
            return;
        }
        currentMatch.tick(1);
        refreshMatchView();
        if (pauseForSecondHalfIfNeeded()) return;
        if (currentMatch.needsSubstitution(managedTeam)) {
            matchTimeline.pause();
            Platform.runLater(() -> {
                openSubDialog(true);
                if (currentMatch != null
                        && !currentMatch.isFinished()
                        && !currentMatch.needsSubstitution(managedTeam)) {
                    selectSpeed(1);
                    applySpeed(1);
                }
            });
            return;
        }
        if (currentMatch.isFinished()) {
            matchTimeline.pause();
            selectPauseSpeed();
            onMatchFinished();
        }
    }

    private void selectSpeed(int rate) {
        if (speedGroup == null) return;
        for (Toggle t : speedGroup.getToggles()) {
            if ((Integer) t.getUserData() == rate) { t.setSelected(true); break; }
        }
    }

    private void selectPauseSpeed() {
        if (speedGroup != null && !speedGroup.getToggles().isEmpty()) {
            speedGroup.getToggles().get(0).setSelected(true);
        }
    }

    private void doQuickFinish() {
        if (currentMatch == null || currentMatch.isFinished()) return;
        if (matchTimeline != null) matchTimeline.pause();
        selectPauseSpeed();
        while (!currentMatch.isFinished()) {
            if (currentMatch.isWaitingForSecondHalf()) {
                currentMatch.startSecondHalf();
                continue;
            }
            currentMatch.tick(20);
            if (currentMatch.isWaitingForSecondHalf()) {
                currentMatch.startSecondHalf();
                continue;
            }
            if (currentMatch.needsSubstitution(managedTeam)) {
                refreshMatchView();
                openSubDialog(true);
                if (currentMatch.needsSubstitution(managedTeam)) break;
            }
        }
        refreshMatchView();
        if (currentMatch.isFinished()) onMatchFinished();
    }

    private void onMatchFinished() {
        currentLeague.autoFinishOtherMatchesInRound(currentMatch);
        if (substituteBtn != null) substituteBtn.setDisable(true);
        if (quickFinishBtn != null) quickFinishBtn.setDisable(true);
        if (speedGroup != null) {
            for (Toggle t : speedGroup.getToggles()) {
                if ((Integer) t.getUserData() > 0) ((ToggleButton) t).setDisable(true);
            }
        }
        if (continueBtn != null) continueBtn.setVisible(true);
        if (secondHalfBtn != null) secondHalfBtn.setVisible(false);
        refreshMatchView();
    }

    private void finishAndReturn() {
        stopMatchTimeline();
        primaryStage.setScene(buildDashboardScene());
    }

    private void confirmLeaveMatch() {
        if (currentMatch == null || currentMatch.isFinished()) {
            stopMatchTimeline();
            primaryStage.setScene(buildDashboardScene());
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                I18n.t("mv.confirmLeaveBody"), ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(I18n.t("mv.confirmLeaveTitle"));
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            stopMatchTimeline();
            currentMatch.setUserTeam(null);
            currentMatch.tickToEnd();
            currentLeague.autoFinishOtherMatchesInRound(currentMatch);
            primaryStage.setScene(buildDashboardScene());
        }
    }

    private void refreshMatchView() {
        if (currentMatch == null) return;
        matchClockLabel.setText(currentMatch.getClockDisplay());
        matchHomeScoreLabel.setText(String.valueOf(currentMatch.getHomeScore()));
        matchAwayScoreLabel.setText(String.valueOf(currentMatch.getAwayScore()));

        eventsView.getItems().setAll(currentMatch.getEvents());
        if (!eventsView.getItems().isEmpty()) eventsView.scrollTo(eventsView.getItems().size() - 1);

        ITeam opp = (currentMatch.getHomeTeam() == managedTeam)
                ? currentMatch.getAwayTeam() : currentMatch.getHomeTeam();
        ourOnFieldView.getItems().setAll(squadOrder(currentMatch.getOnField(managedTeam)));
        ourBenchView.getItems().setAll(squadOrder(currentMatch.getBench(managedTeam)));
        ourUnavailableView.getItems().setAll(squadOrder(exitedPlayers(managedTeam)));
        oppOnFieldView.getItems().setAll(squadOrder(currentMatch.getOnField(opp)));
        oppBenchView.getItems().setAll(squadOrder(currentMatch.getBench(opp)));
        oppUnavailableView.getItems().setAll(squadOrder(exitedPlayers(opp)));
        resizePlayerList(ourOnFieldView, isFootballSport ? 11 : 6);
        resizePlayerList(ourBenchView, isFootballSport ? 7 : 6);
        resizePlayerList(ourUnavailableView, 4);
        resizePlayerList(oppOnFieldView, isFootballSport ? 11 : 6);
        resizePlayerList(oppBenchView, isFootballSport ? 7 : 6);
        resizePlayerList(oppUnavailableView, 4);

        Pane pitch = isFootballSport
                ? FormationView.footballPitch(560, 360,
                        currentMatch.getHomeTeam(), currentMatch.getAwayTeam(),
                        currentMatch.getOnField(currentMatch.getHomeTeam()),
                        currentMatch.getOnField(currentMatch.getAwayTeam()),
                        currentMatch.getBench(currentMatch.getHomeTeam()),
                        currentMatch.getBench(currentMatch.getAwayTeam()))
                : FormationView.volleyballCourtView(560, 360,
                        currentMatch.getHomeTeam(), currentMatch.getAwayTeam(),
                        currentMatch.getOnField(currentMatch.getHomeTeam()),
                        currentMatch.getOnField(currentMatch.getAwayTeam()));
        if (animateNextFormation && !formationHolder.getChildren().isEmpty()) {
            animateNextFormation = false;
            Node previousPitch = formationHolder.getChildren().get(0);
            pitch.setOpacity(0.0);
            pitch.setTranslateY(18);
            formationHolder.getChildren().setAll(previousPitch, pitch);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(650), pitch);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            TranslateTransition slide = new TranslateTransition(Duration.millis(650), pitch);
            slide.setFromY(18);
            slide.setToY(0);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(650), previousPitch);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> formationHolder.getChildren().setAll(pitch));
            fadeIn.play();
            slide.play();
            fadeOut.play();
        } else {
            animateNextFormation = false;
            formationHolder.getChildren().setAll(pitch);
        }

        if (secondHalfBtn != null) secondHalfBtn.setVisible(currentMatch.isWaitingForSecondHalf());

        int remaining = currentMatch.getRemainingSubs(managedTeam);
        int max = currentMatch.getMaxSubs();
        String bg = remaining == 0 ? "#7f8c8d"
                  : (remaining == 1 ? "#c0392b"
                  : (remaining == 2 ? "#d35400" : "#27ae60"));
        substituteBtn.setText(I18n.f("mv.substituteWithLimit", remaining, max));
        substituteBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-background-color: " + bg + "; -fx-padding: 8 16; -fx-cursor: hand;"
                + " -fx-background-radius: 12;");
        substituteBtn.setDisable(remaining <= 0
                || currentMatch.getOnField(managedTeam).isEmpty()
                || currentMatch.getBench(managedTeam).isEmpty()
                || currentMatch.isFinished());
    }

    private boolean pauseForSecondHalfIfNeeded() {
        if (currentMatch == null || !currentMatch.isWaitingForSecondHalf()) {
            if (secondHalfBtn != null) secondHalfBtn.setVisible(false);
            return false;
        }
        if (matchTimeline != null) matchTimeline.pause();
        selectPauseSpeed();
        if (secondHalfBtn != null) secondHalfBtn.setVisible(true);
        return true;
    }

    private List<IPlayer> exitedPlayers(ITeam team) {
        if (currentMatch == null || team == null) return new ArrayList<>();
        return currentMatch.getRemoved(team);
    }

    private List<IPlayer> missingPlayers(ITeam team) {
        List<IPlayer> missing = new ArrayList<>();
        if (team == null) return missing;
        for (IPlayer p : team.getPlayers()) {
            if (p.isInjured() || p.isSuspended()) missing.add(p);
        }
        return missing;
    }

    private List<IPlayer> squadOrder(List<IPlayer> players) {
        List<IPlayer> ordered = new ArrayList<>(players == null ? List.of() : players);
        if (!isFootballSport) return ordered;
        ordered.sort(Comparator
                .comparingInt(MainApp::footballPositionOrder)
                .thenComparingInt(IPlayer::getJerseyNumber)
                .thenComparing(IPlayer::getName));
        return ordered;
    }

    private static int footballPositionOrder(IPlayer player) {
        String pos = player.getPosition() == null ? "" : player.getPosition().toLowerCase();
        if (pos.contains("kale") || pos.contains("goalkeeper") || pos.equals("gk")) return 0;
        if (pos.contains("defans") || pos.contains("bek") || pos.contains("stoper")
                || pos.contains("defender")) return 1;
        if (pos.contains("orta") || pos.contains("midfield")) return 2;
        if (pos.contains("forvet") || pos.contains("kanat") || pos.contains("santrafor")
                || pos.contains("forward") || pos.contains("striker")) return 3;
        return 4;
    }

    private String playerLineWithStatus(IPlayer p) {
        return p.getName() + "  (" + p.getPosition() + ", " + p.getSkillLevel() + ")  —  " + playerStatus(p);
    }

    private String playerStatus(IPlayer p) {
        List<String> status = new ArrayList<>();
        if (p.isInjured()) status.add(I18n.t("status.injured"));
        if (p.isSuspended()) status.add(I18n.f("status.suspended", p.getSuspensionMatches()));
        if (status.isEmpty()) status.add(I18n.t("status.exited"));
        return String.join(", ", status);
    }

    private void resizePlayerList(ListView<IPlayer> view, int maxRows) {
        int rows = Math.max(1, Math.min(maxRows, view.getItems().size()));
        view.setPrefHeight(rows * view.getFixedCellSize() + 8);
    }

    private void openSubDialog(boolean forced) {
        if (currentMatch == null) return;
        List<IPlayer> field = squadOrder(currentMatch.getOnField(managedTeam));
        List<IPlayer> bench = squadOrder(currentMatch.getBench(managedTeam));
        if (bench.isEmpty()) { setStatus(I18n.t("sub.noBench")); return; }
        if (!forced && field.isEmpty()) { setStatus(I18n.t("sub.noField")); return; }

        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(forced ? I18n.t("sub.forcedTitle") : I18n.t("sub.dialogTitle"));

        IPlayer injured = forced ? currentMatch.getLastForceRemoved(managedTeam) : null;
        String forcedText = (injured != null)
                ? I18n.f("sub.forcedNamed", injured.getName(), injured.getPosition(), injured.getSkillLevel())
                : I18n.t("sub.forcedAnon");
        Label title = new Label(forced ? forcedText : I18n.t("sub.pickBoth"));
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"
                + (forced ? " -fx-text-fill: #7a1f9a;" : ""));

        int remaining = currentMatch.getRemainingSubs(managedTeam);
        int max = currentMatch.getMaxSubs();
        Label badge = new Label(I18n.f("sub.remaining", remaining, max));
        badge.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-background-color: #d35400; -fx-padding: 6 14; -fx-background-radius: 10;");

        ListView<IPlayer> outList = new ListView<>();
        outList.getItems().setAll(field);
        outList.setCellFactory(lv -> new PlayerCell());
        outList.setPrefHeight(280);

        ListView<IPlayer> inList = new ListView<>();
        inList.getItems().setAll(bench);
        inList.setCellFactory(lv -> new PlayerCell());
        inList.setPrefHeight(280);

        Label outHeader = new Label(isFootballSport ? I18n.t("sub.outHeaderFootball") : I18n.t("sub.outHeaderVolley"));
        outHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label inHeader = new Label(I18n.t("sub.inHeader"));
        inHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox outBox = new VBox(4, outHeader, outList);
        VBox inBox = new VBox(4, inHeader, inList);
        if (forced) { outBox.setVisible(false); outBox.setManaged(false); }
        HBox lists = new HBox(12, outBox, inBox);
        HBox.setHgrow(outBox, Priority.ALWAYS);
        HBox.setHgrow(inBox, Priority.ALWAYS);

        Button cancel = new Button(forced ? I18n.t("sub.skip") : I18n.t("sub.cancel"));
        cancel.setOnAction(e -> dialog.close());
        Button apply = new Button(I18n.t("sub.apply"));
        apply.setStyle("-fx-font-weight: bold;");
        apply.setOnAction(e -> {
            IPlayer in = inList.getSelectionModel().getSelectedItem();
            boolean ok = forced
                    ? currentMatch.replace(managedTeam, in)
                    : currentMatch.substitute(managedTeam, outList.getSelectionModel().getSelectedItem(), in);
            if (ok) {
                animateNextFormation = true;
                dialog.close();
                refreshMatchView();
            }
            else setStatus(I18n.t("sub.failed"));
        });

        HBox actions = new HBox(10, cancel, apply);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(12, title, badge, lists, actions);
        layout.setPadding(new Insets(16));
        dialog.setScene(new Scene(layout, 700, 460));
        dialog.showAndWait();
    }

    // ============== SAVE / LOAD ==============

    private void ensureSaveDir() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private String slotPath(int slot) {
        String prefix = isFootballSport ? "football" : "volleyball";
        return SAVE_DIR + File.separator + prefix + "_slot" + slot + ".dat";
    }

    private String slotPathForSport(int slot, boolean football) {
        String prefix = football ? "football" : "volleyball";
        return SAVE_DIR + File.separator + prefix + "_slot" + slot + ".dat";
    }

    private String slotLabel(int slot) {
        File f = new File(slotPath(slot));
        if (!f.exists()) return "Slot " + slot + "  —  " + I18n.t("save.empty");
        DataManager.SaveMeta m = DataManager.loadMeta(slotPath(slot));
        String name = (m != null && m.name != null && !m.name.isBlank()) ? m.name : I18n.t("save.unnamed");
        long ts = (m != null && m.timestamp > 0) ? m.timestamp : f.lastModified();
        String when = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(ts));
        return "Slot " + slot + "  —  " + name + "  ·  " + when;
    }

    private void openSaveDialog() {
        SaveSelection sel = chooseSlotForSave();
        if (sel == null) return;
        File existing = new File(slotPath(sel.slot));
        if (existing.exists()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    I18n.f("save.overwriteBody", sel.slot), ButtonType.OK, ButtonType.CANCEL);
            a.setHeaderText(I18n.t("save.overwriteTitle"));
            Optional<ButtonType> r = a.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
        }
        try {
            DataManager.saveGameNamed(currentLeague, slotPath(sel.slot), sel.name);
            setStatus(I18n.f("save.ok", sel.name, sel.slot));
        } catch (Exception ex) {
            setStatus(I18n.f("save.fail", ex.getMessage()));
        }
    }

    private static class SaveSelection { int slot; String name; }

    private SaveSelection chooseSlotForSave() {
        final SaveSelection[] result = { null };
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(I18n.t("save.dialogTitle"));

        String sportLabel = isFootballSport ? I18n.t("save.sportFootball") : I18n.t("save.sportVolleyball");
        Label header = new Label(I18n.f("save.header", sportLabel));
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ToggleGroup group = new ToggleGroup();
        VBox slotsBox = new VBox(8);
        slotsBox.setPadding(new Insets(8, 4, 8, 4));
        for (int i = 1; i <= SLOT_COUNT; i++) {
            RadioButton rb = new RadioButton(slotLabel(i));
            rb.setUserData(i);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-font-size: 13px;");
            if (i == 1) rb.setSelected(true);
            slotsBox.getChildren().add(rb);
        }

        Label nameLabel = new Label(I18n.t("save.nameLabel"));
        TextField nameField = new TextField();
        nameField.setPromptText(I18n.t("save.namePrompt"));
        group.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                int s = (Integer) newT.getUserData();
                DataManager.SaveMeta m = DataManager.loadMeta(slotPath(s));
                nameField.setText(m == null || m.name == null ? "" : m.name);
            }
        });
        DataManager.SaveMeta initial = DataManager.loadMeta(slotPath(1));
        if (initial != null && initial.name != null) nameField.setText(initial.name);

        Button cancel = new Button(I18n.t("save.cancel"));
        cancel.setOnAction(e -> dialog.close());
        Button ok = new Button(I18n.t("save.btn"));
        ok.setDefaultButton(true);
        ok.setStyle("-fx-font-weight: bold;");
        ok.setOnAction(e -> {
            if (group.getSelectedToggle() != null) {
                SaveSelection ss = new SaveSelection();
                ss.slot = (Integer) group.getSelectedToggle().getUserData();
                ss.name = nameField.getText().isBlank() ? ("Save " + ss.slot) : nameField.getText().trim();
                result[0] = ss;
            }
            dialog.close();
        });

        HBox actions = new HBox(10, cancel, ok);
        actions.setAlignment(Pos.CENTER_RIGHT);
        VBox layout = new VBox(10, header, slotsBox, nameLabel, nameField, actions);
        layout.setPadding(new Insets(16));
        dialog.setScene(new Scene(layout, 480, 360));
        dialog.showAndWait();
        return result[0];
    }

    // ============== CELLS ==============

    private static class FixtureItem {
        final int round;
        final IMatch match;
        final boolean isHeader;
        final ITeam managed;
        private FixtureItem(int round, IMatch match, boolean header, ITeam managed) {
            this.round = round; this.match = match; this.isHeader = header; this.managed = managed;
        }
        static FixtureItem header(int round) { return new FixtureItem(round, null, true, null); }
        static FixtureItem match(int round, IMatch m, ITeam managed) { return new FixtureItem(round, m, false, managed); }
    }

    private static class FixtureCell extends ListCell<FixtureItem> {
        @Override
        protected void updateItem(FixtureItem it, boolean empty) {
            super.updateItem(it, empty);
            if (empty || it == null) { setGraphic(null); setText(null); return; }
            if (it.isHeader) {
                Label l = new Label(I18n.f("fixture.weekHeader", it.round));
                l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;"
                        + " -fx-padding: 4 0 2 0;");
                setGraphic(l); setText(null); return;
            }
            IMatch m = it.match;
            ITeam home = m.getHomeTeam(); ITeam away = m.getAwayTeam(); ITeam winner = m.getWinner();

            Text indent = new Text("    ");
            Text time = new Text(" " + m.getKickoffTime() + "  ");
            time.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
            time.setFill(Color.web("#5b6570"));
            Text homeText = new Text(home.getName() + (home == it.managed ? "★" : ""));
            homeText.setFont(Font.font("System", winner == home ? FontWeight.BOLD : FontWeight.NORMAL, 13));
            homeText.setFill(winner == home ? Color.web("#1e6f30") : Color.web("#222"));
            Text middle;
            if (m.isPlayed()) {
                middle = new Text("  " + m.getScore() + "  ");
                middle.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
                middle.setFill(Color.web("#111"));
            } else {
                middle = new Text("   vs   ");
                middle.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
                middle.setFill(Color.web("#888"));
            }
            Text awayText = new Text(away.getName() + (away == it.managed ? "★" : ""));
            awayText.setFont(Font.font("System", winner == away ? FontWeight.BOLD : FontWeight.NORMAL, 13));
            awayText.setFill(winner == away ? Color.web("#1e6f30") : Color.web("#222"));
            Text mark = new Text("  " + (m.isPlayed() ? (winner == null ? "=" : "✓") : "—"));
            mark.setFont(Font.font("System", FontWeight.BOLD, 12));
            mark.setFill(m.isPlayed() ? (winner == null ? Color.web("#888") : Color.web("#1e6f30")) : Color.web("#bbb"));

            TextFlow flow = new TextFlow(indent, time, homeText, middle, awayText, mark);
            setGraphic(flow); setText(null);
        }
    }

    private static class EventCell extends ListCell<MatchEvent> {
        @Override
        protected void updateItem(MatchEvent ev, boolean empty) {
            super.updateItem(ev, empty);
            if (empty || ev == null) { setGraphic(null); setText(null); return; }
            Text icon = new Text(ev.getIcon() + "  ");
            icon.setFont(Font.font("System", FontWeight.BOLD, 13));
            Text clock = new Text(String.format("%-10s ", ev.clock));
            clock.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
            clock.setFill(Color.web("#666"));
            Text body = new Text(ev.text);
            body.setFont(Font.font("System", FontWeight.NORMAL, 13));
            switch (ev.type) {
                case GOAL: body.setFill(Color.web("#1e6f30")); break;
                case YELLOW_CARD: body.setFill(Color.web("#9a7d00")); break;
                case RED_CARD: body.setFill(Color.web("#9b2c2c")); break;
                case INJURY: body.setFill(Color.web("#7a1f9a")); break;
                case SUBSTITUTION: body.setFill(Color.web("#1f4e9a")); break;
                default: body.setFill(Color.web("#333"));
            }
            TextFlow flow = new TextFlow(icon, clock, body);
            setGraphic(flow); setText(null);
        }
    }

    private static class PlayerStatLine {
        final IPlayer player;
        final ITeam team;
        final boolean managed;
        final boolean empty;
        final String message;
        int rank;

        PlayerStatLine(IPlayer player, ITeam team, boolean managed) {
            this.player = player;
            this.team = team;
            this.managed = managed;
            this.empty = false;
            this.message = "";
        }

        private PlayerStatLine(String message) {
            this.player = null;
            this.team = null;
            this.managed = false;
            this.empty = true;
            this.message = message;
        }

        static PlayerStatLine empty(String message) {
            return new PlayerStatLine(message);
        }
    }

    private static class GoalStatCell extends ListCell<PlayerStatLine> {
        @Override
        protected void updateItem(PlayerStatLine line, boolean empty) {
            super.updateItem(line, empty);
            if (empty || line == null) { setGraphic(null); setText(null); return; }
            if (line.empty) { setGraphic(emptyLabel(line.message)); setText(null); return; }

            Label rank = new Label(line.rank + ".");
            rank.setMinWidth(30);
            rank.setStyle("-fx-font-family: 'Consolas','Courier New',monospace; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            VBox info = statIdentity(line);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label goals = pill(I18n.t("stats.goalShort") + " " + line.player.getSeasonGoals(), "#1e8449", "white");
            HBox row = new HBox(8, rank, info, spacer, goals);
            row.setAlignment(Pos.CENTER_LEFT);
            setGraphic(row); setText(null);
        }
    }

    private static class CardStatCell extends ListCell<PlayerStatLine> {
        @Override
        protected void updateItem(PlayerStatLine line, boolean empty) {
            super.updateItem(line, empty);
            if (empty || line == null) { setGraphic(null); setText(null); return; }
            if (line.empty) { setGraphic(emptyLabel(line.message)); setText(null); return; }

            VBox info = statIdentity(line);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox cards = new HBox(6);
            cards.setAlignment(Pos.CENTER_RIGHT);
            if (line.player.getSeasonYellowCards() > 0) {
                cards.getChildren().add(cardWithCount("#f1c40f", line.player.getSeasonYellowCards(), "#2c3e50"));
            }
            if (line.player.getSeasonRedCards() > 0) {
                cards.getChildren().add(cardWithCount("#c0392b", line.player.getSeasonRedCards(), "white"));
            }
            if (line.player.isSuspended()) {
                cards.getChildren().add(pill(I18n.t("status.suspendedShort") + " " + line.player.getSuspensionMatches(), "#9b2c2c", "white"));
            }
            HBox row = new HBox(8, info, spacer, cards);
            row.setAlignment(Pos.CENTER_LEFT);
            setGraphic(row); setText(null);
        }
    }

    private static class AvailabilityStatCell extends ListCell<PlayerStatLine> {
        @Override
        protected void updateItem(PlayerStatLine line, boolean empty) {
            super.updateItem(line, empty);
            if (empty || line == null) { setGraphic(null); setText(null); return; }
            if (line.empty) { setGraphic(emptyLabel(line.message)); setText(null); return; }

            VBox info = statIdentity(line);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox tags = new HBox(6);
            tags.setAlignment(Pos.CENTER_RIGHT);
            if (line.player.isInjured()) tags.getChildren().add(pill(I18n.t("status.injured"), "#7a1f9a", "white"));
            if (line.player.isSuspended()) tags.getChildren().add(pill(I18n.f("status.suspended", line.player.getSuspensionMatches()), "#9b2c2c", "white"));
            HBox row = new HBox(8, info, spacer, tags);
            row.setAlignment(Pos.CENTER_LEFT);
            setGraphic(row); setText(null);
        }
    }

    private static VBox statIdentity(PlayerStatLine line) {
        Label player = new Label("#" + line.player.getJerseyNumber() + " " + line.player.getName());
        player.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1f2d3d;");
        Label team = new Label(line.team.getName() + (line.managed ? " ★" : ""));
        team.setStyle("-fx-font-size: 11px; -fx-text-fill: #607080;");
        return new VBox(1, player, team);
    }

    private static Label emptyLabel(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #607080; -fx-padding: 6 0;");
        return label;
    }

    private static Label pill(String text, String background, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";"
                + " -fx-background-color: " + background + "; -fx-padding: 3 8; -fx-background-radius: 8;");
        return label;
    }

    private static StackPane cardWithCount(String color, int count, String textColor) {
        Rectangle card = new Rectangle(18, 24);
        card.setArcWidth(3);
        card.setArcHeight(3);
        card.setFill(Color.web(color));
        card.setStroke(Color.web("#2c3e50", 0.35));
        Label label = new Label(String.valueOf(count));
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        return new StackPane(card, label);
    }

    private static Rectangle cardIcon(String color) {
        Rectangle card = new Rectangle(10, 14);
        card.setArcWidth(2);
        card.setArcHeight(2);
        card.setFill(Color.web(color));
        card.setStroke(Color.web("#2c3e50", 0.35));
        return card;
    }

    private static class PlayerCell extends ListCell<IPlayer> {
        @Override
        protected void updateItem(IPlayer p, boolean empty) {
            super.updateItem(p, empty);
            if (empty || p == null) { setGraphic(null); setText(null); return; }
            HBox row = new HBox(5);
            row.setAlignment(Pos.CENTER_LEFT);

            if (p.isInjured()) row.getChildren().add(pill(I18n.t("status.injured"), "#7a1f9a", "white"));
            if (p.isSuspended()) row.getChildren().add(pill(I18n.t("status.suspendedShort"), "#9b2c2c", "white"));
            if (p.hasRedCard()) {
                row.getChildren().add(cardIcon("#c0392b"));
            } else {
                for (int i = 0; i < Math.min(2, p.getYellowCards()); i++) {
                    row.getChildren().add(cardIcon("#f1c40f"));
                }
            }

            StringBuilder sb = new StringBuilder();
            if (p.getGoalsThisMatch() > 0) {
                sb.append("⚽");
                if (p.getGoalsThisMatch() > 1) sb.append(p.getGoalsThisMatch());
                sb.append(" ");
            }
            if (p.getSubInClock() != null) {
                sb.append("🔁").append(p.getSubInClock()).append(" ");
            }
            sb.append(p.getName())
              .append("  (").append(p.getPosition()).append(", ").append(p.getSkillLevel()).append(")");
            if (!p.getGoalMinutes().isEmpty()) {
                sb.append("  ⚽ ").append(String.join(", ", p.getGoalMinutes()));
            }
            Label l = new Label(sb.toString());
            l.setStyle("-fx-font-size: 13px;"
                    + (p.isInjured() ? " -fx-text-fill: #7a1f9a;" : "")
                    + (p.isSuspended() ? " -fx-text-fill: #9b2c2c; -fx-font-weight: bold;" : "")
                    + (p.hasRedCard() ? " -fx-text-fill: #9b2c2c; -fx-font-style: italic;" : "")
                    + (p.getSubInClock() != null ? " -fx-font-weight: bold;" : ""));
            row.getChildren().add(l);
            setGraphic(row); setText(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
