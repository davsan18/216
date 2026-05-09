package sportsmanager.volleyball;

import sportsmanager.core.AbstractMatch;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;
import java.util.List;
import java.util.Random;

public class VolleyballMatch extends AbstractMatch {

    public VolleyballMatch(ITeam homeTeam, ITeam awayTeam) {
        super(homeTeam, awayTeam);
    }

    @Override
    public void play() {
        if (played) return;

        // YENİ: Antrenör Bonusları
        int homeCoachBonus = (homeTeam.getCoach() != null) ? homeTeam.getCoach().getBonusSkill() : 0;
        int awayCoachBonus = (awayTeam.getCoach() != null) ? awayTeam.getCoach().getBonusSkill() : 0;

        int homePower = homeTeam.getOverallSkill() + 10 + homeCoachBonus;
        int awayPower = awayTeam.getOverallSkill() + awayCoachBonus;

        // Maç Sonucu Hesaplama (3 Set Alan Kazanır)
        if (homePower > awayPower) {
            homeScore = 3; awayScore = (int) (Math.random() * 3); winner = homeTeam;
            if (awayScore == 2) { homeTeam.addPoints(2); awayTeam.addPoints(1); }
            else { homeTeam.addPoints(3); }
        } else {
            awayScore = 3; homeScore = (int) (Math.random() * 3); winner = awayTeam;
            if (homeScore == 2) { awayTeam.addPoints(2); homeTeam.addPoints(1); }
            else { awayTeam.addPoints(3); }
        }

        // YENİ: Maç Sonu Rastgele Sakatlık Sistemi
        simulateInjuries(homeTeam);
        simulateInjuries(awayTeam);

        played = true;
    }

    private void simulateInjuries(ITeam team) {
        Random rand = new Random();
        List<IPlayer> players = team.getPlayers();
        if (!players.isEmpty() && rand.nextInt(100) < 15) {
            IPlayer unluckyPlayer = players.get(rand.nextInt(players.size()));
            unluckyPlayer.setInjured(true);
            System.out.println("Sakatlık Raporu: " + unluckyPlayer.getName() + " (" + team.getName() + ") voleybol maçında sakatlandı!");
        }
    }
}