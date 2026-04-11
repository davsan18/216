package sportsmanager.football;
import sportsmanager.core.AbstractMatch;
import sportsmanager.core.ITeam;

public class FootballMatch extends AbstractMatch {
    public FootballMatch(ITeam homeTeam, ITeam awayTeam) {
        super(homeTeam, awayTeam);
    }

    @Override
    public void play() {
        if (played) return;
        int homePower = homeTeam.getOverallSkill() + 10;
        int awayPower = awayTeam.getOverallSkill();

        if (homePower > awayPower) {
            homeScore = 2; awayScore = 1; winner = homeTeam; homeTeam.addPoints(3);
        } else if (awayPower > homePower) {
            homeScore = 1; awayScore = 2; winner = awayTeam; awayTeam.addPoints(3);
        } else {
            homeScore = 1; awayScore = 1; homeTeam.addPoints(1); awayTeam.addPoints(1);
        }
        played = true;
    }
}