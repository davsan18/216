package sportsmanager.volleyball;
import sportsmanager.core.AbstractTeam;
import sportsmanager.core.IPlayer;

public class VolleyballTeam extends AbstractTeam {
    public VolleyballTeam(String name) {
        super(name);
    }

    @Override
    public int getOverallSkill() {
        return players.stream().mapToInt(IPlayer::getSkillLevel).sum();
    }
}