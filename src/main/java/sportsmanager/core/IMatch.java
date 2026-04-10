package sportsmanager.core;

public interface IMatch {
    ITeam getHomeTeam();
    ITeam getAwayTeam();
    void play();
    ITeam getWinner(); // Beraberlikte null döner
    String getScore();
    boolean isPlayed();
}