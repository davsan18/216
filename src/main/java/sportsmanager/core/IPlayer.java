package sportsmanager.core;

public interface IPlayer extends java.io.Serializable {
    String getName();
    String getPosition();
    int getSkillLevel();
    boolean isInjured(); // YENİ: Sakatlık durumu
    void setInjured(boolean injured); // YENİ: Sakatlama/İyileştirme metodu
}