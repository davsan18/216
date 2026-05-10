package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sportsmanager.core.*;
import sportsmanager.football.FootballFactory;
import sportsmanager.volleyball.VolleyballFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // At least one side should receive points.
        assertTrue(home.getPoints() > 0 || away.getPoints() > 0);
    }

    @Test
    public void testMatchWinnerMatchesPoints() {
        ITeam home = factory.createTeam("Home");
        home.addPlayer(factory.createPlayer("Star", "FW", 999)); // Makes winning overwhelmingly likely.
        ITeam away = factory.createTeam("Away");
        IMatch match = factory.createMatch(home, away);
        match.play();
        if (match.getWinner() == null) {
            assertEquals(1, home.getPoints());
            assertEquals(1, away.getPoints());
        } else {
            assertEquals(3, match.getWinner().getPoints());
        }
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
        team2.addPoints(5); // Higher than team1.

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

        // Points should no longer be zero.
        assertTrue(team1.getPoints() > 0 || team2.getPoints() > 0);
    }

    @Test
    public void testFootballScheduleAvoidsThreeStraightHomeOrAwayMatches() {
        ILeague league = buildLeagueForSchedule(new FootballFactory());
        assertScheduleHasNoThreeGameHomeAwayStreak(league);
        assertDoubleRoundRobinHomeAwayBalanced(league);
    }

    @Test
    public void testVolleyballScheduleAvoidsThreeStraightHomeOrAwayMatches() {
        ILeague league = buildLeagueForSchedule(new VolleyballFactory());
        assertScheduleHasNoThreeGameHomeAwayStreak(league);
        assertDoubleRoundRobinHomeAwayBalanced(league);
    }

    @Test
    public void testScheduledMatchesHaveEveningKickoffTimes() {
        ILeague league = buildLeagueForSchedule(new FootballFactory());
        for (IMatch match : league.getScheduledMatches()) {
            assertTrue(match.getKickoffTime().matches("(18:00|19:45|21:30|20:00)"));
        }
    }

    @Test
    public void testFootballSubbedOutPlayerCannotReEnterSameMatch() {
        ISportFactory sportFactory = new FootballFactory();
        ITeam home = sportFactory.createTeam("Home");
        ITeam away = sportFactory.createTeam("Away");
        fillTeam(home, sportFactory, 12);
        fillTeam(away, sportFactory, 12);

        IMatch match = sportFactory.createMatch(home, away);
        match.start();
        IPlayer out = match.getOnField(home).get(1);
        IPlayer in = match.getBench(home).get(0);

        assertTrue(match.substitute(home, out, in));
        assertFalse(match.substitute(home, in, out));
        assertTrue(match.getRemoved(home).contains(out));
    }

    @Test
    public void testVolleyballSubbedOutPlayerCanReEnterSameMatch() {
        ISportFactory sportFactory = new VolleyballFactory();
        ITeam home = sportFactory.createTeam("Home");
        ITeam away = sportFactory.createTeam("Away");
        fillTeam(home, sportFactory, 7);
        fillTeam(away, sportFactory, 7);

        IMatch match = sportFactory.createMatch(home, away);
        match.start();
        IPlayer out = match.getOnField(home).get(0);
        IPlayer in = match.getBench(home).get(0);

        assertTrue(match.substitute(home, out, in));
        assertTrue(match.substitute(home, in, out));
        assertFalse(match.getRemoved(home).contains(out));
    }

    @Test
    public void testSuspendedFootballPlayerMissesOneMatchThenReturns() {
        ISportFactory sportFactory = new FootballFactory();
        ITeam home = sportFactory.createTeam("Home");
        ITeam away = sportFactory.createTeam("Away");
        fillTeam(home, sportFactory, 12);
        fillTeam(away, sportFactory, 12);
        IPlayer suspended = home.getPlayers().get(1);
        suspended.addSuspensionMatches(1);

        IMatch match = sportFactory.createMatch(home, away);
        match.start();

        assertFalse(match.getOnField(home).contains(suspended));
        assertFalse(match.getBench(home).contains(suspended));
        assertTrue(suspended.isSuspended());

        match.tickToEnd();
        assertFalse(suspended.isSuspended());
    }

    @Test
    public void testFootballLineupIncludesGoalkeeperEvenWhenGoalkeeperIsLateInRoster() {
        ISportFactory sportFactory = new FootballFactory();
        ITeam home = sportFactory.createTeam("Home");
        ITeam away = sportFactory.createTeam("Away");
        for (int i = 1; i <= 11; i++) {
            home.addPlayer(sportFactory.createPlayer("Outfield " + i, "Forward", 70 + i));
            away.addPlayer(sportFactory.createPlayer("Away Outfield " + i, "Forward", 70 + i));
        }
        home.addPlayer(sportFactory.createPlayer("Late Keeper", "Goalkeeper", 85));
        away.addPlayer(sportFactory.createPlayer("Away Late Keeper", "Goalkeeper", 85));

        IMatch match = sportFactory.createMatch(home, away);
        match.start();

        assertTrue(hasGoalkeeper(match.getOnField(home)));
        assertTrue(hasGoalkeeper(match.getOnField(away)));
    }

    @Test
    public void testFootballCannotSubstituteOnlyGoalkeeperForOutfieldPlayer() {
        ISportFactory sportFactory = new FootballFactory();
        ITeam home = sportFactory.createTeam("Home");
        ITeam away = sportFactory.createTeam("Away");
        fillTeam(home, sportFactory, 12);
        fillTeam(away, sportFactory, 12);

        IMatch match = sportFactory.createMatch(home, away);
        match.start();
        IPlayer goalkeeper = match.getOnField(home).get(0);
        IPlayer outfieldBench = match.getBench(home).get(0);

        assertFalse(match.substitute(home, goalkeeper, outfieldBench));
        assertTrue(hasGoalkeeper(match.getOnField(home)));
    }

    @Test
    public void testFootballWaitsForSecondHalfKickoffAtHalftime() {
        ISportFactory sportFactory = new FootballFactory();
        ITeam home = sportFactory.createTeam("Home");
        ITeam away = sportFactory.createTeam("Away");
        fillTeam(home, sportFactory, 12);
        fillTeam(away, sportFactory, 12);
        home.addPlayer(sportFactory.createPlayer("Backup Keeper", "Goalkeeper", 68));
        away.addPlayer(sportFactory.createPlayer("Away Backup Keeper", "Goalkeeper", 68));

        IMatch match = sportFactory.createMatch(home, away);
        match.start();
        for (int i = 0; i < 200 && !match.isWaitingForSecondHalf() && !match.isFinished(); i++) {
            match.tick(1);
        }

        assertTrue(match.isWaitingForSecondHalf());
        assertEquals("45'", match.getClockDisplay());
        match.tick(1);
        assertEquals("45'", match.getClockDisplay());

        match.startSecondHalf();
        assertFalse(match.isWaitingForSecondHalf());
        match.tick(1);
        assertEquals("46'", match.getClockDisplay());
    }

    private ILeague buildLeagueForSchedule(ISportFactory sportFactory) {
        ILeague league = sportFactory.createLeague("Schedule Test");
        for (int i = 1; i <= 6; i++) {
            league.addTeam(sportFactory.createTeam("Team " + i));
        }
        league.scheduleMatches();
        return league;
    }

    private void fillTeam(ITeam team, ISportFactory sportFactory, int count) {
        for (int i = 1; i <= count; i++) {
            team.addPlayer(sportFactory.createPlayer("Player " + i, i == 1 ? "Goalkeeper" : "Forward", 70 + i));
        }
    }

    private boolean hasGoalkeeper(List<IPlayer> players) {
        for (IPlayer player : players) {
            String pos = player.getPosition() == null ? "" : player.getPosition().toLowerCase();
            if (pos.contains("goalkeeper") || pos.contains("kale")) return true;
        }
        return false;
    }

    private void assertScheduleHasNoThreeGameHomeAwayStreak(ILeague league) {
        Map<ITeam, List<Boolean>> sequences = new HashMap<>();
        for (ITeam team : league.getTeams()) {
            sequences.put(team, new ArrayList<>());
        }
        for (IMatch match : league.getScheduledMatches()) {
            sequences.get(match.getHomeTeam()).add(true);
            sequences.get(match.getAwayTeam()).add(false);
        }
        for (Map.Entry<ITeam, List<Boolean>> entry : sequences.entrySet()) {
            List<Boolean> sequence = entry.getValue();
            for (int i = 2; i < sequence.size(); i++) {
                boolean threeStraight = sequence.get(i).equals(sequence.get(i - 1))
                        && sequence.get(i - 1).equals(sequence.get(i - 2));
                assertFalse(threeStraight, entry.getKey().getName() + " has 3 straight home/away matches");
            }
        }
    }

    private void assertDoubleRoundRobinHomeAwayBalanced(ILeague league) {
        Map<String, Integer> pairCounts = new HashMap<>();
        Map<String, Map<String, Integer>> homeCounts = new HashMap<>();
        for (IMatch match : league.getScheduledMatches()) {
            String home = match.getHomeTeam().getName();
            String away = match.getAwayTeam().getName();
            String key = home.compareTo(away) < 0 ? home + "|" + away : away + "|" + home;
            pairCounts.merge(key, 1, Integer::sum);
            homeCounts.computeIfAbsent(key, k -> new HashMap<>()).merge(home, 1, Integer::sum);
        }

        int teamCount = league.getTeams().size();
        assertEquals(teamCount * (teamCount - 1), league.getScheduledMatches().size());
        for (String pair : pairCounts.keySet()) {
            assertEquals(2, pairCounts.get(pair), pair + " should be played twice");
            assertEquals(2, homeCounts.get(pair).size(), pair + " should have one home match per team");
        }
    }
}
