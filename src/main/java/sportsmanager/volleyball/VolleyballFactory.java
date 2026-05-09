package sportsmanager.volleyball;
import sportsmanager.core.*;

public class VolleyballFactory implements ISportFactory {
    @Override
    public IPlayer createPlayer(String name, String position, int skillLevel) {
        return new VolleyballPlayer(name, position, skillLevel);
    }
    @Override
    public ITeam createTeam(String name) {
        return new VolleyballTeam(name);
    }
    @Override
    public IMatch createMatch(ITeam home, ITeam away) {
        return new VolleyballMatch(home, away);
    }
    @Override
    public ILeague createLeague(String name) {
        return new VolleyballLeague(name);
    }
}