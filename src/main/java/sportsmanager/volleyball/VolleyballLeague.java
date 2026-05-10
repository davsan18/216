package sportsmanager.volleyball;

import sportsmanager.core.AbstractLeague;
import sportsmanager.core.IMatch;
import sportsmanager.core.ITeam;

import java.util.ArrayList;
import java.util.List;

public class VolleyballLeague extends AbstractLeague {
    public VolleyballLeague(String name) {
        super(name);
    }

    /** Circle (Berger) method: each team plays exactly one match per week. Double round-robin
     *  with home/away cluster fixup so no team has 3+ consecutive home or away games. */
    @Override
    public void scheduleMatches() {
        scheduledMatches.clear();
        List<IMatch> leg1 = buildLeg(false);
        List<IMatch> leg2 = buildLeg(true);
        int matchesPerRound = Math.max(1, teams.size() / 2);
        scheduleBalancedDoubleRoundRobin(leg1, leg2, matchesPerRound);
    }

    @Override
    protected IMatch createReversedMatch(IMatch original) {
        return new VolleyballMatch(original.getAwayTeam(), original.getHomeTeam());
    }

    private List<IMatch> buildLeg(boolean reverseLeg) {
        List<IMatch> result = new ArrayList<>();
        int n = teams.size();
        if (n < 2) return result;
        boolean odd = (n % 2 == 1);
        if (odd) n++;
        List<ITeam> arr = new ArrayList<>(teams);
        if (odd) arr.add(null);

        int rounds = n - 1;
        int half = n / 2;
        for (int r = 0; r < rounds; r++) {
            for (int i = 0; i < half; i++) {
                ITeam t1 = arr.get(i);
                ITeam t2 = arr.get(n - 1 - i);
                if (t1 == null || t2 == null) continue;
                ITeam home, away;
                boolean swap = (i == 0) ? (r % 2 == 1) : ((r + i) % 2 == 1);
                if (reverseLeg) swap = !swap;
                if (swap) { home = t2; away = t1; } else { home = t1; away = t2; }
                result.add(new VolleyballMatch(home, away));
            }
            ITeam last = arr.remove(n - 1);
            arr.add(1, last);
        }
        return result;
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
