package sportsmanager;

import sportsmanager.core.*;
import sportsmanager.football.FootballFactory;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   SPORTS MANAGER SIMULATION         ");
        System.out.println("=========================================");

        ISportFactory factory = new FootballFactory();
        ILeague superLig = factory.createLeague("Super Lig");

        ITeam teamA = factory.createTeam("Beşiktaş");
        teamA.addPlayer(factory.createPlayer("Gomez", "Forward", 95));
        teamA.addPlayer(factory.createPlayer("Fabri", "Goalkeeper", 90));

        ITeam teamB = factory.createTeam("Fenerbahce");
        teamB.addPlayer(factory.createPlayer("Dzeko", "Forward", 93));
        teamB.addPlayer(factory.createPlayer("Demirel", "Goalkeeper", 89));

        superLig.addTeam(teamA);
        superLig.addTeam(teamB);

        System.out.println("\n[+] Teams are ready for the season:");
        System.out.println(" - " + teamA.getName() + " (Overall Skill: " + teamA.getOverallSkill() + ")");
        System.out.println(" - " + teamB.getName() + " (Overall Skill: " + teamB.getOverallSkill() + ")");

        System.out.println("\n[!] Scheduling matches and playing the first round...");
        superLig.scheduleMatches();
        superLig.playNextRound();

        System.out.println("\n=========================================");
        System.out.println("           CURRENT STANDINGS             ");
        System.out.println("=========================================");

        List<ITeam> standings = superLig.getStandings();
        for(int i = 0; i < standings.size(); i++) {
            ITeam t = standings.get(i);
            System.out.println((i+1) + ". " + t.getName() + " \t| Points: " + t.getPoints());
        }
        System.out.println("=========================================\n");
    }
}