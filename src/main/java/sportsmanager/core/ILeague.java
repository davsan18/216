package sportsmanager.core;
import java.util.List;

public interface ILeague extends java.io.Serializable {
    String getName();
    void addTeam(ITeam team);
    void scheduleMatches();
    void playNextRound();
    List<ITeam> getStandings();
    List<ITeam> getTeams();
    List<IMatch> getScheduledMatches();

    void setManagedTeam(ITeam team);
    ITeam getManagedTeam();
    IMatch getNextMatchForManaged();
    int getRoundOf(IMatch match);
    void autoFinishOtherMatchesInRound(IMatch reference);
}