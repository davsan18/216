package sportsmanager.core;

public interface IMatch extends java.io.Serializable {
    ITeam getHomeTeam();
    ITeam getAwayTeam();
    void play();
    ITeam getWinner();
    String getScore();
    boolean isPlayed();
}