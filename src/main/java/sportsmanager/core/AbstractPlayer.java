package sportsmanager.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayer implements IPlayer {
    private static final long serialVersionUID = 4L;

    protected String name;
    protected String position;
    protected int skillLevel;
    protected boolean isInjured;
    protected int yellowCards;
    protected boolean redCard;
    protected List<String> goalMinutes = new ArrayList<>();
    protected String subInClock;

    public AbstractPlayer(String name, String position, int skillLevel) {
        this.name = name;
        this.position = position;
        this.skillLevel = skillLevel;
    }

    @Override public String getName() { return name; }
    @Override public String getPosition() { return position; }
    @Override public int getSkillLevel() { return skillLevel; }

    @Override public boolean isInjured() { return isInjured; }
    @Override public void setInjured(boolean injured) { this.isInjured = injured; }

    @Override public int getYellowCards() { return yellowCards; }
    @Override public boolean hasRedCard() { return redCard; }
    @Override public void addYellowCard() { this.yellowCards++; }
    @Override public void giveRedCard() { this.redCard = true; }

    @Override
    public void resetMatchCards() {
        this.yellowCards = 0;
        this.redCard = false;
        if (this.goalMinutes == null) this.goalMinutes = new ArrayList<>();
        else this.goalMinutes.clear();
        this.subInClock = null;
    }

    @Override
    public int getGoalsThisMatch() {
        return goalMinutes == null ? 0 : goalMinutes.size();
    }

    @Override
    public void addGoalThisMatch(String clock) {
        if (goalMinutes == null) goalMinutes = new ArrayList<>();
        goalMinutes.add(clock == null ? "" : clock);
    }

    @Override
    public List<String> getGoalMinutes() {
        return goalMinutes == null ? new ArrayList<>() : goalMinutes;
    }

    @Override public String getSubInClock() { return subInClock; }
    @Override public void setSubInClock(String clock) { this.subInClock = clock; }
}
