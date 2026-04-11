package sportsmanager.football;

import sportsmanager.core.AbstractTeam;
import sportsmanager.core.IPlayer;

public class FootballTeam extends AbstractTeam {

    public FootballTeam(String name) {
        super(name);
    }

    @Override
    public int getOverallSkill() {
        return players.stream().mapToInt(IPlayer::getSkillLevel).sum();
    }
}