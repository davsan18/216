package sportsmanager.core;

public interface ICoach extends java.io.Serializable {
    String getName();
    int getBonusSkill(); // Extra power the coach contributes to the team
}