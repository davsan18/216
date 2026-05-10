package sportsmanager.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayer implements IPlayer {
    private static final long serialVersionUID = 4L;

    protected String name;
    protected String position;
    protected int skillLevel;
    protected int jerseyNumber;
    protected boolean isInjured;
    protected int yellowCards;
    protected boolean redCard;
    protected int seasonYellowCards;
    protected int seasonRedCards;
    protected int seasonGoals;
    protected int suspensionMatches;
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
    @Override public int getJerseyNumber() { return jerseyNumber; }
    @Override public void setJerseyNumber(int number) { this.jerseyNumber = Math.max(0, number); }

    @Override public boolean isInjured() { return isInjured; }
    @Override public void setInjured(boolean injured) { this.isInjured = injured; }

    @Override public int getYellowCards() { return yellowCards; }
    @Override public boolean hasRedCard() { return redCard; }
    @Override public void addYellowCard() { this.yellowCards++; }
    @Override public void giveRedCard() { this.redCard = true; }
    @Override public int getSeasonYellowCards() { return seasonYellowCards; }
    @Override public void addSeasonYellowCard() { this.seasonYellowCards++; }
    @Override public int getSeasonRedCards() { return seasonRedCards; }
    @Override public void addSeasonRedCard() { this.seasonRedCards++; }
    @Override public int getSeasonGoals() { return seasonGoals; }
    @Override public void addSeasonGoal() { this.seasonGoals++; }
    @Override public int getSuspensionMatches() { return suspensionMatches; }
    @Override public boolean isSuspended() { return suspensionMatches > 0; }
    @Override public void addSuspensionMatches(int matches) {
        if (matches > 0) this.suspensionMatches += matches;
    }
    @Override public void decrementSuspensionMatches() {
        if (this.suspensionMatches > 0) this.suspensionMatches--;
    }

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
