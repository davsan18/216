package sportsmanager.football;
import sportsmanager.core.AbstractLeague;
import sportsmanager.core.IMatch;

public class FootballLeague extends AbstractLeague {
    public FootballLeague(String name) {
        super(name);
    }

    @Override
    public void scheduleMatches() {
        // İlk devre
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                scheduledMatches.add(new FootballMatch(teams.get(i), teams.get(j)));
            }
        }
        // İkinci devre — ev sahibi/deplasman tersine
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                scheduledMatches.add(new FootballMatch(teams.get(j), teams.get(i)));
            }
        }
    }

    @Override
    public void playNextRound() {
        for (IMatch match : scheduledMatches) {
            if (!match.isPlayed()) {
                match.play();
                break;
            }
        }
    }
}