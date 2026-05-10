package sportsmanager.core;

public abstract class AbstractPlayer implements IPlayer {
    private static final long serialVersionUID = 2L;

    protected String name;
    protected String position;
    protected int skillLevel;
    protected boolean isInjured;
    protected int yellowCards;
    protected boolean redCard;

    public AbstractPlayer(String name, String position, int skillLevel) {
        this.name = name;
        this.position = position;
        this.skillLevel = skillLevel;
        this.isInjured = false;
        this.yellowCards = 0;
        this.redCard = false;
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
    @Override public void resetMatchCards() { this.yellowCards = 0; this.redCard = false; }
}
