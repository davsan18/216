package sportsmanager.core;

import java.util.List;

public interface IMatch extends java.io.Serializable {
    ITeam getHomeTeam();
    ITeam getAwayTeam();
    ITeam getWinner();
    String getScore();
    int getHomeScore();
    int getAwayScore();
    boolean isPlayed();

    void play();

    void start();
    boolean isStarted();
    boolean isFinished();

    String getClockDisplay();
    int getStartingSize();
    int getMaxSubs();

    List<MatchEvent> getEvents();

    List<IPlayer> getOnField(ITeam team);
    List<IPlayer> getBench(ITeam team);
    int getRemainingSubs(ITeam team);
    int getPendingReplacements(ITeam team);
    boolean needsSubstitution(ITeam team);

    boolean substitute(ITeam team, IPlayer out, IPlayer in);
    boolean replace(ITeam team, IPlayer in);

    void setUserTeam(ITeam team);
    ITeam getUserTeam();

    List<MatchEvent> tick(int amount);
    List<MatchEvent> tickToEnd();
    boolean isPaused();
}
