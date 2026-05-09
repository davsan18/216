package sportsmanager.core;

public abstract class AbstractPlayer implements IPlayer {
    protected String name;
    protected String position;
    protected int skillLevel;
    protected boolean isInjured;

    public AbstractPlayer(String name, String position, int skillLevel) {
        this.name = name;
        this.position = position;
        this.skillLevel = skillLevel;
        this.isInjured = false; // Oyuncular başlarken sağlıklıdır
    }

    @Override public String getName() { return name; }
    @Override public String getPosition() { return position; }
    @Override public int getSkillLevel() { return skillLevel; }
    @Override public boolean isInjured() { return isInjured; }
    @Override public void setInjured(boolean injured) { this.isInjured = injured; }
}