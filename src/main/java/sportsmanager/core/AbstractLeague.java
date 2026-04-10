package sportsmanager.core;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLeague implements ILeague {
    protected String name;
    protected List<ITeam> teams;
    protected List<IMatch> scheduledMatches;

    public AbstractLeague(String name) {
        this.name = name;
        this.teams = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
    }

    @Override public String getName() { return name; }
    @Override public void addTeam(ITeam team) { teams.add(team); }

    @Override
    public List<ITeam> getStandings() {
        List<ITeam> standings = new ArrayList<>(teams);
        standings.sort((t1, t2) -> Integer.compare(t2.getPoints(), t1.getPoints()));
        return standings;
    }

    @Override public abstract void scheduleMatches();
    @Override public abstract void playNextRound();
}