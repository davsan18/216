package sportsmanager.volleyball;
import sportsmanager.core.AbstractLeague;
import sportsmanager.core.IMatch;

public class VolleyballLeague extends AbstractLeague {
    public VolleyballLeague(String name) {
        super(name);
    }

    @Override
    public void scheduleMatches() {
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                scheduledMatches.add(new VolleyballMatch(teams.get(i), teams.get(j)));
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