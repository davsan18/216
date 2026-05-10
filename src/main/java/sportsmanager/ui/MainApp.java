package sportsmanager.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainApp extends Application {

    private static final String SAVE_DIR = "saves";
    private static final int SLOT_COUNT = 3;

    private Stage primaryStage;
    private Scene mainMenuScene;

    private ILeague currentLeague;
    private boolean isFootballSport;
    private ITeam managedTeam;
    private ISportFactory currentFactory;
    private String currentSportName;

    // Dashboard widgets
    private ListView<String> standingsView;
    private ListView<FixtureItem> fixtureView;
    private ListView<String> injuriesView;
    private Label nextMatchLabel;
    private Button playMatchBtn;
    private Label statusLabel;

    // Match view widgets
    private IMatch currentMatch;
    private Label matchClockLabel;
    private Label matchHomeScoreLabel;
    private Label matchAwayScoreLabel;
    private Label matchHomeAbbrLabel;
    private Label matchAwayAbbrLabel;
    private ListView<MatchEvent> eventsView;
    private ListView<IPlayer> ourOnFieldView;
    private ListView<IPlayer> ourBenchView;
    private ListView<IPlayer> oppOnFieldView;
    private ListView<IPlayer> oppBenchView;
    private StackPane formationHolder;
    private Label subsBadge;
    private Button substituteBtn;
    private Button quickFinishBtn;
    private Button continueBtn;
    private ToggleGroup speedGroup;
    private Timeline matchTimeline;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        ensureSaveDir();
        this.mainMenuScene = buildMainMenuScene();
        primaryStage.setTitle("Sports Manager - M3");
        primaryStage.setScene(mainMenuScene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(740);
        primaryStage.show();
    }

    // ============== ANA MENÜ ==============

    private Scene buildMainMenuScene() {
        Label title = new Label("SPORTS MANAGER");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 2, 3);");
        Label subtitle = new Label("Lütfen antrenör olmak istediğiniz sporu seçin");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #ecf0f1; -fx-padding: 0 0 20 0;");

        Button btnFootball = bigMenuButton("⚽ FUTBOL", "#27ae60");
        Button btnVolleyball = bigMenuButton("🏐 VOLEYBOL", "#e67e22");
        btnFootball.setOnAction(e -> openTeamSelect(new FootballFactory(), "Futbol"));
        btnVolleyball.setOnAction(e -> openTeamSelect(new VolleyballFactory(), "Voleybol"));

        HBox sportRow = new HBox(28, btnFootball, btnVolleyball);
        sportRow.setAlignment(Pos.CENTER);

        Button mySaves = new Button("📂 Kayıtlarım");
        mySaves.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #8e44ad;"
                + " -fx-text-fill: white; -fx-pref-width: 220px; -fx-pref-height: 50px; -fx-cursor: hand;"
                + " -fx-background-radius: 22px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 4);");
        mySaves.setOnAction(e -> primaryStage.setScene(buildSavesManagerScene()));

        VBox root = new VBox(15, title, subtitle, sportRow, mySaves);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a2a6c, #b21f1f, #fdbb2d);");
        return new Scene(root, 1100, 740);
    }

    private Button bigMenuButton(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: " + color + ";"
                + " -fx-text-fill: white; -fx-pref-width: 220px; -fx-pref-height: 70px; -fx-cursor: hand;"
                + " -fx-background-radius: 30px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        return b;
    }

    // ============== KAYITLARIM ==============

    private Scene buildSavesManagerScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a2a6c, #b21f1f, #fdbb2d);");

        Label title = new Label("📂 KAYITLARIM");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 2, 2);");

        VBox slotList = new VBox(8);
        slotList.setAlignment(Pos.CENTER);
        for (boolean foot : new boolean[]{true, false}) {
            for (int i = 1; i <= SLOT_COUNT; i++) {
                slotList.getChildren().add(buildSaveSlotRow(i, foot, slotList));
            }
        }

        Button back = secondaryButton("⬅ Ana Menü", "#7f8c8d");
        back.setOnAction(e -> primaryStage.setScene(mainMenuScene));

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

    private HBox buildSaveSlotRow(int slot, boolean football, VBox listContainer) {
        File f = new File(slotPathForSport(slot, football));
        boolean exists = f.exists();
        DataManager.SaveMeta meta = exists ? DataManager.loadMeta(slotPathForSport(slot, football)) : null;
        String sportTag = football ? "⚽ Futbol" : "🏐 Voleybol";
        String name = (meta != null && meta.name != null && !meta.name.isBlank()) ? meta.name : (exists ? "(adsız)" : "");
        long ts = (meta != null && meta.timestamp > 0) ? meta.timestamp : (exists ? f.lastModified() : 0);
        String when = ts > 0 ? new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(ts)) : "";
        String managed = (meta != null && meta.managedTeam != null && !meta.managedTeam.isBlank()) ? meta.managedTeam : "";
        String progress = (meta != null && meta.matchesTotal > 0) ? meta.matchesPlayed + "/" + meta.matchesTotal + " maç" : "";

        Label slotLabel = new Label("Slot " + slot + "  ·  " + sportTag);
        slotLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        Label nameLabel = new Label(exists ? name : "boş");
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label detailLabel = new Label(
                exists ? (when + (managed.isEmpty() ? "" : "  ·  " + managed) + (progress.isEmpty() ? "" : "  ·  " + progress)) : "");
        detailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

        VBox info = new VBox(2, slotLabel, nameLabel, detailLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button loadBtn = new Button("Yükle");
        loadBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #16a085;"
                + " -fx-text-fill: white; -fx-padding: 8 18; -fx-cursor: hand; -fx-background-radius: 8;");
        loadBtn.setDisable(!exists);
        loadBtn.setOnAction(e -> loadFromSlot(slot, football));

        Button delBtn = new Button("Sil");
        delBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #c0392b;"
                + " -fx-text-fill: white; -fx-padding: 8 18; -fx-cursor: hand; -fx-background-radius: 8;");
        delBtn.setDisable(!exists);
        delBtn.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    "Bu kayıt kalıcı olarak silinecek. Emin misiniz?",
                    ButtonType.OK, ButtonType.CANCEL);
            a.setHeaderText("Kaydı Sil");
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
            managedTeam = currentLeague.getManagedTeam();
            if (managedTeam == null && !currentLeague.getTeams().isEmpty()) {
                managedTeam = currentLeague.getTeams().get(0);
                currentLeague.setManagedTeam(managedTeam);
            }
            primaryStage.setScene(buildDashboardScene());
            setStatus("✓ Kayıt yüklendi.");
        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Yükleme hatası: " + ex.getMessage(), ButtonType.OK);
            a.showAndWait();
        }
    }

    private void deleteSlot(int slot, boolean football) {
        File f = new File(slotPathForSport(slot, football));
        File meta = new File(slotPathForSport(slot, football) + ".meta");
        if (f.exists()) f.delete();
        if (meta.exists()) meta.delete();
    }

    // ============== TAKIM SEÇİM ==============

    private void openTeamSelect(ISportFactory factory, String sportName) {
        this.currentFactory = factory;
        this.currentSportName = sportName;
        this.isFootballSport = factory instanceof FootballFactory;
        this.currentLeague = factory.createLeague(sportName + " Süper Ligi");

        String[] teamNames = isFootballSport ? Squads.FOOTBALL_TEAMS : Squads.VOLLEYBALL_TEAMS;
        for (String tn : teamNames) {
            ITeam t = factory.createTeam(tn);
            // TD oyundan kaldırıldı (kullanıcı zaten antrenör)
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

        Label title = new Label((isFootballSport ? "⚽ " : "🏐 ") + "Antrenör olacağınız takımı seçin");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 8, 0, 1, 1);");

        VBox teamCards = new VBox(8);
        teamCards.setAlignment(Pos.CENTER);
        for (ITeam t : currentLeague.getTeams()) teamCards.getChildren().add(buildTeamCard(t));

        Button back = secondaryButton("⬅ Geri", "#7f8c8d");
        back.setOnAction(e -> primaryStage.setScene(mainMenuScene));

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
        Label info = new Label("Ortalama: " + skill
                + "  ·  " + team.getPlayers().size() + " oyuncu"
                + "  ·  Stad: " + Squads.stadium(team.getName()));
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");
        VBox txt = new VBox(2, name, info);

        Button pick = new Button("Bu Takımı Yönet ▶");
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
        Button back = secondaryButton("⬅ Ana Menü", "#7f8c8d");
        back.setOnAction(e -> confirmAndGoToMainMenu());

        String icon = isFootballSport ? "⚽" : "🏐";
        Label header = new Label(icon + "  " + currentLeague.getName().toUpperCase()
                + "  |  Takımım: " + managedTeam.getName());
        header.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button save = secondaryButton("💾 Kaydet", "#2980b9");
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
        VBox standingsCard = card("📊 Puan Durumu", standingsView);

        fixtureView = new ListView<>();
        fixtureView.setStyle("-fx-control-inner-background: rgba(255,255,255,0.95); -fx-background-radius: 10;");
        fixtureView.setCellFactory(lv -> new FixtureCell());
        VBox fixtureCard = card("📅 Fikstür", fixtureView);

        injuriesView = new ListView<>();
        injuriesView.setStyle("-fx-font-size: 13px; -fx-control-inner-background: rgba(255,235,235,0.95);"
                + " -fx-background-radius: 10;");
        VBox injuriesCard = card("⚕ Sakatlar", injuriesView);

        VBox right = new VBox(10, fixtureCard, injuriesCard);
        VBox.setVgrow(fixtureCard, Priority.ALWAYS);
        VBox.setVgrow(injuriesCard, Priority.SOMETIMES);

        HBox.setHgrow(standingsCard, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        return new HBox(12, standingsCard, right);
    }

    private VBox buildDashboardBottomBar() {
        nextMatchLabel = new Label();
        nextMatchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        String btnColor = isFootballSport ? "#f1c40f" : "#2c3e50";
        String btnText = isFootballSport ? "black" : "white";
        playMatchBtn = new Button("▶ Maç Önizleme");
        playMatchBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: " + btnColor + ";"
                + " -fx-text-fill: " + btnText + "; -fx-padding: 10 24; -fx-cursor: hand; -fx-background-radius: 18;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 4);");
        playMatchBtn.setOnAction(e -> {
            IMatch next = currentLeague.getNextMatchForManaged();
            if (next != null) openPreMatchPreview(next);
        });

        statusLabel = new Label("Hazır.");
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
        updateInjuries();
        updateNextMatch();
    }

    private void updateStandings() {
        standingsView.getItems().clear();
        standingsView.getItems().add(String.format(" #  %-22s  Puan", "Takım"));
        int rank = 1;
        for (ITeam team : currentLeague.getStandings()) {
            String marker = (team == managedTeam) ? "★ " : "  ";
            standingsView.getItems().add(String.format(" %d.%s%-22s   %3d", rank, marker, team.getName(), team.getPoints()));
            rank++;
        }
    }

    private void updateFixture() {
        fixtureView.getItems().clear();
        // Group by week
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
        // Auto-scroll to next-match section
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

    private void updateInjuries() {
        injuriesView.getItems().clear();
        for (ITeam team : currentLeague.getTeams()) {
            for (IPlayer p : team.getPlayers()) {
                if (p.isInjured()) {
                    String tag = (team == managedTeam) ? " ★" : "";
                    injuriesView.getItems().add("⚕  " + p.getName() + " — " + team.getName()
                            + " (" + p.getPosition() + ")" + tag);
                }
            }
        }
        if (injuriesView.getItems().isEmpty()) {
            injuriesView.getItems().add("Şu anda sakat oyuncu yok.");
        }
    }

    private void updateNextMatch() {
        IMatch next = currentLeague.getNextMatchForManaged();
        if (next == null) {
            // Şampiyonu öğren
            ITeam champion = currentLeague.getStandings().isEmpty() ? null : currentLeague.getStandings().get(0);
            String champText = (champion != null) ? "  ·  Şampiyon: " + champion.getName() : "";
            nextMatchLabel.setText("✓ Sezon tamamlandı!" + champText);
            playMatchBtn.setText("🔄 Tekrar Oyna (Yeni Sezon)");
            playMatchBtn.setDisable(false);
            playMatchBtn.setOnAction(e -> startNewSeason());
        } else {
            int round = currentLeague.getRoundOf(next);
            String home = next.getHomeTeam().getName();
            String away = next.getAwayTeam().getName();
            String homeAway = (next.getHomeTeam() == managedTeam) ? "  (İÇ SAHA)" :
                              (next.getAwayTeam() == managedTeam ? "  (DEPLASMAN)" : "");
            nextMatchLabel.setText("Sıradaki Maç (Hafta " + round + "):  "
                    + home + "  vs  " + away + homeAway);
            playMatchBtn.setText("▶ Maç Önizleme");
            playMatchBtn.setDisable(false);
            playMatchBtn.setOnAction(e -> {
                IMatch n = currentLeague.getNextMatchForManaged();
                if (n != null) openPreMatchPreview(n);
            });
        }
    }

    private void startNewSeason() {
        ISportFactory f = currentFactory;
        String sn = currentSportName;
        if (f == null) {
            f = isFootballSport ? new FootballFactory() : new VolleyballFactory();
            sn = isFootballSport ? "Futbol" : "Voleybol";
        }
        openTeamSelect(f, sn);
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
                "Ana menüye dönülecek. Kaydedilmemiş ilerleme kaybolacak. Devam edilsin mi?",
                ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Ana menüye dön");
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            stopMatchTimeline();
            currentLeague = null; managedTeam = null; currentMatch = null;
            primaryStage.setScene(mainMenuScene);
        }
    }

    // ============== MAÇ ÖNİZLEME ==============

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

        Label title = new Label("Maç Önizlemesi  ·  Hafta " + round);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        Label vsLine = new Label(h.getName() + (h == managedTeam ? " ★" : "")
                + "    vs    " + a.getName() + (a == managedTeam ? " ★" : ""));
        vsLine.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0, 1, 1);");

        Label hostLine = new Label("⌂ " + h.getName() + " ev sahibi");
        hostLine.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");
        Label stadLine = new Label("🏟 Stad: " + Squads.stadium(h.getName()));
        stadLine.setStyle("-fx-font-size: 13px; -fx-text-fill: #ecf0f1;");
        VBox hostBox = new VBox(2, hostLine, stadLine);
        hostBox.setAlignment(Pos.CENTER);

        Pane pitch = isFootballSport
                ? FormationView.footballPitch(700, 360, h, a,
                        currentMatch.getOnField(h), currentMatch.getOnField(a))
                : FormationView.volleyballCourtView(700, 360, h, a,
                        currentMatch.getOnField(h), currentMatch.getOnField(a));

        VBox homeRoster = buildPreviewRoster(h, currentMatch.getOnField(h), currentMatch.getBench(h));
        VBox awayRoster = buildPreviewRoster(a, currentMatch.getOnField(a), currentMatch.getBench(a));
        HBox rosters = new HBox(14, homeRoster, awayRoster);
        HBox.setHgrow(homeRoster, Priority.ALWAYS);
        HBox.setHgrow(awayRoster, Priority.ALWAYS);

        Button back = secondaryButton("⬅ Geri", "#7f8c8d");
        back.setOnAction(e -> primaryStage.setScene(buildDashboardScene()));
        Button startBtn = new Button("▶ Maça Başla");
        startBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #27ae60;"
                + " -fx-text-fill: white; -fx-padding: 10 24; -fx-cursor: hand; -fx-background-radius: 18;");
        startBtn.setOnAction(e -> openMatchView());

        HBox actions = new HBox(14, back, startBtn);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(10, title, vsLine, hostBox, pitch, rosters, actions);
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

    private VBox buildPreviewRoster(ITeam team, List<IPlayer> onField, List<IPlayer> bench) {
        Label header = new Label(team.getName());
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label firstXIHeader = makeSquadHeader(isFootballSport ? "İlk 11" : "İlk 6", "#16a085");
        ListView<IPlayer> field = new ListView<>();
        field.getItems().setAll(onField);
        field.setCellFactory(lv -> new PlayerCell());
        field.setPrefHeight(280);
        field.setStyle("-fx-control-inner-background: rgba(220,255,220,0.95); -fx-background-radius: 8;"
                + " -fx-font-size: 13px;");

        Label benchHeader = makeSquadHeader("Yedekler", "#7f8c8d");
        ListView<IPlayer> benchView = new ListView<>();
        benchView.getItems().setAll(bench);
        benchView.setCellFactory(lv -> new PlayerCell());
        benchView.setPrefHeight(180);
        benchView.setStyle("-fx-control-inner-background: rgba(245,245,245,0.95); -fx-background-radius: 8;"
                + " -fx-font-size: 13px;");

        return new VBox(4, header, firstXIHeader, field, benchHeader, benchView);
    }

    private Label makeSquadHeader(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-background-color: " + color + ";"
                + " -fx-padding: 5 14; -fx-background-radius: 6;");
        return l;
    }

    // ============== CANLI MAÇ EKRANI ==============

    private void openMatchView() {
        if (currentMatch == null) return;
        primaryStage.setScene(buildMatchViewScene());
        refreshMatchView();
        // Otomatik 1x'den başlat
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
        Button leave = secondaryButton("⬅ Maçtan Çık", "#7f8c8d");
        leave.setOnAction(e -> confirmLeaveMatch());

        Region spacer1 = new Region(); HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);

        // SCOREBOARD
        ITeam h = currentMatch.getHomeTeam();
        ITeam a = currentMatch.getAwayTeam();
        matchHomeAbbrLabel = new Label(abbr(h.getName()) + (h == managedTeam ? " ★" : ""));
        matchHomeAbbrLabel.setStyle(scoreTeamStyle());
        matchHomeScoreLabel = new Label("0");
        matchHomeScoreLabel.setStyle(scoreDigitStyle());
        Label sep = new Label("–"); sep.setStyle(scoreSepStyle());
        matchAwayScoreLabel = new Label("0");
        matchAwayScoreLabel.setStyle(scoreDigitStyle());
        matchAwayAbbrLabel = new Label(abbr(a.getName()) + (a == managedTeam ? " ★" : ""));
        matchAwayAbbrLabel.setStyle(scoreTeamStyle());

        matchClockLabel = new Label("0'");
        matchClockLabel.setStyle("-fx-font-family: 'Consolas','Courier New',monospace;"
                + " -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;"
                + " -fx-padding: 6 14;");

        HBox scoreboardInner = new HBox(14, matchHomeAbbrLabel, matchHomeScoreLabel, sep, matchAwayScoreLabel, matchAwayAbbrLabel);
        scoreboardInner.setAlignment(Pos.CENTER);
        scoreboardInner.setPadding(new Insets(8, 22, 8, 22));
        scoreboardInner.setStyle("-fx-background-color: linear-gradient(to bottom, #111111, #2c2c2c);"
                + " -fx-background-radius: 14; -fx-border-color: #f1c40f; -fx-border-width: 2;"
                + " -fx-border-radius: 14;");

        HBox scoreboard = new HBox(8, scoreboardInner, matchClockLabel);
        scoreboard.setAlignment(Pos.CENTER);
        scoreboard.setStyle("-fx-background-color: rgba(0,0,0,0.45); -fx-background-radius: 16;"
                + " -fx-padding: 6;");

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
        // Take first 3 uppercase letters of first word, plus first letter of next word if any
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
        VBox eventsCard = card("📜 Maç Olayları", eventsView);

        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-tab-min-width: 110px;");

        ourOnFieldView = new ListView<>(); ourOnFieldView.setCellFactory(lv -> new PlayerCell());
        ourOnFieldView.setStyle("-fx-control-inner-background: rgba(220,255,220,0.95); -fx-background-radius: 8;");
        ourBenchView = new ListView<>(); ourBenchView.setCellFactory(lv -> new PlayerCell());
        ourBenchView.setStyle("-fx-control-inner-background: rgba(245,245,245,0.95); -fx-background-radius: 8;");
        VBox ourBox = squadBox(isFootballSport ? "İlk 11" : "İlk 6", ourOnFieldView, "Yedekler", ourBenchView);
        Tab ourTab = new Tab("Bizim Kadro", ourBox); ourTab.setClosable(false);

        oppOnFieldView = new ListView<>(); oppOnFieldView.setCellFactory(lv -> new PlayerCell());
        oppOnFieldView.setStyle("-fx-control-inner-background: rgba(220,230,255,0.95); -fx-background-radius: 8;");
        oppBenchView = new ListView<>(); oppBenchView.setCellFactory(lv -> new PlayerCell());
        oppBenchView.setStyle("-fx-control-inner-background: rgba(245,245,245,0.95); -fx-background-radius: 8;");
        VBox oppBox = squadBox(isFootballSport ? "İlk 11 (rakip)" : "İlk 6 (rakip)", oppOnFieldView,
                "Yedekler (rakip)", oppBenchView);
        Tab oppTab = new Tab("Rakip Kadro", oppBox); oppTab.setClosable(false);

        formationHolder = new StackPane();
        formationHolder.setPadding(new Insets(4));
        Tab pitchTab = new Tab("Diziliş", formationHolder); pitchTab.setClosable(false);

        tabs.getTabs().addAll(ourTab, oppTab, pitchTab);

        VBox tabsCard = card("🧑‍🤝‍🧑 Kadrolar", tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        HBox.setHgrow(eventsCard, Priority.ALWAYS);
        HBox.setHgrow(tabsCard, Priority.ALWAYS);
        return new HBox(10, eventsCard, tabsCard);
    }

    private VBox squadBox(String t1, ListView<IPlayer> v1, String t2, ListView<IPlayer> v2) {
        Label l1 = makeSquadHeader(t1, "#16a085");
        Label l2 = makeSquadHeader(t2, "#7f8c8d");
        VBox v = new VBox(4, l1, v1, l2, v2);
        VBox.setVgrow(v1, Priority.ALWAYS);
        VBox.setVgrow(v2, Priority.ALWAYS);
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

        quickFinishBtn = new Button("⏭ Hızlı Bitir");
        quickFinishBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #2980b9;"
                + " -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 12;");
        quickFinishBtn.setOnAction(e -> doQuickFinish());

        // SUBS BADGE (yanına yapışık)
        subsBadge = new Label();
        subsBadge.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-background-color: #d35400; -fx-padding: 6 12; -fx-background-radius: 10;");

        substituteBtn = new Button("🔁 Oyuncu Değişikliği");
        substituteBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #d35400;"
                + " -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 12;");
        substituteBtn.setOnAction(e -> openSubDialog(false));

        HBox subsCluster = new HBox(6, subsBadge, substituteBtn);
        subsCluster.setAlignment(Pos.CENTER_LEFT);

        continueBtn = new Button("✓ Devam (Panele Dön)");
        continueBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #27ae60;"
                + " -fx-text-fill: white; -fx-padding: 8 18; -fx-cursor: hand; -fx-background-radius: 12;");
        continueBtn.setOnAction(e -> finishAndReturn());
        continueBtn.setVisible(false);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(10, speedBar, quickFinishBtn, spacer, subsCluster, continueBtn);
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

    // ---- Timeline & speed ----

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
        if (currentMatch.needsSubstitution(managedTeam)) {
            matchTimeline.pause();
            // Defer to next FX pulse so showAndWait runs cleanly outside the timeline handler
            Platform.runLater(() -> {
                openSubDialog(true);
                // Modal kapandıktan sonra: değişiklik yapıldıysa veya kullanıcı geçtiyse timeline 1x'te devam etsin
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
            currentMatch.tick(20);
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
                "Maçtan çıkarsanız kalan kısım otomatik oynanır. Emin misiniz?",
                ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Maçtan çık");
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
        ourOnFieldView.getItems().setAll(currentMatch.getOnField(managedTeam));
        ourBenchView.getItems().setAll(currentMatch.getBench(managedTeam));
        oppOnFieldView.getItems().setAll(currentMatch.getOnField(opp));
        oppBenchView.getItems().setAll(currentMatch.getBench(opp));

        Pane pitch = isFootballSport
                ? FormationView.footballPitch(560, 360,
                        currentMatch.getHomeTeam(), currentMatch.getAwayTeam(),
                        currentMatch.getOnField(currentMatch.getHomeTeam()),
                        currentMatch.getOnField(currentMatch.getAwayTeam()))
                : FormationView.volleyballCourtView(560, 360,
                        currentMatch.getHomeTeam(), currentMatch.getAwayTeam(),
                        currentMatch.getOnField(currentMatch.getHomeTeam()),
                        currentMatch.getOnField(currentMatch.getAwayTeam()));
        formationHolder.getChildren().setAll(pitch);

        int remaining = currentMatch.getRemainingSubs(managedTeam);
        int max = currentMatch.getMaxSubs();
        subsBadge.setText("🔁 Değişiklik " + remaining + "/" + max);
        // badge rengini duruma göre değiştir
        String bg = remaining == 0 ? "#7f8c8d" : (remaining == 1 ? "#c0392b" : (remaining == 2 ? "#d35400" : "#27ae60"));
        subsBadge.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-background-color: " + bg + "; -fx-padding: 6 12; -fx-background-radius: 10;");
        substituteBtn.setDisable(remaining <= 0
                || currentMatch.getOnField(managedTeam).isEmpty()
                || currentMatch.getBench(managedTeam).isEmpty()
                || currentMatch.isFinished());
    }

    private void openSubDialog(boolean forced) {
        if (currentMatch == null) return;
        List<IPlayer> field = currentMatch.getOnField(managedTeam);
        List<IPlayer> bench = currentMatch.getBench(managedTeam);
        if (bench.isEmpty()) { setStatus("Yedek oyuncu yok."); return; }
        if (!forced && field.isEmpty()) { setStatus("Sahada oyuncu yok."); return; }

        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(forced ? "Zorunlu Değişiklik" : "Oyuncu Değişikliği");

        IPlayer injured = forced ? currentMatch.getLastForceRemoved(managedTeam) : null;
        String forcedText = (injured != null)
                ? "⚕ " + injured.getName() + " (" + injured.getPosition() + ", " + injured.getSkillLevel()
                  + ") sahadan çıktı. Yerine kim girsin?"
                : "Bir oyuncu çıktı. Yerine kim girsin?";
        Label title = new Label(forced ? forcedText : "Çıkacak ve girecek oyuncuyu seçin.");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"
                + (forced ? " -fx-text-fill: #7a1f9a;" : ""));

        int remaining = currentMatch.getRemainingSubs(managedTeam);
        int max = currentMatch.getMaxSubs();
        Label badge = new Label("Kalan değişiklik: " + remaining + "/" + max);
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

        Label outHeader = new Label(isFootballSport ? "İlk 11" : "İlk 6");
        outHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label inHeader = new Label("Yedekler");
        inHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox outBox = new VBox(4, outHeader, outList);
        VBox inBox = new VBox(4, inHeader, inList);
        if (forced) { outBox.setVisible(false); outBox.setManaged(false); }
        HBox lists = new HBox(12, outBox, inBox);
        HBox.setHgrow(outBox, Priority.ALWAYS);
        HBox.setHgrow(inBox, Priority.ALWAYS);

        Button cancel = new Button(forced ? "Şimdilik Geç" : "İptal");
        cancel.setOnAction(e -> dialog.close());
        Button apply = new Button("Değişikliği Yap");
        apply.setStyle("-fx-font-weight: bold;");
        apply.setOnAction(e -> {
            IPlayer in = inList.getSelectionModel().getSelectedItem();
            boolean ok = forced
                    ? currentMatch.replace(managedTeam, in)
                    : currentMatch.substitute(managedTeam, outList.getSelectionModel().getSelectedItem(), in);
            if (ok) { dialog.close(); refreshMatchView(); }
            else setStatus("Değişiklik yapılamadı (seçim eksik veya limit doldu).");
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
        if (!f.exists()) return "Slot " + slot + "  —  boş";
        DataManager.SaveMeta m = DataManager.loadMeta(slotPath(slot));
        String name = (m != null && m.name != null && !m.name.isBlank()) ? m.name : "(adsız)";
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
                    "Slot " + sel.slot + " dolu. Üzerine yazılsın mı?",
                    ButtonType.OK, ButtonType.CANCEL);
            a.setHeaderText("Üzerine yaz");
            Optional<ButtonType> r = a.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
        }
        try {
            DataManager.saveGameNamed(currentLeague, slotPath(sel.slot), sel.name);
            setStatus("✓ '" + sel.name + "' Slot " + sel.slot + "'e kaydedildi.");
        } catch (Exception ex) {
            setStatus("✗ Kaydetme hatası: " + ex.getMessage());
        }
    }

    private static class SaveSelection { int slot; String name; }

    private SaveSelection chooseSlotForSave() {
        final SaveSelection[] result = { null };
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Kayıt Slotu Seç");

        Label header = new Label("Kayıt Slotu Seç (" + (isFootballSport ? "Futbol" : "Voleybol") + ")");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ToggleGroup group = new ToggleGroup();
        VBox slotsBox = new VBox(8);
        slotsBox.setPadding(new Insets(8, 4, 8, 4));
        for (int i = 1; i <= SLOT_COUNT; i++) {
            javafx.scene.control.RadioButton rb = new javafx.scene.control.RadioButton(slotLabel(i));
            rb.setUserData(i);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-font-size: 13px;");
            if (i == 1) rb.setSelected(true);
            slotsBox.getChildren().add(rb);
        }

        Label nameLabel = new Label("Kayıt Adı:");
        TextField nameField = new TextField();
        nameField.setPromptText("örn. Şampiyonluk Kovalama");
        group.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                int s = (Integer) newT.getUserData();
                DataManager.SaveMeta m = DataManager.loadMeta(slotPath(s));
                nameField.setText(m == null || m.name == null ? "" : m.name);
            }
        });
        DataManager.SaveMeta initial = DataManager.loadMeta(slotPath(1));
        if (initial != null && initial.name != null) nameField.setText(initial.name);

        Button cancel = new Button("İptal");
        cancel.setOnAction(e -> dialog.close());
        Button ok = new Button("Kaydet");
        ok.setDefaultButton(true);
        ok.setStyle("-fx-font-weight: bold;");
        ok.setOnAction(e -> {
            if (group.getSelectedToggle() != null) {
                SaveSelection ss = new SaveSelection();
                ss.slot = (Integer) group.getSelectedToggle().getUserData();
                ss.name = nameField.getText().isBlank() ? ("Kayıt " + ss.slot) : nameField.getText().trim();
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

    // ============== HÜCRELER ==============

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
                Label l = new Label("═══  Hafta " + it.round + "  ═══");
                l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;"
                        + " -fx-padding: 4 0 2 0;");
                setGraphic(l); setText(null); return;
            }
            IMatch m = it.match;
            ITeam home = m.getHomeTeam();
            ITeam away = m.getAwayTeam();
            ITeam winner = m.getWinner();

            Text indent = new Text("    ");
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

            TextFlow flow = new TextFlow(indent, homeText, middle, awayText, mark);
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

    private static class PlayerCell extends ListCell<IPlayer> {
        @Override
        protected void updateItem(IPlayer p, boolean empty) {
            super.updateItem(p, empty);
            if (empty || p == null) { setGraphic(null); setText(null); return; }
            StringBuilder sb = new StringBuilder();
            if (p.isInjured()) sb.append("⚕ ");
            if (p.hasRedCard()) sb.append("🟥 ");
            else if (p.getYellowCards() > 0) sb.append(p.getYellowCards() == 2 ? "🟨🟨 " : "🟨 ");
            if (p.getGoalsThisMatch() > 0) {
                sb.append("⚽");
                if (p.getGoalsThisMatch() > 1) sb.append(p.getGoalsThisMatch());
                sb.append(" ");
            }
            if (p.getSubInClock() != null) {
                sb.append("🔁").append(p.getSubInClock()).append(" ");
            }
            sb.append(p.getName()).append("  (").append(p.getPosition()).append(", ").append(p.getSkillLevel()).append(")");
            if (!p.getGoalMinutes().isEmpty()) {
                sb.append("  ⚽ ").append(String.join(", ", p.getGoalMinutes()));
            }
            Label l = new Label(sb.toString());
            l.setStyle("-fx-font-size: 13px;"
                    + (p.isInjured() ? " -fx-text-fill: #7a1f9a;" : "")
                    + (p.hasRedCard() ? " -fx-text-fill: #9b2c2c; -fx-font-style: italic;" : "")
                    + (p.getSubInClock() != null ? " -fx-font-weight: bold;" : ""));
            setGraphic(l); setText(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
