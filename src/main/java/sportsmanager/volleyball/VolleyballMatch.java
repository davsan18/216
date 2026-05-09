package sportsmanager.volleyball;
import sportsmanager.core.AbstractMatch;
import sportsmanager.core.ITeam;

public class VolleyballMatch extends AbstractMatch {
    public VolleyballMatch(ITeam homeTeam, ITeam awayTeam) {
        super(homeTeam, awayTeam);
    }

    @Override
    public void play() {
        if (played) return;

        int homePower = homeTeam.getOverallSkill() + 10; // Ev sahibi avantajı
        int awayPower = awayTeam.getOverallSkill();

        // Voleybolda beraberlik yoktur, 3 set alan kazanır.
        if (homePower > awayPower) {
            homeScore = 3;
            awayScore = (int) (Math.random() * 3); // Deplasman 0, 1 veya 2 set alabilir
            winner = homeTeam;

            if (awayScore == 2) {
                homeTeam.addPoints(2);
                awayTeam.addPoints(1); // 3-2 biten maçta kaybeden de 1 puan alır
            } else {
                homeTeam.addPoints(3);
            }
        } else {
            awayScore = 3;
            homeScore = (int) (Math.random() * 3);
            winner = awayTeam;

            if (homeScore == 2) {
                awayTeam.addPoints(2);
                homeTeam.addPoints(1);
            } else {
                awayTeam.addPoints(3);
            }
        }
        played = true;
    }
}