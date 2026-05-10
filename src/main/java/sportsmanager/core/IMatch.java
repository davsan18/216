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
    String getKickoffTime();
    void setKickoffTime(String kickoffTime);
    int getStartingSize();
    int getMaxSubs();

    List<MatchEvent> getEvents();

    List<IPlayer> getOnField(ITeam team);
    List<IPlayer> getBench(ITeam team);
    List<IPlayer> getRemoved(ITeam team);
    int getRemainingSubs(ITeam team);
    int getPendingReplacements(ITeam team);
    boolean needsSubstitution(ITeam team);

    boolean substitute(ITeam team, IPlayer out, IPlayer in);
    boolean swapLineup(ITeam team, IPlayer out, IPlayer in);
    boolean replace(ITeam team, IPlayer in);

    /** Returns the most recent player awaiting a forced substitution (after injury or red card). */
    IPlayer getLastForceRemoved(ITeam team);

    void setUserTeam(ITeam team);
    ITeam getUserTeam();

    List<MatchEvent> tick(int amount);
    List<MatchEvent> tickToEnd();
    boolean isPaused();
    default boolean isWaitingForSecondHalf() { return false; }
    default void startSecondHalf() {}
}
