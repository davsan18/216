package sportsmanager.core;

import java.util.List;

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

    /** Total goals scored in the current match. */
    int getGoalsThisMatch();

    /** Records a goal scored at the given clock label (e.g. "67'"). */
    void addGoalThisMatch(String clock);

    /** Clock labels of every goal scored this match (in chronological order). */
    List<String> getGoalMinutes();

    /** Clock at which this player came on as a substitute, or null if started. */
    String getSubInClock();
    void setSubInClock(String clock);
}
