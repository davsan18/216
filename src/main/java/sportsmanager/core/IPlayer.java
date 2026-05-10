package sportsmanager.core;

public interface IPlayer extends java.io.Serializable {
    String getName();
    String getPosition();
    int getSkillLevel();

    boolean isInjured();
    void setInjured(boolean injured);

    int getYellowCards();
    boolean hasRedCard();
    void addYellowCard();
    void giveRedCard();
    void resetMatchCards();
}
