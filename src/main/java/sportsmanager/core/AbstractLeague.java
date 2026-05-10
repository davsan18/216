package sportsmanager.core;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLeague implements ILeague {
    private static final long serialVersionUID = 2L;

    protected String name;
    protected List<ITeam> teams;
    protected List<IMatch> scheduledMatches;
    protected ITeam managedTeam;

    public AbstractLeague(String name) {
        this.name = name;
        this.teams = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
    }

    @Override public String getName() { return name; }
    @Override public void addTeam(ITeam team) { teams.add(team); }
    @Override public List<ITeam> getTeams() { return teams; }
    @Override public List<IMatch> getScheduledMatches() { return scheduledMatches; }

    @Override public void setManagedTeam(ITeam team) { this.managedTeam = team; }
    @Override public ITeam getManagedTeam() { return managedTeam; }

    @Override
    public IMatch getNextMatchForManaged() {
        if (managedTeam == null) return null;
        for (IMatch m : scheduledMatches) {
            if (m.isPlayed()) continue;
            if (m.getHomeTeam() == managedTeam || m.getAwayTeam() == managedTeam) return m;
        }
        return null;
    }

    @Override
    public int getRoundOf(IMatch match) {
        int idx = scheduledMatches.indexOf(match);
        if (idx < 0) return 0;
        int matchesPerRound = Math.max(1, teams.size() / 2);
        return idx / matchesPerRound + 1;
    }

    @Override
    public void autoFinishOtherMatchesInRound(IMatch reference) {
        int idx = scheduledMatches.indexOf(reference);
        if (idx < 0) return;
        int matchesPerRound = Math.max(1, teams.size() / 2);
        int round = idx / matchesPerRound;
        int from = round * matchesPerRound;
        int to = Math.min(scheduledMatches.size(), from + matchesPerRound);
        for (int i = from; i < to; i++) {
            IMatch m = scheduledMatches.get(i);
            if (m == reference || m.isPlayed()) continue;
            // Yönetilen takımın maçlarını ASLA otomatik oynatma (kullanıcı kendi oynayacak)
            if (managedTeam != null
                    && (m.getHomeTeam() == managedTeam || m.getAwayTeam() == managedTeam)) {
                continue;
            }
            m.start();
            m.tickToEnd();
        }
    }

    @Override
    public List<ITeam> getStandings() {
        List<ITeam> standings = new ArrayList<>(teams);
        standings.sort((t1, t2) -> Integer.compare(t2.getPoints(), t1.getPoints()));
        return standings;
    }

    @Override public abstract void scheduleMatches();
    @Override public abstract void playNextRound();

    /** Subclasses must implement to create a swapped (home/away reversed) copy of the given match. */
    protected abstract IMatch createReversedMatch(IMatch original);

    /**
     * Post-process the schedule to break 3+ consecutive home/away streaks for any team.
     * Iteratively swaps the middle match's home/away when a 3-in-a-row is found.
     */
    protected void fixupHomeAwayClusters() {
        int safety = 200;
        boolean fixed = true;
        while (fixed && safety-- > 0) {
            fixed = false;
            for (ITeam team : teams) {
                List<Integer> idx = new ArrayList<>();
                List<Boolean> isHome = new ArrayList<>();
                for (int i = 0; i < scheduledMatches.size(); i++) {
                    IMatch m = scheduledMatches.get(i);
                    if (m.getHomeTeam() == team) { idx.add(i); isHome.add(true); }
                    else if (m.getAwayTeam() == team) { idx.add(i); isHome.add(false); }
                }
                for (int k = 0; k <= isHome.size() - 3; k++) {
                    if (isHome.get(k).equals(isHome.get(k + 1))
                            && isHome.get(k + 1).equals(isHome.get(k + 2))) {
                        int swapIdx = idx.get(k + 1);
                        IMatch original = scheduledMatches.get(swapIdx);
                        scheduledMatches.set(swapIdx, createReversedMatch(original));
                        fixed = true;
                        break;
                    }
                }
                if (fixed) break;
            }
        }
    }
}
