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
import sportsmanager.core.ILeague;
import sportsmanager.core.ISportFactory;
import sportsmanager.core.ITeam;
import sportsmanager.football.FootballFactory;
import sportsmanager.volleyball.VolleyballFactory;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // --- 1. ANA MENÜ EKRANI (Seçim Ekranı) ---
        Label title = new Label("Sports Manager'a Hoş Geldiniz");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Lütfen yönetmek istediğiniz sporu seçin:");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        Button btnFootball = new Button("⚽ Futbol");
        btnFootball.setStyle("-fx-font-size: 18px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-pref-width: 150px; -fx-pref-height: 50px; -fx-cursor: hand; -fx-background-radius: 10px;");

        Button btnVolleyball = new Button("🏐 Voleybol");
        btnVolleyball.setStyle("-fx-font-size: 18px; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-pref-width: 150px; -fx-pref-height: 50px; -fx-cursor: hand; -fx-background-radius: 10px;");

        // --- AKSİYONLAR: Butonlara tıklanınca Motoru (Factory) belirle ve 2. Ekrana geç ---
        btnFootball.setOnAction(e -> openDashboard(primaryStage, new FootballFactory(), "Futbol"));
        btnVolleyball.setOnAction(e -> openDashboard(primaryStage, new VolleyballFactory(), "Voleybol"));

        HBox buttonBox = new HBox(20, btnFootball, btnVolleyball);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, title, subtitle, buttonBox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ecf0f1;");

        Scene mainScene = new Scene(root, 800, 600);
        primaryStage.setTitle("Sports Manager - M3");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // --- 2. MENAJERLİK PANELİ EKRANI (Dashboard) ---
    private void openDashboard(Stage primaryStage, ISportFactory factory, String sportName) {

        // Arka Planda Ligi ve Takımları Oluşturuyoruz
        ILeague league = factory.createLeague(sportName + " Süper Ligi");
        league.addTeam(factory.createTeam("Takım A"));
        league.addTeam(factory.createTeam("Takım B"));
        league.addTeam(factory.createTeam("Takım C"));
        league.addTeam(factory.createTeam("Takım D"));
        league.scheduleMatches();

        // Arayüz Elemanları
        Label header = new Label(sportName + " Menajer Paneli");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label infoLabel = new Label("Sıradaki maçı oynamak için butona tıklayın.");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Lig Tablosunu Göstereceğimiz Liste Çerçevesi
        ListView<String> standingsView = new ListView<>();
        standingsView.setStyle("-fx-font-size: 16px; -fx-pref-height: 300px;");
        updateStandingsUI(standingsView, league); // Tabloyu ilk kez doldur

        // Maç Oynatma Butonu
        Button btnPlayMatch = new Button("▶ Sonraki Haftayı Oyna");
        btnPlayMatch.setStyle("-fx-font-size: 16px; -fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5px;");

        btnPlayMatch.setOnAction(e -> {
            league.playNextRound();
            updateStandingsUI(standingsView, league); // Maç bitince tabloyu güncelle
        });

        VBox dashboardRoot = new VBox(15, header, infoLabel, standingsView, btnPlayMatch);
        dashboardRoot.setAlignment(Pos.TOP_CENTER);
        dashboardRoot.setPadding(new Insets(30));
        dashboardRoot.setStyle("-fx-background-color: #ecf0f1;");

        // Sahneyi Değiştir (Yeni Ekrana Geçiş)
        Scene dashboardScene = new Scene(dashboardRoot, 800, 600);
        primaryStage.setScene(dashboardScene);
    }

    // Tabloyu güncelleyen yardımcı metod
    private void updateStandingsUI(ListView<String> listView, ILeague league) {
        listView.getItems().clear();
        int rank = 1;
        for (ITeam team : league.getStandings()) {
            listView.getItems().add(rank + ". " + team.getName() + " \t| Puan: " + team.getPoints());
            rank++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}