package sportsmanager.core;

public class Coach implements ICoach {
    private String name;
    private int bonusSkill;

    public Coach(String name, int bonusSkill) {
        this.name = name;
        this.bonusSkill = bonusSkill;
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getBonusSkill() { return bonusSkill; }
}