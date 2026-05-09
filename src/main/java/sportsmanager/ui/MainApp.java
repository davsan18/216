package sportsmanager.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sportsmanager.core.*;
import sportsmanager.football.FootballFactory;
import sportsmanager.volleyball.VolleyballFactory;

public class MainApp extends Application {

    private ILeague currentLeague;
    private ListView<String> standingsView;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. ANA MENÜ EKRANI (Modern Tasarım) ---
        Label title = new Label("SPORTS MANAGER");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);");

        Label subtitle = new Label("Lütfen yönetmek istediğiniz sporu seçin");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #ecf0f1; -fx-padding: 0 0 30 0;");

        Button btnFootball = new Button("⚽ FUTBOL");
        btnFootball.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 60px; -fx-cursor: hand; -fx-background-radius: 30px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Button btnVolleyball = new Button("🏐 VOLEYBOL");
        btnVolleyball.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #e67e22; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 60px; -fx-cursor: hand; -fx-background-radius: 30px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        btnFootball.setOnAction(e -> openDashboard(primaryStage, new FootballFactory(), "Futbol"));
        btnVolleyball.setOnAction(e -> openDashboard(primaryStage, new VolleyballFactory(), "Voleybol"));

        HBox buttonBox = new HBox(30, btnFootball, btnVolleyball);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, subtitle, buttonBox);
        root.setAlignment(Pos.CENTER);
        // Ana Menü Arka Planı (Koyu Mavi Gradyan)
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a2a6c, #b21f1f, #fdbb2d);");

        Scene mainScene = new Scene(root, 800, 600);
        primaryStage.setTitle("Sports Manager - M3");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // --- 2. MENAJERLİK PANELİ EKRANI (Dinamik Temalı) ---
    private void openDashboard(Stage primaryStage, ISportFactory factory, String sportName) {
        currentLeague = factory.createLeague(sportName + " Süper Ligi");

        ITeam teamA = factory.createTeam("Galatasaray"); teamA.setCoach(new Coach("Okan Buruk", 10));
        ITeam teamB = factory.createTeam("Fenerbahçe"); teamB.setCoach(new Coach("İsmail Kartal", 10));
        ITeam teamC = factory.createTeam("Beşiktaş"); teamC.setCoach(new Coach("Fernando Santos", 8));
        ITeam teamD = factory.createTeam("Trabzonspor"); teamD.setCoach(new Coach("Abdullah Avcı", 9));

        teamA.addPlayer(factory.createPlayer("Icardi", "Forvet", 90));
        teamB.addPlayer(factory.createPlayer("Dzeko", "Forvet", 88));

        currentLeague.addTeam(teamA);
        currentLeague.addTeam(teamB);
        currentLeague.addTeam(teamC);
        currentLeague.addTeam(teamD);
        currentLeague.scheduleMatches();

        buildDashboardUI(primaryStage, sportName);
    }

    private void buildDashboardUI(Stage primaryStage, String sportName) {
        // --- SPORA GÖRE DİNAMİK TEMA BELİRLEME ---
        String bgColor, primaryBtnColor, icon;
        if (sportName.equals("Futbol")) {
            bgColor = "-fx-background-color: linear-gradient(to bottom, #11998e, #38ef7d);"; // Çim Yeşili
            primaryBtnColor = "#f1c40f"; // Sarı buton
            icon = "⚽";
        } else {
            bgColor = "-fx-background-color: linear-gradient(to bottom, #eb3349, #f45c43);"; // Salon Turuncusu
            primaryBtnColor = "#2c3e50"; // Koyu Lacivert buton
            icon = "🏐";
        }

        Label header = new Label(icon + " " + sportName.toUpperCase() + " LİGİ PANELİ");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 1, 1);");

        // Liste Tasarımını Modernleştirme
        standingsView = new ListView<>();
        standingsView.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-control-inner-background: #ffffff; -fx-background-radius: 15; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        standingsView.setPrefHeight(250);
        standingsView.setMaxWidth(600);
        updateStandingsUI();

        // --- BUTON TASARIMLARI ---
        Button btnPlayMatch = new Button("▶ Sonraki Haftayı Oyna");
        btnPlayMatch.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: " + primaryBtnColor + "; -fx-text-fill: " + (sportName.equals("Futbol") ? "black" : "white") + "; -fx-padding: 12 30; -fx-cursor: hand; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 4);");
        btnPlayMatch.setOnAction(e -> {
            currentLeague.playNextRound();
            updateStandingsUI();
        });

        Button btnSave = new Button("💾 Kaydet");
        btnSave.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 10;");
        btnSave.setOnAction(e -> {
            try {
                DataManager.saveGame(currentLeague, "savegame.dat");
                header.setText("✅ Oyun Kaydedildi!");
            } catch (Exception ex) {
                header.setText("❌ Kaydetme Hatası!");
            }
        });

        Button btnLoad = new Button("📂 Yükle");
        btnLoad.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 10;");
        btnLoad.setOnAction(e -> {
            try {
                currentLeague = DataManager.loadGame("savegame.dat");
                updateStandingsUI();
                header.setText("✅ Oyun Yüklendi!");
            } catch (Exception ex) {
                header.setText("❌ Yükleme Hatası!");
            }
        });

        HBox saveLoadBox = new HBox(20, btnSave, btnLoad);
        saveLoadBox.setAlignment(Pos.CENTER);

        VBox dashboardRoot = new VBox(25, header, standingsView, btnPlayMatch, saveLoadBox);
        dashboardRoot.setAlignment(Pos.CENTER);
        dashboardRoot.setPadding(new Insets(40));
        dashboardRoot.setStyle(bgColor);

        primaryStage.setScene(new Scene(dashboardRoot, 800, 600));
    }

    private void updateStandingsUI() {
        standingsView.getItems().clear();
        int rank = 1;
        for (ITeam team : currentLeague.getStandings()) {
            // Tablonun daha hizalı ve şık görünmesi için formatlama eklendi
            String row = String.format("%d. %-20s | Puan: %d", rank, team.getName(), team.getPoints());
            standingsView.getItems().add(row);
            rank++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}