package sportsmanager.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AbstractMatch implements IMatch {
    private static final long serialVersionUID = 2L;

    protected ITeam homeTeam;
    protected ITeam awayTeam;
    protected int homeScore = 0;
    protected int awayScore = 0;
    protected boolean played = false;
    protected boolean started = false;
    protected ITeam winner = null;

    protected List<MatchEvent> events = new ArrayList<>();

    protected TeamState homeState = new TeamState();
    protected TeamState awayState = new TeamState();
    protected ITeam userTeam;

    protected transient Random rand;

    public AbstractMatch(ITeam homeTeam, ITeam awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    @Override public ITeam getHomeTeam() { return homeTeam; }
    @Override public ITeam getAwayTeam() { return awayTeam; }
    @Override public boolean isPlayed() { return played; }
    @Override public boolean isFinished() { return played; }
    @Override public boolean isStarted() { return started; }
    @Override public ITeam getWinner() { return winner; }
    @Override public String getScore() { return homeScore + " - " + awayScore; }
    @Override public int getHomeScore() { return homeScore; }
    @Override public int getAwayScore() { return awayScore; }

    @Override public List<MatchEvent> getEvents() { return events; }

    @Override
    public List<IPlayer> getOnField(ITeam team) {
        return new ArrayList<>(stateOf(team).onField);
    }

    @Override
    public List<IPlayer> getBench(ITeam team) {
        return new ArrayList<>(stateOf(team).bench);
    }

    @Override
    public int getRemainingSubs(ITeam team) {
        return Math.max(0, getMaxSubs() - stateOf(team).subsUsed);
    }

    @Override
    public int getPendingReplacements(ITeam team) {
        return stateOf(team).pendingReplacements;
    }

    @Override
    public boolean needsSubstitution(ITeam team) {
        return stateOf(team).pendingReplacements > 0
                && !stateOf(team).bench.isEmpty();
    }

    @Override public void setUserTeam(ITeam team) { this.userTeam = team; }
    @Override public ITeam getUserTeam() { return userTeam; }

    @Override
    public boolean isPaused() {
        if (userTeam == null) return false;
        return needsSubstitution(userTeam);
    }

    /** Auto-resolve pending replacements for the non-user team only. */
    public void autoResolveOpponentSubs() {
        if (userTeam != homeTeam && stateOf(homeTeam).pendingReplacements > 0) autoResolveFor(homeTeam);
        if (userTeam != awayTeam && stateOf(awayTeam).pendingReplacements > 0) autoResolveFor(awayTeam);
    }

    @Override
    public void start() {
        if (started || played) return;
        started = true;
        rand = new Random();
        setupLineup(homeTeam, homeState);
        setupLineup(awayTeam, awayState);
        events.add(new MatchEvent(MatchEvent.Type.KICKOFF, getClockDisplay(),
                null, null, null, getKickoffLabel() + " başladı: "
                + homeTeam.getName() + " - " + awayTeam.getName()));
    }

    protected void setupLineup(ITeam team, TeamState state) {
        state.onField.clear();
        state.bench.clear();
        state.removed.clear();
        state.subsUsed = 0;
        state.pendingReplacements = 0;
        int starters = getStartingSize();
        int idx = 0;
        for (IPlayer p : team.getPlayers()) {
            p.resetMatchCards();
            p.setInjured(false);
            if (idx < starters) state.onField.add(p);
            else state.bench.add(p);
            idx++;
        }
    }

    protected TeamState stateOf(ITeam team) {
        return team == homeTeam ? homeState : awayState;
    }

    @Override
    public boolean substitute(ITeam team, IPlayer out, IPlayer in) {
        TeamState s = stateOf(team);
        if (out == null || in == null) return false;
        if (!s.onField.contains(out)) return false;
        if (!s.bench.contains(in)) return false;
        if (s.subsUsed >= getMaxSubs() && s.pendingReplacements == 0) return false;
        s.onField.remove(out);
        s.removed.add(out);
        s.bench.remove(in);
        s.onField.add(in);
        s.subsUsed++;
        events.add(new MatchEvent(MatchEvent.Type.SUBSTITUTION, getClockDisplay(),
                team, out, in, "Değişiklik: " + out.getName() + " ↔ " + in.getName()
                + " (" + team.getName() + ")"));
        return true;
    }

    @Override
    public boolean replace(ITeam team, IPlayer in) {
        TeamState s = stateOf(team);
        if (in == null || !s.bench.contains(in)) return false;
        if (s.pendingReplacements <= 0) return false;
        if (s.subsUsed >= getMaxSubs()) return false;
        s.bench.remove(in);
        s.onField.add(in);
        s.subsUsed++;
        s.pendingReplacements--;
        events.add(new MatchEvent(MatchEvent.Type.SUBSTITUTION, getClockDisplay(),
                team, null, in, in.getName() + " sahaya girdi (" + team.getName() + ")"));
        return true;
    }

    /** Removes an injured/red-carded player from the field. flagsForceSub=true means user must replace if possible. */
    protected void removeFromField(ITeam team, IPlayer player, boolean flagsForceSub) {
        TeamState s = stateOf(team);
        s.onField.remove(player);
        s.removed.add(player);
        if (flagsForceSub && s.subsUsed < getMaxSubs() && !s.bench.isEmpty()) {
            s.pendingReplacements++;
        }
    }

    @Override
    public List<MatchEvent> tickToEnd() {
        List<MatchEvent> all = new ArrayList<>();
        if (!started) start();
        while (!played) {
            if (isPaused()) {
                autoResolveSubs();
            }
            List<MatchEvent> chunk = tick(getAutoChunk());
            all.addAll(chunk);
            if (chunk.isEmpty() && !played) break;
        }
        return all;
    }

    /** AI auto-picks the highest-skilled bench player to replace pending. */
    protected void autoResolveSubs() {
        autoResolveFor(homeTeam);
        autoResolveFor(awayTeam);
    }

    protected void autoResolveFor(ITeam team) {
        TeamState s = stateOf(team);
        while (s.pendingReplacements > 0 && !s.bench.isEmpty() && s.subsUsed < getMaxSubs()) {
            IPlayer best = null;
            for (IPlayer p : s.bench) {
                if (best == null || p.getSkillLevel() > best.getSkillLevel()) best = p;
            }
            replace(team, best);
        }
        if (s.pendingReplacements > 0) {
            s.pendingReplacements = 0; // give up — play short-handed
        }
    }

    protected int getAutoChunk() { return 10; }

    protected abstract String getKickoffLabel();
    protected abstract void awardLeaguePoints();

    public static class TeamState implements Serializable {
        private static final long serialVersionUID = 1L;
        public Set<IPlayer> onField = new HashSet<>();
        public Set<IPlayer> bench = new HashSet<>();
        public Set<IPlayer> removed = new HashSet<>();
        public int subsUsed = 0;
        public int pendingReplacements = 0;
    }
}
