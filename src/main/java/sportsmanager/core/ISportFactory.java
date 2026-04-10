package sportsmanager.core;

public interface ISportFactory {
    IPlayer createPlayer(String name, String position, int skillLevel);
    ITeam createTeam(String name);
    IMatch createMatch(ITeam home, ITeam away);
    ILeague createLeague(String name);
}