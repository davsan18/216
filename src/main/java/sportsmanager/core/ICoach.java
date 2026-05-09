package sportsmanager.core;

public interface ICoach extends java.io.Serializable {
    String getName();
    int getBonusSkill(); // Antrenörün takıma katacağı ekstra güç
}