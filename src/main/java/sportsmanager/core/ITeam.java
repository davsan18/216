package sportsmanager.core;
import java.util.List;

public interface ITeam {
    String getName();
    List<IPlayer> getPlayers();
    void addPlayer(IPlayer player);
    int getPoints();
    void addPoints(int points);
    int getOverallSkill();
}