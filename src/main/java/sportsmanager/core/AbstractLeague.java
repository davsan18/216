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
            if (!m.isPlayed()) {
                m.start();
                m.tickToEnd();
            }
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
}
