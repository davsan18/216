package sportsmanager.football;
import sportsmanager.core.*;

public class FootballFactory implements ISportFactory {
    @Override
    public IPlayer createPlayer(String name, String position, int skillLevel) {
        return new FootballPlayer(name, position, skillLevel);
    }
    @Override
    public ITeam createTeam(String name) {
        return new FootballTeam(name);
    }
    @Override
    public IMatch createMatch(ITeam home, ITeam away) {
        return new FootballMatch(home, away);
    }
    @Override
    public ILeague createLeague(String name) {
        return new FootballLeague(name);
    }
}