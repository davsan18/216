package sportsmanager.volleyball;

import sportsmanager.core.AbstractMatch;
import sportsmanager.core.I18n;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;
import sportsmanager.core.MatchEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class VolleyballMatch extends AbstractMatch {
    private static final long serialVersionUID = 2L;
    private static final int SETS_TO_WIN = 3;
    private static final int MAX_SETS = 5;

    private int homeSetsWon = 0;
    private int awaySetsWon = 0;
    private int currentSet = 1;
    private int homeSetPoints = 0;
    private int awaySetPoints = 0;
    private boolean servingHome = true;
    private Set<IPlayer> homeSetStarters = new HashSet<>();
    private Set<IPlayer> awaySetStarters = new HashSet<>();
    private Set<IPlayer> homeStartersUsedSub = new HashSet<>();
    private Set<IPlayer> awayStartersUsedSub = new HashSet<>();
    private Map<IPlayer, IPlayer> homeSubToStarter = new HashMap<>();
    private Map<IPlayer, IPlayer> awaySubToStarter = new HashMap<>();
    private boolean waitingForNextSet = false;

    public VolleyballMatch(ITeam home, ITeam away) {
        super(home, away);
    }

    @Override public int getStartingSize() { return 6; }
    @Override public int getMaxSubs() { return 6; }
    @Override public int getHomeScore() { return homeSetsWon; }
    @Override public int getAwayScore() { return awaySetsWon; }
    public int getHomeSetPoints() { return homeSetPoints; }
    public int getAwaySetPoints() { return awaySetPoints; }
    public int getCurrentSet() { return currentSet; }

    @Override
    public boolean isPaused() {
        return waitingForNextSet || super.isPaused();
    }

    @Override
    public boolean isWaitingForSecondHalf() {
        return waitingForNextSet;
    }

    @Override
    public void startSecondHalf() {
        if (!waitingForNextSet) return;
        waitingForNextSet = false;
        currentSet++;
        homeSetPoints = 0;
        awaySetPoints = 0;
        servingHome = currentSet % 2 == 1;
        homeState.subsUsed = 0;
        awayState.subsUsed = 0;
        resetSetSubstitutionRules();
        events.add(new MatchEvent(MatchEvent.Type.SET_START, getClockDisplay(), null, null, null,
                I18n.f("ev.setStart", currentSet)));
    }

    @Override
    public String getResumeButtonLabel() {
        return I18n.f("mv.startNextSet", currentSet + 1);
    }
    @Override public String getClockDisplay() {
        if (!started) return "S1 0-0";
        if (played) return homeSetsWon + "-" + awaySetsWon + " (set)";
        return "S" + currentSet + " " + homeSetPoints + "-" + awaySetPoints;
    }
    @Override protected int getAutoChunk() { return 8; }

    @Override
    protected void addKickoffEvent() {
        resetSetSubstitutionRules();
        events.add(new MatchEvent(MatchEvent.Type.KICKOFF, getClockDisplay(),
                null, null, null,
                I18n.f("ev.kickoffVolley", homeTeam.getName(), awayTeam.getName())));
    }

    @Override
    public void play() {
        if (!started) start();
        tickToEnd();
    }

    @Override
    public List<MatchEvent> tickToEnd() {
        List<MatchEvent> all = new ArrayList<>();
        if (!started) start();
        int safety = 10000;
        while (!played) {
            if (waitingForNextSet) {
                startSecondHalf();
            }
            if (super.isPaused()) {
                autoResolveSubs();
            }
            List<MatchEvent> chunk = tick(getAutoChunk());
            all.addAll(chunk);
            if (--safety <= 0) break;
        }
        return all;
    }

    @Override
    public boolean substitute(ITeam team, IPlayer out, IPlayer in) {
        TeamState s = stateOf(team);
        if (out == null || in == null) return false;
        if (!s.onField.contains(out)) return false;
        if (!s.bench.contains(in)) return false;
        if (s.subsUsed >= getMaxSubs()) return false;

        Set<IPlayer> starters = setStarters(team);
        Set<IPlayer> startersUsed = startersUsedSub(team);
        Map<IPlayer, IPlayer> subToStarter = subToStarter(team);
        boolean outIsStarter = starters.contains(out);
        if (outIsStarter) {
            if (startersUsed.contains(out)) return false;
            startersUsed.add(out);
            subToStarter.put(in, out);
        } else {
            IPlayer pairedStarter = subToStarter.get(out);
            if (pairedStarter == null || pairedStarter != in) return false;
            subToStarter.remove(out);
        }

        replaceOnField(s, out, in);
        s.bench.remove(in);
        s.bench.add(out);
        s.subsUsed++;
        in.setSubInClock(getClockDisplay());
        events.add(new MatchEvent(MatchEvent.Type.SUBSTITUTION, getClockDisplay(),
                team, out, in,
                I18n.f("ev.subSwap", out.getName(), in.getName(), team.getName())));
        return true;
    }

    @Override
    public List<MatchEvent> tick(int amount) {
        if (!started) start();
        if (rand == null) rand = new Random();
        List<MatchEvent> chunk = new ArrayList<>();
        if (played || isPaused()) return chunk;

        int startIdx = events.size();
        for (int i = 0; i < amount && !played; i++) {
            playRally();
            autoResolveOpponentSubs();
            if (isSetOver()) endSet();
            if (played) break;
            if (isPaused()) break;
        }
        chunk.addAll(events.subList(startIdx, events.size()));
        return chunk;
    }

    private void playRally() {
        int homePower = computePower(homeTeam, true);
        int awayPower = computePower(awayTeam, false);
        double homeProb = (double) homePower / Math.max(1, homePower + awayPower);

        boolean homePoint;

        // Check injuries
        if (rand.nextDouble() < 0.004) {
            ITeam t = rand.nextBoolean() ? homeTeam : awayTeam;
            TeamState s = stateOf(t);
            if (!s.onField.isEmpty()) {
                IPlayer p = pickRandom(s.onField);
                p.setInjured(true);
                events.add(new MatchEvent(MatchEvent.Type.INJURY, getClockDisplay(), t, p, null,
                        I18n.f("ev.injury", p.getName(), t.getName())));
                removeFromField(t, p, true);
            }
        }

        // Decide who wins the rally
        homePoint = rand.nextDouble() < homeProb;
        if (homePoint) {
            if (!servingHome) {
                servingHome = true;
                rotate(homeTeam);
            }
            homeSetPoints++;
            IPlayer scorer = pickScorer(homeTeam);
            if (scorer != null) {
                scorer.addGoalThisMatch(getClockDisplay());
                scorer.addSeasonGoal();
                events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), homeTeam, scorer, null,
                        I18n.f("ev.scorePoint", scorer.getName(), homeTeam.getName())));
            }
        } else {
            if (servingHome) {
                servingHome = false;
                rotate(awayTeam);
            }
            awaySetPoints++;
            IPlayer scorer = pickScorer(awayTeam);
            if (scorer != null) {
                scorer.addGoalThisMatch(getClockDisplay());
                scorer.addSeasonGoal();
                events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), awayTeam, scorer, null,
                        I18n.f("ev.scorePoint", scorer.getName(), awayTeam.getName())));
            }
        }
    }

    private boolean isSetOver() {
        int target = (currentSet == 5) ? 15 : 25;
        if (homeSetPoints >= target && homeSetPoints - awaySetPoints >= 2) return true;
        if (awaySetPoints >= target && awaySetPoints - homeSetPoints >= 2) return true;
        return false;
    }

    private void endSet() {
        boolean homeWon = homeSetPoints > awaySetPoints;
        if (homeWon) homeSetsWon++; else awaySetsWon++;
        events.add(new MatchEvent(MatchEvent.Type.SET_END, getClockDisplay(), null, null, null,
                I18n.f("ev.setEnd", currentSet, homeSetPoints, awaySetPoints, homeSetsWon, awaySetsWon)));

        if (homeSetsWon == SETS_TO_WIN || awaySetsWon == SETS_TO_WIN || currentSet == MAX_SETS) {
            finish();
            return;
        }
        waitingForNextSet = true;
    }

    private void resetSetSubstitutionRules() {
        homeSetStarters = new HashSet<>(homeState.onField);
        awaySetStarters = new HashSet<>(awayState.onField);
        homeStartersUsedSub.clear();
        awayStartersUsedSub.clear();
        homeSubToStarter.clear();
        awaySubToStarter.clear();
    }

    private Set<IPlayer> setStarters(ITeam team) {
        return team == homeTeam ? homeSetStarters : awaySetStarters;
    }

    private Set<IPlayer> startersUsedSub(ITeam team) {
        return team == homeTeam ? homeStartersUsedSub : awayStartersUsedSub;
    }

    private Map<IPlayer, IPlayer> subToStarter(ITeam team) {
        return team == homeTeam ? homeSubToStarter : awaySubToStarter;
    }

    private void replaceOnField(TeamState s, IPlayer out, IPlayer in) {
        List<IPlayer> order = new ArrayList<>(s.onField);
        int idx = order.indexOf(out);
        if (idx < 0) return;
        order.set(idx, in);
        s.onField.clear();
        s.onField.addAll(order);
    }

    private void rotate(ITeam team) {
        TeamState s = stateOf(team);
        if (s.onField.size() < 2) return;
        List<IPlayer> order = new ArrayList<>(s.onField);
        IPlayer last = order.remove(order.size() - 1);
        order.add(0, last);
        s.onField.clear();
        s.onField.addAll(order);
    }

    private void finish() {
        played = true;
        homeScore = homeSetsWon;
        awayScore = awaySetsWon;
        if (homeSetsWon > awaySetsWon) winner = homeTeam;
        else winner = awayTeam;
        awardLeaguePoints();
        completeSuspensionService();
        events.add(new MatchEvent(MatchEvent.Type.MATCH_END, getClockDisplay(), null, null, null,
                I18n.f("ev.matchEnd", homeTeam.getName(), homeSetsWon, awaySetsWon,
                        awayTeam.getName(), winner.getName())));
    }

    @Override
    protected void awardLeaguePoints() {
        boolean fiveSets = (homeSetsWon + awaySetsWon) == 5;
        if (winner == homeTeam) {
            homeTeam.addPoints(fiveSets ? 2 : 3);
            if (fiveSets) awayTeam.addPoints(1);
        } else if (winner == awayTeam) {
            awayTeam.addPoints(fiveSets ? 2 : 3);
            if (fiveSets) homeTeam.addPoints(1);
        }
    }

    private int computePower(ITeam team, boolean isHome) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return 1;
        int sum = 0;
        for (IPlayer p : s.onField) sum += p.getSkillLevel();
        int avg = sum / s.onField.size();
        int homeAdv = isHome ? 4 : 0;
        int sizePenalty = Math.max(0, 6 - s.onField.size()) * 5;
        return Math.max(1, avg + homeAdv - sizePenalty);
    }

    private IPlayer pickRandom(Set<IPlayer> pool) {
        int idx = rand.nextInt(pool.size());
        Iterator<IPlayer> it = pool.iterator();
        for (int i = 0; i < idx; i++) it.next();
        return it.next();
    }

    private IPlayer pickScorer(ITeam team) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return null;
        // Skill weighted; bonus for attackers and setters.
        int total = 0;
        for (IPlayer p : s.onField) total += weight(p);
        if (total <= 0) return pickRandom(s.onField);
        int r = rand.nextInt(total);
        int acc = 0;
        for (IPlayer p : s.onField) {
            acc += weight(p);
            if (r < acc) return p;
        }
        return pickRandom(s.onField);
    }

    private int weight(IPlayer p) {
        int w = p.getSkillLevel();
        String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
        if (pos.contains("smaç") || pos.contains("çapraz") || pos.contains("forvet") || pos.contains("spiker")) w += 25;
        else if (pos.contains("orta")) w += 18;
        else if (pos.contains("pasör") || pos.contains("setter")) w += 10;
        else if (pos.contains("libero")) w = Math.max(1, w - 30);
        return Math.max(1, w);
    }
}
