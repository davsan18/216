package sportsmanager.core;
import java.util.List;

public interface ITeam extends java.io.Serializable {
    String getName();
    List<IPlayer> getPlayers();
    void addPlayer(IPlayer player);
    int getPoints();
    void addPoints(int points);
    int getOverallSkill();

    // Tactic / coach / mid-game substitution APIs
    void setTactic(String tactic);
    String getTactic();
    void setCoach(ICoach coach);
    ICoach getCoach();
    void substitutePlayer(IPlayer out, IPlayer in);
}