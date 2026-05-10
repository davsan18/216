package sportsmanager.core;

import java.io.Serializable;

public class MatchEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        KICKOFF, GOAL, YELLOW_CARD, RED_CARD, INJURY, SUBSTITUTION,
        HALFTIME, FULLTIME, SET_START, SET_END, MATCH_END,
        VAR_CHECK, PENALTY, FREE_KICK
    }

    public final Type type;
    public final String clock;
    public final ITeam team;
    public final IPlayer player;
    public final IPlayer playerIn;
    public final String text;

    public MatchEvent(Type type, String clock, ITeam team, IPlayer player, IPlayer playerIn, String text) {
        this.type = type;
        this.clock = clock;
        this.team = team;
        this.player = player;
        this.playerIn = playerIn;
        this.text = text;
    }

    public String getIcon() {
        switch (type) {
            case GOAL: return "⚽";
            case YELLOW_CARD: return "🟨";
            case RED_CARD: return "🟥";
            case INJURY: return "⚕";
            case SUBSTITUTION: return "🔁";
            case KICKOFF: return "▶";
            case HALFTIME: return "⏸";
            case FULLTIME:
            case MATCH_END: return "⏹";
            case SET_START: return "▶";
            case SET_END: return "✓";
            case VAR_CHECK: return "📺";
            case PENALTY: return "🎯";
            case FREE_KICK: return "🦶";
            default: return "•";
        }
    }
}
