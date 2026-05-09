package sportsmanager.football;

import sportsmanager.core.AbstractMatch;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;
import java.util.List;
import java.util.Random;

public class FootballMatch extends AbstractMatch {

    public FootballMatch(ITeam homeTeam, ITeam awayTeam) {
        super(homeTeam, awayTeam);
    }

    @Override
    public void play() {
        if (played) return;

        // YENİ: Antrenör Bonusları (Eğer antrenör varsa yeteneğini takıma ekle)
        int homeCoachBonus = (homeTeam.getCoach() != null) ? homeTeam.getCoach().getBonusSkill() : 0;
        int awayCoachBonus = (awayTeam.getCoach() != null) ? awayTeam.getCoach().getBonusSkill() : 0;

        int homePower = homeTeam.getOverallSkill() + 10 + homeCoachBonus; // Ev sahibi avantajı + Antrenör
        int awayPower = awayTeam.getOverallSkill() + awayCoachBonus;

        // Maç Sonucu Hesaplama
        if (homePower > awayPower) {
            homeScore = 2; awayScore = 1; winner = homeTeam; homeTeam.addPoints(3);
        } else if (awayPower > homePower) {
            homeScore = 1; awayScore = 2; winner = awayTeam; awayTeam.addPoints(3);
        } else {
            homeScore = 1; awayScore = 1; homeTeam.addPoints(1); awayTeam.addPoints(1);
        }

        // YENİ: Maç Sonu Rastgele Sakatlık (Injury) Sistemi
        simulateInjuries(homeTeam);
        simulateInjuries(awayTeam);

        played = true;
    }

    // Sakatlık simülasyonu yardımcı metodu
    private void simulateInjuries(ITeam team) {
        Random rand = new Random();
        List<IPlayer> players = team.getPlayers();
        // %15 ihtimalle maçta bir oyuncu sakatlanır
        if (!players.isEmpty() && rand.nextInt(100) < 15) {
            IPlayer unluckyPlayer = players.get(rand.nextInt(players.size()));
            unluckyPlayer.setInjured(true);
            System.out.println("Sakatlık Raporu: " + unluckyPlayer.getName() + " (" + team.getName() + ") futbol maçında sakatlandı!");
        }
    }
}