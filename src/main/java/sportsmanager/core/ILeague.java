package sportsmanager.core;
import java.util.List;

public interface ILeague {
    String getName();
    void addTeam(ITeam team);
    void scheduleMatches();
    void playNextRound();
    List<ITeam> getStandings();
}