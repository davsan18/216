package sportsmanager.core;

public interface IMatch {
    ITeam getHomeTeam();
    ITeam getAwayTeam();
    void play();
    ITeam getWinner();
    String getScore();
    boolean isPlayed();
}