package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sportsmanager.core.*;
import sportsmanager.football.FootballFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SportsManagerTest {

    private ISportFactory factory;

    @BeforeEach
    public void setup() {
        factory = new FootballFactory();
    }

    // --- FACTORY TESTS ---
    @Test
    public void testFactoryCreatesCorrectPlayer() {
        IPlayer player = factory.createPlayer("Arda", "Midfielder", 85);
        assertEquals("Arda", player.getName());
    }

    @Test
    public void testFactoryCreatesCorrectTeam() {
        ITeam team = factory.createTeam("Real Madrid");
        assertEquals("Real Madrid", team.getName());
    }

    @Test
    public void testFactoryCreatesCorrectLeague() {
        ILeague league = factory.createLeague("La Liga");
        assertEquals("La Liga", league.getName());
    }

    // --- PLAYER TESTS ---
    @Test
    public void testPlayerPosition() {
        IPlayer player = factory.createPlayer("Messi", "Forward", 95);
        assertEquals("Forward", player.getPosition());
    }

    @Test
    public void testPlayerSkillLevel() {
        IPlayer player = factory.createPlayer("Ronaldo", "Forward", 95);
        assertEquals(95, player.getSkillLevel());
    }

    // --- TEAM TESTS ---
    @Test
    public void testTeamStartsEmpty() {
        ITeam team = factory.createTeam("Besiktas");
        assertTrue(team.getPlayers().isEmpty());
    }

    @Test
    public void testTeamStartsZeroPoints() {
        ITeam team = factory.createTeam("Trabzonspor");
        assertEquals(0, team.getPoints());
    }

    @Test
    public void testAddPlayerIncreasesTeamSize() {
        ITeam team = factory.createTeam("Arsenal");
        team.addPlayer(factory.createPlayer("Saka", "Forward", 88));
        assertEquals(1, team.getPlayers().size());
    }

    @Test
    public void testAddPointsToTeam() {
        ITeam team = factory.createTeam("Chelsea");
        team.addPoints(3);
        assertEquals(3, team.getPoints());
    }

    @Test
    public void testTeamOverallSkillCalculation() {
        ITeam team = factory.createTeam("Man City");
        team.addPlayer(factory.createPlayer("De Bruyne", "Mid", 90));
        team.addPlayer(factory.createPlayer("Haaland", "Forward", 92));
        assertEquals(182, team.getOverallSkill()); // 90 + 92
    }

    // --- MATCH TESTS ---
    @Test
    public void testMatchInitialStateIsNotPlayed() {
        ITeam home = factory.createTeam("Home");
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        assertFalse(match.isPlayed());
    }

    @Test
    public void testMatchAssignsTeamsCorrectly() {
        ITeam home = factory.createTeam("Home");
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        assertEquals(home, match.getHomeTeam());
        assertEquals(away, match.getAwayTeam());
    }

    @Test
    public void testMatchPlayChangesPlayedState() {
        ITeam home = factory.createTeam("Home");
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        match.play();
        assertTrue(match.isPlayed());
    }

    @Test
    public void testMatchPlayAwardsPoints() {
        ITeam home = factory.createTeam("Home");
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        match.play();
        // Birisi puan almış olmalı
        assertTrue(home.getPoints() > 0 || away.getPoints() > 0);
    }

    @Test
    public void testMatchWinnerMatchesPoints() {
        ITeam home = factory.createTeam("Home");
        home.addPlayer(factory.createPlayer("Star", "FW", 999)); // Kazanması garanti
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        match.play();
        assertEquals(home, match.getWinner());
        assertEquals(3, home.getPoints());
    }

    @Test
    public void testMatchScoreFormat() {
        ITeam home = factory.createTeam("Home");
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        match.play();
        assertTrue(match.getScore().contains("-"));
    }

    // --- LEAGUE TESTS ---
    @Test
    public void testLeagueStartsEmpty() {
        ILeague league = factory.createLeague("Serie A");
        assertTrue(league.getStandings().isEmpty());
    }

    @Test
    public void testLeagueAddTeam() {
        ILeague league = factory.createLeague("Serie A");
        league.addTeam(factory.createTeam("Juventus"));
        assertEquals(1, league.getStandings().size());
    }

    @Test
    public void testLeagueStandingsAreSorted() {
        ILeague league = factory.createLeague("Test League");
        ITeam team1 = factory.createTeam("Team 1");
        ITeam team2 = factory.createTeam("Team 2");

        team1.addPoints(1);
        team2.addPoints(5); // Daha yüksek

        league.addTeam(team1);
        league.addTeam(team2);

        List<ITeam> standings = league.getStandings();
        assertEquals("Team 2", standings.get(0).getName());
        assertEquals("Team 1", standings.get(1).getName());
    }

    @Test
    public void testPlayNextRoundTriggersMatch() {
        ILeague league = factory.createLeague("Test League");
        ITeam team1 = factory.createTeam("Team 1");
        ITeam team2 = factory.createTeam("Team 2");
        league.addTeam(team1);
        league.addTeam(team2);

        league.scheduleMatches();
        league.playNextRound();

        // Puanlar artık 0 olmamalı
        assertTrue(team1.getPoints() > 0 || team2.getPoints() > 0);
    }
}