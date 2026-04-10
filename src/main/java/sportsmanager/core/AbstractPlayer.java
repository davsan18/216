package sportsmanager.core;

public abstract class AbstractPlayer implements IPlayer {
    protected String name;
    protected String position;
    protected int skillLevel;

    public AbstractPlayer(String name, String position, int skillLevel) {
        this.name = name;
        this.position = position;
        this.skillLevel = skillLevel;
    }

    @Override public String getName() { return name; }
    @Override public String getPosition() { return position; }
    @Override public int getSkillLevel() { return skillLevel; }
}