package sportsmanager.core;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTeam implements ITeam {
    protected String name;
    protected List<IPlayer> players;
    protected int points;

    // Tactic and coach (coach may be null since the user is the coach)
    protected String tactic;
    protected ICoach coach;

    public AbstractTeam(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.points = 0;
        this.tactic = "Standard"; // Default tactic
    }

    @Override public String getName() { return name; }
    @Override public List<IPlayer> getPlayers() { return players; }
    @Override public void addPlayer(IPlayer player) {
        if (player != null && player.getJerseyNumber() <= 0) {
            player.setJerseyNumber(players.size() + 1);
        }
        this.players.add(player);
    }
    @Override public int getPoints() { return points; }
    @Override public void addPoints(int points) { this.points += points; }

    @Override public void setTactic(String tactic) { this.tactic = tactic; }
    @Override public String getTactic() { return tactic; }
    @Override public void setCoach(ICoach coach) { this.coach = coach; }
    @Override public ICoach getCoach() { return coach; }

    @Override
    public void substitutePlayer(IPlayer out, IPlayer in) {
        if (players.contains(out)) {
            players.remove(out);
            players.add(in);
        }
    }

    @Override public abstract int getOverallSkill();
}
