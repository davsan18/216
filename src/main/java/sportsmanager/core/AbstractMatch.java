package sportsmanager.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
    protected String kickoffTime = "";

    protected List<MatchEvent> events = new ArrayList<>();

    protected TeamState homeState = new TeamState();
    protected TeamState awayState = new TeamState();
    protected ITeam userTeam;

    /** Last player removed by injury/red card who is awaiting a forced substitution (shown in UI dialog). */
    protected IPlayer homeLastForceRemoved;
    protected IPlayer awayLastForceRemoved;

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
    @Override public String getKickoffTime() { return kickoffTime == null ? "" : kickoffTime; }
    @Override public void setKickoffTime(String kickoffTime) { this.kickoffTime = kickoffTime == null ? "" : kickoffTime; }

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
    public List<IPlayer> getRemoved(ITeam team) {
        return new ArrayList<>(stateOf(team).removed);
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
        addKickoffEvent();
    }

    /** Subclasses add their localized kickoff event. */
    protected abstract void addKickoffEvent();

    protected void setupLineup(ITeam team, TeamState state) {
        state.onField.clear();
        state.bench.clear();
        state.removed.clear();
        state.missing.clear();
        state.suspended.clear();
        state.subsUsed = 0;
        state.pendingReplacements = 0;
        int starters = getStartingSize();

        // Injured players have a 35% chance of healing before the next match
        java.util.List<IPlayer> healthy = new java.util.ArrayList<>();
        for (IPlayer p : team.getPlayers()) {
            p.resetMatchCards();
            if (p.isInjured() && Math.random() < 0.35) {
                p.setInjured(false);
            }
            if (p.isSuspended()) {
                state.suspended.add(p);
                state.missing.add(p);
            } else if (!p.isInjured()) {
                healthy.add(p);
            } else {
                state.missing.add(p); // Still injured; cannot play this match
            }
        }
        int idx = 0;
        for (IPlayer p : healthy) {
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
        in.setSubInClock(getClockDisplay());
        events.add(new MatchEvent(MatchEvent.Type.SUBSTITUTION, getClockDisplay(),
                team, out, in,
                I18n.f("ev.subSwap", out.getName(), in.getName(), team.getName())));
        return true;
    }

    @Override
    public boolean swapLineup(ITeam team, IPlayer out, IPlayer in) {
        TeamState s = stateOf(team);
        if (out == null || in == null) return false;
        if (!s.onField.contains(out) || !s.bench.contains(in)) return false;
        s.onField.remove(out);
        s.bench.add(out);
        s.bench.remove(in);
        s.onField.add(in);
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
        in.setSubInClock(getClockDisplay());
        if (team == homeTeam) homeLastForceRemoved = null;
        else awayLastForceRemoved = null;
        events.add(new MatchEvent(MatchEvent.Type.SUBSTITUTION, getClockDisplay(),
                team, null, in,
                I18n.f("ev.subIn", in.getName(), team.getName())));
        return true;
    }

    @Override
    public IPlayer getLastForceRemoved(ITeam team) {
        return team == homeTeam ? homeLastForceRemoved : awayLastForceRemoved;
    }

    /** Removes an injured/red-carded player from the field. flagsForceSub=true means user must replace if possible. */
    protected void removeFromField(ITeam team, IPlayer player, boolean flagsForceSub) {
        TeamState s = stateOf(team);
        s.onField.remove(player);
        s.removed.add(player);
        if (flagsForceSub && s.subsUsed < getMaxSubs() && !s.bench.isEmpty()) {
            s.pendingReplacements++;
            if (team == homeTeam) homeLastForceRemoved = player;
            else awayLastForceRemoved = player;
        }
    }

    @Override
    public List<MatchEvent> tickToEnd() {
        List<MatchEvent> all = new ArrayList<>();
        if (!started) start();
        int safety = 10000;
        while (!played) {
            if (isPaused()) {
                autoResolveSubs();
            }
            List<MatchEvent> chunk = tick(getAutoChunk());
            all.addAll(chunk);
            if (--safety <= 0) break;
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
            s.pendingReplacements = 0; // give up and play short-handed
        }
    }

    protected int getAutoChunk() { return 10; }

    protected void completeSuspensionService() {
        for (IPlayer p : homeState.suspended) p.decrementSuspensionMatches();
        for (IPlayer p : awayState.suspended) p.decrementSuspensionMatches();
        homeState.suspended.clear();
        awayState.suspended.clear();
    }

    protected abstract void awardLeaguePoints();

    public static class TeamState implements Serializable {
        private static final long serialVersionUID = 1L;
        public Set<IPlayer> onField = new LinkedHashSet<>();
        public Set<IPlayer> bench = new LinkedHashSet<>();
        public Set<IPlayer> removed = new LinkedHashSet<>();
        public Set<IPlayer> missing = new LinkedHashSet<>();
        public Set<IPlayer> suspended = new LinkedHashSet<>();
        public int subsUsed = 0;
        public int pendingReplacements = 0;
    }
}
