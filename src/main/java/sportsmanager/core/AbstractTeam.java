package sportsmanager.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTeam implements ITeam {

    protected String name;
    protected List<IPlayer> players;
    protected int points;

    public AbstractTeam(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.points = 0;
    }

    @Override
    public String getName() { return name; }

    @Override
    public List<IPlayer> getPlayers() { return players; }

    @Override
    public void addPlayer(IPlayer player) { this.players.add(player); }

    @Override
    public int getPoints() { return points; }

    @Override
    public void addPoints(int points) { this.points += points; }

    @Override
    public abstract int getOverallSkill();
}